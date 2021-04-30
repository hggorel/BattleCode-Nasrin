package nasrinplayer;
import battlecode.common.*;

/**
 * DesignSchool Class
 * Description: Design school creates landscapers, we have it planned that the landscapers will
 * be created within a certain amount of rounds if it has enough soup to build them!
 */
public class DesignSchool extends Building{

    static int numLandscapers;
    public DesignSchool(RobotController robotController) {

        super(robotController);
    }

    public void takeTurn() throws GameActionException{
        super.takeTurn();

        //try to make landscapers :)
        if(rc.getTeamSoup()>200 && rc.getRoundNum()>250 && rc.getRoundNum()<=450){
            for(Direction dir : HQ.directions){
                if(tryBuild(RobotType.LANDSCAPER, dir)){
                    numLandscapers++;
                }
            }
        }
    }
}
