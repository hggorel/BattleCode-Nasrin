package nasrinplayer;

import battlecode.common.*;

class Landscaper extends Unit {


    static MapLocation hqLoc = null;
    static int dirtAmount = 0;

    public Landscaper(RobotController robotController) {
        super(robotController);                                // declare the robot controller
        // initializes dirt amount to zero
        rc = robotController;


    }

    public void takeTurn() throws GameActionException {
        super.takeTurn();

        if (hqLoc == null) {
            //setting HQ location

            hqLoc = comms.getHqLoc();
        }

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
        //Hasnt moved at all yet


        while (!rc.getLocation().isWithinDistanceSquared(hqLoc, 1)) {
            //while we are not adjacent to the Hq go to hq
            pathing.goToWorstCase(hqLoc);

        }



        if (!rc.getLocation().add(Direction.NORTH).equals(hqLoc)&&rc.senseElevation(rc.getLocation().add(Direction.NORTH)) < 30&&rc.getCooldownTurns()==0 && rc.getType()!=RobotType.HQ) {


            while (dirtAmount <= 8) {
                System.out.println("DIGGING");

                rc.digDirt(Direction.CENTER);
                dirtAmount++;
            }


            System.out.println("THIS IS OUR DIR AMOOUNT" + dirtAmount);


            if (dirtAmount > 0) {
                System.out.println("We passed our if statement test");
                if (rc.canDepositDirt(Direction.NORTH)) {
                    System.out.println(rc.senseElevation(rc.getLocation().add(Direction.NORTH)));

                    rc.depositDirt(Direction.NORTH);
                    dirtAmount--;
                    System.out.println("Placed this direction" + Direction.NORTH);
                } else {

                }

            } else {

            }
        }
        else if (!rc.getLocation().add(Direction.EAST).equals(hqLoc)&&rc.senseElevation(rc.getLocation().add(Direction.EAST)) < 30&&rc.getCooldownTurns()==0) {



            while (dirtAmount<=8) {
                System.out.println("DIGGING");

                rc.digDirt(Direction.CENTER);
                dirtAmount++;
            }



            System.out.println("EAST MODE OUR IMPORTANT BOOLEAN"+rc.canDepositDirt(Direction.EAST));


            if (dirtAmount > 0) {
                System.out.println("We passed our if statement test");
                if (rc.canDepositDirt(Direction.EAST)) {
                    System.out.println(rc.senseElevation(rc.getLocation().add(Direction.EAST)));

                    rc.depositDirt(Direction.EAST);
                    dirtAmount--;
                    System.out.println("Placed this direction" + Direction.EAST);
                } else {

                }

            } else {

            }
        }
        else if (!rc.getLocation().add(Direction.SOUTH).equals(hqLoc)&&rc.senseElevation(rc.getLocation().add(Direction.SOUTH)) < 30&&rc.getCooldownTurns()==0) {



            while (dirtAmount<=8) {
                System.out.println("DIGGING");

                rc.digDirt(Direction.CENTER);
                dirtAmount++;
            }



            System.out.println("EAST MODE OUR IMPORTANT BOOLEAN"+rc.canDepositDirt(Direction.SOUTH));


            if (dirtAmount > 0) {
                System.out.println("We passed our if statement test");
                if (rc.canDepositDirt(Direction.SOUTH)) {
                    System.out.println(rc.senseElevation(rc.getLocation().add(Direction.SOUTH)));

                    rc.depositDirt(Direction.SOUTH);
                    dirtAmount--;
                    System.out.println("Placed this direction" + Direction.SOUTH);
                } else {

                }

            } else {

            }
        }
        else if (!rc.getLocation().add(Direction.WEST).equals(hqLoc)&&rc.senseElevation(rc.getLocation().add(Direction.WEST)) < 30&&rc.getCooldownTurns()==0) {



            while (dirtAmount<=8) {
                System.out.println("DIGGING");

                rc.digDirt(Direction.CENTER);
                dirtAmount++;
            }



            System.out.println("EAST MODE OUR IMPORTANT BOOLEAN"+rc.canDepositDirt(Direction.WEST));


            if (dirtAmount > 0) {
                System.out.println("We passed our if statement test");
                if (rc.canDepositDirt(Direction.WEST)) {
                    System.out.println(rc.senseElevation(rc.getLocation().add(Direction.WEST)));

                    rc.depositDirt(Direction.WEST);
                    dirtAmount--;
                    System.out.println("Placed this direction" + Direction.WEST);
                } else {

                }

            } else {

            }
        }
        else{


        }
    }








    public void move() throws GameActionException {


    }

}