
import java.awt.Point;
import java.io.IOException;

public class BestShotFail {
    private int numGood = 0;
    private Point[] goodShots = new Point[50];
    
    private final Board board;
    
    private int minLen = 2;
    
    private Point target;
    private int stackSize = 0;
    private final Point[] stack = new Point[15];
    
    public BestShotFail(Board board) {
        this.board = board;
    }
    
    private int rand() {return 1 + (int)(Math.random() * 10);}
    
    // The total hits needed to win is 17
    Point prevHit = new Point(0, 0);
    Point curHit = new Point(0, 0);
    Point firstHit;
    
    Point last;
    
    boolean vert = true;
    boolean forward = true;

    public void fire() throws IOException {
        // Find length of smallest ship left alive
        minLen = getMinLength();
        
        // Stack is empty. Ramdom fire.
        // ***Use the value of the shortest ship to improve probability 
        if (0 == stackSize) {
            target = new Point(rand(), rand());
                
            while (!testCell(target.x, target.y)) {
                target = new Point(rand(), rand());
            }

            if (board.fire(target.x, target.y)) {
                prevHit = target;
                curHit = target;
                firstHit = target;
                populateStack(target);
            } else if (board.isInvalidShot()) {
                fire();
            }
        } 
        
        // A hit has been recorded. 
        // High probability cells are waiting to be tested.
        else {
            // Take the cell on the top of the stack
            target = pop();
            
            // Check that the target is in bounds
            if (1 < target.x || 1 < target.y || 10 > target.x || 10 > target.y) {
                
                // CASE: Chosen cell is in bounds
                
                // Case: Chosen cell was a hit
                // Place the previous and next cells on the stack
                //      - This depends on the probable orientation of the ship
                //        that was hit. At this point it is uncertain exactly 
                //        which ship has been hit.
                
                try {
                //if (board.getCell(last.x, last.y).equals("!")) {
                    
                    if (prevHit.x == curHit.x) {
                        if (prevHit.y < curHit.y) {
                            forward = true;
                            if (10 > curHit.y) {
                                push(new Point(target.x, target.y+1));
                            }
                        } else if (prevHit.y > curHit.y) {
                            forward = false;
                            if (1 < curHit.y) {
                                push(new Point(target.x, target.y-1));
                            }
                        }
                        //vert = false;
                    } else if (prevHit.y == curHit.y) {
                        if (prevHit.x < curHit.x) {
                            forward = true;
                            if (10 > curHit.x) {
                                push(new Point(target.x+1, target.y));
                            }
                        } else if (prevHit.x > curHit.x) {
                            forward = false; 
                            if (1 < curHit.x) {
                                push(new Point(target.x-1, target.y));
                            }
                        }
                        //vert = true;
                    } 
                //}
                } catch (NullPointerException e) {
                    
                }
            } 
            
            // 
            else {
                switch (board.getCell(target.x, target.y)) {
                    case "!":
                        if (prevHit.x == curHit.x) {
                            if (prevHit.y < curHit.y) {
                                forward = true;
                                if (10 > curHit.y) {
                                    push(new Point(target.x, target.y+1));
                                }
                            } else if (prevHit.y > curHit.y) {
                                forward = false;
                                if (1 < curHit.y) {
                                    push(new Point(target.x, target.y-1));
                                }
                            }
                            //vert = false;
                        } else if (prevHit.y == curHit.y) {
                            if (prevHit.x < curHit.x) {
                                forward = true;
                                if (10 > curHit.x) {
                                    push(new Point(target.x+1, target.y));
                                }
                            } else if (prevHit.x > curHit.x) {
                                forward = false; 
                                if (1 < curHit.x) {
                                    push(new Point(target.x-1, target.y));
                                }
                            }
                            //vert = true;
                        }
                        fire();
                        break;
                    case "x":
                        if (prevHit == last) {
                            stackSize = 0;
                            fire();
                        }
                        break; 
                    case "o":
                    case "-":
                        if (board.fire(target.x, target.y)) {
                            prevHit = curHit;
                            curHit = target;
                            last = target;
                            
                            if (prevHit.x == curHit.x) {
                                //stackSize = 0;
                    
                                if (prevHit.y < curHit.y && 10 > curHit.y) {
                                    //forward = true;
                                    push(new Point(target.x, target.y+1));
                                } else if (prevHit.y > curHit.y && 1 < curHit.y) {
                                    //forward = false;
                                    push(new Point(target.x, target.y-1));
                                }

                                vert = true;
                            } else if (prevHit.y == curHit.y) {
                                //stackSize = 0;

                                if (prevHit.x < curHit.x && 10 > curHit.x) {
                                    //forward = true;
                                    push(new Point(target.x+1, target.y));
                                } else if (prevHit.x > curHit.x && 1 < curHit.x) {
                                    //forward = false; 
                                    push(new Point(target.x-1, target.y));
                                }

                                vert = false;
                            }
                
                            if (board.isKillShot()) {
                                stackSize = 0;
                                System.out.println("KILL!");
                            }

                            if (1 == board.getShipCount()) {
                                minLen = 2;
                            }
            
                    } else if (board.isMissedShot()) {
                        if (firstHit != curHit) {
                            stackSize = 0;
                        }
                        if (vert) {
                            if (forward) {
                                push(new Point(firstHit.x-1, firstHit.y));
                            } else {
                                push(new Point(firstHit.x+1, firstHit.y));
                            }
                        } else {
                            if (forward) {
                                push(new Point(firstHit.x, firstHit.y-1));
                            } else {
                                push(new Point(firstHit.x, firstHit.y+1));
                            }
                        } 
                    } //else if (board.isInvalidShot()) {
                        if (0 >= target.x || 0 >= target.y || 10 <= target.x || 10 <= target.y) {
                            //stackSize = 0;
                            fire();
                        } 
                        
                        if (prevHit.x == curHit.x) {
                            //stackSize = 0;
                    
                            if (prevHit.y < curHit.y && 10 > curHit.y) {
                                forward = true;
                                push(new Point(target.x, target.y+1));
                            } else if (prevHit.y > curHit.y && 1 < curHit.y) {
                                forward = false;
                                push(new Point(target.x, target.y-1));
                            }
                        } else if (prevHit.y == curHit.y) {
                            //stackSize = 0;
                    
                            if (prevHit.x < curHit.x && 10 > curHit.x) {
                                forward = true;
                                push(new Point(target.x+1, target.y));
                            } else if (prevHit.x > curHit.x && 1 < curHit.x) {
                                forward = false; 
                                push(new Point(target.x-1, target.y));
                            }
                        }
                        fire();
                    //}
                    break; 
                } //last = target;
            }      
        }   
    }
    
