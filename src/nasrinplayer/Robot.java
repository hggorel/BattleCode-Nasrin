package nasrinplayer;
import battlecode.common.*;

/**
 * Robot Class
 * Description: The Robot class is the parent class for all of the bots we use,
 * it contains the general variables and sets teh comms, pathfinder and robotcontroller
 * that all of the bots will use in their individual classes.
 */
public class Robot {
    RobotController rc;
    Communications comms;
    PathFinder pathing;


    int turnCount=0;

    public Robot(RobotController robotController){

        this.rc = robotController;

        comms = new Communications(rc);

        pathing = new PathFinder(rc);
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
