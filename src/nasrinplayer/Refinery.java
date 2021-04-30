package nasrinplayer;
import battlecode.common.*;

/**
 * Refinery Class
 * Description: The Refinery Class is just here to build the objects, no code was necessary
 * because we did not choose to broadcast it's description in the blockchain or anything,
 * so it just calls its super constructor to create the building.
 */
public class Refinery extends Building{

    public Refinery(RobotController robotController) {
        super(robotController);
    }
}
