package nasrinplayer;

import battlecode.common.*;


public class Communications {


    RobotController rc;

    boolean HQloc = false;

    static final int key = 1;
    //Can be changed, will be the first int array submitted to the blockchain, identifying that the message is coming from the nasrin team

    /*
    General Param Constructor, allowing the robot to have access to the methods within this class
     */
    public Communications(RobotController r) {

        rc = r;
    }


    /*
    This is a method meant to send HQ location
    Notice that the transaction cost is "high", we are hoping that the enemy team wont go beyond 10 soup the first round
    if not, at the next round we should have our HQ location sent into the blockchain
    Please Also note our key for Message[x]:
    0=HQ
    1=Refinery
    2=Vaporator
    3=Design School
    4=Fufillment Center
    5=Soup
    ..
    ..

     */
    public void sendHQLoc(MapLocation current) throws GameActionException {

        int[] message = new int[7];
        //We are allowed 7 integers in the blockchain
        message[0] = key;
        message[1] = 0;
        message[2] = current.x; // x coord of HQ
        message[3] = current.y; // y coord of HQ
        if (rc.canSubmitTransaction(message, 10)) {
            rc.submitTransaction(message, 10);
        }
    }

    /*
    This is a method meant to get the HQ location from the blockchain
    Every bot created should spend a the first round it can getting to know where the HQ is, that way if soup is found we can
    refine it at the HQ until a refinery or other building meant to refine soup is built
    -This was the way that the lecture on the MIT battlecode used to communicate the locations needed
     */
    public MapLocation getHqLoc() throws GameActionException {


        for (int i = 1; i < rc.getRoundNum(); i++) {
            //We go up to the round number because "If there are 400 round there will be 399 items in the block chain"
            for (Transaction ts : rc.getBlock(i)) {
                //in each transacation
                int[] hqfinder = ts.getMessage();
                if (hqfinder[0] == key && hqfinder[1] == 0) {
                    //if we find the key for the HQ
                    System.out.println("found the HQ!");
                    HQloc = true;
                    return new MapLocation(hqfinder[2], hqfinder[3]);
                }
            }
        }
        return null;
    }

    /*
    This method is meant to send the Soup Location to the blockchain. Note the higher transcation fee,
    It can be changed but cost/benefit is important and it should be prioritized
    We should also be taking the most recent soup locations
     */
    public void sendSoupLoc(MapLocation current) throws GameActionException {
        int[] message = new int[7];
        message[0] = key;
        message[1] = 5;
        message[2] = current.x;
        message[3] = current.y;
        if (rc.canSubmitTransaction(message, 15)) {
            rc.submitTransaction(message, 15);
        }
    }

    /*
    This method is used for getting the Soup Location
    Please reference the method above or lectures from on the 2020 BattleCode playlist if you
    need to understand what is going on
     */
    public MapLocation getSoupLoc() throws GameActionException {
        for (int i = 1; i < rc.getRoundNum(); i++) {
            for (Transaction ts : rc.getBlock(i)) {

                int[] hqfinder = ts.getMessage();
                if (hqfinder[0] == key && hqfinder[1] == 5) {
                    //if we find the key for Soup
                    System.out.println("found some SOUP!");
                    return new MapLocation(hqfinder[2], hqfinder[3]);
                }
            }
        }
        return null;
    }


}
