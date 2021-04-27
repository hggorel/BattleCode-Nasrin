package nasrinplayer;
import battlecode.common.*;

import java.util.Map;


public class Miner extends Unit {

    boolean hasContributed = false;
    boolean currentlyWalking = true;
    boolean soupMining = false;
    MapLocation[] soups;

    public Miner(RobotController robotController) {

        super(robotController);
    }

    public void takeTurn() throws GameActionException {
        super.takeTurn();

        //If full and cannot get any more soup -- go to close refinery or HQ to deposit
        if(rc.getSoupCarrying()>=100){
            //look for repository
            //how do i look for a repository?

            //look for HQ
            MapLocation hq = comms.getHqLoc();
            pathing.tanBugPath(hq);
            MapLocation currentLoc = rc.getLocation();
            tryRefine(currentLoc.directionTo(hq));
        }
        else{   //if not full and can keep looking for soup
            //first look immediately next to miner
            //list of soup that are immediately by the miner
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

                //ways to build different buildings... where to use this?
                rc.buildRobot(RobotType.REFINERY, randomDirection());
                rc.buildRobot(RobotType.FULFILLMENT_CENTER, randomDirection());
                rc.buildRobot(RobotType.DESIGN_SCHOOL, randomDirection());
                rc.buildRobot(RobotType.VAPORATOR, randomDirection());
                rc.buildRobot(RobotType.NET_GUN, randomDirection());
            }




            //ADD IN STUFF HERE FOR MOVING TO MINE THINGS THAT ARE FARTHER AWAYYY



            //.......

            //.......

        }

        for(Direction dir: Direction.allDirections()) {
            if(rc.canBuildRobot(RobotType.FULFILLMENT_CENTER, dir)){
                rc.buildRobot(RobotType.FULFILLMENT_CENTER, dir);
            }
        }
        //look for soup and try to mine :)
        MapLocation a = new MapLocation(6, 5);
        //Example in which the directAppoach works;

        pathing.tanBugPath(a);

    }


    //if full, deposit soup at refinery
    //if not full, see if we know there's soup places and go there
    //if not full, and don't have any saved locations .. move randomly

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
