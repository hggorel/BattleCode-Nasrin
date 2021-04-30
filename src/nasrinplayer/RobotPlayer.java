package nasrinplayer;

import battlecode.common.*;

/**
 * RobotPlayer Class
 * Description: The RobotPlayer Class has some helpful global variables and also runs the bots!
 * It looks at the type of the Bot that has been instantiated, and then creates the proper object
 * Some of this class was patterned after the RobotPlayer class in examplefuncsplayer
 */
public strictfp class RobotPlayer {
    static RobotController rc;

    static Direction[] directions = {
            Direction.NORTH,
            Direction.NORTHEAST,
            Direction.EAST,
            Direction.SOUTHEAST,
            Direction.SOUTH,
            Direction.SOUTHWEST,
            Direction.WEST,
            Direction.NORTHWEST
    };

    static int turnCount;

    /**
     * run() is the method that is called when a robot is instantiated in the Battlecode world.
     * If this method returns, the robot dies!
     **/
    @SuppressWarnings("unused")
    public static void run(RobotController rc) throws GameActionException {

        // This is the RobotController object. You use it to perform actions from this robot,
        // and to get information on its current status.
        Robot robot = null;

        System.out.println("I'm a " + rc.getType() + " and I just got created!");
        while (true) {
            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {
                // Here, we've separated the controls into a different method for each RobotType.
                System.out.println("I'm a " + rc.getType() + "! Location " + rc.getLocation());
                switch (rc.getType()) {
                    case HQ:
                        robot = new HQ(rc);
                        break;
                    case MINER:
                        robot = new Miner(rc);
                        break;
                    case REFINERY:
                        robot = new Refinery(rc);
                        break;
                    case VAPORATOR:
                        robot = new Vaporator(rc);
                        break;
                    case DESIGN_SCHOOL:
                        robot = new DesignSchool(rc);
                        break;
                    case FULFILLMENT_CENTER:
                        robot = new FulfillmentCenter(rc);
                        break;
                    case LANDSCAPER:
                        robot = new Landscaper(rc);
                        break;
                    case DELIVERY_DRONE:
                        robot = new DeliveryDrone(rc);
                        break;
                    case NET_GUN:
                        robot = new NetGun(rc);
                        break;
                }

                robot.takeTurn();

                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                Clock.yield();

            } catch (Exception e) {
                System.out.println(rc.getType() + " Exception");
                e.printStackTrace();
            }
        }


    }
}