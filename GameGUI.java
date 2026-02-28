import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class GameGUI extends JFrame implements ActionListener {
    
    // Your existing variables
    private JButton startButton;
    private JButton settingsButton;
    private JButton helpButton;
    private JButton aboutButton;
    private JButton exitButton;
    private JLabel titleLabel;
    private JLabel versionLabel;
    private JPanel mainMenuPanel;
    private JPanel settingsPanel;
    private JPanel helpPanel;
    private JPanel aboutPanel;
    private String currentPanel = "main";
    
    public GameGUI() {
        // Your existing constructor code
        setTitle("MARVEL ASCENSION - GAME PROTOTYPE");
        setSize(1024, 800);
        setMaximumSize(new Dimension(1280, 1024));
        setMinimumSize(new Dimension(800, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);

        // Icon for game GUI
        try {
            ImageIcon logo = new ImageIcon("src/main/resources/logo.png");
            setIconImage(logo.getImage());
        } catch (Exception ex) {
            System.out.println("Logo not found - continuing without icon");
        }
        
        setLayout(new CardLayout());
        
        createMainMenuPanel();
        createSettingsPanel();
        createHelpPanel();
        createAboutPanel();
        
        add(mainMenuPanel, "main");
        add(settingsPanel, "settings");
        add(helpPanel, "help");
        add(aboutPanel, "about");
        
        showPanel("main");
    }
    
    // === NEW PUBLIC METHODS FOR MAIN TO CONTROL ===
    
    /**
     * Public method to set a custom title
     */
    public void setCustomTitle(String newTitle) {
        setTitle(newTitle);
        if (titleLabel != null) {
            titleLabel.setText(newTitle);
        }
    }
    
    /**
     * Public method to change background color
     */
    public void setBackgroundColor(Color color) {
        if (mainMenuPanel != null) {
            mainMenuPanel.setBackground(color);
        }
    }
    
    /**
     * Public method to get current panel
     */
    public String getCurrentPanel() {
        return currentPanel;
    }
    
    /**
     * Public method to navigate to a specific panel
     */
    public void navigateTo(String panelName) {
        showPanel(panelName);
    }
    
    /**
     * Public method to update version label
     */
    public void setVersion(String version) {
        if (versionLabel != null) {
            versionLabel.setText("Version " + version);
        }
    }
    
    // === YOUR EXISTING METHODS (with fixes) ===
    
    private void createMainMenuPanel() {
        mainMenuPanel = new JPanel();
        mainMenuPanel.setLayout(new GridBagLayout());
        mainMenuPanel.setBackground(new Color(30, 30, 30));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        titleLabel = new JLabel("MARVEL ASCENSION", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 48));
        titleLabel.setForeground(new Color(255, 215, 0));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainMenuPanel.add(titleLabel, gbc);
        
        // FIXED: Using improved button creation method
        startButton = createEnhancedButton("START GAME", new Color(0, 150, 0));
        settingsButton = createEnhancedButton("SETTINGS", new Color(150, 150, 0));
        helpButton = createEnhancedButton("HELP", new Color(0, 100, 200));
        aboutButton = createEnhancedButton("ABOUT", new Color(150, 0, 150));
        exitButton = createEnhancedButton("EXIT", new Color(150, 0, 0));
        
        startButton.addActionListener(this);
        settingsButton.addActionListener(this);
        helpButton.addActionListener(this);
        aboutButton.addActionListener(this);
        exitButton.addActionListener(this);
        
        gbc.gridwidth = 2;
        gbc.gridy = 1;
        mainMenuPanel.add(startButton, gbc);
        
        gbc.gridy = 2;
        mainMenuPanel.add(settingsButton, gbc);
        
        gbc.gridy = 3;
        mainMenuPanel.add(helpButton, gbc);
        
        gbc.gridy = 4;
        mainMenuPanel.add(aboutButton, gbc);
        
        gbc.gridy = 5;
        mainMenuPanel.add(exitButton, gbc);
        
        versionLabel = new JLabel("Version 0.1 - Prototype");
        versionLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        versionLabel.setForeground(Color.GRAY);
        
        gbc.gridy = 6;
        gbc.insets = new Insets(20, 10, 10, 10);
        mainMenuPanel.add(versionLabel, gbc);
    }
    
    /**
     * FIXED: Enhanced button creation method with guaranteed visibility
     */
    private JButton createEnhancedButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        
        // Set basic properties
        button.setFont(new Font("Arial", Font.BOLD, 24));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        
        // CRITICAL FIX: Ensure colors are applied
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(true);
        
        // Add a nice border for better visibility
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.WHITE, bgColor.darker()),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        
        // Set size
        button.setPreferredSize(new Dimension(250, 60));
        
        // FIX: Override UI to prevent look-and-feel from changing colors
        button.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        
        // Add hover effect for better user experience
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.brighter());
                button.setForeground(Color.YELLOW); // Highlight text on hover
            }
            
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
                button.setForeground(Color.WHITE);
            }
        });
        
        return button;
    }
    
    private void createSettingsPanel() {
        settingsPanel = new JPanel();
        settingsPanel.setLayout(new BorderLayout());
        settingsPanel.setBackground(new Color(40, 40, 40));
        
        JLabel title = new JLabel("SETTINGS", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 36));
        title.setForeground(Color.YELLOW);
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new GridLayout(4, 1, 10, 10));
        optionsPanel.setBackground(new Color(40, 40, 40));
        optionsPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        
        JPanel volumePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        volumePanel.setBackground(new Color(40, 40, 40));
        JLabel volumeLabel = new JLabel("Volume: ");
        volumeLabel.setForeground(Color.WHITE);
        volumeLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        JSlider volumeSlider = new JSlider(0, 100, 50);
        volumeSlider.setBackground(new Color(40, 40, 40));
        volumeSlider.setForeground(Color.WHITE);
        volumePanel.add(volumeLabel);
        volumePanel.add(volumeSlider);
        
        JPanel diffPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        diffPanel.setBackground(new Color(40, 40, 40));
        JLabel diffLabel = new JLabel("Difficulty: ");
        diffLabel.setForeground(Color.WHITE);
        diffLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        String[] difficulties = {"Easy", "Normal", "Hard"};
        JComboBox<String> diffBox = new JComboBox<>(difficulties);
        diffBox.setBackground(Color.WHITE);
        diffPanel.add(diffLabel);
        diffPanel.add(diffBox);
        
        JPanel fsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fsPanel.setBackground(new Color(40, 40, 40));

       


        JCheckBox fsCheck = new JCheckBox("Fullscreen Mode");

        
        /* condition of selected checkbox to work fullscreen */
        /* di man mo gana yaaaaaaaaaaaaaaaaaaaaaaaaaaa  */
        JFrame frame = new JFrame("Display Settings");

        fsCheck.addItemListener(new ItemListener() {
       @Override
       public void itemStateChanged(ItemEvent e) {
       
        dispose(); 

        if (fsCheck.isSelected()) {
            setUndecorated(true); 
            setExtendedState(JFrame.MAXIMIZED_BOTH);
        } else {
            setUndecorated(false); 
            setExtendedState(JFrame.NORMAL);
            setSize(1024, 800); 
            setLocationRelativeTo(null); 
        }

       
        setVisible(true);
        revalidate();
        repaint();
        }
     });




        fsCheck.setBackground(new Color(40, 40, 40));
        fsCheck.setForeground(Color.WHITE);
        fsCheck.setFont(new Font("Arial", Font.PLAIN, 16));
        fsPanel.add(fsCheck);
        
        // FIXED: Using enhanced button for back button too
        JButton backButton = createEnhancedButton("BACK TO MENU", new Color(100, 100, 100));
        backButton.setFont(new Font("Arial", Font.BOLD, 18));
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showPanel("main");
            }
        });
        
        optionsPanel.add(volumePanel);
        optionsPanel.add(diffPanel);
        optionsPanel.add(fsPanel);
        
        settingsPanel.add(title, BorderLayout.NORTH);
        settingsPanel.add(optionsPanel, BorderLayout.CENTER);
        settingsPanel.add(backButton, BorderLayout.SOUTH);
    }
    
    private void createHelpPanel() {
        helpPanel = new JPanel();
        helpPanel.setLayout(new BorderLayout());
        helpPanel.setBackground(new Color(40, 40, 40));
        
        JLabel title = new JLabel("HELP", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 36));
        title.setForeground(Color.GREEN);
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        JTextArea helpText = new JTextArea();
        helpText.setEditable(false);
        helpText.setBackground(new Color(50, 50, 50));
        helpText.setForeground(Color.WHITE);
        helpText.setFont(new Font("Monospaced", Font.PLAIN, 16));
        helpText.setText(
            "HOW TO PLAY:\n\n" +
            "This is a game prototype.\n\n" +
            "Controls:\n" +
            "• Use mouse to navigate menus\n" +
            "• Click buttons to select options\n\n" +
            "Game Features:\n" +
            "• Main menu with multiple options\n" +
            "• Settings panel\n" +
            "• Help section\n" +
            "• About section\n" +
            "• Easy to extend with game logic\n\n" +
            "Press BACK to return to main menu."
        );
        helpText.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // FIXED: Using enhanced button
        JButton backButton = createEnhancedButton("BACK TO MENU", new Color(100, 100, 100));
        backButton.setFont(new Font("Arial", Font.BOLD, 18));
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showPanel("main");
            }
        });
        
        helpPanel.add(title, BorderLayout.NORTH);
        helpPanel.add(new JScrollPane(helpText), BorderLayout.CENTER);
        helpPanel.add(backButton, BorderLayout.SOUTH);
    }
    
    private void createAboutPanel() {
        aboutPanel = new JPanel();
        aboutPanel.setLayout(new BorderLayout());
        aboutPanel.setBackground(new Color(40, 40, 40));
        
        JLabel title = new JLabel("ABOUT", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 36));
        title.setForeground(new Color(255, 69, 0));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        JTextArea aboutText = new JTextArea();
        aboutText.setEditable(false);
        aboutText.setBackground(new Color(50, 50, 50));
        aboutText.setForeground(Color.WHITE);
        aboutText.setFont(new Font("Monospaced", Font.PLAIN, 16));
        aboutText.setText(
            "MARVEL ASCENSION\n\n" +
            "Version: 0.1\n\n" +
            "Created by: Group Unturned\n\n" +
            "Description:\n" +
            "This is a starter GUI for OOP2.\n" +
            "It includes a main menu with:\n" +
            "• Start Game (placeholder)\n" +
            "• Settings\n" +
            "• Help\n" +
            "• About\n" +
            "• Exit\n\n" +
            "Extend this with your game logic!"
        );
        aboutText.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // FIXED: Using enhanced button
        JButton backButton = createEnhancedButton("BACK TO MENU", new Color(100, 100, 100));
        backButton.setFont(new Font("Arial", Font.BOLD, 18));
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showPanel("main");
            }
        });
        
        aboutPanel.add(title, BorderLayout.NORTH);
        aboutPanel.add(new JScrollPane(aboutText), BorderLayout.CENTER);
        aboutPanel.add(backButton, BorderLayout.SOUTH);
    }
    
    private void showPanel(String panelName) {
        CardLayout cl = (CardLayout) getContentPane().getLayout();
        cl.show(getContentPane(), panelName);
        currentPanel = panelName;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == startButton) {
            JOptionPane.showMessageDialog(this, 
                "START GAME - Add your game here!\n\nThis is where the main game will launch.", 
                "Start Game", 
                JOptionPane.INFORMATION_MESSAGE);
                
        } else if (e.getSource() == settingsButton) {
            showPanel("settings");
            
        } else if (e.getSource() == helpButton) {
            showPanel("help");
            
        } else if (e.getSource() == aboutButton) {
            showPanel("about");
            
        } else if (e.getSource() == exitButton) {
            int choice = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to exit?", 
                "Exit Game", 
                JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        }
    }
}