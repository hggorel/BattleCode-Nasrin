package nasrinplayer;

import battlecode.common.*;

import java.util.Random;

import java.util.Hashtable;

public class DeliveryDrone extends Unit {
    public DeliveryDrone(RobotController robotController) {
        super(robotController);
    }

    static MapLocation HQLocation; //the location of the home HQ
    static MapLocation EnemyHQLoc; //the location of the enemy HQ
    MapLocation droneLocation = rc.getLocation();
    static Hashtable<MapLocation, String> canDrop = new Hashtable<>();

    Team A = rc.getTeam(); //player team
    Team B = rc.getTeam().opponent();
    int seedchange = 1;


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
     *
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
            if (rc.canPickUpUnit(robotID)) {
                rc.pickUpUnit(robotID);
                return true;
            }
        }
        return false;
    }

    /**
     * drone will try to pick up a friendly unit, most likely will not be used
     *
     * @param dir
     * @return
     * @throws GameActionException
     */
    boolean tryToPickUpFriend(Direction dir) throws GameActionException {
        RobotInfo[] robots = rc.senseNearbyRobots(24, friend);
        for (RobotInfo r : robots) {
            int robotID = r.getID();
            if (rc.canPickUpUnit(robotID)) {
                rc.pickUpUnit(robotID);
                return true;
            } else {
                pathing.tryMove(dir);
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

        for (Direction dir : HQ.directions) {
            if (rc.senseFlooding(rc.getLocation().add(dir)) || rc.senseElevation(rc.getLocation().add(dir)) > rc.senseElevation(rc.getLocation()) + 3 || rc.senseElevation(rc.getLocation().add(dir)) < rc.senseElevation(rc.getLocation()) - 3) {
                canDrop.put(rc.getLocation().add(dir), "stuck");
            }
        }

        /*
            The drones will sense their surroundings
         */
        RobotInfo[] robots = rc.senseNearbyRobots(24, enemy);
        if (robots.length == 0) {
            //No robots near me

            Random oracle = new Random(seedchange);
            seedchange++;

            //drones will try to move in any direction they can
            if (rc.canMove(HQ.directions[oracle.nextInt(HQ.directions.length - 1)])) {
                pathing.tryMove(HQ.directions[oracle.nextInt(HQ.directions.length - 1)]);
            }


        } else {

            //while a target is not yet found, keep searching for an opponent unit to grab
            while (!pathing.tanBugPath(robots[robots.length - 1].getLocation())) {

                if (rc.senseRobotAtLocation(robots[robots.length - 1].getLocation()) == null) {
                    robots = rc.senseNearbyRobots(24, enemy);
                }




            }


            //drone should should have a target to pick up
            while (!tryToPickUpEnemy(rc.getLocation().directionTo(robots[robots.length - 1].getLocation()))) {
            }

            System.out.println("Should be picking up someone");
            System.out.println("KEYSETTING" + canDrop.keySet().toString());
            for (MapLocation keys : canDrop.keySet()) {
                if (canDrop.containsKey(keys) && canDrop.get(keys) != "put") {
                    System.out.println("FREAKING TARGET" + keys);
                    while (rc.getLocation() != keys) {
                        System.out.println("FREAKING TARGET" + keys);
                        pathing.directApproach(rc.getLocation(), keys);

                    }
                    if (rc.canMove(Direction.NORTH)) {
                        if (pathing.tryMove(Direction.NORTH)) {
                            rc.dropUnit(Direction.SOUTH);
                            canDrop.put(keys, "put");
                        }
                    } else {
                        Clock.yield();
                    }


                }
            }


        }


    }
}