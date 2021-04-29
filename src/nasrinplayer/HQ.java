package nasrinplayer;

import battlecode.common.*;


//The HQ should be "intelligent" in the sense that it will halt production and attack if the enemies are
//attacking it
public class HQ extends Building package nasrinplayer;

        import battlecode.common.*;


//The HQ should be "intelligent" in the sense that it will halt production and attack if the enemies are
//attacking it
public class HQ extends Building {


    //Directions we can use
    static Direction[] directions = {
            Direction.NORTHEAST,
            Direction.NORTH,
            Direction.WEST,
            Direction.NORTHWEST,
            Direction.SOUTHWEST,
            Direction.EAST,
            Direction.SOUTHEAST,
            Direction.SOUTH
    };


    static int builtBeginningMiners = 0;
    //The number of miners we have build, I am distinguishing the beginning miners, as the beginning stage of the defense


    public HQ(RobotController robotController) throws GameActionException {


        super(robotController);

        if (comms.getHqLoc()==null) {
            System.out.println("UH OH");
            //The HQloc is set to true if a miner has found it in the blockchain, allowing us not to submit it to the block chain afterwards
            comms.sendHQLoc(rc.getLocation());

        }


    }

    public void takeTurn() throws GameActionException {
        super.takeTurn();


        //make miners :)

        if (builtBeginningMiners < 2) {

            for (Direction dir : HQ.directions) {
                if (tryBuild(RobotType.MINER, dir)) {
                    builtBeginningMiners++;
                }

            }

        }


        //shoot down enemy :)
    }
}

