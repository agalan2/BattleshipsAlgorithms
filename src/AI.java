import java.awt.Point;
import java.io.IOException;

public class AI {
    private Point target;
    
    private final Board board;
    
    private int stackSize = 0;
    private final Point[] stack = new Point[50];
    
    public AI(Board b) {
        this.board = b;
    }
    
    // Same as randomVolley but used for player Vs AI
    public void easyShot() throws IOException {
        target = new Point(rand(), rand());
        board.fire(target.x, target.y);
        if (board.isInvalidShot()) {
            easyShot();
        }
    }
    
    // Same as hunt but used for player Vs AI
    public void mediumShot() throws IOException {
        if (0 == stackSize) {
            target = new Point(rand(), rand());
            if (board.fire(target.x, target.y)) {
                populateStack(target);
            } else if (board.isInvalidShot()) {
                mediumShot();
            }
        } else {
            target = pop();
            if (board.fire(target.x, target.y)) {
                populateStack(target);
            } else if (board.isInvalidShot()) {
                mediumShot();
            }
        }
    }
    
    // Same as huntWithParity but used for Player Vs AI
    public void hardShot() throws IOException {
        if (0 == stackSize) {
            target = new Point(rand(), rand());
              
            numAttempts = 0;
            while (!testChoice(target, 1)) {
                target = new Point(rand(), rand());
            }

            if (board.fire(target.x, target.y)) {
                populateStack(target);
            } else if (board.isInvalidShot()) {
                hardShot();
            }
        } else {
            target = pop();
            
            if (board.fire(target.x, target.y)) {
                populateStack(target);
            } else if (board.isInvalidShot()) {
                hardShot();
            }
        }
    }
    
    private int getMinLength() {
        if (1 == board.getShipCount()) {
            return 1;
        } else {
            int min = 1;
            for (int i = 4; 0 >= i; i--) {
                if (!board.getShip(i).checkDead()) {
                    min =  board.getShip(i).getMaxHP()-1;
                } 
            } return min;
        }
    }
    
    public void veryHardShot() throws IOException {
        if (0 == stackSize) {
            int min = getMinLength();
            
            target = new Point(rand(), rand());
              
            numAttempts = 0;
            while (!testChoice(target, min)) {
                target = new Point(rand(), rand());
            }

            if (board.fire(target.x, target.y)) {
                populateStack(target);
            } else if (board.isInvalidShot()) {
                hardShot();
            }
        } else {
            target = pop();
            
            if (board.fire(target.x, target.y)) {
                populateStack(target);
            } else if (board.isInvalidShot()) {
                hardShot();
            }
        }
    }
    
