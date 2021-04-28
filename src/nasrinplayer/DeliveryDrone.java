package nasrinplayer;
import battlecode.common.*;

public class DeliveryDrone extends Unit {
    public DeliveryDrone(RobotController robotController) {
        super(robotController);
    }

    static MapLocation HQLocation; //the location of the home HQ
    static MapLocation EnemyHQLoc; //the location of the enemy HQ
    MapLocation droneLocation = rc.getLocation();

    Team A; //player team
    Team B; //enemy team

    //a method to sense all nearby bots
    boolean nearbyRobot(RobotType target) throws GameActionException {
        RobotInfo[] robots = rc.senseNearbyRobots(24);
        for (RobotInfo r : robots) {
            if (r.getType() == target) {
                return true;
            }
        }
        return false;
    }


    //method to sense all nearby enemy, non-building, robots specifically
    boolean nearbyEnemyRobot(RobotInfo target) throws GameActionException {
        RobotInfo[] robots = rc.senseNearbyRobots();
        for (RobotInfo r : robots) {
            //get all the robots' type
            r.getType();
            //if the nearby bot is not a player bot and is not a building...
            if (r.getTeam() == B) {
                int enemyBotID = rc.getID();
            }
        }
        return false;
    }


    /**
     * drone will try to pick up an enemy unit
     * @param dir
     * @return
     * @throws GameActionException
     */
    Team enemy = rc.getTeam().opponent();
    Team friend = rc.getTeam();

    boolean tryToPickUpEnemy(Direction dir) throws GameActionException {
        RobotInfo[] robots = rc.senseNearbyRobots(24, enemy);
        for (RobotInfo r : robots) {
            int robotID = r.getID();
            if(rc.canPickUpUnit(robotID)){
                rc.pickUpUnit(robotID);
                return true;
            }
        }
        return false;
    }

    /**
     * drone will try to pick up a friendly unit, most likely will not be used
     * @param dir
     * @return
     * @throws GameActionException
     */
    boolean tryToPickUpFriend(Direction dir) throws GameActionException{
        RobotInfo[] robots = rc.senseNearbyRobots(24, friend);
        for(RobotInfo r : robots){
            int robotID = r.getID();
            if(rc.canPickUpUnit(robotID)){
                rc.pickUpUnit(robotID);
                return true;
            }
        }
        return false;
    }

    public void takeTurn() throws GameActionException {
        super.takeTurn();

        /**
         * drones will pick up tiles/robots, place them back down
         *
         * Drones have a sensor radius of 24 and can fly over all units,
         * besides buildings and other drones. Drones can pick up any units, regardless of team
         *
         *
         * Planned Tactics:
         *
         * [1] Find Enemy HQ, if and only if it has yet to be found
         *
         * [2] Engage defensive mode, encircle team HQ (not enemy) and defend against
         * enemy units by sensing surroundings and picking up any enemies that near the HQ.
         * Drop enemy units into nearest water pool, then return to formation.
         */



        //[1] All drones should try to find home and enemy base locations
        HQLocation = comms.getHqLoc();

        if (HQLocation == null) {
            //search area for HQ
            RobotInfo[] agents = rc.senseNearbyRobots(24);
            for (RobotInfo agent : agents) {
                //RobotInfo contains: robot ID num, location, team, and type.
                if (agent.type == RobotType.HQ && agent.getTeam() == A) {
                    //home team HQ found
                    HQLocation = agent.location;
                    //enemy HQ found (?)
                } else if (agent.type == RobotType.HQ == true && agent.getTeam() == enemy) {
                    EnemyHQLoc = agent.location;
                } else {
                    //if the enemy's HQ location is already found
                    System.out.println("Drone Reporting: Enemy HQ Location at: " + EnemyHQLoc);
                }
            }
        } else {
            //if the HQ is already found, print the location
            System.out.println("Drone Reporting: Home HQ Location at: " + HQLocation);

        }


        /**
         * [2] Assemble drones near home base, begin defend and destroy tactics
         *
         */
        //moving in all directions
        for (Direction dir : Direction.allDirections()) {

            //find HQ
            HQLocation = comms.getHqLoc();
            pathing.tanBugPath(HQLocation);

            //have the drones faff about
            rc.move(dir);

            //as long as they can move, drones should grab what they can
            if(rc.canMove(Direction.NORTH)){
                tryToPickUpEnemy(dir);
            }
            else if(rc.canMove(Direction.NORTHEAST)){
                tryToPickUpEnemy(dir);
            }
            else if(rc.canMove(Direction.NORTHWEST)){
                tryToPickUpEnemy(dir);
            }
            else if(rc.canMove(Direction.SOUTH)){
                tryToPickUpEnemy(dir);
            }
            else if(rc.canMove(Direction.SOUTHEAST)){
                tryToPickUpEnemy(dir);
            }
            else if(rc.canMove(Direction.SOUTHWEST)){
                tryToPickUpEnemy(dir);
            }
            else if(rc.canMove(Direction.EAST)){
                tryToPickUpEnemy(dir);
            }
            else if(rc.canMove(Direction.WEST)){
                tryToPickUpEnemy(dir);
            }

            //if/when they come across another bot, check if it can move over it
            //if the drone is obstructed, try to redirect elsewhere

            if(rc.canMove(Direction.NORTH) == false){
                rc.move(Direction.NORTHEAST);
                rc.move(dir);
            }
            else if(rc.canMove(Direction.SOUTH) == false){
                rc.move(Direction.SOUTHEAST);
                rc.move(dir);
            }
            else if(rc.canMove(Direction.EAST) == false){
                rc.move(Direction.NORTHEAST);
                rc.move(dir);
            }

            /**
             * Drones should look for floods to drop enemies in
             */
            //drone will check for flooded tiles for yeeting purposes
            rc.senseNearbyRobots(24);
            //look for flooded areas nearby
            boolean floodedLoc = rc.senseFlooding(droneLocation.add(dir));
            if(floodedLoc == true){
                //check if the drone is actually holding anything
                rc.isCurrentlyHoldingUnit();
                if(rc.isCurrentlyHoldingUnit() == true && rc.canDropUnit(Direction.CENTER)){
                    //assuming center is down and no particular direction, drop the unit in the flood
                    rc.dropUnit(Direction.CENTER);
                }
            }
            //cow coverage. if drone senses a cow agent, try to pick it up and rain cows on enemy HQ?
            rc.senseNearbyRobots(24);
            if(rc.getType() == RobotType.COW){
                tryToPickUpEnemy(dir);
                Direction toEnemyHQ = rc.getLocation().directionTo(EnemyHQLoc);
                rc.move(toEnemyHQ);
                rc.dropUnit(Direction.CENTER);
            }
        } //end for
    }
}

