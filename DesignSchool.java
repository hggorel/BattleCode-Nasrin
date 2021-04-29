package nasrinplayer;
import battlecode.common.*;

public class DesignSchool extends Building{

    public DesignSchool(RobotController robotController) {
        super(robotController);
    }

    public void takeTurn() throws GameActionException{
        super.takeTurn();

        //try to make landscapers :)
        tryBuild(RobotType.LANDSCAPER, Direction.WEST);

    }

}
