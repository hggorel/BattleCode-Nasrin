package nasrinplayer;


import java.util.ArrayList;

import battlecode.common.*;

import java.util.Hashtable;


public class PathFinder {


    static boolean bugMode = false;
    static boolean predeterminedTanPath = false;


    static int stuckInt = 0;
    //Used to know if we need to go into BugMode
    static int bugDirection = 0;
    //Special case if there are no locations around us that are a wall in bugMode, mainly, if you move to a location that allows a move
    //that was not previously possible

    static Hashtable<MapLocation, String> dangerousLocations = new Hashtable<>();
    //global track of all dangerous Location
    static Hashtable<MapLocation, String> bugDangerousLocations = new Hashtable<>();
    //bugMode resets this often
    static ArrayList<MapLocation> canMove = new ArrayList<>();
    //bugMode resets this often
    static ArrayList<Direction> canMoveDirection = new ArrayList<>();
    //bugMode resets this often
    static MapLocation lastLocation = null;
    //bugMode wall
    static Hashtable<MapLocation, String> lastWallAdjacencyList = new Hashtable<>();
    //keeps track of walls around lastWall


    static RobotController rc;
    static ArrayList<Integer> shortestDistance = new ArrayList<>();
    //distance to target
    static Direction lastDirection = Direction.CENTER;
    //Might use
    static MapLocation lastWall = null;
    //keeps track of the wall we are using in bugMode

    static ArrayList<Direction> toMove = new ArrayList<>();
    //We have to move this direction


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

