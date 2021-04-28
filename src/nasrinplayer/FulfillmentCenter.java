package nasrinplayer;
import battlecode.common.*;

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
        if(soupCount >= 150){

            for(Direction dir : Direction.allDirections()){
                if(tryBuild(RobotType.DELIVERY_DRONE, dir)){
                    droneCount++;
                }
            }
        }
    }
}
