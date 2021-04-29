package nasrinplayer;
import battlecode.common.*;

class Landscaper extends Unit {

    private int dirtAmount;                                     // keeps track of the dirt amount
    private RobotController rc;
    private MapLocation hqLoc;

    public Landscaper(RobotController robotController) {
        super(robotController);                                // declare the robot controller
        dirtAmount = 0;                                        // initializes dirt amount to zero
        rc = robotController;
        try {
            MapLocation hqLoc = comms.getHqLoc();

        } catch(GameActionException gameActionException){

        }
    }

    public void takeTurn() throws GameActionException {
        super.takeTurn();
    }

    public void digDirt(Direction dir) throws GameActionException {

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
            } else if (rc.canDigDirt(Direction.SOUTH)) {                                                     // otherwise if we can dig in south position
//                MapLocation xy = adjacentLocation(Direction.SOUTH);
                digDirt(Direction.SOUTH);
                dirtAmount++;

            } else if (rc.canDigDirt(Direction.NORTHEAST)) { // otherwise if we can dig in northeast position
//                MapLocation xy = adjacentLocation(Direction.NORTH);
                digDirt(Direction.NORTHEAST);
                dirtAmount++;
            } else if (rc.canDigDirt(Direction.NORTHWEST)) {        // otherwise if we can dig in northwest position
//                MapLocation xy = adjacentLocation(Direction.EAST);
                digDirt(Direction.NORTHWEST);
                dirtAmount++;
            } else if (rc.canDigDirt(Direction.SOUTHEAST)) {       // otherwise if we can dig in southeast position
//                MapLocation xy = adjacentLocation(Direction.WEST);
                digDirt(Direction.SOUTHEAST);
                dirtAmount++;
            } else if (rc.canDigDirt(Direction.SOUTHWEST)) {        // otherwise if we can dig in southwest position
//                MapLocation xy = adjacentLocation(Direction.SOUTH);
                digDirt(Direction.SOUTHWEST);
                dirtAmount++;
            }
        }
    }


    public void depositDirt(Direction dir) throws GameActionException {
        /*
         * We are keeping track of the dirt amount that the landscaper holds, ensuring that
         * it never exceeds the limit. We will keep checking the landscaper and its adjacent locations
         * to see if we can deposit there. The RobotController checks the conditions of each location
         * and will deposit if possible, handling all of the cases and doing its effects
         */
        MapLocation north = hqLoc.add(Direction.NORTH);
        MapLocation east = hqLoc.add(Direction.EAST);
        MapLocation south = hqLoc.add(Direction.SOUTH);
        MapLocation west = hqLoc.add(Direction.WEST);

        MapLocation northEast = hqLoc.add(Direction.NORTHEAST);
        MapLocation northWest = hqLoc.add(Direction.NORTHWEST);
        MapLocation southEast = hqLoc.add(Direction.SOUTHEAST);
        MapLocation southWest = hqLoc.add(Direction.SOUTHWEST);

        if (dirtAmount > 0 && dirtAmount <= 25 && rc.isReady()) {
            MapLocation currLocation = rc.adjacentLocation(Direction.CENTER);
            if (currLocation.isWithinDistanceSquared(north, 1) && rc.senseElevation(north) < 25) {
                Direction whereTo = currLocation.directionTo(north);
                if (rc.canDepositDirt(whereTo)) {
                    depositDirt(whereTo);
                    dirtAmount--;
                }
            } else if (currLocation.isWithinDistanceSquared(east, 1) && rc.senseElevation(east) < 25) {
                Direction whereTo = currLocation.directionTo(east);
                if (rc.canDepositDirt(whereTo)) {
                    depositDirt(whereTo);
                    dirtAmount--;
                }
            } else if (currLocation.isWithinDistanceSquared(south, 1) && rc.senseElevation(south) < 25) {
                Direction whereTo = currLocation.directionTo(south);
                if (rc.canDepositDirt(whereTo)) {
                    depositDirt(whereTo);
                    dirtAmount--;
                }
            } else if (currLocation.isWithinDistanceSquared(west, 1) && rc.senseElevation(west) < 25) {
                Direction whereTo = currLocation.directionTo(west);
                if (rc.canDepositDirt(whereTo)) {
                    depositDirt(whereTo);
                    dirtAmount--;
                }
            } else if (currLocation.isWithinDistanceSquared(northEast, 1) && rc.senseElevation(northEast) < 25) {
                Direction whereTo = currLocation.directionTo(northEast);
                if (rc.canDepositDirt(whereTo)) {
                    depositDirt(whereTo);
                    dirtAmount--;
                }
            } else if (currLocation.isWithinDistanceSquared(northWest, 1) && rc.senseElevation(northWest) < 25) {
                Direction whereTo = currLocation.directionTo(northWest);
                if (rc.canDepositDirt(whereTo)) {
                    depositDirt(whereTo);
                    dirtAmount--;
                }
            } else if (currLocation.isWithinDistanceSquared(southEast, 1) && rc.senseElevation(southEast) < 25) {
                Direction whereTo = currLocation.directionTo(southEast);
                if (rc.canDepositDirt(whereTo)) {
                    depositDirt(whereTo);
                    dirtAmount--;
                }
            } else if (currLocation.isWithinDistanceSquared(southWest, 1) && rc.senseElevation(southWest) < 25) {
                Direction whereTo = currLocation.directionTo(southWest);
                if (rc.canDepositDirt(whereTo)) {
                    depositDirt(whereTo);
                    dirtAmount--;
                }
            }
        }
    }

    public void move () throws GameActionException {
        MapLocation north = hqLoc.add(Direction.NORTH);
        MapLocation east = hqLoc.add(Direction.EAST);
        MapLocation south = hqLoc.add(Direction.SOUTH);
        MapLocation west = hqLoc.add(Direction.WEST);

        MapLocation northEast = hqLoc.add(Direction.NORTHEAST);
        MapLocation northWest = hqLoc.add(Direction.NORTHWEST);
        MapLocation southEast = hqLoc.add(Direction.SOUTHEAST);
        MapLocation southWest = hqLoc.add(Direction.SOUTHWEST);

        /*
         * If the RobotController is ready and if the landscaper can move in the specified direction,
         * we will move in that direction, otherwise nothing happens. If the landscaper has 8 dirt, we will move towards
         * the HQ for depositing. Otherwise, we will move away from the HQ.
         */
        MapLocation currLocation = rc.adjacentLocation(Direction.CENTER);
        if (!currLocation.isWithinDistanceSquared(hqLoc, 2)) {
            Direction goTowards = currLocation.directionTo(hqLoc);
            if (rc.isReady() && rc.canMove(goTowards)) {
                rc.move(goTowards);
            }
        } else {
            if (currLocation.isWithinDistanceSquared(north, 1) && rc.senseElevation(north) >= 25) {
                if (rc.isReady() && rc.canMove(Direction.EAST)) {
                    rc.move(Direction.EAST);
                }
            } else if (currLocation.isWithinDistanceSquared(east, 1) && rc.senseElevation(east) >= 25) {
                if (rc.isReady() && rc.canMove(Direction.SOUTH)) {
                    rc.move(Direction.SOUTH);
                }
            } else if (currLocation.isWithinDistanceSquared(south, 1) && rc.senseElevation(south) >= 25) {
                if (rc.isReady() && rc.canMove(Direction.WEST)) {
                    rc.move(Direction.WEST);
                }
            } else if (currLocation.isWithinDistanceSquared(west, 1) && rc.senseElevation(west) >= 25) {
                if (rc.isReady() && rc.canMove(Direction.NORTH)) {
                    rc.move(Direction.NORTH);
                }
            } else if (currLocation.isWithinDistanceSquared(northEast, 1) && rc.senseElevation(northEast) >= 25) {
                if (rc.isReady() && rc.canMove(Direction.NORTHEAST)) {
                    rc.move(Direction.NORTHEAST);
                }
            } else if (currLocation.isWithinDistanceSquared(northWest, 1) && rc.senseElevation(northWest) >= 25) {
                if (rc.isReady() && rc.canMove(Direction.SOUTHEAST)) {
                    rc.move(Direction.SOUTHEAST);
                }
            } else if (currLocation.isWithinDistanceSquared(southEast, 1) && rc.senseElevation(southEast) >= 25) {
                if (rc.isReady() && rc.canMove(Direction.SOUTHWEST)) {
                    rc.move(Direction.SOUTHWEST);
                }
            } else if (currLocation.isWithinDistanceSquared(southWest, 1) && rc.senseElevation(southWest) >= 25) {
                if (rc.isReady() && rc.canMove(Direction.NORTHWEST)) {
                    rc.move(Direction.NORTHWEST);
                }
            }
        }


    }

}