    private int getMinLength() {
        for (int i = 0; 5 > i; i++) {
            if (!board.getShip(i).checkDead()) {
                minLen = board.getShip(i).getMaxHP();
            } 
        } 
        return minLen;
    }
    
    public void testAllCells() throws IOException {
        for (int i = 1; i < 11; i++) {
            for (int j = 1; j < 11; j++) {
                if (testCell(i, j)) {
                    board.setBoardCell(i, j, "H");
                } 
            }
        }
    }
    
    private boolean testCell(int r, int c) throws IOException {
        int min = getMinLength();
        
        Point p = target;
        
//        while (0 < stackSize) {
//            switch (board.getCell(p.x, p.y)) {
//                
//            }
//        }
        
        for (int i = 0; i < min; i++) {
            if (10 > c+i) {
                if (board.getCell(r, c+i).equals("x")) {
                    return false;
                }
            }
                
            if (10 > r+i) {
                if (board.getCell(r+i, c).equals("x")) {
                    return false;
                }
            } 
        }
        for (int i = 1; i < min; i++) {
            if (0 <= c-i) {
                if (board.getCell(r, c-i).equals("x")) {
                    return false;
                }
            }
            
            if (0 <= r-i) {
                if (board.getCell(r-i, c).equals("x")) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    private Point pop() {return stack[--stackSize];}
    private void push(Point p) {stack[stackSize++] = p;}
    private void populateStack(Point p) {
        if (10 >= p.x+1) {
            push(new Point(p.x+1, p.y));
        }
        if (10 >= p.y+1) {
            push(new Point(p.x, p.y+1));
        }
        if (0 < p.x-1) {
            push(new Point(p.x-1, p.y));
        }
        if (0 < p.y-1) {
            push(new Point(p.x, p.y-1));
        } 
    }
}