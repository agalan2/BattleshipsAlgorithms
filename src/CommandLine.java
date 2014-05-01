import java.awt.Point;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Scanner;

public class CommandLine {
    public static final Scanner s = new Scanner(System.in);
    private int runIterations = 20;
   
    private void displayFolder(String str) throws IOException {
        String[] names = new File("testCases/" + str + "/").list();
        int len = names.length;
        for (int i = 0; i < len; i++) {
            Setup.init();
            System.out.print("\n(" + i + ")\n");
            load("testCases/" + str + "/" + names[i], true);
        }
    }
    
    private void display() throws IOException {
        try {
            switch (strings[1]) {
                case "-c":
                    displayFolder("cluster"); break;
                case "-n":
                    displayFolder("nonCluster"); break;
                default:
                    Setup.getPlayerBoard().draw();
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            Setup.getPlayerBoard().draw();
        }
    }
    
    public void help() throws IOException {
        System.out.print("**************************************************\n"
                + " ------------\n"
                + "|Command List|\n"
                + " ------------\n"
                + "ai <option>\n"
                + "     -r: random volley\n"
                + "     -h: hunt/target\n"
                + "     -p: hunt/target with parity\n"
                + "bug: run bug tracker\n"
                + "continue (c): continue ship placement\n"
                + "debug <option>\n"
                + "     no option: do not write to file\n"
                + "     -w: write to file\n"
                + "display (d) <option> \n"
                + "     no option: display player board\n"
                + "     -c: display all saves cluster test cases\n"
                + "     -n: display all saves non-cluster test cases\n"
                + "help (h): display help\n"
                + "load <fileName>\n"
                + "     no fileName: promt for file to load\n"
                + "     fileName given: load the spacified save file\n"
                + "play (p) <load command>\n"
                + "     no load command: play game with normal setup\n"
                + "     load command: play game with saved arangement\n"
                + "quit (q): quit program\n"
                + "remove (r): remaove a ship\n"
                + "reset: reset board\n"
                + "runcases <option>\n"
                + "     -r: random volley\n"
                + "     -h: hunt/target\n"
                + "     -p: hunt/target with parity\n"
                + "     ***A 3rd int argument can be given to specify the\n"
                + "        number of times to test each case***\n"
                + "save (s) <option>\n"
                + "     no option: save placement to default saves folder\n"
                + "     -c: save to 'cluster' testcase folder\n"
                + "     -n: save to 'nonCluster' testcase folder\n"
                + "setup <option>\n"
                + "     no option: setup player board manually\n"
                + "     -r: use AI random placement\n"
                + "setout: set output file\n"
                + "showout: show output file name\n"
                + "To set up the board use 'setup'.\n "
                + " Use 'load' to load a saved ship configuration.\n"
                + "**************************************************\n\n");
    }

    private String getDebugString() {
        return "Current variable values: \n"
                + "     outFile: " + Setup.getOutFile()
                + "\n\n     positioned: "
                + Setup.getPositioner().getPositioned()
                + "\n\n     stackSize: "
                + Setup.getPositioner().getStackSize() + "\n";
    }
    
    private void debug(String str) throws IOException {
        System.out.print(getDebugString());
        for (int i = 0; i < Setup.getPositioner().getStackSize(); i++) {
            System.out.print("     stack[" + i + "]: "
                    + Setup.getPositioner().getFromStack(i) + "\n");
        }
        if (str.equals("-w")) {
            try (FileWriter fw = new FileWriter(new File("debug/DebugData_" 
                    + new Date().toString() + ".txt"))) {
                fw.write(getDebugString());
                for (int i=0; i < Setup.getPositioner().getStackSize(); i++) {
                    fw.write("     stack[" + i + "]: "
                            + Setup.getPositioner().getFromStack(i) + "\n");
                }
            }
        } 
    }

    private void remove() throws IOException {
        System.out.print("Ship index guide:\n"
                + "     Carrier: 0\n"
                + "     Battleship: 1\n"
                + "     Submarine: 2\n"
                + "     Destroyer: 3\n"
                + "     Patrol Boat: 4\n"
                + "Enter index of ship to remove: ");
        String str = s.next();
        try {
            int i = Integer.parseInt(str);
            if (0 <= i && 5 > i) {
                if (0 != Setup.getPlayerBoard().getShip(i).locationIndex) {
                    //Setup.getPositioner().pushToStack(i);
                    Setup.getPositioner().decrementPositioned();
                    Setup.getPlayerBoard().removeShip(i);
                    Setup.getPlayerBoard().draw();
                    System.out.print("Use 'continue' to replace ship: \n");
                } else {
                    System.out.print("Ship not yet placed.\n");
                    remove();
                }
            } else {
                System.out.print("Index out of bounds.\n");
                remove();
            }
        } catch (NumberFormatException e) {
            if (!str.equals("c") && !str.equals("cancel")) {
                System.out.print("Invalid input.\n");
            }
        } 
    }

    public void reset() throws IOException {
        System.out.print("Do you want to reset? (y/n) ");
        switch (s.next()) {
            case "yes":
            case "y":
                Setup.init();
                break;
        }
    }

    private int shotCount = 0;
    private void runAI(String str, boolean draw) throws IOException {
        if (5 == Setup.getPositioner().getPositioned()) {
            switch (str) {
                case "-r":
                    Setup.getAI().randomVolley(); break;
                case "-h":
                    Setup.getAI().hunt(); break;
                case "-p":
                    Setup.getAI().huntWithParity(); break;
                case "-d":
                    Setup.getAI().dynamicHuntWithParity(); break;
                case "-s":
                    Setup.getAI().smartHuntWithParity(); break;
                default:
                    System.out.print("Invalid algorithm.\n");
            }
            shotCount = Setup.getPlayerBoard().getShotCount();
            
            if (draw) {
                Setup.getPlayerBoard().draw();
            }
            
            Setup.init();
        } else {
            System.out.print("Cannot run AI at this time.\n");
        }
    }

    private String[] strings;
    public void showCmdPrompt() throws IOException {
        if (!Setup.isInGame()) {
            System.out.print("cmd> ");
            strings = s.nextLine().split(" ");
            parse(strings[0]);
        }
    }

    public String showInGameCmdPrompt() {
        System.out.print("GAME~cmd> ");
        return s.next();
    }

    public void showAllSaves() {
        String[] list = new File("saves/").list();
        System.out.print("\nSaves:\n");
        for (String list1 : list) {
            System.out.println("    " + list1);
        } System.out.println();
    }
    
    private void commitSave(String str) throws IOException {
        int num = new File("testCases/" + str + "/").list().length;
        File f = new File("testCases/" + str + "/" + str + "-" + num + ".tc");
        try (FileWriter fw = new FileWriter(f)) {
            for (int i = 0; i < 5; i++) {
                Point[] pt = Setup.getPlayerBoard()
                        .getShip(i)
                        .getLocations();
                fw.write(Setup.getPlayerBoard().getShip(i).getName()
                        + "\n" + pt[0].x + " " + pt[0].y + "\n"
                        + pt[1].x + " " + pt[1].y + "\n");
            }
        } 
    }

    public void save() throws IOException {
        System.out.print("Enter file name: ");
        File f = new File("saves/" + s.next());
        if (!f.exists()) {
            try (FileWriter fw = new FileWriter(f)) {
                for (int i = 0; i < 5; i++) {
                    Point[] pt = Setup.getPlayerBoard()
                            .getShip(i)
                            .getLocations();
                    fw.write(Setup.getPlayerBoard().getShip(i).getName()
                            + "\n" + pt[0].x + " " + pt[0].y + "\n"
                            + pt[1].x + " " + pt[1].y + "\n");
                }
            }
        } else {
            System.out.print("File already exists.\n");
        } 
    }

    public void save(String str) throws IOException {
        switch (str) {
            case "-c":
                commitSave("cluster"); break;
            case "-n":
                commitSave("nonCluster"); break;
        }
    }

    public void load(String str, boolean draw) throws IOException {
        try (Scanner sc = new Scanner(new File(str))) {
            for (int i = 0; i < 5; i++) {
                sc.next();
                int x = sc.nextInt();
                int y = sc.nextInt();
                if (x == sc.nextInt()) {
                    Setup.getPlayerBoard().placeShip(x, y, "h",
                            Setup.getPlayerBoard().getShip(i));
                } else {
                    Setup.getPlayerBoard().placeShip(x, y, "v",
                            Setup.getPlayerBoard().getShip(i));
                } sc.next();
            }
            Setup.getPositioner().setStackSize(0);
            Setup.getPositioner().setPositioned(5);
            
            if (draw) {
                Setup.getPlayerBoard().draw();
            }
        } 
    }

    public void parse(String str) throws IOException {
        switch (str) {
            case "+r":
                Setup.getAI().easyShot();
                Setup.getPlayerBoard().draw();
                showCmdPrompt(); break;
            case ">":
                Setup.getAI().mediumShot();
                Setup.getPlayerBoard().draw();
                showCmdPrompt(); break;
            case "i":
                Setup.getAI().hardShot();
                Setup.getPlayerBoard().draw();
                showCmdPrompt(); break;
            case "o":
                Setup.getAI().extremeShot();
                Setup.getPlayerBoard().draw();
                showCmdPrompt(); break;
            
            case "ai":
                try {
                    runAI(strings[1], true);
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.print("Enter algorithm to run: ");
                    runAI(s.next(), true);
                } showCmdPrompt(); 
                break;
            case "bug":
                CSBug.run();
                showCmdPrompt(); break;
            case "continue":
            case "c":
                if (5 > Setup.getPositioner().getPositioned()) {
                    Setup.getPositioner().position(Setup.getPlayerBoard());
                } else {
                    System.out.print("All ships are placed.\n");
                } showCmdPrompt();
                break;
            case "debug":
                try {
                    debug(strings[1]);
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.print("Debug info not saved...\n");
                    debug("null");
                } showCmdPrompt();
                break;
            case "display":
            case "d":
                display(); 
                showCmdPrompt(); break;
            case "help":
            case "h":
                help(); 
                showCmdPrompt(); break;
            case "load":
            case "l":
                try {
                    load("saves/" + strings[1], true);
                } catch (ArrayIndexOutOfBoundsException e) {
                    showAllSaves();
                    System.out.print("Enter file to load: ");
                    load(s.next(), true);
                } showCmdPrompt();
                break;
            case "play":
            case "p":
                Setup.setInGame(true);
                try {
                    if (strings[1].equals("-l")) {
                        Setup.getGameLogic().initGame("-l");
                        try {
                            load(strings[2], true);
                            Setup.getGameLogic().playGame("null");
                        } catch (ArrayIndexOutOfBoundsException e) {
                            showAllSaves();
                            System.out.print("Enter file to load: ");
                            load(s.next(), true);
                            Setup.getGameLogic().playGame("null");
                        }
                    } Setup.getGameLogic().initGame(strings[1]);
                } catch (ArrayIndexOutOfBoundsException e) {
                    Setup.getGameLogic().initGame("null");
                } break;
            case "quit":
            case "q":
                System.out.print("Quit? (y/n) ");
                switch (CommandLine.s.next()) {
                    case "yes": 
                    case "y":
                        System.out.print("Quitting Execution...\n");
                        System.exit(0); break;
                    default: 
                        showCmdPrompt();
                    } break;
            case "remove":
            case "r":
                remove(); 
                showCmdPrompt(); break;
            case "reset":
                reset(); 
                showCmdPrompt(); break;
            case "save":
            case "s":
                try {
                    save(strings[1]);
                } catch (ArrayIndexOutOfBoundsException e) {
                    save();
                } showCmdPrompt();
                break;
            case "setup":
                try {
                    if (strings[1].equals("-r")) {
                        Setup.init();
                        Setup.getAI().randomPlacement(Setup.getPlayerBoard());
                    } else {
                        Setup.init();
                        Setup.getPositioner().position(Setup.getPlayerBoard());
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    Setup.getPositioner().position(Setup.getPlayerBoard());
                } showCmdPrompt();
                break;
            case "runcases":
                try {
                    runIterations = Integer.parseInt(strings[2]);
                } catch (ArrayIndexOutOfBoundsException
                        | NumberFormatException e) {
                }
                
                String alg;
                
                float rootAvg = 0;
                float clusterAvg = 0;
                float nclusterAvg = 0;
                
                int totShots = 0;
                
                String help = "Select an algorithm to use:\n"
                        + "     -r: random volley\n"
                        + "     -h: hunt/target\n"
                        + "     -p: h/t with parity\n"
                        + "     -d: dynamic h/t with parity\n"
                        + "     -s: smart h/t with parity\n"
                        + "Enter selection: ";
                try {
                    alg = strings[1];
                    if (!alg.equals("-r") 
                            && !alg.equals("-h") 
                            && !alg.equals("-p")
                            && !alg.equals("-d")
                            && !alg.equals("-s")) {
                        System.out.print(help);
                        alg = s.next();
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.print(help);
                    alg = s.next();
                }
                
                double lng = new File("testCases/runData/data" 
                        + alg + "/longData/").list().length;
                
                File longFormat = new File("testCases/runData/data" + alg 
                        + "/longData/"  + (int)lng + "-LongFormat.txt");
                File shortFormat = new File("testCases/runData/data" + alg 
                        + "/shortData/" + (int)lng + "-ShortFormat.txt");
                File logFile = new File("testCases/runData/Log" + alg + ".txt");
                
                FileWriter lfw = new FileWriter(longFormat);
                FileWriter sfw = new FileWriter(shortFormat);
                FileWriter logfw = new FileWriter(logFile, true);
                
                lfw.write(" ------------------ \n");
                sfw.write(" ------------------ \n");
                
                switch (alg) {
                    case "-r":
                        lfw.write("|  Random Volley  |\n"); 
                        sfw.write("|  Random Volley  |\n"); break;
                    case "-h":
                        lfw.write("|   Hunt/Target   |\n"); 
                        sfw.write("|   Hunt/Target   |\n"); break;
                    case "-p":
                        lfw.write("| H/T With parity |\n"); 
                        sfw.write("| H/T With parity |\n"); break;
                    case "-d":
                        lfw.write("| Dynamic H/T With parity |\n"); 
                        sfw.write("| Dynamic H/T With parity |\n"); break;
                } 
                
                lfw.write(" ------------------ \n\n");
                sfw.write(" ------------------ \n\n");

                String[] clusters = new File("testCases/cluster/").list();
                double c = clusters.length;

                lfw.write(" ---------- \n" 
                        + "| Clusters |\n" 
                        + " ---------- \n");
                sfw.write(" ---------- \n" 
                        + "| Clusters |\n" 
                        + " ---------- \n");
                
                for (int i = 0; i < c; i++) {
                    load("testCases/cluster/" + clusters[i], false);
                    
                    lfw.write("--------------------------------\n"
                            + "| (" + i + ")                          |\n"
                            + "| |-----------------------|    |\n"
                            + "| |");
                    sfw.write("(" + i + ")\n");
                    
                    for (int col = 0; 10 > col; col++) {
                        lfw.write(Setup.getPlayerBoard().getCell(0, col) + " ");
                    } 
                    
                    lfw.write(Setup.getPlayerBoard().getCell(0, 10) 
                            + " |    |\n");
                    
                    for (int rows = 1; 11 > rows; rows++) {
                        lfw.write("| |");
                        
                        for (int columns = 0; 11 > columns; columns++) {
                            lfw.write(Setup.getPlayerBoard()
                                           .getCell(rows, columns) + " ");
                        } 
                        
                        lfw.write(" |    |\n");
                    } 
                    
                    lfw.write("| |-----------------------|    |\n"
                            + "|  _____________               |\n"
                            + "| | Run | Shots |              |\n"
                            + "| |-------------|              |\n"
                            + "|");
                    
                    float perBoardShots = 0;
                    for (int j = 0; j < runIterations; j++) {
                        load("testCases/cluster/" + clusters[i], false);
                        
                        runAI(alg, false);
                        
                        if (9 > j) {
                            lfw.write(" |   " + (j+1) + " |  ");
                        } else if (99 > j) {
                            lfw.write(" |  " + (j+1) + " |  ");
                        } else {
                            lfw.write(" | " + (j+1) + " |  ");
                        }
                        
                        if (100 > shotCount) {
                            lfw.write(shotCount + "   |              |\n|");
                        } else {
                            lfw.write(100 +  "  |              |\n|");
                        }
                        
                        perBoardShots += shotCount;
                        totShots += shotCount;
                    } 
                    
                    float boardAverage = (perBoardShots / runIterations);
                    
                    if (0 != clusterAvg) {
                        clusterAvg = (clusterAvg + boardAverage)/2;
                    } else {
                        clusterAvg = boardAverage;
                    }
                    
                    rootAvg = clusterAvg;
                    
                    lfw.write(" |-------------|              |\n"
                            + "|                              |\n"
                            + "| Total Shots for board: " 
                            + (int)perBoardShots + "  |\n"
                            + "| --------------------         |\n"
                            + "| Average shots (Board): "
                            + boardAverage + " |\n"
                            + "| --------------------         |\n"
                            + "| Avg shots (Clusters): "
                            + clusterAvg + " |\n"
                            + "| --------------------         |\n"
                            + "| Average shots (Root): "
                            + rootAvg + " |\n");
                    sfw.write("Total Shots for board: " 
                            + (int)perBoardShots
                            + "\nAverage shots (Board): "
                            + boardAverage
                            + "\nAvg shots (Clusters): "
                            + clusterAvg
                            + "\nAverage shots (Root): "
                            + rootAvg
                            + "\n--------------------\n\n");
                } 
                
                lfw.write("|------------------------------|\n"
                        + "\nCluster shot average: " 
                        + totShots/(c*runIterations) + "\n");
                
                sfw.write("Cluster shot average: " 
                        + totShots/(c*runIterations) + "\n");

                int totNCShots = 0;
                
                String[] nonClusters 
                        = new File("testCases/nonCluster/").list();
                double nc = nonClusters.length;
                
                lfw.write(" -------------- \n" 
                        + "| Non Clusters |\n" 
                        + " -------------- \n");
                sfw.write("\n -------------- \n" 
                        + "| Non Clusters |\n" 
                        + " -------------- \n");
                
                for (int i = 0; i < nc; i++) {
                    load("testCases/nonCluster/" + nonClusters[i], false);
                    
                    lfw.write("|------------------------------|\n"
                            + "| (" + i + ")                          |\n"
                            + "| |-----------------------|    |\n| |");
                    sfw.write("(" + i + ")\n");
                    
                    for (int col = 0; 10 > col; col++) {
                        lfw.write(Setup.getPlayerBoard().getCell(0, col) + " ");
                    } 
                    
                    lfw.write(Setup.getPlayerBoard().getCell(0, 10) 
                            + " |    |\n");
                    
                    for (int rows = 1; 11 > rows; rows++) {
                        lfw.write("| |");
                        
                        for (int columns = 0; 11 > columns; columns++) {
                            lfw.write(Setup.getPlayerBoard()
                                           .getCell(rows, columns) + " ");
                        } 
                        
                        lfw.write(" |    |\n");
                    } 
                    
                    lfw.write("| |-----------------------|    |\n"
                            + "|  _____________               |\n"
                            + "| | Run | Shots |              |\n"
                            + "| |-------------|              |\n"
                            + "|");
                    
                    float perBoardShots = 0;
                    for (int j = 0; j < runIterations; j++) {
                        load("testCases/nonCluster/" + nonClusters[i], false);
                        
                        runAI(alg, false);
                        
                        if (9 > j) {
                            lfw.write(" |   " + (j+1) + " |  ");
                        } else if (99 > j) {
                            lfw.write(" |  " + (j+1) + " |  ");
                        } else {
                            lfw.write(" | " + (j+1) + " |  ");
                        }
                        
                        if (100 > shotCount) {
                            lfw.write(shotCount + "   |              |\n|");
                        } else {
                            lfw.write(100 + "  |              |\n|");
                        }
                        
                        perBoardShots += shotCount;
                        totNCShots += shotCount;
                        totShots += shotCount;
                    } 
                    
                    float boardAverage = (perBoardShots / runIterations);
                    
                    if (0 != nclusterAvg) {
                        nclusterAvg = (nclusterAvg + boardAverage)/2;
                    } else {
                        nclusterAvg = boardAverage;
                    }
                    
                    lfw.write(" |-------------|              |\n"
                            + "|                              |\n"
                            + "| Total Shots for board: " 
                            + (int)perBoardShots + "  |\n"
                            + "| --------------------         |\n"
                            + "| Average shots (Board): "
                            + boardAverage + " |\n"
                            + "| --------------------         |\n"
                            + "| Avg shots (NonClustr): "
                            + nclusterAvg + " |\n"
                            + "| --------------------         |\n"
                            + "| Average shots (Root): "
                            + rootAvg + " |\n");
                    sfw.write("Total Shots for board: " 
                            + (int)perBoardShots
                            + "\nAverage shots (Board): "
                            + boardAverage
                            + "\nAvg shots (Non Clusters): "
                            + nclusterAvg
                            + "\nAverage shots (Root): "
                            + rootAvg
                            + "\n--------------------\n\n");
                } 
                
                double avg = totShots / ( (c + nc)*runIterations);
                
                lfw.write("|------------------------------|\n\n"
                        + "Non Cluster shot average: " 
                        + totNCShots / (nc*runIterations)
                        + "\nCluster shot average: " + clusterAvg
                        + "\n\nRuns Completed: " 
                        + (int)((c + nc) * runIterations)
                        + "\nAverage shot count: " + avg);
                
                sfw.write("Non Cluster shot average: " 
                        + totNCShots / (nc*runIterations)
                        + "\nCluster shot average: " + clusterAvg
                        + "\nRuns Completed: " 
                        + (int)((c + nc) * runIterations)
                        + "\nAverage shot count: " + avg);
                
                logfw.write("\nCluster shot average: " + clusterAvg
                        + "\nNon Cluster shot average: " 
                        + totNCShots / (nc*runIterations)
                        + "\nRuns Completed: " 
                        + (int)((c + nc) * runIterations)
                        + "\nAverage shot count: " + avg
                        + "\n--------------------------------------\n");
                
                lfw.close();
                sfw.close();
                logfw.close();
                
                showCmdPrompt();
                break;
            case "setout":
                System.out.print("Enter output file name: ");
                Setup.setOutFile(s.next());
                showCmdPrompt(); 
                break;
            case "showout":
                System.out.println(Setup.getOutFile()); 
                showCmdPrompt(); break;
            default:
                showCmdPrompt(); break;
        }
    }
}