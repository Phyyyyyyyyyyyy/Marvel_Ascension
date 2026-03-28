import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Random;

/**
 * PvpBattleArena — Local 2-player turn-based combat.
 * Best of 3 rounds — first to win 2 rounds wins the match.
 * 
 * UI STYLE: Mirrored from GauntletBattle's sleek dark theme with transparent panels,
 * absolute positioning, and professional combat interface.
 */
public class PvpBattleArena extends JPanel {

    private final GameGUI mainFrame;
    private Image battlefieldImage = null;
    private Image p1Sprite = null, p2Sprite = null;

    // Animation & FX States
    private int shakeX = 0, shakeY = 0;
    private boolean isProcessing = false;
    private String floatingText = "";
    private int floatX = 0, floatY = 0, floatOpacity = 0;
    private Color floatColor = Color.WHITE;

    // Game Logic States
    private Fighter p1, p2;
    private boolean p1Turn = true;
    private boolean gameOver = false;
    private boolean roundOver = false;
    private boolean matchOver = false;
    private boolean isVictory = false;

    // Round / Score Tracking
    private static final int MAX_ROUNDS = 3;
    private static final int ROUNDS_TO_WIN = 2;
    private int currentRound = 1;
    private int p1Wins = 0, p2Wins = 0;

    // UI Components
    private JLabel roundLabel, scoreLabel, turnIndicator;
    private JProgressBar hpBarP, manaBarP, hpBarE, manaBarE;
    private JLabel hpLblP, hpLblE;
    private JTextArea battleLog;
    private JButton btnBasic, btnSkill1, btnSkill2, btnSkill3, btnUlt;
    private JButton btnNext, btnForfeit, btnTryAgain;

    private CharacterSelector.CharacterData savedD1, savedD2;
    private String mapName = "";

    public PvpBattleArena(GameGUI frame) {
        this.mainFrame = frame;
        setLayout(null);
        setPreferredSize(new Dimension(1280, 720));
        setupUI();
    }

    public void startBattle(CharacterSelector.CharacterData d1,
                            CharacterSelector.CharacterData d2,
                            String map) {
        this.savedD1 = d1;
        this.savedD2 = d2;
        this.mapName = map;
        
        loadBattlefield(map);
        p1 = new Fighter(d1);
        p2 = new Fighter(d2);

        currentRound = 1;
        p1Wins = 0;
        p2Wins = 0;
        matchOver = false;
        gameOver = false;
        roundOver = false;
        isProcessing = false;

        // Load initial IDLE GIFs
        setSprite(true, "idle", "idle");
        setSprite(false, "idle", "idle");

        syncBarMaximums();
        startRound();
        logMessage("System Online. Battle Started at " + map);
    }

    private void loadBattlefield(String mapName) {
        String[][] mapData = {
            { "Asgard", "asgardgamebg" },
            { "Avengers Tower", "avengerstowercover" },
            { "Avengers HQ", "avengerstowerinside" },
            { "City Court", "citubballcourt" },
            { "Jollibee Arena", "jollibeeinside" },
            { "Nyan Realm", "nyanmap" },
            { "Random Stage", "randompicture" },
            { "Sokovia", "sokoviagamemap" },
            { "Titan", "titangame" },
            { "Wakanda", "wakandacover" },
            { "Wakanda Inside", "wakandainside" },
        };
        String key = null;
        for (String[] entry : mapData)
            if (entry[0].equals(mapName)) {
                key = entry[1];
                break;
            }
        if (key == null) {
            battlefieldImage = null;
            return;
        }

        File png = new File("maps/" + key + ".png");
        if (png.exists()) {
            battlefieldImage = new ImageIcon("maps/" + key + ".png").getImage();
            return;
        }
        File jpg = new File("maps/" + key + ".jpg");
        if (jpg.exists()) {
            battlefieldImage = new ImageIcon("maps/" + key + ".jpg").getImage();
            return;
        }
        battlefieldImage = null;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (battlefieldImage != null) {
            g2.drawImage(battlefieldImage, 0, 0, getWidth(), getHeight(), this);
        }

        // Draw Sprites with Shake Offset
        if (p2Sprite != null) {
            g2.drawImage(p2Sprite, (int) (getWidth() * 0.65) + shakeX, (int) (getHeight() * 0.15) + shakeY, 320, 320, this);
        }
        if (p1Sprite != null) {
            g2.drawImage(p1Sprite, (int) (getWidth() * 0.05) + shakeX, (int) (getHeight() * 0.38) + shakeY, 480, 480, this);
        }

        // Floating Combat Text
        if (floatOpacity > 0) {
            g2.setFont(new Font("Impact", Font.BOLD, 52));
            g2.setColor(new Color(0, 0, 0, floatOpacity));
            g2.drawString(floatingText, floatX + 3, floatY + 3);
            g2.setColor(new Color(floatColor.getRed(), floatColor.getGreen(), floatColor.getBlue(), floatOpacity));
            g2.drawString(floatingText, floatX, floatY);
        }

        if (matchOver) {
            g2.setColor(new Color(0, 0, 0, 200));
            g2.fillRect(0, 0, getWidth(), getHeight());
            String msg = isVictory ? "MISSION ACCOMPLISHED" : "DEFEATED";
            g2.setFont(new Font("Impact", Font.ITALIC, 110));
            g2.setColor(isVictory ? Color.GREEN : Color.RED);
            g2.drawString(msg, (getWidth() - g2.getFontMetrics().stringWidth(msg)) / 2, getHeight() / 2);
        }
    }

