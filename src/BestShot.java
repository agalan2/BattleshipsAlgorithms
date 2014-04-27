
import java.awt.Point;
import java.io.IOException;

public class BestShot {
   
    private Point target;
    private Point newTarget;
    private int stackSize = 0;
    private final Point[] stack = new Point[15];
    
    private final Board board;
    
    private int minLen = 2;
    
    boolean vert = true;
    boolean forward = true;
    
    public BestShot(Board board) {
        this.board = board;
        
//        board.setBoardCell(2, 5, "x");
//        board.setBoardCell(3, 4, "x");
//        board.setBoardCell(2, 6, "x");
//        board.setBoardCell(1, 4, "x");
//        board.setBoardCell(8, 5, "x");
//        board.setBoardCell(5, 6, "x");
//        board.setBoardCell(8, 3, "x");
//        board.setBoardCell(6, 4, "x");
//        board.setBoardCell(9, 2, "x");
    }
    
    Point prevHit = new Point(0, 0);
    Point curHit = new Point(0, 0);
    Point firstHit;
    
    Point lastHit;
    
    public void fire() throws IOException {
        if (0 == stackSize) {
            getMinLength();
            
            target = new Point(rand(), rand());
            
            testUp(minLen);
            testLeft(minLen);
            testDown(minLen);
            testRight(minLen);
            
            if (up && down && left && right) {
                if (board.fire(target.x, target.y)) {
                    firstHit = target;
                    lastHit = target;
                    prevHit = target;
                    curHit = target;
                    
                    populateStack(target);
                    
                    for (int i = 0; i < stackSize; i++) {
                        System.out.println(stack[i]);
                    }
                    
                } else if (board.isInvalidShot()) {
                    
                    fire();
                    
                }
            } 
        } else {
            target = pop();
            
            if (board.fire(target.x, target.y)) {
                prevHit = curHit;
                curHit = target;
                lastHit = target;
                
                getDirection();
                
                if (vert) {
                    if (forward) {
                        push(new Point(firstHit.x, firstHit.y+1));
                    } else {
                        push(new Point(firstHit.x, firstHit.y-1));
                    }
                } else {
                    if (forward) {
                        push(new Point(firstHit.x+1, firstHit.y));
                    } else {
                        push(new Point(firstHit.x-1, firstHit.y));
                    }
                }
                
                if (board.isKillShot()) {
                    stackSize = 0;
                }
            } else if (board.isMissedShot()) {
                getDirection();
                
                if (vert) {
                    if (forward) {
                        push(new Point(firstHit.x, firstHit.y-1));
                    } else {
                        push(new Point(firstHit.x, firstHit.y+1));
                    }
                } else {
                    if (forward) {
                        push(new Point(firstHit.x-1, firstHit.y));
                    } else {
                        push(new Point(firstHit.x+1, firstHit.y));
                    }
                }
            } else if (board.isInvalidShot()) {
                fire();
            }
        }
    }
    
    private void getMinLength() {
        if (1 == board.getShipCount()) {
            minLen = 1;
        } else {
            for (int i = 0; 5 > i; i++) {
                if (!board.getShip(i).checkDead()) {
                    minLen = board.getShip(i).getMaxHP()-1;
                } 
            } 
        }
    }
    
    private void getDirection() {
        if (prevHit.x == curHit.x) {
            vert = false;
            forward = prevHit.y < curHit.y;
        } else if (prevHit.y == curHit.y) {
            vert = true;
            forward = prevHit.x < curHit.x;
        }
    }
    
    private boolean up = true;
    private void testUp(int min) { 
        up = true;
        
        for (int i = 1; i <= min; i++) {
            if (0 < target.x-i) {
                if (board.getCell(target.x-i, target.y).equals("x")) {
                    up = false;
                    break;
//                } else {
//                    board.setBoardCell(target.x-i, target.y, "U");
                }
            }
        }
    }
    private boolean down = true;
    private void testDown(int min) { 
        down = true;
        
        for (int i = 1; i <= min; i++) {
            if (11 > target.x+i) {
                if (board.getCell(target.x+i, target.y).equals("x")) {
                    down = false;
                    break;
//                } else {
//                    board.setBoardCell(target.x+i, target.y, "D");
                }
            }
        }
    }
    private boolean left = true;
    private void testLeft(int min) { 
        left = true;
        
        for (int i = 1; i <= min; i++) {
            if (0 < target.y-i) {
                if (board.getCell(target.x, target.y-i).equals("x")) {
                    left = false;
                    break;
//                } else {
//                    board.setBoardCell(target.x, target.y-i, "L");
                }
            }
        }
    }
    private boolean right = true;
    private void testRight(int min) { 
        right = true;
        
        for (int i = 1; i <= min; i++) {
            if (11 > target.y+i) {
                if (board.getCell(target.x, target.y+i).equals("x")) {
                    right = false;
                    break;
//                } else {
//                    board.setBoardCell(target.x, target.y+i, "R");
                }
            }
        }
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
    
    private int rand() {return 1 + (int)(Math.random() * 10);}
}