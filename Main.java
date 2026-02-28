import javax.swing.*;
import java.awt.*;

public class Main {
    
    private static GameGUI gameGUI;
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String CYAN = "\u001B[36m";   /*color color ya */
    public static final String PURPLE = "\u001B[35m";
    
    public static void main(String[] args) {

       
    
        printEpicHeader();
        loadingSequence("\t\t\t" +PURPLE+ "CONNECTING TO S.H.I.E.L.D. DATABASE" + RESET, 3);
        loadingSequence("\t\t\t" + PURPLE + "BYPASSING HYDRA FIREWALLS" + RESET, 4);
        loadingSequence("\t\t\t" + PURPLE + "AUTHENTICATING AVENGERS INITIATIVE" + RESET, 2);
        
        System.out.println(GREEN + "\n\t\t\t[SUCCESS] Access Granted. Welcome, Director." + RESET);
        System.out.println("\t\t\t---------------------------------\n");
        
        // Original Logic
        initializeGUI();
        customizeGUI();
        showGUI();
        startupComplete();
    }

   
    private static void printEpicHeader() {
        System.out.println("\n\n\n\n\n\n");
        System.out.println(CYAN + "\t\t\t#################################################" + RESET);
        System.out.println(CYAN + "\t\t\t#                                               #" + RESET);
        System.out.println(CYAN + "\t\t\t#      M A R V E L    A S C E N S I O N         #" + RESET);
        System.out.println(CYAN + "\t\t\t#           --- by Group Unturned ---           #" + RESET);
        System.out.println(CYAN + "\t\t\t#                                               #" + RESET);
        System.out.println(CYAN + "\t\t\t#################################################" + RESET);
    }

    
    private static void loadingSequence(String message, int dots) {
        System.out.print("> " + message);
        for (int i = 0; i < dots; i++) {
            try {
                Thread.sleep(400); // Pulse delay
                System.out.print(".");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println("\t\t\t[OK]");
    }
    
    public static void initializeGUI() {
        System.out.println(GREEN + "\t\t\t[INFO] Initializing GUI Engine..." + RESET);
        
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                UIManager.put("Button.font", new Font("Impact", Font.PLAIN, 24)); 
            } catch (Exception e) {
                System.out.println(RED + "\t\t\t[ERROR] LookAndFeel Failure: " + e.getMessage() + RESET);
            }
            
            gameGUI = new GameGUI();
            System.out.println(GREEN + "\t\t\t[INFO] UI Components Rendered." + RESET);
        });
        
        try { Thread.sleep(800); } catch (InterruptedException e) {}
    }
    
    public static void customizeGUI() {
        if (gameGUI != null) {
            SwingUtilities.invokeLater(() -> {
                System.out.println(GREEN + "\t\t\t[INFO] Applying Epic Theme Overlays..." + RESET);
            });
        }
    }
    
    public static void showGUI() {
        if (gameGUI != null) {
            SwingUtilities.invokeLater(() -> {
                gameGUI.setVisible(true);
                System.out.println(GREEN + "\t\t\t[INFO] Window Deployed to Desktop." + RESET);
            });
        }
    }
    
    public static void startupComplete() {
        System.out.println(GREEN + "\n\t\t\t=================================" + RESET);
        System.out.println(GREEN + "\t\t\t   MISSION START: ENJOY THE GAME " + RESET);
        System.out.println(GREEN + "\t\t\t=================================\n" + RESET);
    }

    
}
