import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameGUI extends JFrame implements ActionListener {
    
    private JButton startButton, settingsButton, helpButton, aboutButton, exitButton;
    private JLabel titleLabel, versionLabel;
    private JPanel mainMenuPanel, settingsPanel, helpPanel, aboutPanel;
    private CharacterSelector selectorPanel; 
    private String currentPanel = "main";
    
    public GameGUI() {
        setTitle("MARVEL ASCENSION - GAME PROTOTYPE");
        setSize(1024, 800);
        setMinimumSize(new Dimension(800, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Icon Handling
        try {
            ImageIcon logo = new ImageIcon("Resources/icon.jpg");
            setIconImage(logo.getImage());
        } catch (Exception ex) {
            System.out.println("Logo not found - continuing without icon");
        }
        
        // Using CardLayout to swap between different game screens
        setLayout(new CardLayout());
        
        // Initialize Panels
        createMainMenuPanel();
        createSettingsPanel();
        createHelpPanel();
        createAboutPanel();
        selectorPanel = new CharacterSelector(this); // Pass 'this' so it can call navigateTo
        
        // Add to CardLayout
        add(mainMenuPanel, "main");
        add(settingsPanel, "settings");
        add(helpPanel, "help");
        add(aboutPanel, "about");
        add(selectorPanel, "selector"); // The hero selection screen
        
        showPanel("main");
    }
    
    // Global navigation method
    public void navigateTo(String panelName) {
        showPanel(panelName);
    }

    private void showPanel(String panelName) {
        CardLayout cl = (CardLayout) getContentPane().getLayout();
        cl.show(getContentPane(), panelName);
        currentPanel = panelName;
    }
    
    private void createMainMenuPanel() {
        mainMenuPanel = new JPanel(new GridBagLayout());
        mainMenuPanel.setBackground(new Color(30, 30, 30));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        titleLabel = new JLabel("Marvel Ascension", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 48));
        titleLabel.setForeground(new Color(255, 215, 0));
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        mainMenuPanel.add(titleLabel, gbc);
        
        startButton = createEnhancedButton("START GAME", new Color(0, 150, 0));
        settingsButton = createEnhancedButton("SETTINGS", new Color(150, 150, 0));
        helpButton = createEnhancedButton("HELP", new Color(0, 100, 200));
        aboutButton = createEnhancedButton("ABOUT", new Color(150, 0, 150));
        exitButton = createEnhancedButton("EXIT", new Color(150, 0, 0));
        
        JButton[] buttons = {startButton, settingsButton, helpButton, aboutButton, exitButton};
        for(int i = 0; i < buttons.length; i++) {
            buttons[i].addActionListener(this);
            gbc.gridy = i + 1;
            mainMenuPanel.add(buttons[i], gbc);
        }
        
        versionLabel = new JLabel("Version 0.2 - Prototype");
        versionLabel.setForeground(Color.GRAY);
        gbc.gridy = 6;
        mainMenuPanel.add(versionLabel, gbc);
    }

    private JButton createEnhancedButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 24));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.WHITE, bgColor.darker()),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { button.setBackground(bgColor.brighter()); }
            public void mouseExited(java.awt.event.MouseEvent e) { button.setBackground(bgColor); }
        });
        return button;
    }

    private void createSettingsPanel() {
        settingsPanel = new JPanel(new BorderLayout());
        settingsPanel.setBackground(new Color(40, 40, 40));
        JLabel title = new JLabel("SETTINGS", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 36));
        title.setForeground(Color.YELLOW);
        
        JCheckBox fsCheck = new JCheckBox("Fullscreen Mode");
        fsCheck.setFont(new Font("Arial", Font.PLAIN, 18));
        fsCheck.setForeground(Color.WHITE);
        fsCheck.setOpaque(false);
        fsCheck.addItemListener(e -> {
            dispose();
            if (fsCheck.isSelected()) {
                setUndecorated(true); setExtendedState(MAXIMIZED_BOTH);
            } else {
                setUndecorated(false); setSize(1024, 800); setLocationRelativeTo(null);
            }
            setVisible(true);
        });

        JButton back = createEnhancedButton("BACK TO MENU", Color.GRAY);
        back.addActionListener(e -> showPanel("main"));

        settingsPanel.add(title, BorderLayout.NORTH);
        settingsPanel.add(fsCheck, BorderLayout.CENTER);
        settingsPanel.add(back, BorderLayout.SOUTH);
    }

    private void createHelpPanel() {
        helpPanel = new JPanel(new BorderLayout());
        JTextArea txt = new JTextArea("HOW TO PLAY:\n1. Click Start Game\n2. Hover over heroes to see stats\n3. Click a hero to deploy!");
        txt.setEditable(false);
        txt.setBackground(new Color(50, 50, 50));
        txt.setForeground(Color.WHITE);
        JButton back = createEnhancedButton("BACK", Color.GRAY);
        back.addActionListener(e -> showPanel("main"));
        helpPanel.add(new JScrollPane(txt), BorderLayout.CENTER);
        helpPanel.add(back, BorderLayout.SOUTH);
    }

    private void createAboutPanel() {
        aboutPanel = new JPanel(new BorderLayout());
        JTextArea txt = new JTextArea("MARVEL ASCENSION\nCreated by: Group Unturned\nOOP2 Project Prototype");
        txt.setEditable(false);
        txt.setBackground(new Color(50, 50, 50));
        txt.setForeground(Color.WHITE);
        JButton back = createEnhancedButton("BACK", Color.GRAY);
        back.addActionListener(e -> showPanel("main"));
        aboutPanel.add(new JScrollPane(txt), BorderLayout.CENTER);
        aboutPanel.add(back, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == startButton) {
            showPanel("selector"); // Goes to the CharacterSelector screen
        } else if (e.getSource() == settingsButton) {
            showPanel("settings");
        } else if (e.getSource() == helpButton) {
            showPanel("help");
        } else if (e.getSource() == aboutButton) {
            showPanel("about");
        } else if (e.getSource() == exitButton) {
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GameGUI().setVisible(true));
    }
}
