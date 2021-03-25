package nasrinplayer;
import battlecode.common.*;

public class DeliveryDrone extends Unit{
    public DeliveryDrone(RobotController robotController) {
        super(robotController);
    }

    public void takeTurn() throws GameActionException{
        super.takeTurn();

        //pick up tiles/robots
        //place them back down
    }
}