    private void setupUI() {
        JPanel hud = new JPanel(new BorderLayout());
        hud.setOpaque(false);
        hud.setBounds(0, 0, 1280, 720);

        // TOP PANEL
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.setBorder(BorderFactory.createEmptyBorder(20, 40, 0, 40));
        top.add(createStatPanel("PLAYER 2", false), BorderLayout.WEST);

        JPanel centerInfo = new JPanel(new GridLayout(2, 1));
        centerInfo.setOpaque(false);
        roundLabel = makeLabel("ROUND 1 / 3", 24, Color.WHITE);
        scoreLabel = makeLabel("P1: ☆ | P2: ☆", 16, Color.LIGHT_GRAY);
        centerInfo.add(roundLabel);
        centerInfo.add(scoreLabel);
        top.add(centerInfo, BorderLayout.CENTER);

        battleLog = new JTextArea();
        battleLog.setEditable(false);
        battleLog.setBackground(new Color(0, 0, 0, 150));
        battleLog.setForeground(Color.CYAN);
        JScrollPane scroll = new JScrollPane(battleLog);
        scroll.setPreferredSize(new Dimension(280, 110));
        top.add(scroll, BorderLayout.EAST);
        hud.add(top, BorderLayout.NORTH);

        // BOTTOM PANEL
        JPanel bot = new JPanel(new GridBagLayout());
        bot.setOpaque(false);
        bot.setBorder(BorderFactory.createEmptyBorder(0, 40, 25, 40));
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        turnIndicator = makeLabel("PLAYER 1'S TURN", 32, Color.YELLOW);
        bot.add(turnIndicator, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        bot.add(createStatPanel("PLAYER 1", true), gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 0, 0, 0);
        JPanel actions = new JPanel(new GridLayout(1, 5, 12, 0));
        actions.setOpaque(false);
        btnBasic = makeActionBtn("BASIC", new Color(60, 60, 60), 0);
        btnSkill1 = makeActionBtn("SKILL 1", new Color(30, 70, 140), 1);
        btnSkill2 = makeActionBtn("SKILL 2", new Color(30, 70, 140), 2);
        btnSkill3 = makeActionBtn("SKILL 3", new Color(30, 70, 140), 3);
        btnUlt = makeActionBtn("ULTIMATE", new Color(140, 30, 30), 4);
        actions.add(btnBasic);
        actions.add(btnSkill1);
        actions.add(btnSkill2);
        actions.add(btnSkill3);
        actions.add(btnUlt);
        bot.add(actions, gbc);

        btnNext = new JButton("CONTINUE ▶");
        btnNext.setVisible(false);
        btnNext.addActionListener(e -> handleNextRound());
        gbc.gridy = 2;
        bot.add(btnNext, gbc);

        hud.add(bot, BorderLayout.SOUTH);
        add(hud);

        // Forfeit Button at Bottom Right
        btnForfeit = new JButton("FORFEIT");
        btnForfeit.setBackground(new Color(180, 50, 50));
        btnForfeit.setForeground(Color.WHITE);
        btnForfeit.setFocusPainted(false);
        btnForfeit.setFont(new Font("Arial", Font.BOLD, 14));
        btnForfeit.addActionListener(e -> handleForfeit());
        btnForfeit.setBounds(getWidth() - 120, getHeight() - 70, 100, 40);
        add(btnForfeit);

        btnTryAgain = new JButton("RETRY MISSION");
        btnTryAgain.setBounds(515, 430, 250, 65);
        btnTryAgain.setVisible(false);
        btnTryAgain.addActionListener(e -> resetFullMatch());
        add(btnTryAgain);
    }

    @Override
    public void doLayout() {
        super.doLayout();
        if (btnForfeit != null) {
            btnForfeit.setBounds(getWidth() - 120, getHeight() - 70, 100, 40);
        }
    }

    private void startRound() {
        p1Turn = true;
        gameOver = false;
        roundOver = false;
        isProcessing = false;
        matchOver = false;

        p1.resetForNewRound();
        p2.resetForNewRound();

        // Reset sprites to idle
        setSprite(true, "idle", "idle");
        setSprite(false, "idle", "idle");

        btnNext.setVisible(false);
        btnTryAgain.setVisible(false);
        setActionsEnabled(true);

        logMessage("══════════════════════════════════");
        logMessage("  ROUND " + currentRound + " of " + MAX_ROUNDS
                + "   |   " + p1.name + "  vs  " + p2.name);
        logMessage("══════════════════════════════════\n");

        syncBarMaximums();
        updateRoundLabel();
        updateScoreLabel();
        updateTurnIndicator();
    }

    private void handlePlayerAction(int index) {
        if (gameOver || roundOver || isProcessing || matchOver) return;
        if (!p1Turn) return;

        isProcessing = true;
        setActionsEnabled(false);
        updateTurnIndicator("PLAYER 1 ATTACKING...");

        // Start animation
        playAnimation(true, "basic", "basic", 1200);

        // Impact happens at 0.7s
        Timer impact = new Timer(700, e -> {
            int dmg = p1.useAction(index);
            int actual = p2.takeDamage(dmg);
            refreshUI();
            logMessage("[P1 — " + p1.name + "]");
            logMessage("  ⚔ " + p1.skillName(index) + " → " + actual + " damage!");
            logMessage("  " + p2.name + " HP: " + p2.hp + "/" + p2.maxHp);
            showCombatText("-" + actual, (int) (getWidth() * 0.75), (int) (getHeight() * 0.25), Color.RED);
            playAnimation(false, "damaged", "damaged", 600);

            p1.tickCooldowns();

            if (!p2.isAlive()) {
                endRound(true);
            } else {
                // Switch to Player 2's turn
                Timer transition = new Timer(1000, a -> {
                    p1Turn = false;
                    isProcessing = false;
                    setActionsEnabled(true);
                    updateTurnIndicator();
                });
                transition.setRepeats(false);
                transition.start();
            }
        });
        impact.setRepeats(false);
        impact.start();
    }

    private void handlePlayer2Action(int index) {
        if (gameOver || roundOver || isProcessing || matchOver) return;
        if (p1Turn) return;

        isProcessing = true;
        setActionsEnabled(false);
        updateTurnIndicator("PLAYER 2 ATTACKING...");

        // Start animation
        playAnimation(false, "basic", "basic", 1200);

        // Impact happens at 0.7s
        Timer impact = new Timer(700, e -> {
            int dmg = p2.useAction(index);
            int actual = p1.takeDamage(dmg);
            refreshUI();
            logMessage("[P2 — " + p2.name + "]");
            logMessage("  ⚔ " + p2.skillName(index) + " → " + actual + " damage!");
            logMessage("  " + p1.name + " HP: " + p1.hp + "/" + p1.maxHp);
            showCombatText("-" + actual, (int) (getWidth() * 0.20), (int) (getHeight() * 0.45), Color.RED);
            playAnimation(true, "damaged", "damaged", 600);

            p2.tickCooldowns();

            if (!p1.isAlive()) {
                endRound(false);
            } else {
                // Switch back to Player 1's turn
                Timer transition = new Timer(1000, a -> {
                    p1Turn = true;
                    isProcessing = false;
                    setActionsEnabled(true);
                    updateTurnIndicator();
                });
                transition.setRepeats(false);
                transition.start();
            }
        });
        impact.setRepeats(false);
        impact.start();
    }

    private void endRound(boolean p1Won) {
        roundOver = true;
        isProcessing = false;

        if (p1Won) {
            p1Wins++;
            logMessage("\n── Round " + currentRound + " Result ──");
            logMessage("  P1 (" + p1.name + ") wins Round " + currentRound + "!");
        } else {
            p2Wins++;
            logMessage("\n── Round " + currentRound + " Result ──");
            logMessage("  P2 (" + p2.name + ") wins Round " + currentRound + "!");
        }
        logMessage("  Score — P1: " + p1Wins + "  |  P2: " + p2Wins + "\n");

        updateScoreLabel();

        if (p1Wins >= ROUNDS_TO_WIN || p2Wins >= ROUNDS_TO_WIN || currentRound >= MAX_ROUNDS) {
            endMatch();
            return;
        }

        btnNext.setVisible(true);
        updateTurnIndicator("ROUND " + currentRound + " COMPLETE");
    }

    private void endMatch() {
        matchOver = true;
        gameOver = true;
        isVictory = (p1Wins > p2Wins);
        
        btnTryAgain.setVisible(true);
        btnNext.setText("RETURN TO MENU");
        btnNext.setVisible(true);
        setActionsEnabled(false);

        LeaderboardManager.recordPvpResult("Player 1", p1.name, p1Wins, p2Wins);
        LeaderboardManager.recordPvpResult("Player 2", p2.name, p2Wins, p1Wins);

        String matchWinner = p1Wins > p2Wins ? "PLAYER 1" : (p2Wins > p1Wins ? "PLAYER 2" : "DRAW");
        Fighter winner = p1Wins >= p2Wins ? p1 : p2;

        logMessage("══════════════════════════════════");
        if (p1Wins == p2Wins) {
            logMessage("  IT'S A DRAW!  Score: " + p1Wins + " — " + p2Wins);
        } else {
            logMessage("  MATCH OVER — " + matchWinner + " (" + winner.name + ") WINS THE MATCH!");
            logMessage("  Final Score — P1: " + p1Wins + "  |  P2: " + p2Wins);
        }
        logMessage("══════════════════════════════════");

        repaint();
    }

    private void handleNextRound() {
        if (btnNext.getText().contains("MENU")) {
            mainFrame.getSelectorPanel().resetSelections();
            mainFrame.navigateTo("main");
            return;
        }
        currentRound++;
        startRound();
    }

    private void resetFullMatch() {
        startBattle(savedD1, savedD2, mapName);
    }

    private void handleForfeit() {
        if (matchOver) {
            mainFrame.getSelectorPanel().resetSelections();
            mainFrame.navigateTo("main");
            return;
        }
        
        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to forfeit?\nThis ends the entire match.",
                "Forfeit Match", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (choice == JOptionPane.YES_OPTION) {
            logMessage("\n⚑ Match forfeited!");
            matchOver = true;
            isVictory = false;
            btnTryAgain.setVisible(true);
            btnNext.setText("RETURN TO MENU");
            btnNext.setVisible(true);
            setActionsEnabled(false);
            repaint();
        }
    }

    // --- Animation & Effects ---

    private void playAnimation(boolean isP1, String folder, String action, int duration) {
        if (folder.equals("damaged")) triggerShake(15, 350);
        setSprite(isP1, folder, action);

        Timer timer = new Timer(duration, e -> {
            Fighter f = isP1 ? p1 : p2;
            if (f != null && f.isAlive()) {
                setSprite(isP1, "idle", "idle");
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    private void setSprite(boolean isP1, String folder, String action) {
        Fighter f = isP1 ? p1 : p2;
        if (f == null) return;
        
        String name = f.name.replace("-", "").replace(" ", "").toLowerCase();
        String side = isP1 ? "NE" : "SW";
        String path = "gifs/" + folder + "/" + name + action + side + ".gif";

        File file = new File(path);
        if (file.exists()) {
            ImageIcon icon = new ImageIcon(file.getPath());
            icon.getImage().flush(); // Force GIF restart
            if (isP1) p1Sprite = icon.getImage();
            else p2Sprite = icon.getImage();
        }
        repaint();
    }

    private void showCombatText(String text, int x, int y, Color color) {
        this.floatingText = text;
        this.floatX = x;
        this.floatY = y;
        this.floatColor = color;
        this.floatOpacity = 255;
        Timer t = new Timer(25, e -> {
            floatY -= 4;
            floatOpacity -= 8;
            if (floatOpacity <= 0) {
                floatOpacity = 0;
                ((Timer) e.getSource()).stop();
            }
            repaint();
        });
        t.start();
    }

    private void triggerShake(int intensity, int duration) {
        long start = System.currentTimeMillis();
        Random rand = new Random();
        Timer t = new Timer(20, e -> {
            if (System.currentTimeMillis() - start > duration) {
                shakeX = 0;
                shakeY = 0;
                ((Timer) e.getSource()).stop();
            } else {
                shakeX = rand.nextInt(intensity * 2) - intensity;
                shakeY = rand.nextInt(intensity * 2) - intensity;
            }
            repaint();
        });
        t.start();
    }

    // --- UI Helpers ---

    private void refreshUI() {
        if (p1 == null || p2 == null) return;
        hpBarP.setValue(p1.getHp());
        hpLblP.setText("HP: " + p1.getHp() + "/" + p1.getMaxHp());
        hpBarE.setValue(p2.getHp());
        hpLblE.setText("HP: " + p2.getHp() + "/" + p2.getMaxHp());
        manaBarP.setValue(p1.getMana());
        manaBarE.setValue(p2.getMana());
        
        // Update skill button text with current player's skills
        if (p1Turn && p1 != null) {
            updateSkillButtonTexts(p1);
            updateSkillButtonStates(p1);
        } else if (!p1Turn && p2 != null) {
            updateSkillButtonTexts(p2);
            updateSkillButtonStates(p2);
        }
    }

    private void syncBarMaximums() {
        if (p1 == null || p2 == null) return;
        hpBarP.setMaximum(p1.getMaxHp());
        hpBarE.setMaximum(p2.getMaxHp());
        manaBarP.setMaximum(p1.getMaxMana());
        manaBarE.setMaximum(p2.getMaxMana());
        refreshUI();
    }

    private void updateSkillButtonTexts(Fighter f) {
        btnSkill1.setText("<html><center>" + f.skill1 + "<br><font size='2'>" + Fighter.SK1_COST + " mana</font></center></html>");
        btnSkill2.setText("<html><center>" + f.skill2 + "<br><font size='2'>" + Fighter.SK2_COST + " mana</font></center></html>");
        btnSkill3.setText("<html><center>" + f.skill3 + "<br><font size='2'>" + Fighter.SK3_COST + " mana</font></center></html>");
        btnUlt.setText("<html><center>" + f.ultimate + "<br><font size='2'>" + Fighter.ULT_COST + " mana</font></center></html>");
    }

    private void updateSkillButtonStates(Fighter f) {
        btnSkill1.setEnabled(f.canUse(1));
        btnSkill2.setEnabled(f.canUse(2));
        btnSkill3.setEnabled(f.canUse(3));
        btnUlt.setEnabled(f.canUse(4));
    }

    private JPanel createStatPanel(String name, boolean isPlayer) {
        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBackground(new Color(255, 255, 255, 220));
        box.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        box.setPreferredSize(new Dimension(240, 100));

        JProgressBar hp = new JProgressBar();
        hp.setForeground(new Color(40, 180, 100));
        JProgressBar mn = new JProgressBar();
        mn.setForeground(new Color(40, 120, 200));
        JLabel n = new JLabel(name.toUpperCase());

        if (isPlayer) {
            hpBarP = hp;
            manaBarP = mn;
            hpLblP = new JLabel("HP: 100/100");
            box.add(n);
            box.add(hpLblP);
            box.add(hp);
            box.add(new JLabel("MANA"));
            box.add(mn);
        } else {
            hpBarE = hp;
            manaBarE = mn;
            hpLblE = new JLabel("HP: 100/100");
            box.add(n);
            box.add(hpLblE);
            box.add(hp);
            box.add(new JLabel("MANA"));
            box.add(mn);
        }
        return box;
    }

    private void setActionsEnabled(boolean enabled) {
        if (p1Turn && p1 != null) {
            btnBasic.setEnabled(enabled);
            btnSkill1.setEnabled(enabled && p1.canUse(1));
            btnSkill2.setEnabled(enabled && p1.canUse(2));
            btnSkill3.setEnabled(enabled && p1.canUse(3));
            btnUlt.setEnabled(enabled && p1.canUse(4));
        } else if (!p1Turn && p2 != null) {
            btnBasic.setEnabled(enabled);
            btnSkill1.setEnabled(enabled && p2.canUse(1));
            btnSkill2.setEnabled(enabled && p2.canUse(2));
            btnSkill3.setEnabled(enabled && p2.canUse(3));
            btnUlt.setEnabled(enabled && p2.canUse(4));
        } else {
            btnBasic.setEnabled(false);
            btnSkill1.setEnabled(false);
            btnSkill2.setEnabled(false);
            btnSkill3.setEnabled(false);
            btnUlt.setEnabled(false);
        }
    }

    private void updateTurnIndicator() {
        if (matchOver) return;
        if (p1Turn) {
            turnIndicator.setText("PLAYER 1'S TURN");
            turnIndicator.setForeground(Color.YELLOW);
        } else {
            turnIndicator.setText("PLAYER 2'S TURN");
            turnIndicator.setForeground(Color.YELLOW);
        }
    }

    private void updateTurnIndicator(String text) {
        turnIndicator.setText(text);
    }

    private void updateRoundLabel() {
        roundLabel.setText("ROUND " + currentRound + " / " + MAX_ROUNDS);
    }

    private void updateScoreLabel() {
        String p1Stars = "★".repeat(p1Wins) + "☆".repeat(ROUNDS_TO_WIN - Math.min(p1Wins, ROUNDS_TO_WIN));
        String p2Stars = "★".repeat(p2Wins) + "☆".repeat(ROUNDS_TO_WIN - Math.min(p2Wins, ROUNDS_TO_WIN));
        scoreLabel.setText("P1: " + p1Stars + "  |  P2: " + p2Stars);
    }

    private void logMessage(String msg) {
        battleLog.append(msg + "\n");
        battleLog.setCaretPosition(battleLog.getDocument().getLength());
    }

    private JLabel makeLabel(String t, int s, Color c) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("Impact", Font.PLAIN, s));
        l.setForeground(c);
        return l;
    }

    private JButton makeActionBtn(String t, Color bg, int i) {
        JButton b = new JButton("<html><center>" + t + "</center></html>");
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        
        // Determine which player's action to use based on turn
        b.addActionListener(e -> {
            if (p1Turn) {
                handlePlayerAction(i);
            } else {
                handlePlayer2Action(i);
            }
        });
        return b;
    }

    // --- Inner Fighter Class ---

    private static class Fighter {
        String name, skill1, skill2, skill3, ultimate;
        int hp, maxHp, attack;
        int mana = 50;
        static final int MAX_MANA = 100;
        static final int SK1_COST = 20, SK2_COST = 25, SK3_COST = 30, ULT_COST = 50;

        final CooldownManager cooldown = new CooldownManager();

        Fighter(CharacterSelector.CharacterData d) {
            name = d.name;
            maxHp = d.hp;
            hp = d.hp;
            attack = d.attack;
            skill1 = d.skill1;
            skill2 = d.skill2;
            skill3 = d.skill3;
            ultimate = d.ultimate;
        }

        void resetForNewRound() {
            hp = maxHp;
            mana = 50;
            cooldown.resetAll();
        }

        boolean isAlive() {
            return hp > 0;
        }

        int takeDamage(int dmg) {
            int actual = Math.max(1, dmg);
            hp = Math.max(0, hp - actual);
            return actual;
        }

        void restoreMana(int v) {
            mana = Math.min(MAX_MANA, mana + v);
        }

        void tickCooldowns() {
            cooldown.tickAll();
        }

        boolean canUse(int idx) {
            if (idx >= 1 && idx <= 4 && !cooldown.isReady(idx)) return false;
            switch (idx) {
                case 1:
                    return mana >= SK1_COST;
                case 2:
                    return mana >= SK2_COST;
                case 3:
                    return mana >= SK3_COST;
                case 4:
                    return mana >= ULT_COST;
                default:
                    return true;
            }
        }

        String skillName(int idx) {
            switch (idx) {
                case 0:
                    return "Basic Attack";
                case 1:
                    return skill1;
                case 2:
                    return skill2;
                case 3:
                    return skill3;
                case 4:
                    return ultimate;
                default:
                    return "?";
            }
        }

        int calcDamage(int idx) {
            switch (idx) {
                case 0:
                    return attack;
                case 1:
                    return attack + 5;
                case 2:
                    return attack + 10;
                case 3:
                    return attack + 15;
                case 4:
                    return attack * 2;
                default:
                    return attack;
            }
        }

        int useAction(int idx) {
            if (!canUse(idx)) {
                restoreMana(10);
                return attack;
            }
            int cost = new int[]{0, SK1_COST, SK2_COST, SK3_COST, ULT_COST}[idx];
            mana -= cost;
            if (idx >= 1 && idx <= 4) cooldown.useSkill(idx);
            if (idx == 0) restoreMana(10);
            else restoreMana(5);
            return calcDamage(idx);
        }

        int getHp() {
            return hp;
        }

        int getMaxHp() {
            return maxHp;
        }

        int getMana() {
            return mana;
        }

        int getMaxMana() {
            return MAX_MANA;
        }
    }
}
