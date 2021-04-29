package nasrinplayer;
import battlecode.common.*;

public class DesignSchool extends Building{

    static int numLandscapers;
    public DesignSchool(RobotController robotController) {

        super(robotController);
    }

    public void takeTurn() throws GameActionException{
        super.takeTurn();

        //try to make landscapers :)
        if(rc.getTeamSoup()>450){
            for(Direction dir : HQ.directions){
                if(tryBuild(RobotType.LANDSCAPER, dir)){
                    numLandscapers++;
                }
            }
        }
    }
}
