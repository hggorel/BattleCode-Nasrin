package nasrinplayer;

import battlecode.common.*;


public class PathFinder {

    RobotController rc;


    public PathFinder(RobotController r){

        rc = r;


    }


    /*
    This method allows the robot to take the most "optimal" approach to a given location
    TODO:Add more arguments and that take more seneory data into account!
     */
    boolean directApproach(MapLocation current,MapLocation target)throws GameActionException{
       int xPos=current.x;
       int yPos=current.y;

       int targetX= target.x;
       int targetY=target.y;

           Direction dirToTarget=current.directionTo(target);
           //directionTo gives the closest approximate direction towards the goal location
           Direction[] toTry = {dirToTarget, dirToTarget.rotateLeft(), dirToTarget.rotateRight(), dirToTarget.rotateLeft().rotateLeft(), dirToTarget.rotateRight().rotateRight()};
           //Checks all the directions around the current player with the closest direction attached first! Will improve this by having them in best to worst order

           for (Direction d : toTry){
               if(tryMove(d)) {
                   System.out.println("WE DID MOVE");
                   break;
               }
           }
           return true;
    }






    /*
    Basic way to try to move, if the robot is ready to move and does not sense water in that direction
    This was
    TODO: Add different tryMoves to account for enemy robots
     */
    boolean tryMove(Direction dir) throws GameActionException {
        if (rc.isReady() && rc.canMove(dir) && !rc.senseFlooding(rc.getLocation().add(dir))) {
            rc.move(dir);
            return true;
        } else return false;
    }


}
