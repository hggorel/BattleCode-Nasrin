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
    static boolean wallMode = false;


    static RobotController rc;
    static ArrayList<Integer> shortestDistance = new ArrayList<>();
    //distance to target
    static Direction lastDirection = Direction.CENTER;
    //Might use
    static MapLocation lastWall = null;
    //keeps track of the wall we are using in bugMode

    static ArrayList<Direction> toMove = new ArrayList<>();
    //We have to move this direction
    static int northWall = 100, eastWall = 100, southWall = -5, westWall = -5;


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
                bugMode=false;
                predeterminedTanPath=false;
                lastWall=null;
                if (rc.getLocation().distanceSquaredTo(target) <= shortestDistance.get(0)) {
                    shortestDistance.add(0, rc.getLocation().distanceSquaredTo(target));


                }
                System.out.println("WE DID MOVE Direct Approach");
                break;
            }
        }
        return true;
    }

    boolean tanBugPath(MapLocation target) throws GameActionException {
        //Three parts to this implementation
        //Can we take the optimal path?
        //Are we taking a predetermined path?
        //Are we in bug mode
        //If none of those work we should find a mode that will

        if (shortestDistance.isEmpty()) {
            shortestDistance.add(0, rc.getLocation().distanceSquaredTo(target));


        }

        if (rc.getLocation().distanceSquaredTo(target) < shortestDistance.get(0)) {
            shortestDistance.set(0, rc.getLocation().distanceSquaredTo(target));
            System.out.println("Out of every mode");
            bugMode = false;
            predeterminedTanPath = false;
            stuckInt = 0;
            lastWall = null;
        }


        if (rc.getLocation().isWithinDistanceSquared(target, 2)) {
            //First thing we have to check for is if we are there
            //This is the only method that will return true. Thus while(tanBugPath(current, target)==false){only go to the path}
            System.out.println("YOU ARE AT YOUR DESTINATION");
            return true;

        } else if (stuckInt >= 5 || bugMode) {
            //If we are in the same location for 5 moves or more we are stuck and need to go into bug mode
            bugMode = true;

            if (buggerMode(target)) {
                stuckInt = 0;
                System.out.println("AND WE MOVED BUG MODE" + "LastWall" + lastWall);

                return false;
            }


        } else if (predeterminedTanPath) {
            lastWall = null;

            System.out.println("WE ARE FOLLOWING A PREDETERMINED PATH");
            if (determinedPathMove()) {
                stuckInt = 0;


            }
            if (toMove.isEmpty()) {
                predeterminedTanPath = false;
                return false;
            } else {
                return false;
            }


        }
        //If nothing has returned, we need to check which mode we should be in

        lastWall = null;
        //resets, not in bug mode so it doesnt matter

        if (rc.isReady()) {
            System.out.println("We are trying out a direct Path");

            Direction dirToTarget = rc.getLocation().directionTo(target);
            System.out.println("The Direction to Target is" + dirToTarget);

            dangerousLocations.clear();
            //Resets just in case we moved to a larger elevation

            if (rc.senseFlooding(rc.getLocation().add(dirToTarget)) || rc.senseElevation(rc.getLocation().add(dirToTarget)) > rc.senseElevation(rc.getLocation()) + 3 || rc.senseElevation(rc.getLocation().add(dirToTarget)) < rc.senseElevation(rc.getLocation()) - 3) {
                System.out.println("It happens to be the case that the Direction to targets isnt safe");
                dangerousLocations.put(rc.getLocation().add(dirToTarget), "water");
            } else if (rc.senseRobotAtLocation(rc.getLocation().add(dirToTarget)) != null) {
                System.out.println("There is a robot in our way Maybe the annoying cow? We will wait");
                if (finalTanModeCheck(rc.getLocation(), rc.getLocation(), target)) {
                    return false;

                } else {
                    bugMode = true;
                }
                return false;
            } else {

                System.out.println("Trying the direct Path");
                if (tryMove(dirToTarget)) {
                    System.out.println("Direct path was a success");
                    //if we did move the most optimal direction
                    shortestDistance.set(0, rc.getLocation().add(dirToTarget).distanceSquaredTo(target));
                    bugMode = false;
                    predeterminedTanPath = false;
                    stuckInt = 0;
                    return false;
                    //Making sure that the elements are up to date

                }
            }
            System.out.println("The Direct Path definely didnt work Lets try our other options before tan move");

            for (Direction check : HQ.directions) {
                System.out.println("Entering our first for loop");

                if (rc.senseFlooding(rc.getLocation().add(check)) || rc.senseElevation(rc.getLocation().add(check)) > rc.senseElevation(rc.getLocation()) + 3 || rc.senseElevation(rc.getLocation().add(check)) < rc.senseElevation(rc.getLocation()) - 3) {
                    dangerousLocations.put(rc.getLocation().add(check), "water");

                }

                if (!dangerousLocations.containsKey(rc.getLocation().add(check)) && (rc.getLocation().add(check).distanceSquaredTo(target) <= shortestDistance.get(0))) {
                    System.out.println("Checking all other options before tangent");
                    //Not flooded && elevation is safe

                    if (tryMove(check)) {
                        shortestDistance.set(0, rc.getLocation().add(dirToTarget).distanceSquaredTo(target));
                        //This is a bit redundant but ensures saftey in moves and shortestDistance array updating!
                        stuckInt = 0;
                        bugMode = false;
                        predeterminedTanPath = false;
                        lastWall = null;
                        return false;

                    } else {
                        //If we couldnt make the move, I could care less lets keep checking
                    }

                } else {
                    System.out.println("Why are we here");
                }


            }//End of First for loop! This should try every possible option that has a distance less than our original distance
            System.out.println("Our first for loop was no help time to do tangent path");

            for (Direction tanPath : HQ.directions) {
                //DangerousLocations have already been set
                if (!dangerousLocations.containsKey(rc.getLocation().add(tanPath))) {
                    System.out.println("First Move Tan Path" + tanPath);

                    if (finalTanModeCheck(rc.getLocation(), rc.getLocation().add(tanPath), target)) {
                        toMove.add(tanPath);
                        predeterminedTanPath = true;
                        System.out.println("predetermined path move" + toMove.toString());
                        determinedPathMove();
                        return false;


                    } else {
                        //if it hasnt return keep checking
                        toMove.clear();

                    }
                } else {
                    //If it is dangerous do nothing

                }


            }//End of for loop


            System.out.println("Bugging around");
            bugMode = true;


        } else {
            //if we arent ready dont do anything

        }


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

    boolean buggerMode(MapLocation target) throws GameActionException {
        //The goal of this mode is to follow a wall

        //First, we will move to the West, this is arbitrary, until we hit a wall


        canMove.clear();
        canMoveDirection.clear();
        bugDangerousLocations.clear();
        lastWallAdjacencyList.clear();
        wallMode = false;
        //Clear all list to ensure we are working with the most up to date data!

        //Now that we have our last wall, we need to get every wall around it
        //Note that the map locations outside the map throws an error, we are specifically checking that becasue we dont know the
        //end of everymap(Different Sizes)

        //Also, this isnt the best way to handle this but if there is a robot we will wait for it to move.
        //At the end of the day, if the robot Stays there then we will have their robot also stuck until we move
        //Rendering them both useless. That is if the enemy teams robot isnt doing a productive task.
        try {
            while (lastWall == null) {
                //Move west until we hit the wall
                if ((!rc.senseFlooding(rc.getLocation().add(Direction.WEST)) && rc.senseElevation(rc.getLocation().add(Direction.WEST)) < rc.senseElevation(rc.getLocation()) + 3 && rc.senseElevation(rc.getLocation().add(Direction.WEST)) > rc.senseElevation(rc.getLocation()) - 3) && tryMove(Direction.WEST)) {
                    System.out.println("BUGGIN West");
                    lastWall = null;
                    return false;
                } else {
                    //if we can move it must be a wall
                    lastWall = rc.getLocation().add(Direction.WEST);
                    System.out.println("lastWall" + lastWall);
                }
            }
        } catch (GameActionException e) {

            System.out.println("CATCH");
            //This doesnt do much but if we are looking at the edge of the map it allows us to keep lookin


            westWall = rc.getLocation().x;
            wallMode = true;


        }

        //Checking for adjacent walls
        Direction bites = Direction.CENTER;


        try {
            for (Direction bytes : HQ.directions) {
                bites = bytes;
                System.out.println(bytes + "Adjacent Wall check");
                if (lastWall != null && (rc.senseFlooding(lastWall.add(bytes)) || rc.senseElevation(lastWall.add(bytes)) > rc.senseElevation(rc.getLocation()) + 3 || rc.senseElevation(lastWall.add(bytes)) < rc.senseElevation(rc.getLocation()) - 3) && rc.getLocation().isWithinDistanceSquared(rc.getLocation().add(bytes), 2)) {
                    lastWallAdjacencyList.put(lastWall.add(bytes), "wall");
                    System.out.println("This is a Wall of our wall" + lastWall.add(bytes));

                } else if (lastWall == null) {
                    while (lastWall == null) {
                        //Move west until we hit the wall
                        if ((!rc.senseFlooding(rc.getLocation().add(Direction.WEST)) && rc.senseElevation(rc.getLocation().add(Direction.WEST)) < rc.senseElevation(rc.getLocation()) + 3 && rc.senseElevation(rc.getLocation().add(Direction.WEST)) > rc.senseElevation(rc.getLocation()) - 3) && tryMove(Direction.WEST)) {
                            System.out.println("BUGGIN West");
                            lastWall = null;
                            return false;
                        } else {
                            //if we can move it must be a wall
                            lastWall = rc.getLocation().add(Direction.WEST);
                            System.out.println("lastWall" + lastWall);
                        }
                    }

                    System.out.println("This was not an adjeacnet wall");
                    //Maybe add an argument for robots, the problem is when you pause at the end of one round and resume the next one,
                    //The robot has the posibiity to move


                }
            }


        } catch (GameActionException e) {

            System.out.println("CATCH");
            directApproach(rc.getLocation(),target);
            //This doesnt do much but if we are looking at the edge of the map it allows us to keep lookin
            wallMode = true;

            if (bites == Direction.NORTH) {
                northWall = rc.getLocation().y;


            } else if (bites == Direction.EAST) {
                eastWall = rc.getLocation().x;


            } else if (bites == Direction.SOUTH) {
                southWall = rc.getLocation().y;


            } else if (bites == Direction.WEST) {
                westWall = rc.getLocation().x;


            } else {
                System.out.print("Non cardinal Direction");

            }


        }//End of adjacent wall check


        if (lastWall != null && !lastWallAdjacencyList.containsKey(lastWall)) {
            lastWallAdjacencyList.put(lastWall, "WAlL");

        }


        System.out.println("THIS IS OUR LASTWALL ADJACENCY LIST" + lastWallAdjacencyList.keySet().toString());


        //Now to get the places we can and cant move


        Direction dirr = Direction.CENTER;
        try {
            for (Direction dir : HQ.directions) {
                dirr = dir;
                System.out.println("Checking this Direction: For CANMOVE" + dir);
                if (rc.senseFlooding(rc.getLocation().add(dir)) || rc.senseElevation(rc.getLocation().add(dir)) > rc.senseElevation(rc.getLocation()) + 3 || rc.senseElevation(rc.getLocation().add(dir)) < rc.senseElevation(rc.getLocation()) - 3 || lastWallAdjacencyList.containsKey(rc.getLocation().add(dir))) {
                    bugDangerousLocations.put(rc.getLocation().add(dir), "water");
                    System.out.println("We can not move this Direction" + dir + "Because flooding?" + rc.senseFlooding(rc.getLocation().add(dir)) + "OR elevation ?3" + (rc.senseElevation(rc.getLocation().add(dir)) > rc.senseElevation(rc.getLocation()) + 3));
                    bugDirection++;


                } else {
                    for (MapLocation keys : lastWallAdjacencyList.keySet()) {
                        System.out.println("Checking this direction " + dir + "And Wall near it" + keys + "IS IT?" + rc.getLocation().add(dir).isWithinDistanceSquared(keys, 2));
                        if (rc.getLocation().add(dir).isWithinDistanceSquared(keys, 2)) {
                            //
                            canMove.add(rc.getLocation().add(dir));
                            canMoveDirection.add(dir);
                            System.out.println("We can Move this Direction" + dir);
                        }
                    }


                }
            }
        } catch (GameActionException e) {
            System.out.println("CATCH");
            directApproach(rc.getLocation(),target);
            //This doesnt do much but if we are looking at the edge of the map it allows us to keep lookin
            wallMode = true;

            if (dirr == Direction.NORTH) {
                northWall = rc.getLocation().y;


            } else if (dirr == Direction.EAST) {
                eastWall = rc.getLocation().x;


            } else if (dirr == Direction.SOUTH) {
                southWall = rc.getLocation().y;


            } else if (dirr == Direction.WEST) {
                westWall = rc.getLocation().y;


            } else {
                System.out.print("Non cardinal Direction");

            }


        }//End of Try/Catch

        try {
            if (bugDirection == 0) {
                while (bugDirection == 0) {

                    if (tryMove(Direction.WEST)) {
                        System.out.println("BUGGIN West");
                        lastWall = null;
                        return false;
                    }

                }
            }
        } catch (GameActionException e) {
            directApproach(rc.getLocation(),target);
            westWall = rc.getLocation().y;


        }
        //Just in case there were no walls


        //Now that we know all the walls
        System.out.println("THIS IS OUR CAN MOVE LIST" + canMoveDirection.toString());
        bugDirection = 0;


        //Determine Path move
        for (int x = 0; x <= canMoveDirection.size() - 1; x++) {
            System.out.println("Looking at this direction FOR CANMOVE " + canMoveDirection.get(x));

            if (wallMode) {
                //Edge of map arugment
                System.out.println("WELL WE ARE AT A WALL");

            } else {

                if (canMove.size() == 1) {
                    //We can Only move in one direction

                    if (rc.getLocation().add(canMoveDirection.get(x)).equals(lastLocation)) {
                        bugMode = false;
                        directApproach(rc.getLocation(), target);

                    } else {
                        if (tryMove(canMoveDirection.get(x))) {
                            for (MapLocation keys : lastWallAdjacencyList.keySet()) {
                                if (rc.getLocation().add(canMoveDirection.get(x)).isAdjacentTo(keys))


                                    lastWall = keys;
                                return true;
                            }

                        }
                    }

                } else {

                    if ((!rc.getLocation().add(canMoveDirection.get(x)).equals(lastLocation)) && (!bugDangerousLocations.containsKey(rc.getLocation().add(canMoveDirection.get(x)))) && (rc.getLocation().add(canMoveDirection.get(x)).x < eastWall && rc.getLocation().add(canMoveDirection.get(x)).x > westWall && rc.getLocation().add(canMoveDirection.get(x)).y > southWall && rc.getLocation().add(canMoveDirection.get(x)).y < northWall)) {
                        //Check How I am defining the dangerousLocatons!
                        //ALso dont let it go in the direction we just came from!
                        System.out.println("PASSED OUR TEST" + canMoveDirection.get(x));
                        for (MapLocation keys : lastWallAdjacencyList.keySet()) {

                            if (canMove.get(x).isWithinDistanceSquared(keys, 1) && lastLocation != rc.getLocation().add(canMoveDirection.get(x))) {
                                if (tryMove(canMoveDirection.get(x))) {
                                    System.out.println("OMG ARE WE ACTUALLY BUGGIN");
                                    lastWall = keys;
                                    return true;
                                } else {

                                }

                            }
                        }

                        while (!lastWall.equals(rc.getLocation().add(Direction.WEST))) {
                            //Move west until we hit the wall
                            if ((!rc.senseFlooding(rc.getLocation().add(Direction.WEST)) && rc.senseElevation(rc.getLocation().add(Direction.WEST)) < rc.senseElevation(rc.getLocation()) + 3 && rc.senseElevation(rc.getLocation().add(Direction.WEST)) > rc.senseElevation(rc.getLocation()) - 3) && tryMove(Direction.WEST)) {
                                System.out.println("BUGGIN West");
                                lastWall = null;
                                return false;
                            } else {
                                //if we can move it must be a wall
                                lastWall = rc.getLocation().add(Direction.WEST);
                                System.out.println("lastWall" + lastWall);
                            }
                        }


                    }

                }
            }


        }


        return true;

    }

    boolean finalTanModeCheck(MapLocation here, MapLocation firstMove, MapLocation target) throws GameActionException {
        for (Direction secondMove : HQ.directions) {
            System.out.println("Second Move Checking" + secondMove);
            if (rc.senseFlooding(firstMove.add(secondMove)) || rc.senseElevation(firstMove.add(secondMove)) > rc.senseElevation(firstMove) + 3 || rc.senseElevation(firstMove.add(secondMove)) < rc.senseElevation(firstMove) - 3) {
                dangerousLocations.put(rc.getLocation().add(secondMove), "water");
                System.out.println("THIS DIRECTION CHECKED and dangerous" + secondMove);


            } else if (firstMove.equals(firstMove.add(secondMove))) {
                System.out.println("Not Moving this way" + secondMove);
            } else if (!dangerousLocations.containsKey(firstMove) && !dangerousLocations.containsKey(firstMove.add(secondMove)) && firstMove.add(secondMove).distanceSquaredTo(target) < shortestDistance.get(0)) {
                toMove.add(secondMove);
                System.out.println("WE REALLY DID ADD THIS AND ONLY THIS DIRECTION" + secondMove);
                predeterminedTanPath = true;
                return true;

            } else {


            }


        }
        return false;


    }


    boolean finalTanModeChecks(MapLocation here, MapLocation firstMove, MapLocation target) throws GameActionException {
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
            } else if (!dangerousLocations.containsKey(firstMove.add(dr)) && !dangerousLocations.containsKey(firstMove) && firstMove.add(dr).distanceSquaredTo(target) < shortestDistance.get(0)) {
                System.out.println("MOVING ELEVAION" + rc.senseElevation(firstMove.add(dr)));
                System.out.println("BEFORE WE MOVE" + (rc.senseElevation(firstMove)));
                shortestDistance.set(0, firstMove.add(dr).distanceSquaredTo(target));
                toMove.add(dr);
                System.out.println("TanMode added this and only this" + dr);


                //This way, the most recent move we need to make is at the end of the list
                //(Add this move, then add the direction that we took when the method was called)
                return true;

            } else if (!dangerousLocations.containsKey(firstMove.add(dr))) {
                for (Direction third : HQ.directions) {
                    System.out.println("THIS DIRECTION CHECKED IN ELSE" + dr + third);


                    if (rc.senseFlooding(firstMove.add(dr).add(third)) || rc.senseElevation(firstMove.add(dr).add(third)) > rc.senseElevation(firstMove.add(dr)) + 3 || rc.senseElevation(firstMove.add(dr).add(third)) < rc.senseElevation(firstMove.add(dr)) - 3) {
                        dangerousLocations.put(firstMove.add(dr).add(third), "water");


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


                }//End of 2nd for loop


            }


        }//End of for loop
        bugMode = true;
        return false;


    }

    boolean tryMove(Direction dir) throws GameActionException {
        lastLocation = rc.getLocation();

        if (rc.getCooldownTurns() > 0) {
            Clock.yield();
        }


        if (rc.isReady() && rc.canMove(dir)) {
            lastDirection = dir;

            rc.move(dir);
            stuckInt = 0;
            if (!bugMode) {
                lastWall = null;
            }
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







