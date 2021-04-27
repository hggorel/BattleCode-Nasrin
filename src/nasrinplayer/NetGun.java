package nasrinplayer;
import battlecode.common.*;

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
