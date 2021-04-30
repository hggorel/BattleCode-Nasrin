package nasrinplayer;
import battlecode.common.*;

/**
 * NetGun Class
 * Description: The NetGun shoots down opponents -- unfortunately we have not fully implemented
 * this because we ended up always running out of soup and since we are not a very offensive team
 * these were deemed slightly less important -- however, we did code them to show how they would be
 * implemented if we knew how to create them properly!
 */
public class NetGun extends Robot{

    public NetGun(RobotController robotController) {
        super(robotController);
    }

    public void takeTurn() throws GameActionException{
        super.takeTurn();

        //shoot enemies that are close
        Team enemy = rc.getTeam().opponent();
        RobotInfo[] enemiesClose = rc.senseNearbyRobots(GameConstants.NET_GUN_SHOOT_RADIUS_SQUARED, enemy);

        for(RobotInfo enemyBot : enemiesClose){
            //we'd rather shoot a delivery drone because they can drop things
            if(enemyBot.type == RobotType.DELIVERY_DRONE){
                if(rc.canShootUnit(enemyBot.ID)){
                    rc.shootUnit(enemyBot.ID);
                    break;
                }
            }
            //do we want to shoot net guns too?
            else if(enemyBot.type == RobotType.NET_GUN){
                if(rc.canShootUnit(enemyBot.ID)){
                    rc.shootUnit(enemyBot.ID);
                    break;
                }
            }
        }
    }
}
