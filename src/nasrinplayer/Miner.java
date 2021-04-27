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

    public Miner(RobotController robotController) {

        super(robotController);
    }

    public void takeTurn() throws GameActionException {
        super.takeTurn();

        RobotInfo[] nearbyFriendlyRobots = rc.senseNearbyRobots(-1, rc.getTeam());

        //first check to see if in range of any enemy netguns or drones
        RobotInfo[] nearbyEnemyRobots = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
        MapLocation myLocation = rc.getLocation();

        /*TODO: make a better choice if the opposite distance isn't an option.
         *Maybe go through and store all the dangerous locations and then rule them out...
         *that way it checks to see if you are running back into a drone too!!! */
        for(RobotInfo robot: nearbyEnemyRobots){
            switch(robot.type){
                case NET_GUN:
                    //check to see if in range of enemy net gun -- if so retreat
                    //farthest net gun range is 13... so
                    MapLocation gunLocation = robot.getLocation();
                    int distance = myLocation.distanceSquaredTo(gunLocation);
                    if(distance <=13){
                        //this means we are in range -- so want to retreat
                        Direction gunsDirection = myLocation.directionTo(gunLocation);
                        if(rc.canMove(gunsDirection.opposite())){
                            rc.move(gunsDirection.opposite());
                        }
                        else{
                            MapLocation destination = myLocation.subtract(gunsDirection);
                            pathing.tanBugPath(destination);
                        }
                    }
                    break;

                case DELIVERY_DRONE:
                    //check to see if in range of drone -- avoid getting picked up
                    int dist = myLocation.distanceSquaredTo(robot.getLocation());
                    if(dist <= 13){
                        Direction dronesDirection = myLocation.directionTo(robot.getLocation());
                        if(rc.canMove(dronesDirection.opposite())){
                            rc.move(dronesDirection.opposite());
                        }
                        else{
                            MapLocation destination = myLocation.subtract(dronesDirection);
                            pathing.tanBugPath(destination);
                        }
                    }
            }
        }

        //If full and cannot get any more soup -- go to close refinery or HQ to deposit
        if(rc.getSoupCarrying()>=RobotType.MINER.soupLimit - GameConstants.SOUP_MINING_RATE){
            //look for repository
            if(lastUsedRefinery==null){ //then we're early enough in the game that HQ is it
                MapLocation hqLoc = comms.getHqLoc();
                targetLocation = hqLoc;
            }
            else{
                targetLocation = lastUsedRefinery;
            }
        }
        else{
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
                //TODO: WHAT TO DO HERE????
            }

        }

        /*TODO:
            1. FIGURE OUT WHAT TO DO WITH THE TARGET LOCATION -- SEE IF FAR ENOUGH
            FROM HQ THAT BUILDING A NEW REFINERY IS THE BEST BET
            2. WORK ON MAKING THE IMMEDIATE BACKTRACKING SMARTER IF RUNS INTO ENEMIES
         */

        //ways to build different buildings... where to use this?
        rc.buildRobot(RobotType.REFINERY, randomDirection());
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
