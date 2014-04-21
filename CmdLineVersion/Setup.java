import java.io.IOException;

public class Setup {
    private static AI a;
    private static Board playerBoard;
    private static CommandLine cl;
    private static GameLogic gl;
    private static Positioner sp;

    private static boolean inDebug = false;
    private static boolean inGame = false;
    
    private static String outFile;
    
    public static void init() throws IOException {
        playerBoard = new Board();
        a = new AI(playerBoard);
        cl = new CommandLine();
        sp = new Positioner();
        gl = new GameLogic();   
    }
    
    public static Board getPlayerBoard() {return playerBoard;}
    public static void resetPlayerBoard() throws IOException {
        playerBoard = new Board();
    }
    
    public static boolean isInDebug() {return inDebug;}
    public static boolean isInGame() {return inGame;}
    public static void setInDebug(boolean b) {inDebug = b;}
    public static void setInGame(boolean b) {inGame = b;}
    
    public static String getOutFile() {return outFile;}
    public static void setOutFile(String s) {outFile = s;}
    
    public static Positioner getPositioner() {return sp;}
    
    public static CommandLine getCommandLine() {return cl;}
    
    public static GameLogic getGameLogic() {return gl;}
    
    public static AI getAI() {return a;}
    
    public static void main(String[] args) throws IOException {
        init();
        cl.help();
        cl.showCmdPrompt();
    }
}