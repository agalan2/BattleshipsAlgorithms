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
            } 
        }
    }
    
    // Same as huntWithParity but used for Player Vs AI
    public void hardShot() throws IOException {
        if (0 == stackSize) {
            target = testSurrounding(new Point(rand(), rand()));
            if (board.fire(target.x, target.y)) {
                populateStack(target);
            } else if (board.isInvalidShot()) {
                hardShot();
            }
        } else {
            target = pop();
            if (board.fire(target.x, target.y)) {
                populateStack(target);
            } 
        }
    }

    // Randomly pick a spot and fire if it is availible. 
    // If it isnt randomly pick another.
    public void randomVolley() throws IOException {
        while (!board.isDone()) {
            target = new Point(rand(), rand());
            board.fire(target.x, target.y);
        }
    }
    
    // Randomly fire until a ship is hit. Once a ship is hit add the 4 
    // surrounding locations to the stack and fire on the positions in the
    // stack untill stack is empty.  Any time a shot results in a hit the 4
    // surrounding positions should be added to the stack.
    public void hunt() throws IOException {
        while (!board.isDone()) {
            while (1 > stackSize) {
                target = new Point(rand(), rand());
                if (board.fire(target.x, target.y)) {
                    populateStack(target);
                }
            }
            while (0 < stackSize) {
                target = pop();
                if (board.fire(target.x, target.y)) {
                    populateStack(target);
                } 
            }
        }
    }
    
    // Same as hunt. The difference is that when firing randomly to find a 
    // ship, the shot should never be directly adjacent to a missed shot.
    public void huntWithParity() throws IOException {
        while (!board.isDone()) {
            while (1 > stackSize) {
                target = testSurrounding(new Point(rand(), rand()));
                if (board.fire(target.x, target.y)) {
                    populateStack(target);
                }
            }
            while (0 < stackSize) {
                target = pop();
                if (board.fire(target.x, target.y)) {
                    populateStack(target);
                } 
            }
        }
    }
    
    // Utility function for huntWith pairity to ensure that the next shot is
    // next to a missed shot.
    private Point testSurrounding(Point p) {
        Point newTarget = p;    
        if (11 > p.x+1) {
            if (board.getCell(p.x+1, p.y).equals("x")) {
                newTarget.x-=1;
            } else if (0 < p.x-1) {
                if (board.getCell(p.x-1, p.y).equals("x")) {
                    newTarget.x+=1;
                } else if (11 > p.y+1) {
                    if (board.getCell(p.x, p.y+1).equals("x")) {
                        newTarget.y-=1;
                    } else if (0 < p.y-1) {
                        if (board.getCell(p.x+1, p.y).equals("x")) {
                            newTarget.y+=1;
                        }
                    }
                }
            }
        } return newTarget;
    }
    
    private Point pop() {return stack[--stackSize];}
    private void push(Point p) {stack[stackSize++] = p;}
    private void populateStack(Point p) {
        if (0 < p.x-1) {
            push(new Point(p.x-1, p.y));
        } 
        if (11 > p.x+1) {
            push(new Point(p.x+1, p.y));
        } 
        if (0 < p.y-1) {
            push(new Point(p.x, p.y-1));
        } 
        if (11 > p.y+1) {
            push(new Point(p.x, p.y+1));
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
                } Setup.getPositioner().incrementPositioned();
            }
        } b.draw();
        System.out.println("AI placement done");
    }
 
    private int rand() {return 1 + (int)(Math.random() * 10);} 
    public Point getTarget() {return target;}
}