package nasrinplayer;
import battlecode.common.*;


//The HQ should be "intelligent" in the sense that it will halt production and attack if the enemies are
//attacking it
public class HQ extends Building{

    public HQ(RobotController robotController) {

        super(robotController);
    }

    public void takeTurn() throws GameActionException{
        super.takeTurn();

        //make miners :)

        //shoot down enemy :)
    }
}
