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

    /**
    //method to sense all nearby enemy, non-building, robots specifically
    boolean nearbyEnemyRobot(RobotInfo target) throws GameActionException {
        RobotInfo[] robots = rc.senseNearbyRobots();
        for (RobotInfo r : robots) {
            //get all the robots' type
            r.getType();
            //if the nearby bot is not a player bot and is not a building...
            if (r.getTeam() == B && r.tryToPickUpEnemy == false) {
                int enemyBotID = rc.getID();
            }
        }
        return false;
    }
     */

    /**
     * drone will try to pick up an enemy unit
     * @param dir
     * @return
     * @throws GameActionException
     */
    boolean tryToPickUpEnemy(Direction dir) throws GameActionException {
        RobotInfo[] robots = rc.senseNearbyRobots(24, B);
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
     * drone will try to pick up a friendly unit
     * @param dir
     * @return
     * @throws GameActionException
     */
    boolean tryToPickUpFriend(Direction dir) throws GameActionException{
        RobotInfo[] robots = rc.senseNearbyRobots(24, A);
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



        //[1] All drones should find home and enemy base locations

        if (HQLocation == null) {
            //search area for HQ
            RobotInfo[] agents = rc.senseNearbyRobots(24);
            for (RobotInfo agent : agents) {
                //RobotInfo contains: robot ID num, location, team, and type.
                if (agent.type == RobotType.HQ && agent.team == rc.getTeam()) {
                    //home team HQ found
                    HQLocation = agent.location;
                    //enemy HQ found (?)
                } else if (agent.type == RobotType.HQ == false) {
                    EnemyHQLoc = agent.location;
                } else {
                    //if the enemy's HQ location is already found
                    System.out.println("Enemy HQ Location at: " + EnemyHQLoc);
                }
            }
        } else {
            //if the HQ is already found, print the location
            System.out.println("Home HQ Location at: " + HQLocation);
            //get direction to location of home base, move to home
            Direction directionToHQ = rc.getLocation().directionTo(HQLocation);

        }

        HQLocation = comms.getHqLoc();
        pathing.tanBugPath(HQLocation);


        /**
         * [2] Assemble drones near home base, begin defend and destroy tactics
         *
         */
        //moving in all directions
        for (Direction dir : Direction.allDirections()) {

            //have the drones faff about
            rc.move(dir);

            //if/when they come across another bot, check if it can move over it
            //if the drone is obstructed, try to redirect

            if(!rc.canMove(Direction.NORTH)){
                rc.move(Direction.NORTHEAST);
                rc.move(dir);
            }
            else if(!rc.canMove(Direction.SOUTH)){
                rc.move(Direction.SOUTHEAST);
                rc.move(dir);
            }
            else if(!rc.canMove(Direction.EAST)){
                rc.move(Direction.NORTHEAST);
                rc.move(dir);
            }
            //drone will check for flooded tiles for yeeting purposes
            rc.senseNearbyRobots(24);
            //look for flooded areas nearby
            boolean floodedLoc = rc.senseFlooding(droneLocation.add(dir));
            if(floodedLoc == true){
                //check if the drone is actually holding anything
                rc.isCurrentlyHoldingUnit();
                if(rc.isCurrentlyHoldingUnit() == true){
                    //assuming center is down and no particular direction, drop the unit in the flood
                    rc.dropUnit(Direction.CENTER);
                }
            }



            //MapLocation[] robots = rc.adjacentLocation(droneLocation);
            //tryToPickUpEnemy(Direction.NORTH);
            RobotInfo[] robots = rc.senseNearbyRobots();
            for(RobotInfo r : robots){
                boolean occupied = rc.isLocationOccupied(rc.adjacentLocation(dir));
                if(rc.isLocationOccupied(rc.adjacentLocation(dir))){
                    rc.getType();
                    //rc.canMove(rc.adjacentLocation(dir));
                }
            }


            } //end for


        }
}

