package nasrinplayer;
import battlecode.common.*;

/**
 * Vaporator Class:
 * Description: Vaporator did not need much implementation at all because
 * we did not want to broadcast its location or anything to the blockchain, so
 * it only needed its constructor
 * Note -- we didn't end up implement Vaporator because like with NetGun we
 * had problems with time and amounts of soup
 */
public class Vaporator extends Building{

    public Vaporator(RobotController robotController) {
        super(robotController);
    }
}
