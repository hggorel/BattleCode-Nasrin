package nasrinplayer;

import battlecode.common.*;

/**
 * HQ Class
 * Description: The HQ originally builds a few miners and then builds a few more as time goes on
 * We've typically been running out of soup so decreased the number of miners built
 */
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

    static int totalBuiltMiners = 0;

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

        if (builtBeginningMiners < 5) {

            for (Direction dir : HQ.directions) {
                if (tryBuild(RobotType.MINER, dir)) {
                    builtBeginningMiners++;
                    totalBuiltMiners++;
                }

            }

        }

        //now maybe in the first 200 rounds, every 10 check to see if it can make a miner?
        if(builtBeginningMiners==5 && totalBuiltMiners<=7 && rc.getRoundNum()<200){
            if(rc.getRoundNum()%25==0){
                for(Direction dir: HQ.directions){
                    if(tryBuild(RobotType.MINER, dir)){
                        totalBuiltMiners++;
                    }
                }
            }
        }

    }
}

