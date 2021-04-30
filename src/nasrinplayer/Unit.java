package nasrinplayer;
import battlecode.common.*;

/**
 * Unit Class
 * Description: This class is just the parent class for all of the robots -- it extends
 * Unit but for the most part has no substance other than being a parent to the units and
 * having methods for them to extend.
 */
public class Unit extends Robot{

    public Unit(RobotController robotController) {
        super(robotController);
    }

    public void takeTurn() throws GameActionException{
        super.takeTurn();
    }


}
