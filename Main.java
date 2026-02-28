import javax.swing.*;
import java.awt.*;

public class Main {
    
    // Reference to the game GUI
    private static GameGUI gameGUI;
    
    public static void main(String[] args) {
        System.out.println("=================================");
        System.out.println("MARVEL ASCENSION - Game Starting");
        System.out.println("=================================");
        
        // Method 1: Initialize the GUI
        initializeGUI();
        
        // Method 2: Customize GUI settings
        customizeGUI();
        
        // Method 3: Show the GUI
        showGUI();
        
        // Method 4: Log startup complete
        startupComplete();
    }
    
    /**
     * Method 1: Creates and initializes the GUI
     */
    public static void initializeGUI() {
        System.out.println("Initializing GUI...");
        
        // Run on Event Dispatch Thread
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    // FIX: Use cross-platform look and feel instead of system
                    // This ensures consistent colors across all operating systems
                    UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                    
                    // Additional UI tweaks for better button visibility
                    UIManager.put("Button.background", null); // Remove default background
                    UIManager.put("Button.foreground", null); // Remove default foreground
                    UIManager.put("Button.font", new Font("Arial", Font.BOLD, 24)); // Set default font
                    
                } catch (Exception e) {
                    System.out.println("Could not set look and feel: " + e.getMessage());
                }
                
                // Create the GUI instance
                gameGUI = new GameGUI();
                System.out.println("GUI created successfully");
            }
        });
        
        // Small delay to ensure initialization
        try { Thread.sleep(500); } catch (InterruptedException e) {}
    }
    
    /**
     * Method 2: Apply custom settings to the GUI
     */
    public static void customizeGUI() {
        System.out.println("Applying custom settings...");
        
        if (gameGUI != null) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    // You can call public methods from GameGUI here
                    System.out.println("Custom settings applied");
                }
            });
        } else {
            System.out.println("GUI not initialized yet");
        }
    }
    
    /**
     * Method 3: Display the GUI
     */
    public static void showGUI() {
        System.out.println("Displaying GUI...");
        
        if (gameGUI != null) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    gameGUI.setVisible(true);
                    System.out.println("GUI is now visible");
                }
            });
        } else {
            System.out.println("Cannot show GUI - not initialized");
        }
    }
    
    /**
     * Method 4: Log startup complete
     */
    public static void startupComplete() {
        System.out.println("=================================");
        System.out.println("Game ready! Use the menu to navigate.");
        System.out.println("=================================");
    }
    
    /**
     * Method 5: Get the GUI instance (for other classes to use)
     */
    public static GameGUI getGameGUI() {
        return gameGUI;
    }
    
    /**
     * Method 6: Close the game properly
     */
    public static void exitGame() {
        System.out.println("Exiting game...");
        
        if (gameGUI != null) {
            gameGUI.dispose();
        }
        
        System.out.println("Game closed. Goodbye!");
        System.exit(0);
    }
    
    /**
     * Method 7: Restart the game
     */
    public static void restartGame() {
        System.out.println("Restarting game...");
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Close old GUI
                if (gameGUI != null) {
                    gameGUI.dispose();
                }
                
                // Create new GUI
                gameGUI = new GameGUI();
                gameGUI.setVisible(true);
                
                System.out.println("✓ Game restarted");
            }
        });
    }
}