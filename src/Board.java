import java.awt.Point;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public final class Board {
    private int shipCount = 5;
    private int shotCount = 0;
    
    private boolean done;
    private boolean hitShot;
    private boolean invalidShot;
    private boolean missedShot;
    private boolean killShot;
    
    private final Ship[] ships;
    private final String[][] board;
    
    public Board() throws IOException {
        this.done = false;
        this.hitShot = false;
        this.invalidShot = false;
        this.missedShot = false;
        this.killShot = false;

        board = new String[11][11];
        
        ships = new Ship[5];
        ships[0] = new Ship("Carrier", 5);
        ships[1] = new Ship("Battleship", 4);
        ships[2] = new Ship("Submarine", 3);
        ships[3] = new Ship("Destroyer", 3);
        ships[4] = new Ship("PatrolBoat", 2);
        
        createBoard();
    }
    
    public void createBoard() {
        board[0][0] = " ";
        
        // Column labels
        board[0][1] = "1";
        board[0][2] = "2";
        board[0][3] = "3";
        board[0][4] = "4";
        board[0][5] = "5";
        board[0][6] = "6";
        board[0][7] = "7";
        board[0][8] = "8";
        board[0][9] = "9";
        board[0][10] = "10";
        
        // Row Labels
        board[1][0] = "A";
        board[2][0] = "B";
        board[3][0] = "C";
        board[4][0] = "D";
        board[5][0] = "E";
        board[6][0] = "F";
        board[7][0] = "G";
        board[8][0] = "H";
        board[9][0] = "I";
        board[10][0] = "J";
        
        for (int r = 1; r < 11; r++) {
            for (int c = 1; c < 11; c++) {
                board[r][c] = "-";
            }
        }
    }
    
    // Print to standard output
    public void draw() {
        int i = 10;
        System.out.print("------------------------\n|");
        for (int c = 0; c < i; c++) {
            System.out.print(board[0][c] + " ");
        } System.out.println(board[0][i] + "|");
        for (int r = 1; r < 11; r++) {
            System.out.print("|");
            for (int c = 0; c < 11; c++) {
                System.out.print(board[r][c] + " ");
            } System.out.println("|"); 
        } System.out.println("------------------------\n"); 
    }
    
    // Print to output file
    public void drawToFile(String f, int s) throws IOException {
        int i = 10;
        try (FileWriter fw = new FileWriter(new File(f), true)) {
            fw.write("\n------------------------\n|");
            for (int c = 0; c < i; c++) {
                fw.write(board[0][c] + " ");
            } fw.write(board[0][i] + "|\n");
            for (int r = 1; r < 11; r++) {
                fw.write("|");
                for (int c = 0; c < 11; c++) {
                    fw.write(board[r][c] + " ");
                } fw.write("|\n");
            } fw.write("------------------------\nTotal Shots: " + s);
        }
    }
    
    public boolean placeShip(int r, int c, String or, Ship ship) {
        int start, end;
        if (or.equalsIgnoreCase("Vertical") || or.equalsIgnoreCase("V")) {
            start = r;
            end = r + ship.getMaxHP() - 1;
            if (10 < end) {
                start = r - (end-10);
                end = 10;
            }
            int i = start;
            while (i <= end) {
                if (board[i][c].equals("-")) {
                    board[i][c] = "o";
                    ship.addLocation(new Point(i++, c));
                } else {
                    while (start <= --i) {
                        board[i][c] = "-";
                    } 
                    ship.locationIndex = 0;
                    return false;
                }
            }
        } else if (or.equalsIgnoreCase("Horizontal") || 
                or.equalsIgnoreCase("H")) {
            start = c;
            end = c + ship.getMaxHP() - 1;
            if (10 < end) {
                start = c - (end-10);
                end = 10;
            } 
            int j = start;
            while (j <= end) {
                if (board[r][j].equals("-")) {
                    board[r][j] = "o";
                    ship.addLocation(new Point(r, j++));
                } else {
                    while (start <= --j) {
                        board[r][j] = "-";
                    } 
                    ship.locationIndex = 0;
                    return false;
                }
            }
        } return true; 
    }
    
    public void removeShip(int index) {
        Ship ship = ships[index];
        int size = ship.getMaxHP();
        for (int i = 0; i < size; i++) {
            board[ship.getLocation(i).x][ship.getLocation(i).y] = "-";
        }
        ship.locationIndex = 0;
    }
    
    public boolean unavailibleShot() {
        invalidShot = true;
        missedShot = false;
        killShot = false;
        hitShot = false;
        return false;
    }
    
    public boolean missedShot(int r, int c) {
        invalidShot = false;
        missedShot = true;
        killShot = false;
        hitShot = false;
        shotCount++;
        board[r][c] = "x";
        return false;
    }
   
    public boolean hitShot(int r, int c) throws IOException {
        invalidShot = false;
        missedShot = false;
        killShot = false;
        hitShot = true;
        shotCount++;
        board[r][c] = "!";
        for (int i = 0; i < 5; i++) {
            Ship s = ships[i];
            int len = s.getMaxHP();
            for (int j = 0; j < len; j++) {
                if (s.getLocation(j).x == r && s.getLocation(j).y == c) {
                    s.hit();
                    if (s.checkDead()) {
                        killShot = true;
                        shipCount--; 
                        done = checkDone();
                    }
                }
            }
        } return true;
    }
    
    public boolean checkDone() throws IOException {
        if (0 == shipCount) {
            System.out.println("----------------\n"
                    + "Game over!\nShots Fired: " + shotCount);
            if ("True".equals(Setup.getOutFile())) {
                drawToFile(Setup.getOutFile(), shotCount);
            } return true;
        } return false;
    }
    
    public boolean fire(int r, int c) throws IOException { 
        if (1 > r || 1 > c || 10 < r || 10 < c) {
            invalidShot = true;
        } else {
            switch (board[r][c]) {
                case "x":
                case "!": return unavailibleShot();
                case "-": return missedShot(r, c);
                case "o": return hitShot(r, c);
            }
        } return false;
    }
    
    public int getShipCount() {return shipCount;}
    public void setShipCount(int shipCount) {this.shipCount = shipCount;}

    public int getShotCount() {return shotCount;}
    public void setShotCount(int shotCount) {this.shotCount = shotCount;}

    public Ship[] getShips() {return ships;}
    public Ship getShip(int i) {return ships[i];}

    public String getCell(int r, int c) {return board[r][c];}
    public void setBoardCell(int r, int c, String s) {board[r][c] = s;}

    public boolean isDone() {return done;}
    public void setDone(boolean done) {this.done = done;}

    public boolean isHitShot() {return hitShot;}
    public boolean isInvalidShot() {return invalidShot;}
    public boolean isMissedShot() {return missedShot;}
    public boolean isKillShot() {return killShot;}
}