package nasrinplayer;
import battlecode.common.*;

class Landscaper extends Unit {

    private int dirtAmount;                                     // keeps track of the dirt amount
    private RobotController rc;

    public Landscaper(RobotController robotController) {
        super(robotController);                                // declare the robot controller
        dirtAmount = 0;
        rc = robotController;
        // initializes dirt amount to zero
    }
    public void takeTurn() throws GameActionException {
        super.takeTurn();
    }
    public void digDirt(Direction dir) throws GameActionException{

        /*
         * We are keeping track of the dirt amount that the landscaper holds, ensuring that
         * it never exceeds the limit. We will keep checking the landscaper and its adjacent locations
         * to see if we can dig there. The RobotController checks the conditions of each location
         * and will dig if possible, handling all of the cases and doing its effects
         */

        if (dirtAmount < RobotType.LANDSCAPER.dirtLimit
                && rc.isReady()) {

            if (rc.canDigDirt(Direction.CENTER)) {         // if we can dig in curr position
//                MapLocation xy = adjacentLocation(Direction.CENTER);
                digDirt(Direction.CENTER);
                dirtAmount++;
            } else if (rc.canDigDirt(Direction.NORTH)) {   // otherwise if we can dig in north position
//                MapLocation xy = adjacentLocation(Direction.NORTH);
                digDirt(Direction.NORTH);
                dirtAmount++;
            } else if (rc.canDigDirt(Direction.EAST)) {    // otherwise if we can dig in east position
//                MapLocation xy = adjacentLocation(Direction.EAST);
                digDirt(Direction.EAST);
                dirtAmount++;
            } else if (rc.canDigDirt(Direction.WEST)) {    // otherwise if we can dig in west position
//                MapLocation xy = adjacentLocation(Direction.WEST);
                digDirt(Direction.WEST);
                dirtAmount++;
            } else {                                                     // otherwise if we can dig in north position
//                MapLocation xy = adjacentLocation(Direction.SOUTH);
                digDirt(Direction.SOUTH);
                dirtAmount++;
            }
        }
    }

    public void depositDirt(Direction dir) throws GameActionException {
        if (dirtAmount > 0 && dirtAmount <= 25 && rc.isReady()) {

            /*
             * We are keeping track of the dirt amount that the landscaper holds, ensuring that
             * it never exceeds the limit. We will keep checking the landscaper and its adjacent locations
             * to see if we can deposit there. The RobotController checks the conditions of each location
             * and will deposit if possible, handling all of the cases and doing its effects
             */

            if (rc.canDepositDirt(Direction.CENTER)) {         // if we can deposit in curr position
//                MapLocation xy = adjacentLocation(Direction.CENTER);
                depositDirt(Direction.CENTER);
                dirtAmount--;
            } else if (rc.canDepositDirt(Direction.NORTH)) {   // otherwise if we can deposit in north
//                MapLocation xy = adjacentLocation(Direction.NORTH);
                depositDirt(Direction.NORTH);
                dirtAmount--;
            } else if (rc.canDepositDirt(Direction.EAST)) {     // otherwise if we can deposit in east
//                MapLocation xy = adjacentLocation(Direction.EAST);
                depositDirt(Direction.EAST);
                dirtAmount--;
            } else if (rc.canDepositDirt(Direction.WEST)) {    // otherwise if we can deposit in west
//                MapLocation xy = adjacentLocation(Direction.WEST);
                depositDirt(Direction.WEST);
                dirtAmount--;
            } else {                                                          // otherwise if we can deposit in north
//                MapLocation xy = adjacentLocation(Direction.SOUTH);
                depositDirt(Direction.SOUTH);
                dirtAmount--;
            }
        }
    }

    public void move(Direction dir) throws GameActionException {

        /*
         * If the RobotController is ready and if the landscaper can move in the specified direction,
         * we will move in that direction, otherwise nothing happens. If the landscaper has 8 dirt, we will move towards
         * the HQ for depositing. Otherwise, we will move away from the HQ.
         */

        if (rc.isReady() && rc.canMove(dir)) {
            rc.move(dir);
        }
    }
}