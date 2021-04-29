package nasrinplayer;
import battlecode.common.*;

import java.util.ArrayList;
import java.util.Map;

/**
 * Miner Class
 * Responsible for mining soup, depositing soup at refineries, finding more soup,
 * creating repositories, design schools, fulfillment centers, etc.
 */
public class Miner extends Unit {

    //some global variables for the class to use...
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

    //always start in mining -- first priority
    int mode = MINING;

    /**
     * Constructor method
     * @param robotController - rc from RobotController class
     */
    public Miner(RobotController robotController) {

        super(robotController);
    }

    /**
     * takeTurn Method
     * @throws GameActionException
     * Description: takeTurn runs every turn once to determine what the miner will do on each
     * particular turn, first priority is running from immediate danger, second priority is mining,
     * third priority is building the buildings, but the priorities slightly change as the game
     * goes on for longer
     */
    public void takeTurn() throws GameActionException {
        super.takeTurn();       //call the super method

        //some print outs for the logs
        System.out.println("Soup Total:" + rc.getTeamSoup());
        System.out.println("My soup carrying: " + rc.getSoupCarrying());

        //two important locations to always be able to access, hq and your own
        MapLocation myLocation = rc.getLocation();
        MapLocation hqLoc = comms.getHqLoc();

        //currently here for testing purposes to show that you never get above 150 which is a PROBLEM
        if(rc.getTeamSoup()>150){
            rc.buildRobot(RobotType.DESIGN_SCHOOL, randomDirection());
        }


        //first check to see if in range of any enemy netguns or drones
        RobotInfo[] nearbyEnemyRobots = rc.senseNearbyRobots(-1, rc.getTeam().opponent());

        /*
        Loop to go through all the enemy robots that are in your immediate vicinity
        If any are close enough that you're in their range, run in the opposite direction to avoid
        being caught and killed (dropped in water)
         */
        //constants to be updated by loop
        int numEnemyNetGuns = 0;
        int numEnemyDrones = 0;
        int minDistanceToEnemy = 99999;
        RobotInfo closestEnemy = null;
        for(RobotInfo robot: nearbyEnemyRobots){        //going through all the enemy robots within range to find the closest one
            MapLocation enemyLoc = robot.getLocation();
            int distance = myLocation.distanceSquaredTo(enemyLoc);
            if(distance<minDistanceToEnemy){            //if this enemy is closest than current closest
                minDistanceToEnemy = distance;      //reset both mindistance
                closestEnemy = robot;               //and closestEnemy variables to this newly discovered bot
            }
            switch(robot.type){                     //depending on which type of robot it is
                case NET_GUN:
                    numEnemyNetGuns++;              //increment the count
                    break;

                case DELIVERY_DRONE:
                    numEnemyDrones++;               //increment the count
                    break;
            }
        }

        //check to see if in range of enemy
        if(closestEnemy !=null && myLocation.distanceSquaredTo(closestEnemy.getLocation()) <=13){
            mode = FLEEING; //set mode to fleeing
            //this means we are in range -- so want to retreat
            Direction enemyDirection = myLocation.directionTo(closestEnemy.getLocation());  //direction the enemy's in
            if(rc.canMove(enemyDirection.opposite())){      //attempt to move in the opposite direction
                rc.move(enemyDirection.opposite());
            }
            else{
                MapLocation destination = myLocation.subtract(enemyDirection);  //if can't just go - use the tanPath to get there
                pathing.tanBugPath(destination);
            }
        }

        /*
        Now onto the second priority -- mining :) this is a BIG one, but one that is slightly broken?
        For some reason the overall team soup decreases even if there is nothing being built,
        and I cannot figure out why but that is preventing buildings from being created and I am stumped.
         */
        //If the robot is at capacity or just cannot take in any more soup
        if(rc.getSoupCarrying()>=RobotType.MINER.soupLimit - GameConstants.SOUP_MINING_RATE){
            //look for repository
            mode = RETURNING;       //set the mode to returning
            if(lastUsedRefinery==null){ //then we're early enough in the game that HQ is it
                targetLocation = hqLoc;
            }
            else{
                targetLocation = lastUsedRefinery;
            }
        }
        else if (mode == MINING){       //if not full and in mining mode
            System.out.println("Looking for soup");
            //if not full and can looking for soup, first look immediately next to us
            MapLocation[] nextToSoup = rc.senseNearbySoup(2);       //array of locations with soup adjacent to us

            MapLocation[] nearbySoup = rc.senseNearbySoup();            //array of locations with soup near us

            if(nextToSoup.length > 0) {     //if there is soup adjacent to us
                //Mine all soup that's immediately next to the robot until full
                MapLocation currentLocation = rc.getLocation();
                for(MapLocation loc: nextToSoup){       //loop through all adjacent soup locations
                    Direction possDir = currentLocation.directionTo(loc);
                    if(tryMine(possDir)){           //try to mine it
                        //if(soups.size()>0 && soups.contains(loc)){
                        //    soups.remove(loc);
                        //}
                        System.out.println("Mined soup at Location:" + rc.getLocation());
                        System.out.println("Current soup held: " + rc.getSoupCarrying());
                    }
                }
            }
            else if (nearbySoup.length > 0){    //if there aren't any adjacent ones but are some sensed
                //if(soups.size()==0){
                //    soups.add(nearbySoup[0]);
                //}
                //If no things next to us, look at the ones within the possible distances and move to the closest
                int minDistance = 1000000;      //set a large minDistance so it will definitely be reset
                MapLocation targetSoup = rc.getLocation();
                for(int i=0; i<nearbySoup.length; i++){
                    //if(soups.size()>0 && !soups.contains(nearbySoup[i])){
                    //soups.add(nearbySoup[i]);
                    //System.out.println("Added soup at location " + nearbySoup[i] + " to soups.")
                    //}
                    int distance = rc.getLocation().distanceSquaredTo(nearbySoup[i]);
                    //if the new distance is closer, reset all relevant variables to save this as the closest
                    if (distance<minDistance) {
                        minDistance=distance;
                        targetSoup = nearbySoup[i];
                    }
                }
                //now, start pathing to the targetSoup location
                while(!pathing.tanBugPath(targetSoup)){

                }
                //try to mine it (shouldn't ever get here though)
                if(tryMine(rc.getLocation().directionTo(targetSoup))){
                    //soups.remove(targetSoup);
                    System.out.println("Mined soup at " + rc.getLocation());
                    System.out.println("Current soup held: " + rc.getSoupCarrying());
                }
            }
            /*
             --- for some reason this caused HUGE problems in the execution - but I thought the best idea
             would be to keep an arraylist of all known soup locations and then return to one that we know of
             but this kept erroring and NEVER worked
             */

            //else if(soups.size()>0){
            //    while(!pathing.tanBugPath(soups.get(0))){

            //    }
            //}
            else {  //if there is no sensed soup choose a random one, eventually will end up near soup
                numRandomMoves++;
                if(numRandomMoves%5==0 && rc.getRoundNum()>100){    //every once in a while switch to building mode
                    mode=BUILDING;
                    System.out.println("Building MODE");
                }
                else{   //if not one of those times, move randomly
                    Direction moveIn = randomDirection();   //choose a random direction
                    if(rc.canMove(moveIn) && rc.isReady()){     //move there
                        rc.move(moveIn);
                        System.out.println("Moving randomly!");
                    }
                }
            }
        }

        /*
        Now - loops through all the robots that are within sensing distance that are on our team
        This gives us a count of how many of each thing we have and what we might need to build
        on this part of the map
         */
        //array of all the sensed robots
        RobotInfo[] nearbyFriendlyRobots = rc.senseNearbyRobots(-1, rc.getTeam());

        //variables to be updated later
        int numRefinery = 0;
        int numNetGuns = 0;
        int numMiners = 0;
        int numDesignSchools = 0;
        int numVaporators = 0;
        int numFulfillmentCenters = 0;
        int numLandscapers = 0;
        MapLocation closestRefinery = hqLoc;

        //loop through all of them
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

                case REFINERY:                  //if it's a refinery, we want to find the closest one
                    numRefinery++;
                    if(mode == RETURNING){      //only if in returning mode, otherwise we don't care
                        //want to save the closest refinery
                        int distance = myLocation.distanceSquaredTo(robot.getLocation());
                        int minDistance = myLocation.distanceSquaredTo(hqLoc);
                        //if new minimum, set it
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

        /*
        If we are returning, first check to see if right next to somewhere we can refine
        If yes refine there, if not path to the targetLocation -- if too far from there try
        to build a refinery where you are
         */
        if(mode == RETURNING){
            //check to see if right next to HQ or right next to
            int distance = myLocation.distanceSquaredTo(targetLocation);
            if(myLocation.isAdjacentTo(targetLocation)){
                Direction refineDirection = myLocation.directionTo(targetLocation);
                if(tryRefine(refineDirection)){
                    System.out.println("Refined soup!");
                }
                //reset the variables to allow miner to continue on
                lastUsedRefinery =targetLocation;
                targetLocation = null;
                mode = MINING;
            }
            else if(distance <=64){ //if within reasonable distance
                //move towards that refinery
                while(!pathing.tanBugPath(targetLocation)){

                }
                //try to refine in that direction (Again I think this may be redundant)
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
                else{   //if can't build a repository, go back to HQ since we know where that is
                    targetLocation = hqLoc;
                    while(!pathing.tanBugPath(targetLocation)){

                    }

                }
            }
        }

        //if in building mode
        RobotType toBuild = null;
        if(mode == BUILDING){
            //if(myLocation.distanceSquaredTo(hqLoc) > 36 && numRefinery==0 && rc.getTeamSoup()>RobotType.REFINERY.cost){
            if(rc.getRoundNum()<200 && rc.getTeamSoup()>RobotType.REFINERY.cost){   //make refineries early
                toBuild = RobotType.REFINERY;
            }
            else{   //then 50/50 make a design_school or fulfillment center
                int choice = (int)(Math.random()*2);
                if(choice == 1 && rc.getTeamSoup()>RobotType.DESIGN_SCHOOL.cost) {
                    toBuild = RobotType.DESIGN_SCHOOL;
                }else{
                    if(rc.getTeamSoup()>RobotType.FULFILLMENT_CENTER.cost){
                        toBuild = RobotType.FULFILLMENT_CENTER;
                    }
                }
            }

            //once toBuild has been assigned, try to build it in a random direction
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
        //make sure you can actually mine -- then do it
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
        //check to make sure allowed -- then deposit
        if (rc.isReady() && rc.canDepositSoup(dir)) {
            rc.depositSoup(dir, rc.getSoupCarrying());
            return true;
        } else return false;
    }
}

