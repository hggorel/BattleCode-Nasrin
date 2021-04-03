package nasrinplayer;
import battlecode.common.*;
import lectureplayer.Communications;

public class Robot {
    RobotController rc;
    Communications comms;


    int turnCount=0;

    public Robot(RobotController robotController){
        this.rc = robotController;
    }

    public void takeTurn() throws GameActionException{
        turnCount+=1;
    }


    /**
     * Attempts to build a given robot in a given direction.
     *
     * @param type The type of the robot to build
     * @param dir The intended direction of movement
     * @return true if a move was performed
     * @throws GameActionException
     */
    boolean tryBuild(RobotType type, Direction dir) throws GameActionException{
        if(rc.isReady() && rc.canBuildRobot(type, dir)){
            rc.buildRobot(type, dir);
            return true;
        }
        return false;
    }

}
