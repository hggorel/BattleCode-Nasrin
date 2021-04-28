package nasrinplayer;
import battlecode.common.*;

import java.util.Map;


public class Miner extends Unit {

    boolean hasContributed = false;
    boolean currentlyWalking = true;
    boolean soupMining = false;
    MapLocation[] soups;
    MapLocation lastUsedRefinery;
    MapLocation targetLocation;

    //set up different modes for the miner -- mining, building, returning, fleeing
    final int MINING = 1;
    final int BUILDING = 2;
    final int RETURNING = 3;
    final int FLEEING = 4;

    int mode = MINING;

    public Miner(RobotController robotController) {

        super(robotController);
    }

    public void takeTurn() throws GameActionException {
        super.takeTurn();

        //first check to see if in range of any enemy netguns or drones
        RobotInfo[] nearbyEnemyRobots = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
        MapLocation myLocation = rc.getLocation();
        MapLocation hqLoc = comms.getHqLoc();

        /*TODO: make a better choice if the opposite distance isn't an option.
         *Maybe go through and store all the dangerous locations and then rule them out...
         *that way it checks to see if you are running back into a drone too!!! */
        int numEnemyNetGuns = 0;
        int numEnemyDrones = 0;
        int minDistanceToEnemy = 99999;
        RobotInfo closestEnemy = null;
        for(RobotInfo robot: nearbyEnemyRobots){
            MapLocation enemyLoc = robot.getLocation();
            int distance = myLocation.distanceSquaredTo(enemyLoc);
            if(distance<minDistanceToEnemy){
                minDistanceToEnemy = distance;
                closestEnemy = robot;
            }
            switch(robot.type){
                case NET_GUN:
                    numEnemyNetGuns++;
                    break;

                case DELIVERY_DRONE:
                    numEnemyDrones++;
                    break;
            }
        }

        //check to see if in range of enemy -- if so retreat
        //farthest range is 13... so
        if(myLocation.distanceSquaredTo(closestEnemy.getLocation()) <=13){
            mode = FLEEING;
            //this means we are in range -- so want to retreat
            Direction enemyDirection = myLocation.directionTo(closestEnemy.getLocation());
            if(rc.canMove(enemyDirection.opposite())){
                rc.move(enemyDirection.opposite());
            }
            else{
                MapLocation destination = myLocation.subtract(enemyDirection);
                pathing.tanBugPath(destination);
            }
        }

        //If full and cannot get any more soup -- go to close refinery or HQ to deposit
        if(rc.getSoupCarrying()>=RobotType.MINER.soupLimit - GameConstants.SOUP_MINING_RATE){
            //look for repository
            mode = RETURNING;
            if(lastUsedRefinery==null){ //then we're early enough in the game that HQ is it
                targetLocation = hqLoc;
            }
            else{
                targetLocation = lastUsedRefinery;
            }
        }
        else if (mode == MINING){
            //if not full and can keep looking for soup, first look immediately next to us
            MapLocation[] nextToSoup = rc.senseNearbySoup(1);

            MapLocation[] nearbySoup = rc.senseNearbySoup();

            if(nextToSoup.length > 0) {
                //Mine all soup that's immediately next to the robot until full
                MapLocation currentLocation = rc.getLocation();
                for(MapLocation loc: nextToSoup){
                    Direction possDir = currentLocation.directionTo(loc);
                    tryMine(possDir);
                }
            }
            else if (nearbySoup.length > 0){
                //If no things next to us, look at the ones within the possible distances and move to the closest
                int minDistance = 1000000;
                MapLocation targetSoup = rc.getLocation();
                for(int i=0; i<nearbySoup.length; i++){
                    int distance = rc.getLocation().distanceSquaredTo(nearbySoup[i]);
                    if (distance<minDistance) {
                        minDistance=distance;
                        targetSoup = nearbySoup[i];
                    }
                }
                pathing.tanBugPath(targetSoup);
                tryMine(rc.getLocation().directionTo(targetSoup));
            }
            else{
                mode = BUILDING;
            }
        }

        RobotInfo[] nearbyFriendlyRobots = rc.senseNearbyRobots(-1, rc.getTeam());
        int numRefinery = 0;
        int numNetGuns = 0;
        int numMiners = 0;
        int numDesignSchools = 0;
        int numVaporators = 0;
        int numFulfillmentCenters = 0;
        int numLandscapers = 0;
        MapLocation closestRefinery = hqLoc;
        for(RobotInfo robot: nearbyFriendlyRobots){
            switch(robot.type){
                case NET_GUN:
                    numNetGuns++;
                    break;

                case DESIGN_SCHOOL:
                    numDesignSchools++;
                    break;

                case MINER:
                    numMiners++;
                    break;

                case REFINERY:
                    numRefinery++;
                    if(mode == RETURNING){
                        //want to save the closest refinery
                        int distance = myLocation.distanceSquaredTo(robot.getLocation());
                        int minDistance = myLocation.distanceSquaredTo(hqLoc);
                        if(distance<minDistance){
                            minDistance = distance;
                            targetLocation = robot.getLocation();
                        }
                    }
                    break;

                case FULFILLMENT_CENTER:
                    numFulfillmentCenters++;
                    break;

                case VAPORATOR:
                    numVaporators++;
                    break;

                case LANDSCAPER:
                    numLandscapers++;
                    break;
            }
        }


        if(mode == RETURNING){
            //check to see if right next to HQ or right next to
            int distance = myLocation.distanceSquaredTo(targetLocation);
            if(myLocation.isAdjacentTo(targetLocation)){
                Direction refineDirection = myLocation.directionTo(targetLocation);
                if(tryRefine(refineDirection)){
                    System.out.println("Refined soup!");
                }
            }
            else if(distance <=36){
                //move towards that refinery
                pathing.tanBugPath(targetLocation);
            }
            else{
                //build a refinery if possible
                if(rc.isReady() && rc.canBuildRobot(RobotType.REFINERY, Direction.NORTH) && rc.getTeamSoup()>RobotType.REFINERY.cost){
                    rc.buildRobot(RobotType.REFINERY, Direction.NORTH);
                }
                else{
                    mode = BUILDING;
                }
            }
        }

        RobotType toBuild = null;
        if(mode == BUILDING){
            if(numEnemyDrones>0 && numNetGuns==0 && rc.getTeamSoup()>RobotType.NET_GUN.cost){
                toBuild = RobotType.NET_GUN;
            }
            else if(lastUsedRefinery == hqLoc && myLocation.distanceSquaredTo(hqLoc) > 36 && numRefinery==0 && rc.getTeamSoup()>RobotType.REFINERY.cost){
                toBuild = RobotType.REFINERY;
            }
            else if(numDesignSchools==0 && rc.getTeamSoup()>RobotType.DESIGN_SCHOOL.cost){
                toBuild = RobotType.DESIGN_SCHOOL;
            }
        }

        //ways to build different buildings... where to use this?
        rc.buildRobot(RobotType.FULFILLMENT_CENTER, randomDirection());
        rc.buildRobot(RobotType.DESIGN_SCHOOL, randomDirection());
        rc.buildRobot(RobotType.VAPORATOR, randomDirection());
        rc.buildRobot(RobotType.NET_GUN, randomDirection());

    }

    /**
     * Attempts to mine soup in a given direction.
     *
     * @param dir The intended direction of mining
     * @return true if a move was performed
     * @throws GameActionException
     */
    boolean tryMine(Direction dir) throws GameActionException {
        if (rc.isReady() && rc.canMineSoup(dir)) {
            rc.mineSoup(dir);
            return true;
        } else return false;
    }

    /*
    Wont throw GameActionException, because no method is directly being called
     */
    static Direction randomDirection() {
        return HQ.directions[(int) (Math.random() * HQ.directions.length)];
    }

    /**
     * Attempts to refine soup in a given direction.
     *
     * @param dir The intended direction of refining
     * @return true if a move was performed
     * @throws GameActionException
     */
    boolean tryRefine(Direction dir) throws GameActionException {
        if (rc.isReady() && rc.canDepositSoup(dir)) {
            rc.depositSoup(dir, rc.getSoupCarrying());
            return true;
        } else return false;
    }
}
