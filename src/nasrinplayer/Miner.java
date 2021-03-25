package nasrinplayer;
import battlecode.common.*;

public class Miner extends Unit{

    public Miner(RobotController robotController) {
        super(robotController);
    }

    public void takeTurn() throws GameActionException{
        super.takeTurn();

        //look for soup and try to mine :)
        //if full, deposit soup at refinery
        //if not full, see if we know there's soup places and go there
        //if not full, and don't have any saved locations .. move randomly
    }
    /**
     * Attempts to mine soup in a given direction.
     *
     * @param dir The intended direction of mining
     * @return true if a move was performed
     * @throws GameActionException
     */
    boolean tryMine(Direction dir) throws GameActionException {
        if (rc.isReady() && rc.canMineSoup(dir)) {
            rc.mineSoup(dir);
            return true;
        } else return false;
    }

    /**
     * Attempts to refine soup in a given direction.
     *
     * @param dir The intended direction of refining
     * @return true if a move was performed
     * @throws GameActionException
     */
    boolean tryRefine(Direction dir) throws GameActionException {
        if (rc.isReady() && rc.canDepositSoup(dir)) {
            rc.depositSoup(dir, rc.getSoupCarrying());
            return true;
        } else return false;
    }
}
