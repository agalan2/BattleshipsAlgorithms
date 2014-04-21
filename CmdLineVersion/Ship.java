import java.awt.Point;

public class Ship {
    private int currentHP;
    private final int maxHP;

    private final String name;

    private final Point[] locations;

    public Ship(String name, int maxHP) {
        this.name = name;
        this.maxHP = maxHP;
        this.currentHP = maxHP;
        this.locations = new Point[maxHP];
    }

    public void hit() {currentHP--;}

    public boolean checkDead() {return 0 >= currentHP;}
    
    public int locationIndex = 0;
    public void addLocation(Point p) {
        locations[locationIndex++] = p;
    }

    public int getMaxHP() {return maxHP;}

    public int getCurrentHP() {return currentHP;}
    public void setCurrentHP(int i) {currentHP = i;}
   
    public String getName() {return name;}

    public Point[] getLocations() {return locations;}
    public Point getLocation(int i) {return locations[i];}
}