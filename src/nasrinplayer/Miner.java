package nasrinplayer;
import battlecode.common.*;

import java.util.ArrayList;
import java.util.Map;


public class Miner extends Unit {

    ArrayList<MapLocation> soups = new ArrayList<MapLocation>();
    MapLocation lastUsedRefinery;
    MapLocation targetLocation;
    MapLocation lastMinedSoup;
    int numRandomMoves=0;

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

        System.out.println("Soup Total:" + rc.getTeamSoup());
        System.out.println("My soup carrying: " + rc.getSoupCarrying());

        MapLocation myLocation = rc.getLocation();
        MapLocation hqLoc = comms.getHqLoc();


        //first check to see if in range of any enemy netguns or drones
        RobotInfo[] nearbyEnemyRobots = rc.senseNearbyRobots(-1, rc.getTeam().opponent());

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
        if(closestEnemy !=null && myLocation.distanceSquaredTo(closestEnemy.getLocation()) <=13){
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
            System.out.println("Looking for soup");
            //if not full and can looking for soup, first look immediately next to us
            MapLocation[] nextToSoup = rc.senseNearbySoup(2);

            MapLocation[] nearbySoup = rc.senseNearbySoup();

            if(nextToSoup.length > 0) {
                //Mine all soup that's immediately next to the robot until full
                MapLocation currentLocation = rc.getLocation();
                for(MapLocation loc: nextToSoup){
                    Direction possDir = currentLocation.directionTo(loc);
                    if(tryMine(possDir)){
                        //if(soups.size()>0 && soups.contains(loc)){
                        //    soups.remove(loc);
                        //}
                        System.out.println("Mined soup at Location:" + rc.getLocation());
                        System.out.println("Current soup held: " + rc.getSoupCarrying());
                    }
                }
            }
            else if (nearbySoup.length > 0){
                //if(soups.size()==0){
                //    soups.add(nearbySoup[0]);
                //}
                //If no things next to us, look at the ones within the possible distances and move to the closest
                int minDistance = 1000000;
                MapLocation targetSoup = rc.getLocation();
                for(int i=0; i<nearbySoup.length; i++){
                    //if(soups.size()>0 && !soups.contains(nearbySoup[i])){
                        //soups.add(nearbySoup[i]);
                        //System.out.println("Added soup at location " + nearbySoup[i] + " to soups.")
                    //}
                    int distance = rc.getLocation().distanceSquaredTo(nearbySoup[i]);
                    if (distance<minDistance) {
                        minDistance=distance;
                        targetSoup = nearbySoup[i];
                    }
                }
                while(!pathing.tanBugPath(targetSoup)){

                }
                if(tryMine(rc.getLocation().directionTo(targetSoup))){
                    //soups.remove(targetSoup);
                    System.out.println("Mined soup at " + rc.getLocation());
                    System.out.println("Current soup held: " + rc.getSoupCarrying());
                }
            }
            //else if(soups.size()>0){
            //    while(!pathing.tanBugPath(soups.get(0))){

            //    }
            //}
            else {
                numRandomMoves++;
                if(numRandomMoves%5==0 && rc.getRoundNum()>100){
                    mode=BUILDING;
                    System.out.println("Building MODE");
                }
                else{
                    Direction moveIn = randomDirection();
                    if(rc.canMove(moveIn) && rc.isReady()){
                        rc.move(moveIn);
                        System.out.println("Moving randomly!");
                    }
                }
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
                            System.out.println("Setting target location to " + targetLocation.toString());
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
                lastUsedRefinery =targetLocation;
                targetLocation = null;
                mode = MINING;
            }
            else if(distance <=64){
                //move towards that refinery
                while(!pathing.tanBugPath(targetLocation)){

                }
                Direction refineDirection = myLocation.directionTo(targetLocation);
                if(tryRefine(refineDirection)){
                    System.out.println("Refined soup!");
                }
                lastUsedRefinery = targetLocation;
                targetLocation = null;
                mode = MINING;
            }
            else{
                //build a refinery if possible
                if(rc.isReady() && rc.canBuildRobot(RobotType.REFINERY, Direction.NORTH) && rc.getTeamSoup()>RobotType.REFINERY.cost && numRefinery==0){
                    rc.buildRobot(RobotType.REFINERY, Direction.NORTH);
                    System.out.println("Building refinery!");
                }
                else{
                    targetLocation = hqLoc;
                    while(!pathing.tanBugPath(targetLocation)){

                    }

                }
            }
        }

        RobotType toBuild = null;
        if(mode == BUILDING){
            //if(myLocation.distanceSquaredTo(hqLoc) > 36 && numRefinery==0 && rc.getTeamSoup()>RobotType.REFINERY.cost){
            if(rc.getRoundNum()<200 && rc.getTeamSoup()>RobotType.REFINERY.cost){
                toBuild = RobotType.REFINERY;
            }
            else{
                int choice = (int)(Math.random()*2);
                if(choice == 1 && rc.getTeamSoup()>RobotType.DESIGN_SCHOOL.cost) {
                    toBuild = RobotType.DESIGN_SCHOOL;
                }else{
                    if(rc.getTeamSoup()>RobotType.FULFILLMENT_CENTER.cost){
                        toBuild = RobotType.FULFILLMENT_CENTER;
                    }
                }
            }

            for(int i=0; i<8; i++){
                if(toBuild!=null && tryBuild(toBuild, HQ.directions[i])){
                    System.out.println("Building a " + toBuild.name());
                    i=8;        //this stops the loop, we don't want it to build them in all adjacent squares
                }
            }
        }


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