    private boolean canEmptyStack(Point p) {
        if (p.x == lastHit.x) {
            if (0 < p.y-1 && 11 > p.y+1) {
                if (board.getCell(p.x, p.y+1).equals("!") 
                        || board.getCell(p.x, p.y-1).equals("!")) {
                    return false;
                }
            }
        }
        
        if (p.y == lastHit.y) {
            if (0 < p.x-1 && 11 > p.x+1) {
                if (board.getCell(p.x+1, p.y).equals("!") 
                        || board.getCell(p.x-1, p.y).equals("!")) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    Point lastHit;
    Point firstHit;
    public void extremeShot() throws IOException {
        if (0 == stackSize) {
            target = new Point(rand(), rand());
              
            numAttempts = 5;
            
            while (!testChoice(target, 1)) {
                target = new Point(rand(), rand());
            }

            if (board.fire(target.x, target.y)) {
                lastHit = target;
                firstHit = target;
                populateStack(target);
            } else if (board.isInvalidShot()) {
                extremeShot();
            }
        } else {
            target = pop();
            
            if (board.fire(target.x, target.y)) {
                if (board.isKillShot()) { 
                    boolean ok = true;
                    
                    while (0 < stackSize) {
                        target = pop();
                        if (board.getCell(target.x, target.y).equals("!")) {
                            //lastHit = target;
                            //populateStack(target);
                            break;
                        }
                    }
                } else {
                    populateStack(target);
                    if (lastHit.x == target.x) {
                        if (lastHit.y < target.y) {
                            if (11 > target.y+1) {
                                push(new Point(target.x, target.y+1));
                            }
                        } else {
                            if (0 < target.y-1) {
                                push(new Point(target.x, target.y-1));
                            }
                        }
                    } else if (lastHit.y == target.y) {
                        if (lastHit.x < target.x) {
                            if (11 > target.x+1) {
                                push(new Point(target.x+1, target.y));
                            }
                        } else {
                            if (0 < target.x-1) {
                                push(new Point(target.x-1, target.y));
                            }
                        }
                    } 
                    lastHit = target;
                }
            } else if (board.isMissedShot()) {
                if (firstHit != lastHit) {
                    stackSize = 0;
                    //populateStack(lastHit);
                }
                if (lastHit.x == target.x) {
                    if (lastHit.y < target.y) {
                        if (11 > firstHit.y+1) {
                            push(new Point(firstHit.x, firstHit.y+1));
                        }
                    } else {
                        if (0 < firstHit.y-1) {
                            push(new Point(firstHit.x, firstHit.y-1));
                        }
                    }
                } else if (lastHit.y == target.y) {
                    if (lastHit.x < target.x) {
                        if (11 > firstHit.x+1) {
                            push(new Point(firstHit.x+1, firstHit.y));
                        }
                    } else {
                        if (0 < target.x-1) {
                            push(new Point(firstHit.x-1, firstHit.y));
                        }
                    }
                }
            } else if (board.isInvalidShot()) {
                extremeShot();
            }
        }
    }
    
    public void smartHuntWithParity() throws IOException {
        while (!board.isDone()) {
            extremeShot();
        }
    }

    // Randomly pick a spot and fire if it is availible. 
    // If it isnt randomly pick another.
    public void randomVolley() throws IOException {
        while (!board.isDone()) {
            easyShot();
        }
    }
    
    // Randomly fire until a ship is hit. Once a ship is hit add the 4 
    // surrounding locations to the stack and fire on the positions in the
    // stack untill stack is empty.  Any time a shot results in a hit the 4
    // surrounding positions should be added to the stack.
    public void hunt() throws IOException {
        while (!board.isDone()) {
            mediumShot();
        }
    }
    
    // Same as hunt. The difference is that when firing randomly to find a 
    // ship, the shot should never be directly adjacent to a missed shot.
    public void huntWithParity() throws IOException {
        hardShot();
        if (!board.isDone()) {
            huntWithParity();
        }
    }
    
    public void dynamicHuntWithParity() throws IOException {
        while (!board.isDone()) {
            veryHardShot();
        }
    }
    
    // Utility function for huntWith pairity to ensure that the next shot is
    // next to a missed shot.
    
    int numAttempts = 0;
    private boolean testChoice(Point p, int i) {
        boolean up = true;
        boolean down = true;
        boolean left = true;
        boolean right = true;
        
        for (int j = 1; j <= i; j++) {
            if (0 < p.x-j) {
                if (board.getCell(p.x-j, p.y).equals("x")) {
                    down = false;
                }
            }
            if (0 < p.y-j) { 
                if (board.getCell(p.x, p.y-j).equals("x")) {
                    left = false;
                }
            }
            if (11 > p.x+j) {
                if (board.getCell(p.x+j, p.y).equals("x")) {
                    up = false;
                }
            }
            if (11 > p.y+j) {
                if (board.getCell(p.x, p.y+j).equals("x")) {
                    right = false;
                }
            }
        }
        
        if (up && down && left && right) {
            return true;
        } else if (15 > numAttempts) {
            numAttempts++;
            return false;
        } return true;
    }
    
    
    private Point pop() {return stack[--stackSize];}
    private void push(Point p) {stack[stackSize++] = p;}
    private void populateStack(Point p) {
        if (11 > p.x+1) {
            push(new Point(p.x+1, p.y));
        } 
        if (11 > p.y+1) {
            push(new Point(p.x, p.y+1));
        }
        if (0 < p.x-1) {
            push(new Point(p.x-1, p.y));
        } 
        
        if (0 < p.y-1) {
            push(new Point(p.x, p.y-1));
        } 
    }
        
    
    // Randomly place ships
    public void randomPlacement(Board b) {
        boolean valid;
        for (int i = 0; i < 5; i++) {
            valid = false;
            int dir = (int)(Math.random() * 2);
            while (!valid) {
                if (0 == dir) {
                    valid = b.placeShip(rand(), rand(), "h", b.getShip(i));
                } else {
                    valid = b.placeShip(rand(), rand(), "v", b.getShip(i));
                } 
            }
        }
        Setup.getPositioner().setPositioned(5);
        b.draw();
        System.out.println("AI placement done");
    }
 
    private int rand() {return 1 + (int)(Math.random() * 10);} 
    public Point getTarget() {return target;}
}