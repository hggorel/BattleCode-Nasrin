package nasrinplayer;
import battlecode.common.*;

public class DeliveryDrone extends Unit{
    public DeliveryDrone(RobotController robotController) {
        super(robotController);
    }

    static MapLocation HQLocation; //the location of the home HQ
    static MapLocation EnemyHQLoc; //the location of the enemy HQ

    //a method to sense all nearby bots
    static boolean nearbyRobot(RobotType target) throws GameActionException{
        //RobotInfo[] robots = rc.senseNearbyRobots();
        //for(RobotInfo r : robots){
            //if(r.getType() == target){
                //return true;
            //}
        //}
        return true;
    }

    //method to sense all nearby enemy, non-building, robots specifically
    static boolean nearbyEnemyRobot(RobotInfo target) throws GameActionException{
        //RobotInfo[] robots = rc.senseNearbyRobots();
        //for(RobotInfo r : robots){
            //get all the robots' type
            //r.getType();
            //if the nearby bot is not a player bot and is not a building...
            //if(r.isPlayer == false && r.isBuilding == false){
                //int enemyBotID = rc.getID();
            //}
        //}
        return true;
    }

    public void takeTurn() throws GameActionException{
        super.takeTurn();

        Robot drone = null; 
        /**
         *drones will pick up tiles/robots, place them back down
         *
         * Planned Tactics:
         *
         * Priority Alpha: Find Enemy HQ, if and only if it has yet to be found
         *
         * Priority Beta: Engage defensive mode, encircle team HQ (not enemy) and defend against
         * enemy units by sensing surroundings and picking up any enemies that near the HQ.
         * Drop enemy units into nearest water pool, then return to formation.
         *
         *
         *
         * https://www.youtube.com/watch?v=B0dYT3KZd9Y - battlecode lecture vids
         *
         */

       

        /**
         * Priority Alpha: All drones should find home and enemy base locations
         */
        //if(HQLocation == null){
            //search area for HQ
            //RobotInfo[] agents = rc.senseNearbyRobots();
            //for(RobotInfo agent : agents){
                //RobotInfo contains: robot ID num, location, team, and type.
                //if(agent.type == RobotType.HQ && agent.team == rc.getTeam()){
                    //home team HQ found
                    //HQLocation = agent.location;
                    //enemy HQ found (?)
                //}
                //else if(agent.type == RobotType.HQ && isPlayer() == false){
                        //EnemyHQLoc = agent.location;
                    //}
                    //else{
                        //if the enemy's HQ location is already found
                        //System.out.println("Enemy HQ Location at: " + EnemyHQLoc);
                    //}
                //}
            //}else{
                //if the HQ is already found, print the location
                //System.out.println("Home HQ Location at: " + HQLocation);
                //get direction to location of home base, move to home
                //Direction directionToHQ  = rc.getLocation().directionTo(HQLocation);
                //tryMove(directionToHQ);

            }

        /**
         * Priority Beta: Assemble drones near home base, begin defend and destroy tactics
         * 
         * usefull methods:
         * boolean senseFlooding(Map location loc)
         */
       //if(drone.currentlyHoldingUnit == false){
        //while(true){
            //if(tanBugPath(drone.location, HQLocation)== true){
                //break;
                  //send the drones back to HQ location
               //Direction directionToHQ = rc.getLocation().directionTo(HQLocation);
               //if(tryMove(directionToHQ)){
                   //System.out.println("I am a Drone and I'm returning to HQ!");
            //}
             //assuming player team is team A and enemy is team B,
             //sense enemy bots (team B) within drone radius and pick them up
             //RobotType[] robots = drone.senseNearbyRobots(24, B); //drone radius squared is 24
             //for(RobotType r : robots){
                 //if the robot is able to be picked up
                 //if(r.canBePickedUp == true && drone.currentlyHoldingUnit == false){
                     //drone.pickUpUnit(r);
                     //tryMove(randomDirection());
                     //for(Directions dir : directions){
                         //sense nearby water and try to path for dropping?
                     //}
                 //}
             //}
        //}
      //}
    //}

      //moving in all directions
    //for(Direction dir : directions){

              //have the drones faff about
               //tryMove(randomDirection());
               //if/when they come across another bot, check if it can move over it
               //if(adjacentLocation(drone.location).isLocationOccupied() == true){

               //a drone cannot occupy a spot that is taken by another drone or building
               
               //boolean isDrone = nearbyRobot(DELIVERY_DRONE);
               //boolean isbuilding = nearbyRobot(HQ || FULFILLMENT_CENTER || REFINERY || DESIGN_SCHOOL || VAPORATOR);
               //if(isDrone == false){
                   //move elsewhere?
                   //drone.tryMove(dir);
               //}else if(isbuilding == false){
                   //move elsewhere?
                   //drone.tryMove(dir);
               //}
              /*
              switch(rc.nearbyRobot()){
                  case HQ:
                  drone.canMove() == false;
                  break;

                  case DELIVERY_DRONE:
                  drone.canMove() == false;
                  break;

                  case DESIGN_SCHOOL:
                  drone.canMove() == false;
                  break;

                  case FULFILLMENT_CENTER:
                  drone.canMove() == false;
                  break;

                  case REFINERY:
                  drone.canMove() == false;
                  break;

                  case VAPORATOR:
                  drone.canMove() == false;

              }
              */
          }
      
   //}//end for


        

//}// end of take turn
//}//end of class