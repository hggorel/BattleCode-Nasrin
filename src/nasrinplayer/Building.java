package nasrinplayer;
import battlecode.common.*;

/**
 * Building class -- parent class to a lot of the buildings, not too much substance
 */
public class Building extends Robot{

    public Building(RobotController robotController) {

        super(robotController);
    }

    public void takeTurn() throws GameActionException{
        super.takeTurn();
    }
}
