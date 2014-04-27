import java.io.IOException;

public final class Positioner {
    private int positioned;
    private int stackSize = 5;
    private final int[] stack = new int[] {4, 3, 2, 1, 0};
   
    // Prompt the user to enter an integer coordinate
    // Recursively call itself until input is valid or 'quit' is entered 
    // Valid inputs: 1-10 inclusively
    public int locInPrompt(String p, String n) {
        System.out.print("Enter starting " + p + " position for " + n + ": ");
        String str = CommandLine.s.next();
        try {
            int i = Integer.parseInt(str);
            if (0 < i && 11 > i) {return i;}
            else {
                System.out.print("Index out of bounds.\n");
                return locInPrompt(p, n);
            }
        } catch (NumberFormatException e) {
            if (str.equalsIgnoreCase("quit") || str.equalsIgnoreCase("q")) {
                System.out.print("Quit? (y/n) ");
            switch (CommandLine.s.next()) {
                case "yes": 
                case "y":
                    System.out.print("Quitting Execution...\n");
                    System.exit(0); break;
                default: 
                    return locInPrompt(p, n);
                }
            }
        } return locInPrompt(p, n);
    }
    
    // Prompt the user to enter sting value representing 
    //      the orientation of the ship.
    // Recursively call itself until input is valid or 'quit' is entered 
    // Valid inputs: Horizontal, Vertical, H, V
    //  *Note: inputs are not case sensitive
    public String oriInPrompt(String name) {
        System.out.print("Enter orientation for " + name + ": ");
        String o = CommandLine.s.next();
        if (!o.equalsIgnoreCase("Horizontal") 
                && !o.equalsIgnoreCase("h") 
                && !o.equalsIgnoreCase("Vertical") 
                && !o.equalsIgnoreCase("v")) {
            System.out.print("Invalid input.\n");
            return oriInPrompt(name);
        } else {
            return o;
        }
    }
    
    // If ship has been placed, remove it and add to the stack
    // After remove run position command
    public void remove(int i, Board b) throws IOException {
        if ( (0 <= i) && (5 > i) && (0 != b.getShip(i).locationIndex) ) {
            b.removeShip(i);
            positioned--;
            pushToStack(i);
            b.draw();
            position(b);
        } else {
            System.out.print("Cannot remove...\n");
            //Setup.getCommandLine().showCmdPrompt();
        }
    }
    
    // Position ships until stack is empty, but wait 
    //  for user input inbetween each
    // If a ship is removed, it should be placed back on the stack.
    public void position(Board b) throws IOException {
        getAndPosition(b);
        if (0 < stackSize) {
            Setup.getPlayerBoard().draw();
            System.out.print("Use 'continue' to place next ship.\n");
            Setup.getCommandLine().showCmdPrompt();
        } else {
            System.out.print("All ships have been placed.\n");
        } Setup.getPlayerBoard().draw();
        if (Setup.isInGame()) {
            Setup.getGameLogic().playGame("null");
        } else {
            Setup.getCommandLine().showCmdPrompt();
        }
        
        
    }
    
    private void getAndPosition(Board b) throws IOException {
        Ship ship = b.getShip(stack[--stackSize]);
        int row = locInPrompt("row", ship.getName());
        int column = locInPrompt("column", ship.getName());
        String orientation = oriInPrompt(ship.getName());
        
        if (b.placeShip(row, column, orientation, ship)) {
            positioned++;
        } else {
            getAndPosition(b);
        }
    }
    
    public void pushToStack(int i) {stack[stackSize++] = i;}
    
    public int getFromStack(int i) {return stack[i];}
    public int getPositioned() {return positioned;}
    public int getStackSize() {return stackSize;}
    public void setStackSize(int i) {stackSize = i;}
    public void setPositioned(int i) {positioned = i;}
    public void incrementPositioned() {positioned++;}
    public void decrementPositioned() {positioned--;}
}