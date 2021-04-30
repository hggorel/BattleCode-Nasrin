package nasrinplayer;
import battlecode.common.*;

/**
 * Fulfillment Center Class
 * Description: The fulfillment center creates drones! It checks to see if it has enough
 * soup to make the bot, and then builds them if the rounds have reached over 450 --
 * this is to make sure the landscapers are favored since their purpose is more essential
 */
public class FulfillmentCenter extends Building{

    //the fulfillment center is responsible for building drones. is built itself by miner.


    public FulfillmentCenter(RobotController robotController) {
        super(robotController);
    }

    static int droneCount = 0;
    static int soupCount = 0;

    public void takeTurn() throws GameActionException{
        super.takeTurn();

        //get total team soup. drones cost 150 each
        soupCount = rc.getTeamSoup();
        //if fulfillment center can afford, build drones.
        if(soupCount >= 150 && rc.getRoundNum()>450){
            for(Direction dir : Direction.allDirections()){
                if(tryBuild(RobotType.DELIVERY_DRONE, dir)){
                    droneCount++;
                }
            }
        }
    }
}