        for (Direction d : HQ.directions) {

            if (tryMove(d)) {
                if (rc.getLocation().distanceSquaredTo(target) <= shortestDistance.get(0)) {
                    shortestDistance.add(0, rc.getLocation().distanceSquaredTo(target));


                }
                System.out.println("WE DID MOVE Direct Approach");
                break;
            }
        }
        return true;
    }

    boolean tanBugPath(MapLocation targetL) throws GameActionException {
        //Three parts to this implementation
        //Can we take the optimal path?
        //Are we taking a predetermined path?
        //Are we in bug mode
        //If none of those work we should find a mode that will


        //Always have a shortest distance to compare to(should only happen once)
        if (shortestDistance.isEmpty()) {
            shortestDistance.add(0, rc.getLocation().distanceSquaredTo(targetL));


        }

        //If we find a new shortest distance no more need for bug modes or predetermined path
        if (rc.getLocation().distanceSquaredTo(targetL) < shortestDistance.get(0)) {
            shortestDistance.set(0, rc.getLocation().distanceSquaredTo(targetL));
            System.out.println("Out of every mode");
            bugMode = false;
            predeterminedTanPath = false;
            stuckInt = 0;
        }
        if (toMove.isEmpty()) {
            predeterminedTanPath = false;
        }


        if (rc.getLocation().equals(targetL)) {
            //First thing we have to check for is if we are there
            //This is the only method that will return true. Thus while(tanBugPath(current, target)==false){only go to the path}
            System.out.println("YOU ARE AT YOUR DESTINATION");
            return true;

        } else if (stuckInt >= 5 || bugMode) {
            //If we are in the same location for 5 moves or more we are stuck and need to go into bug mode
            bugMode = true;

            if (doBugStuff(targetL)) {
                System.out.println("AND WE MOVED BUG MODE");
            }


        } else if (predeterminedTanPath) {
            System.out.println("WE ARE FOLLOWING A PREDETERMINED PATH");
            if (determinedPathMove()) {

            }


        } else {
            //There is no predetermined path to take and no bug mode so we must ensure that we
            //can move directly or we can check three moves out.
            //Not this these are quite expensive computationally

            Direction dirToTarget = rc.getLocation().directionTo(targetL);

            dangerousLocations.clear();
            //Resets just in case we moved to a larger elevation


            if (rc.senseFlooding(rc.getLocation().add(dirToTarget)) || rc.senseElevation(rc.getLocation().add(dirToTarget)) > rc.senseElevation(rc.getLocation()) + 3 || rc.senseElevation(rc.getLocation().add(dirToTarget)) < rc.senseElevation(rc.getLocation()) - 3) {
                dangerousLocations.put(rc.getLocation().add(dirToTarget), "water");
                //if we sense flooding, or have a difference of elevation greater than 3 we need to note that it is dangerous


            } else {
                if (tryMove(dirToTarget)) {
                    //if we did move the most optimal direction
                    shortestDistance.set(0, rc.getLocation().add(dirToTarget).distanceSquaredTo(targetL));
                    bugMode = false;
                    predeterminedTanPath = false;
                    stuckInt = 0;
                    return false;
                    //Making sure that the elements are up to date

                } else {

                    if (rc.senseRobotAtLocation(rc.getLocation().add(dirToTarget)) != null) {
                        finalTanModeCheck(rc.getLocation(), rc.getLocation(), targetL);
                        predeterminedTanPath = true;
                        determinedPathMove();
                        return false;

                    }


                }

            }//End of Direct Path Option(If no move is made it wont return anything and we can continue checking )
            //Just because the direction to target is flooded, doesnt mean theres not a good move to make
            //So we are checking if we can make that move

            for (Direction check : HQ.directions) {

                if (rc.senseFlooding(rc.getLocation().add(check)) || rc.senseElevation(rc.getLocation().add(check)) > rc.senseElevation(rc.getLocation()) + 3 || rc.senseElevation(rc.getLocation().add(dirToTarget)) < rc.senseElevation(rc.getLocation()) - 3) {
                    dangerousLocations.put(rc.getLocation().add(check), "water");


                }

                if (!dangerousLocations.containsKey(rc.getLocation().add(check)) && (rc.getLocation().add(check).distanceSquaredTo(targetL) <= shortestDistance.get(0))) {
                    //Not flooded && elevation is safe

                    if (tryMove(check)) {
                        shortestDistance.set(0, rc.getLocation().add(dirToTarget).distanceSquaredTo(targetL));
                        //This is a bit redundant but ensures saftey in moves and shortestDistance array updating!
                        stuckInt = 0;
                        bugMode = false;
                        predeterminedTanPath = false;
                        return false;

                    } else {
                        //If we couldnt make the move, I could care less lets keep checking
                    }

                } else {
                    //If it is dangerous or doesnt contribute to a shorter distance


                }


            }//End of first for loop
            //If we couldnt find a better path lets check tanget mode
            //This will run if nothing is

            for (Direction tanPath : HQ.directions) {
                //DangerousLocations have already been set
                if (!dangerousLocations.containsKey(rc.getLocation().add(tanPath))) {

                    if (finalTanModeCheck(rc.getLocation(), rc.getLocation().add(tanPath), targetL)) {
                        toMove.add(tanPath);
                        predeterminedTanPath = true;
                        determinedPathMove();
                        return false;


                    } else {
                        //if it hasnt return keep checking

                    }
                } else {
                    //If it is dangerous do nothing

                }


            }//End of for loop
            //If our for loop found nothing, bugMode
            bugMode = true;


        }//End of else after all of our checks

        return false;


    }


    boolean determinedPathMove() throws GameActionException {

        if (tryMove(toMove.get(toMove.size() - 1))) {
            toMove.remove(toMove.size() - 1);
            System.out.println(toMove.toString());
            if (toMove.isEmpty()) {
                predeterminedTanPath = false;
            }
            System.out.println("We have taken a predetermined Path should be the most optimal move");
            return false;
        } else {
            System.out.println("The path is not flooded and would couldnt move, if its a building we will move around it, if it is a robot we will wait for to move");
            return false;


        }

    }


    boolean doBugStuff(MapLocation target) throws GameActionException {
        //Rare case of cant move because of elevation but then you can


        while (lastWall == null) {
            if (!rc.senseFlooding(rc.getLocation().add(Direction.WEST)) && tryMove(Direction.WEST)) {
                System.out.println("BUGGIN West");
                lastWall = null;
                return false;
            } else {
                lastWall = rc.getLocation().add(Direction.WEST);
                System.out.println("lastWall" + lastWall);
            }
        }
        canMove.clear();
        canMoveDirection.clear();
        bugDangerousLocations.clear();
        lastWallAdjacencyList.clear();

        System.out.println("THIS IS THE LAST WALL" + lastWall);

        for (Direction bytes : HQ.directions) {

            try {
                if (rc.senseFlooding(lastWall.add(bytes)) || rc.senseElevation(lastWall.add(bytes)) >= rc.senseElevation(rc.getLocation()) + 3) {
                    lastWallAdjacencyList.put(lastWall.add(bytes), "wall");
                    System.out.println("Wall of wall" + lastWall.add(bytes));
                }
            } catch (GameActionException e) {

                if (e.getType() == GameActionExceptionType.OUT_OF_RANGE) {
                    lastWallAdjacencyList.put(lastWall.add(bytes), "OutOfRange");
                    System.out.println("EDGECASE" + lastWall.toString());
                }

            }
        }


        //Trying to map this out so we can move every round, given our tan mode check waste at most 2 turns this is because we must be stuck for 5 rounds to enter bug mode
        //Cant use direction towards target or else we would get stuck in some loops
        for (Direction dir : HQ.directions) {

            try {


                //get all the locations we can move

                if (rc.senseFlooding(rc.getLocation().add(dir)) || rc.senseElevation(rc.getLocation().add(dir)) >= rc.senseElevation(rc.getLocation()) + 3) {
                    bugDangerousLocations.put(rc.getLocation().add(dir), "water");
                    System.out.println("PLEADE BUGDIRECTION" + dir);
                    bugDirection++;


                } else {

                    canMove.add(rc.getLocation().add(dir));
                    canMoveDirection.add(dir);
                    System.out.println("CANMOVE DIR" + dir);


                }
            } catch (GameActionException e) {

                if (e.getType() == GameActionExceptionType.OUT_OF_RANGE) {
                    bugDangerousLocations.put(rc.getLocation().add(dir), "OutOfRange");
                }

            }


        }

        if (bugDirection == 0) {
            while (bugDirection == 0) {

                if (tryMove(Direction.WEST)) {
                    System.out.println("BUGGIN West");
                    lastWall = null;
                    return false;
                }

            }
        }
        //the goal is to check if that the space we are moving to is adjacent to a wall that is adjacent to the last wall we hit
        //Quite expensive computationally!
        System.out.println(canMoveDirection.toString());
        bugDirection = 0;

        for (int x = 0; x <= canMoveDirection.size() - 1; x++) {
            for (Direction dd : HQ.directions) {

                System.out.print("CAN MOVE" + canMoveDirection.get(x) + "Directin" + dd + "(!canMove.contains(rc.getLocation().add(canMoveDirection.get(x)).add(dd)))" + !(canMove.contains(rc.getLocation().add(canMoveDirection.get(x)).add(dd))) + "bugDangerousLocations.containsKey(canMove.get(x).add(dd))" + bugDangerousLocations.containsKey(rc.getLocation().add(canMoveDirection.get(x)).add(dd)) + "canMove.get(x).add(dd).isAdjacentTo(lastWall)" + rc.getLocation().add(canMoveDirection.get(x)).add(dd).isAdjacentTo(lastWall));

                if (canMove.size() == 1) {
                    if (tryMove(canMoveDirection.get(x))) {
                        return true;
                    }

                } else {
                    if (((!canMove.contains(rc.getLocation().add(canMoveDirection.get(x)).add(dd))) || bugDangerousLocations.containsKey(rc.getLocation().add(canMoveDirection.get(x)).add(dd))) && !lastLocation.equals(rc.getLocation().add(canMoveDirection.get(x))) && (!canMove.contains(rc.getLocation().add(canMoveDirection.get(x)).add(dd)) || lastWallAdjacencyList.containsKey(rc.getLocation().add(canMoveDirection.get(x)).add(dd)))) {
                        //Check How I am defining the dangerousLocatons!
                        //ALso dont let it go in the direction we just came from!
                        System.out.println("PASSED OUR TEST");
                        if (tryMove(canMoveDirection.get(x))) {
                            System.out.println("OMG ARE WE ACTUALLY BUGGIN");
                            lastWall = rc.getLocation().add(canMoveDirection.get(x)).add(dd);
                            return true;
                        } else {
                            System.out.println("IDK MAN");

                        }

                    }

                }
            }


        }
        tryMove(Direction.NORTHEAST);
        lastWall = null;

        //worst case

        return false;


    }

    boolean buggerMode(MapLocation target) throws GameActionException {
        //The goal of this mode is to follow a wall
        //Lots of small things that can go wrong!

        //Move west(Arbitrary) till we hit a wall
        while (lastWall == null) {
            if ((!rc.senseFlooding(rc.getLocation().add(Direction.WEST)) && rc.senseElevation(rc.getLocation().add(Direction.WEST)) <= rc.senseElevation(rc.getLocation()) + 3 || rc.senseElevation(rc.getLocation().add(Direction.WEST)) >= rc.senseElevation(rc.getLocation()) - 3) && tryMove(Direction.WEST)) {
                System.out.println("BUGGIN West");
                lastWall = null;
                return false;
            } else {
                lastWall = rc.getLocation().add(Direction.WEST);
                System.out.println("lastWall" + lastWall);
            }
        }
        canMove.clear();
        canMoveDirection.clear();
        bugDangerousLocations.clear();
        lastWallAdjacencyList.clear();
        //Clear all list to ensure we are working with our current elevation and the most up to date data!

        //Now that we have our last wall, we need to get every wall around it
        //Note that the map locations outside the map throws an error, we are specifically checking that becasue we dont know the
        //end of everymap(Different Sizes)

        for (Direction bytes : HQ.directions) {

            try {
                if (rc.senseFlooding(lastWall.add(bytes)) || rc.senseElevation(lastWall.add(bytes)) >= rc.senseElevation(rc.getLocation()) + 3 || rc.senseElevation(lastWall.add(bytes)) <= rc.senseElevation(rc.getLocation()) - 3) {
                    lastWallAdjacencyList.put(lastWall.add(bytes), "wall");
                    System.out.println("Wall of wall" + lastWall.add(bytes));
                }
            } catch (GameActionException e) {

                if (e.getType() == GameActionExceptionType.OUT_OF_RANGE) {
                    lastWallAdjacencyList.put(lastWall.add(bytes), "OutOfRange");
                    System.out.println("Wall of wall" + lastWall.add(bytes));
                    //checking to see if the map location actually gets added
                    System.out.println("EDGECASE" + lastWall.toString());
                }

            }
        }


        return false;

    }


    boolean finalTanModeCheck(MapLocation here, MapLocation firstMove, MapLocation target) throws GameActionException {
        for (Direction dr : HQ.directions) {
            System.out.println("THIS DIRECTION CHECKED" + dr);

            if (rc.senseFlooding(firstMove.add(dr)) || rc.senseElevation(firstMove.add(dr)) > rc.senseElevation(firstMove) + 3) {
                dangerousLocations.put(rc.getLocation().add(dr), "water");
                System.out.println("THIS DIRECTION CHECKED and dangerous" + dr);


            }
            //Check 1 Postion from our first Move for a new shortestDistance
            if (firstMove.add(dr) == here) {
                //If we are moving back to the place in which we started, we should do nothing
                //That would be a waste of bytecodes
            } else if (!dangerousLocations.containsKey(firstMove.add(dr)) && !dangerousLocations.containsKey(firstMove) && firstMove.add(dr).distanceSquaredTo(target) < shortestDistance.get(0) + 40) {
                System.out.println("MOVING ELEVAION" + rc.senseElevation(firstMove.add(dr)));
                System.out.println("BEFORE WE MOVE" + (rc.senseElevation(firstMove)));
                shortestDistance.set(0, firstMove.add(dr).distanceSquaredTo(target));
                toMove.add(dr);
                System.out.println("TanMode added this and only this" + dr);


                //This way, the most recent move we need to make is at the end of the list
                //(Add this move, then add the direction that we took when the method was called)
                return true;

            } else {
                for (Direction third : HQ.directions) {
                    System.out.println("THIS DIRECTION CHECKED IN ELSE" + dr + third);


                    if (rc.senseFlooding(firstMove.add(dr).add(third)) || rc.senseElevation(firstMove.add(dr).add(third)) > rc.senseElevation(firstMove.add(dr)) + 3) {
                        dangerousLocations.put(rc.getLocation().add(dr).add(third), "water");


                    }
                    if (!dangerousLocations.containsKey(firstMove) && !dangerousLocations.containsKey(firstMove.add(dr)) && !dangerousLocations.containsKey(firstMove.add(dr).add(third)) && findRoundTileFloods(firstMove.add(dr)) != -1
                            && findRoundTileFloods(firstMove.add(dr).add(third)) != -1 && firstMove.add(dr).add(third).distanceSquaredTo(target) < shortestDistance.get(0)) {
                        shortestDistance.set(0, firstMove.add(dr).add(third).distanceSquaredTo(target));
                        toMove.add(third);
                        toMove.add(dr);
                        System.out.println("TanMode added this and third" + third + dr);
                        System.out.println("MOVING ELEVAION" + rc.senseElevation(firstMove.add(dr)));
                        System.out.println("BEFORE WE MOVE" + (rc.senseElevation(firstMove)));

                        return true;


                    }


                }


            }


        }//End of for loop
        return false;


    }

    boolean tryMove(Direction dir) throws GameActionException {
        lastLocation = rc.getLocation();


        if (rc.isReady() && rc.canMove(dir)) {
            lastDirection = dir;

            rc.move(dir);
            stuckInt = 0;
            return true;
        } else {
            System.out.println("COOLDOWN" + rc.getCooldownTurns() + "READy" + rc.isReady() + "CANMOVE" + rc.canMove(dir) + "FLOODING" + rc.senseFlooding(rc.getLocation().add(dir)));


            return false;
        }
    }


    /*
    This is meant to get the round in which the TileFloods, This is computationally expensive for high elevations so it only checks if it will flood within the next three rounds,
    //as we dont care about which round it floods for our current pathing
     */
    static int findRoundTileFloods(MapLocation moveTo) throws GameActionException {
        //We want to make sure, that no matter where we move, we will be able to move there without the worry of being flooded

        int b = rc.senseElevation(moveTo);


        int threeRoundsLater = rc.getRoundNum() + 3;


        int waterLevel = (int) Math.floor(Math.pow(Math.E, 0.0028 * threeRoundsLater - 1.38 * Math.sin(0.00157 * threeRoundsLater - 1.73) + 1.38 * Math.sin(-1.73)) - 1);

        if (waterLevel < b) {
            return 1;
            //if the current waterLevel, 3 rounds later, is less than the elevation we are trying to move to it is safe
        } else {
            return -1;
            //else it will be flooded
        }

    }


}







