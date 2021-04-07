package nasrinplayer;


import java.util.ArrayList;

import battlecode.common.*;

import java.util.Hashtable;


public class PathFinder {

    static Direction lastMove = Direction.NORTH;
    static MapLocation hitWall = null;

    static boolean foundFloodedTile = false;
    static boolean bugMode = false;
    static boolean predeterminedTanPath = false;

    static Hashtable<MapLocation, String> dangerousLocations = new Hashtable<>();

    static RobotController rc;
    static ArrayList<Integer> shortestDistance = new ArrayList<>();

    static ArrayList<Direction> toMove = new ArrayList<>();

    public PathFinder(RobotController r) {


        rc = r;


    }


    /*
    This method allows the robot to take the most "optimal" approach to a given location
    TODO:Add more arguments and that take more sensory data into account!
     */
    boolean directApproach(MapLocation current, MapLocation target) throws GameActionException {
        Direction dirToTarget = current.directionTo(target);
        //directionTo gives the closest approximate direction towards the goal location
        //2 Bytecode

        Direction[] toTry = {dirToTarget, dirToTarget.rotateLeft(), dirToTarget.rotateRight(), dirToTarget.rotateLeft().rotateLeft(), dirToTarget.rotateRight().rotateRight()};
        //Checks all the directions around the current player with the closest direction attached first! Will improve this by having them in best to worst order

        for (Direction d : toTry) {

            if (tryMove(d)) {
                if (rc.getLocation().distanceSquaredTo(target) <= (int) shortestDistance.get(0)) {
                    shortestDistance.add(0, rc.getLocation().distanceSquaredTo(target));


                }
                System.out.println("WE DID MOVE");
                break;
            }
        }
        return true;
    }


    boolean tanBugPath(MapLocation current, MapLocation target) throws GameActionException {
        //Three general things to check for, most optimal/route predetermined path/bugMode

        if (current.equals(target)) {
            //First thing we have to check for is if we are there
            //This is the only method that will return true. Thus while(tanBugPath(current, target)==false){only go to the path}
            System.out.println("YOU ARE HERE");
            return true;

        } else if (bugMode == true) {
            System.out.println("WE ARE IN BUGMODE");

        } else if (predeterminedTanPath == true) {
            System.out.println("WE ARE FOLLOWING A PREDETERMINED PATH");


        } else {
            //checking for the three things

            Direction dirToTarget = current.directionTo(target);
            //The most optimal approach


            if (!rc.senseFlooding(rc.getLocation().add(dirToTarget))) {
                //If we dont sense flooding
                if (tryMove(dirToTarget)) {
                    System.out.println("We have taken the most optimal move");
                    return false;
                } else {
                    System.out.println("The direct path is not flooded and would couldnt move, if its a building we will move around it, if it is a robot we will wait for to move");
                    System.out.println("TRIED TO MOVE IN THIS DIRECTION" + dirToTarget);
                    return false;


                }

            }//End of direct path not flooded
            else {
                System.out.println("The optimal path was flooded");

                System.out.println("NO bugMode and no Predetermined Path");


                for (Direction dir : HQ.directions) {
                    System.out.println("NOW IN THE FOR LOOP CHECKING THIS DIRECTION:" + dir + "DISTANCE SQ" + current.add(dir).distanceSquaredTo(target));

                    if (shortestDistance.isEmpty()) {
                        //Always have a shortest distance to compare to(should only happen once)
                        shortestDistance.add(0, current.distanceSquaredTo(target));
                        System.out.println("THE FIRST SHORTEST DISTANCE");

                    }
                    if (shortestDistance.get(0) > current.add(dir).distanceSquaredTo(target) && !rc.senseFlooding(current.add(dir))) {
                        System.out.println("NEW SHORTEST PATH");
                        toMove.clear();
                        shortestDistance.set(0, current.add(dir).distanceSquaredTo(target));

                        if (tryMove(dir)) {
                            System.out.println("We have taken the almost most optimal move");
                            return false;
                        } else {
                            System.out.println("The direct path is not flooded and would couldnt move, if its a building we will move around it, if it is a robot we will wait for to move");
                            return false;


                        }
                    }


                    if (!bugMode && predeterminedTanPath == false && !rc.senseFlooding(current.add(dir))) {
                        //No Moves to take, bugMode and predeterminedPath are not set
                        //We need to see if there is a path to take
                        if (reWriteTanModeCheck(current, current.add(dir), target)) {
                            System.out.println("WE FOUND A NEW SHORTEST PATH");
                            toMove.add(dir);
                            System.out.println("THIS IS THE SHORTEST PATH WE FOUND" + toMove.toString());


                        }
                    } else if (bugMode == false && predeterminedTanPath == true && !toMove.isEmpty()) {


                    } else if (toMove.isEmpty() && predeterminedTanPath == true) {
                        //if we need to update the predetermined path

                    }


                }


            }


        }


        return false;
    }


