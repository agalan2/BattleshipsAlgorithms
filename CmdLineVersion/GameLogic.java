import java.io.IOException;

public class GameLogic {
    private final Board ab;
    private final Board ps;

    private int dificulty;
    
    public GameLogic() throws IOException {
        ab = new Board();
        ps = new Board();
    }
    
    public void initGame(String str) throws IOException {
        Setup.setInGame(true);
        Setup.getAI().randomPlacement(ab);
        getDificulty();
        playerSetup();
        playGame(str);
    }
    
    public void playGame(String str) throws IOException {
        if (str.equals("-d")) {
            System.out.print("*****NOTICE: Debug is Active*****");
            displayStarSeperator("GAME START");
        } else {
            Setup.setInDebug(false);
            displayStarSeperator("GAME START");
        }
        boolean turn = (0 == (int)(Math.random() * 2));
        while (!Setup.getPlayerBoard().isDone() && !ab.isDone()) { 
            if (turn) {
                System.out.print("\nfire (f): Fire shot\n"
                        + "state (s): Display game state\n"
                        + "quit (q): Exit\n\n");
                String cmd = Setup.getCommandLine().showInGameCmdPrompt();
                while (turn) {
                    switch (cmd) {
                        case "fire":
                        case "f":
                            shot();
                            displayGameState("Player", "turn");
                            showEOT(ab, "Player", r, c);
                            turn = !turn; break;
                        case "quit":
                        case "q":
                            System.exit(0); break;
                        case "state":
                        case "s":
                            displayStarSeperator("GAME STATE");
                            displayGameState("Player", "State");
                            System.out.print("\nAI board:\n");
                            getAIBoard().draw();
                            displayStarSeperator("GAME STATE END");
                            cmd = Setup.getCommandLine().showInGameCmdPrompt();
                            break;
                        default:
                            cmd = Setup.getCommandLine().showInGameCmdPrompt();
                    }
                }
            } else {
                aiShot(dificulty);
                showEOT(Setup.getPlayerBoard(), "AI", 
                        Setup.getAI().getTarget().x,
                        Setup.getAI().getTarget().y);
                turn = !turn;
            }
        } displayStarSeperator("GAME END");
    }
  
    public void verifyQuit(boolean inGame) throws IOException {
        System.out.print("Quit? (y/n) ");
        switch (Setup.getCommandLine().s.next()) {
            case "yes":
            case "y":
                System.out.print("Quitting Execution...\n");
                System.exit(0); break;
            case "no":
            case "n":
                if (Setup.isInGame()) {
                    shot();
                } else {
                    Setup.getCommandLine().showCmdPrompt();
                } break;
            default:
                System.out.print("Invalid entry\n");
                verifyQuit(inGame);
        }
    }
    
    private void playerSetup() throws IOException {
        System.out.print("Enter 'Manual' for manual or "
                + "'Random' for random placement: ");
        String str = CommandLine.s.next();
        if (0 == Setup.getPositioner().getPositioned()) {
            switch (str) {
                case "Manual":
                case "m":
                    Setup.getPositioner().position(Setup.getPlayerBoard()); 
                    break;
                case "Random":
                case "r":
                    Setup.getAI().randomPlacement(Setup.getPlayerBoard()); 
                    break;
                case "Quit":
                case "q":
                    verifyQuit(true); break;
                default:
                    System.out.print("Invalid selection.\n");
                    playerSetup();
            }
        } 
    }
    
    private void getDificulty() throws IOException {
        System.out.print("Select dificulty (easy, medium, hard): ");
        String str = CommandLine.s.next();
        if (str.equalsIgnoreCase("Easy") || str.equalsIgnoreCase("e")) {
            dificulty = 1;
        } else if (str.equalsIgnoreCase("Medium") || str.equalsIgnoreCase("m")) {
            dificulty = 2;
        } else if (str.equalsIgnoreCase("Hard") || str.equalsIgnoreCase("h")) {
            dificulty = 3;
        } else if (str.equalsIgnoreCase("Quit") || str.equalsIgnoreCase("q")) {
            verifyQuit(true);
        } else {
            System.out.print("Invalid entry");
            getDificulty();
        }
    }
    
    private int r, c;
    public void shot() throws IOException {
        System.out.print("#################################\n\n"
                + "Shots you've taken\n");
        ps.draw();
        
        r = Setup.getPositioner().locInPrompt("row", "shot");
        c = Setup.getPositioner().locInPrompt("column", "shot");
        ab.fire(r, c);
        
        if (ab.isHitShot()) {
            ps.setBoardCell(r, c, "!");
        } else if (ab.isMissedShot()) {
            ps.setBoardCell(r, c, "x");
        } 
    }
    
    public void aiShot(int i) throws IOException {
        switch (i) {
            case 1: Setup.getAI().easyShot(); break;
            case 2: Setup.getAI().mediumShot(); break;
            case 3: Setup.getAI().hardShot(); break;
        } 
    }
    
    private void showEOT(Board board, String name, int r, int c) {
        if (board.isHitShot()) {
            System.out.print("\n-------\n" 
                    + name 
                    + " shot at location (" 
                    + r + "," + c 
                    + ")  was a HIT! End of " 
                    + name 
                    + " turn...\n-------\n");
        } else if (board.isMissedShot()) {
            System.out.print("\n-------\n" 
                    + name 
                    + " shot at location (" 
                    + r + "," + c 
                    + ")  was a MISS! End of " 
                    + name 
                    + " turn...\n-------\n");
        } else if (board.isInvalidShot()) {
            System.out.print("\n-------------------------\n" 
                    + name 
                    + " shot at location (" 
                    + r + "," + c 
                    + ")  is not a valid shot"
                    + "\n-------------------------\n");
            try {
                shot();
            } catch (IOException ex) {}
        }
    }
    
    public void displayAIBoard() {
        if (Setup.isInGame() && Setup.isInDebug()) {
            System.out.print("\nAI Board:\n");
            ab.draw();
        }
    }
    
    public void displayGameState(String name, String label) {
        int len = 8 + name.length();
        String str = "";
        for (int i = 0; i < len; i++) {str += "-";}
        
        System.out.print("#################################\n " 
                + str 
                + "\n|" + name + " " + label + "...|\n " 
                + str
                + "\n\nPlayer Shots:\n");
        ps.draw();
        System.out.print("\nPlayer Board:\n");
        Setup.getPlayerBoard().draw();
        displayAIBoard();
        System.out.print("#################################\n");
    }
    
    public void displayStarSeperator(String label) {
        System.out.print("\n**************************** " + label 
                + " ****************************\n");
    }
    
    public Board getAIBoard() {return ab;}
}