    /*
    ToDo:Implement the HashTable to see if it contains dangerous locations and reduce some computation

     */
    boolean reWriteTanModeCheck(MapLocation weAreHere, MapLocation firstMove, MapLocation target) throws
            GameActionException {
        MapLocation starter = weAreHere;
        System.out.print("DISTANCE SQUARED" + starter.distanceSquaredTo(target));
        //This is location the robot is currently on(Mainly used to ensure we dont end up in a loop going back and forth)

        for (Direction dr : HQ.directions) {
            MapLocation checkingFromPastMove = firstMove;

            if (checkingFromPastMove.add(dr) == starter) {
                //If we are moving back to the place in which we started, we should do nothing
                //That would be a waste of bytecodes
            }

            if (checkingFromPastMove.add(dr).distanceSquaredTo(target) < shortestDistance.get(0) && !rc.senseFlooding(checkingFromPastMove.add(dr)) && findRoundTileFloods(checkingFromPastMove.add(dr)) != -1) {
                //If we move and the distance is less than the shortest distance we have known, has not been flooded, and wont be flooded within 3 rounds we should add that move to the
                //ArrayList that indicates the next moves we should take
                shortestDistance.set(0, checkingFromPastMove.add(dr).distanceSquaredTo(target));


                toMove.add(dr);
                System.out.println("SECOND TO LAST" + dr);


                //This way, the most recent move we need to make is at the end of the list
                //(Add this move, then add the direction that we took when the method was called)
                return true;
                //There is no need to do any more computation


            } else {
                //If we have not found the shortest path we will check one more direction from the current position
                if (findRoundTileFloods(checkingFromPastMove.add(dr)) != -1) {
                    //as long as the next tile we are checking does will not flood within the next 3 rounds
                    for (Direction dirr : HQ.directions) {

                        if (checkingFromPastMove.add(dr).add(dirr) == starter) {
                            //If we are moving back to the place in which we started, we should do nothing
                            //That would be a waste of bytecodes

                        } else if (checkingFromPastMove.add(dr).add(dirr).distanceSquaredTo(target) < shortestDistance.get(0) && !rc.senseFlooding(checkingFromPastMove.add(dr).add(dirr))) {
                            //If we move and the distance is less than the shortest distance we have known, we should add that move to the
                            //ArrayList that indicates the next moves we should take
                            shortestDistance.add(0, checkingFromPastMove.add(dr).add(dirr).distanceSquaredTo(target));
                            System.out.print("Final Distance SQUARED" + checkingFromPastMove.add(dr).add(dirr));

                            toMove.add(dirr);
                            System.out.println("FIRST to BE ADDED" + dirr);
                            toMove.add(dr);
                            System.out.println("SECOND to BE ADDED" + dirr);
                            //This way, the most recent move we need to make is at the end of the list
                            //(Third Move added, Second Move added, then add the direction that we took when the method was called)
                            return true;
                            //There is no need to do any more computation


                        } else {
                            return false;
                            //we couldnt find a new shortest path, continue with Regular Bug Mode
                        }


                    }
                } else {
                    //No need check anything if the block before this will flood within three rounds
                }


            }


        }
        return false;
        //Im sure if it didnt return true, our answer must be false


    }


    boolean tryMove(Direction dir) throws GameActionException {
        if (rc.isReady() && rc.canMove(dir) && !rc.senseFlooding(rc.getLocation().add(dir))) {
            rc.move(dir);
            return true;
        } else return false;
    }


    /*
    This is meant to get the round in which the TileFloods, This is computationally expensive for high elevations so it only checks if it will flood within the next three rounds,
    //as we dont care about which round it floods for our current pathing
     */
    static int findRoundTileFloods(MapLocation moveTo) throws GameActionException {

        int b = rc.senseElevation(moveTo);
        //1 bytecode
        switch (b) {
            case (0): {
                return 0;
            }
            case (1): {
                return 256;

            }
            case (3): {
                return 677;

            }
            case (5): {
                return 1210;

            }
            case (10): {
                return 1771;

            }
            case (25): {
                return 2143;

            }
            case (50): {
                return 2348;

            }
            case (100): {
                return 2524;

            }
            case (1000): {
                return 3019;

            }
            default: {
                int x = rc.getRoundNum();

                int threeRoundsLater = x + 3;

                //We are checking 3 rounds ahead to ensure that for everymove we pre-check it is a valid


                int waterLevel = (int) Math.floor(Math.pow(Math.E, 0.0028 * threeRoundsLater - 1.38 * Math.sin(0.00157 * threeRoundsLater - 1.73) + 1.38 * Math.sin(-1.73)) - 1);

                if (waterLevel < rc.senseElevation(moveTo)) {
                    //(DEBUG)System.out.println("THIS WAS SAFE");
                    return x;
                    //Save to move for the next 3 rounds
                } else {
                    //if its not safe 3 rounds later, check
                    waterLevel = (int) Math.floor(Math.pow(Math.E, 0.0028 * rc.getRoundNum() - 1.38 * Math.sin(0.00157 * b - 1.73) + 1.38 * Math.sin(-1.73)) - 1);
                    if (waterLevel > rc.senseElevation(moveTo)) {
                        //For already flooded
                        dangerousLocations.put(moveTo, "Water");
                        //(DEBUG)System.out.println("DID NOT MOVE FLOODED");

                        return -1;

                    } else {
                        //we are checking 3 moves out so regardless, we should not move there
                        dangerousLocations.put(moveTo, "Water");
                        //(DEBUG)System.out.println("DID NOT MOVE ALMOST FLOODED");
                        return -1;
                    }


                }
            }


        }


    }

}
