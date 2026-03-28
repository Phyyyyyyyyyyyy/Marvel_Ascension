import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Random;

/**
 * MARVEL ASCENSION: GAUNTLET BATTLE SYSTEM
 * Player uses Combatant, AI uses Enemy
 */
public class GauntletBattle extends JPanel {

    private final GameGUI mainFrame;
    private Image battlefieldImage = null;
    private Image playerSprite = null;
    private Image enemySprite = null;

    // Animation & FX States
    private int shakeX = 0, shakeY = 0;
    private boolean isProcessing = false;
    private String floatingText = "";
    private int floatX = 0, floatY = 0, floatOpacity = 0;
    private Color floatColor = Color.WHITE;

    // Fade-out animation fields
    private float playerOpacity = 1.0f;
    private float enemyOpacity = 1.0f;
    private Timer fadeTimer = null;
    private boolean isFadingOut = false;

    // Game Logic States
    private Combatant player;
    private Enemy enemy;
    private int playerScore = 0, aiScore = 0, currentRound = 1;
    private boolean playerTurn = true, matchOver = false, isVictory = false;
    private boolean roundOver = false;
    private boolean gameOver = false;

    // UI Components
    private JPanel hud;
    private JLabel roundLabel, scoreLabel, turnIndicator;
    private JProgressBar hpBarP, manaBarP, hpBarE, manaBarE;
    private JLabel hpLblP, manaLblP, hpLblE, manaLblE;
    private JTextArea battleLog;
    private JButton btnBasic, btnSkill1, btnSkill2,btnUlt, btnForfeit;
    private JButton btnNext, btnTryAgain;

    // Action mapping for animations (matching PvpBattleArena style)
    private static final String[] ACTION_FOLDERS = {
        "basic",    // index 0 — basic attack
        "skill1",   // index 1 — skill 1
        "skill2",   // index 2 — skill 2
        "skill3",   // index 3 — skill 3
        "ultimate"  // index 4 — ultimate
    };

    public GauntletBattle(GameGUI frame, String heroName, String mapName) {
        this.mainFrame = frame;

        // Create player from hero data
        CharacterSelector selector = frame.getSelectorPanel();
        CharacterSelector.CharacterData heroData = selector.getHeroData(heroName);
        
        if (heroData == null) {
            heroData = selector.getHeroData("Iron Man");
        }
        
        // Create Combatant for player using hero data
        this.player = new Combatant(
            heroData.name, heroData.hp, heroData.attack,
            heroData.skill1, heroData.skill2, heroData.skill3, heroData.ultimate,
            20, 25, 30, 50,
            heroData.attack + 5,
            heroData.attack + 10,
            heroData.attack + 15,
            heroData.attack * 2
        ) {
            @Override
            public int decideAction() {
                return 0;
            }
        };
        this.player.setCooldownManager(new CooldownManager());
        
        // Create enemy
        this.enemy = Enemy.getRandomEnemy();

        setLayout(null);
        setPreferredSize(new Dimension(1280, 720));
        loadBattlefield(mapName);

        setSprite(true, "idle", "idle");
        setSprite(false, "idle", "idle");

        setupUI();
        syncBarMaximums();
        
        startRound();
    }

    private void loadBattlefield(String mapName) {
        String fileKey = null;

        File mapsFolder = new File("maps");
        if (mapsFolder.exists() && mapsFolder.isDirectory()) {
            File[] files = mapsFolder.listFiles((dir, name) ->
                name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg"));

            if (files != null) {
                for (File file : files) {
                    String fileName = file.getName();
                    String nameWithoutExt = fileName.substring(0, fileName.lastIndexOf('.'));

                    if (mapName.equalsIgnoreCase(formatMapName(nameWithoutExt))) {
                        fileKey = nameWithoutExt;
                        break;
                    }
                }
            }
        }

        if (fileKey == null) {
            fileKey = "asgardgamebg";
        }

        File bgFile = new File("maps/" + fileKey + ".png");
        if (!bgFile.exists()) {
            bgFile = new File("maps/" + fileKey + ".jpg");
        }

        if (bgFile.exists()) {
            battlefieldImage = new ImageIcon(bgFile.getPath()).getImage();
        } else {
            File defaultBg = new File("maps/asgardgamebg.png");
            if (defaultBg.exists()) {
                battlefieldImage = new ImageIcon(defaultBg.getPath()).getImage();
            }
        }
    }

    private String formatMapName(String raw) {
        switch (raw.toLowerCase()) {
            case "asgardgamebg":      return "Asgard";
            case "avengerstowercover": return "Avengers Tower";
            case "avengerstowerinside":return "Avengers HQ";
            case "citubballcourt":    return "City Court";
            case "jollibeeinside":    return "Jollibee Arena";
            case "nyanmap":           return "Nyan Realm";
            case "randompicture":     return "Random Stage";
            case "sokoviagamemap":    return "Sokovia";
            case "titangame":         return "Titan";
            case "wakandacover":      return "Wakanda";
            case "wakandainside":     return "Wakanda Inside";
            default:
                String formatted = raw.replaceAll("([a-z])([A-Z])", "$1 $2");
                if (formatted.length() > 0) {
                    formatted = formatted.substring(0, 1).toUpperCase() + formatted.substring(1);
                }
                return formatted;
        }
    }

    private void startRound() {
        playerTurn = true;
        gameOver = false;
        roundOver = false;
        matchOver = false;
        isProcessing = false;

        player.resetForNewRound();
        enemy.resetForNewRound();

        setSprite(true, "idle", "idle");
        setSprite(false, "idle", "idle");

        btnNext.setVisible(false);
        btnTryAgain.setVisible(false);
        setActionsEnabled(true);
        updateTurnIndicator("YOUR TURN");

        logMessage("══════════════════════════════════");
        logMessage("  ROUND " + currentRound + " of 3");
        logMessage("  " + player.getName() + "  vs  " + enemy.getName());
        logMessage("══════════════════════════════════\n");

        syncBarMaximums();
        updateRoundLabel();
        updateScoreLabel();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        if (battlefieldImage != null) {
            g2.drawImage(battlefieldImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            GradientPaint gradient = new GradientPaint(0, 0, new Color(20, 20, 40),
                getWidth(), getHeight(), new Color(10, 10, 30));
            g2.setPaint(gradient);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }

        int playerSize = (int)(getWidth() * 0.375);
        int enemySize = (int)(getWidth() * 0.25);

        if (enemySprite != null) {
            AlphaComposite alphaComposite = AlphaComposite.getInstance(
                AlphaComposite.SRC_OVER, enemyOpacity);
            g2.setComposite(alphaComposite);
            g2.drawImage(enemySprite, (int)(getWidth()*0.65) + shakeX, (int)(getHeight()*0.15) + shakeY, enemySize, enemySize, this);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        }

        if (playerSprite != null) {
            AlphaComposite alphaComposite = AlphaComposite.getInstance(
                AlphaComposite.SRC_OVER, playerOpacity);
            g2.setComposite(alphaComposite);
            g2.drawImage(playerSprite, (int)(getWidth()*0.05) + shakeX, (int)(getHeight()*0.38) + shakeY, playerSize, playerSize, this);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        }

        if (floatOpacity > 0) {
            g2.setFont(new Font("Impact", Font.BOLD, 52));
            g2.setColor(new Color(0, 0, 0, floatOpacity));
            g2.drawString(floatingText, floatX + 3, floatY + 3);
            g2.setColor(new Color(floatColor.getRed(), floatColor.getGreen(), floatColor.getBlue(), floatOpacity));
            g2.drawString(floatingText, floatX, floatY);
        }

        if (matchOver && !isFadingOut) {
            g2.setColor(new Color(0, 0, 0, 200));
            g2.fillRect(0, 0, getWidth(), getHeight());
            String msg = isVictory ? "MISSION ACCOMPLISHED" : "DEFEATED";
            g2.setFont(new Font("Impact", Font.ITALIC, 110));
            g2.setColor(isVictory ? new Color(0, 255, 0) : new Color(255, 0, 0));
            g2.drawString(msg, (getWidth() - g2.getFontMetrics().stringWidth(msg))/2, getHeight()/2);
        }
    }

    private void setupUI() {
        hud = new JPanel(new BorderLayout()); 
        hud.setOpaque(false);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.setBorder(BorderFactory.createEmptyBorder(20, 40, 0, 40));
        top.add(createStatPanel(enemy.getName(), false), BorderLayout.WEST);

        JPanel centerInfo = new JPanel(new GridLayout(2, 1));
        centerInfo.setOpaque(false);
        roundLabel = makeLabel("ROUND 1 / 3", 28, new Color(255, 215, 0));
        scoreLabel = makeLabel("0 - 0", 20, Color.WHITE);
        centerInfo.add(roundLabel);
        centerInfo.add(scoreLabel);
        top.add(centerInfo, BorderLayout.CENTER);

        battleLog = new JTextArea();
        battleLog.setEditable(false);
        battleLog.setBackground(new Color(0, 0, 0, 180));
        battleLog.setForeground(new Color(0, 255, 255));
        battleLog.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scroll = new JScrollPane(battleLog);
        scroll.setPreferredSize(new Dimension(280, 120));
        scroll.setBorder(BorderFactory.createLineBorder(new Color(255, 215, 0), 1));
        top.add(scroll, BorderLayout.EAST);
        hud.add(top, BorderLayout.NORTH);

        JPanel bot = new JPanel(new GridBagLayout());
        bot.setOpaque(false);
        bot.setBorder(BorderFactory.createEmptyBorder(0, 40, 25, 40));
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        turnIndicator = makeLabel("YOUR TURN", 32, Color.YELLOW);
        turnIndicator.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        bot.add(turnIndicator, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        bot.add(createStatPanel(player.getName(), true), gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 0, 0, 0);

        JPanel actions = new JPanel(new GridLayout(1, 6, 10, 0));
        actions.setOpaque(false);

        btnBasic  = createActionButton("BASIC",                   0);
        btnSkill1 = createActionButton(player.getSkill1Name(),    1);
        btnSkill2 = createActionButton(player.getSkill2Name(),    2);
    
        btnUlt    = createActionButton(player.getUltimateName(),  4);

        btnForfeit = new JButton("<html><center>FORFEIT<br><font size='2'>[END MATCH]</font></center></html>");
        btnForfeit.setBackground(new Color(120, 30, 30));
        btnForfeit.setForeground(Color.WHITE);
        btnForfeit.setFocusPainted(false);
        btnForfeit.setFont(new Font("Arial", Font.BOLD, 12));
        btnForfeit.setPreferredSize(new Dimension(120, 70));
        btnForfeit.addActionListener(e -> handleForfeit());

        actions.add(btnBasic);
        actions.add(btnSkill1);
        actions.add(btnSkill2);
       
        actions.add(btnUlt);
        actions.add(btnForfeit);
        bot.add(actions, gbc);

        btnNext = new JButton("CONTINUE ▶");
        btnNext.setFont(new Font("Impact", Font.PLAIN, 20));
        btnNext.setBackground(new Color(0, 100, 0));
        btnNext.setForeground(Color.WHITE);
        btnNext.setFocusPainted(false);
        btnNext.setVisible(false);
        btnNext.addActionListener(e -> handleNextRound());
        gbc.gridy = 2;
        gbc.insets = new Insets(10, 0, 0, 0);
        bot.add(btnNext, gbc);

        hud.add(bot, BorderLayout.SOUTH);
        add(hud);

        btnTryAgain = new JButton("RETRY MISSION");
        btnTryAgain.setFont(new Font("Impact", Font.PLAIN, 24));
        btnTryAgain.setBackground(new Color(0, 100, 0));
        btnTryAgain.setForeground(Color.WHITE);
        btnTryAgain.setFocusPainted(false);
        btnTryAgain.setVisible(false);
        btnTryAgain.addActionListener(e -> resetFullMatch());
        add(btnTryAgain);
    }

    @Override
    public void doLayout() {
        super.doLayout();
        if (hud != null) {
            hud.setBounds(0, 0, getWidth(), getHeight());
        }
        if (btnTryAgain != null) {
            btnTryAgain.setBounds((getWidth() - 250) / 2, (getHeight() - 65) / 2, 250, 65);
        }
    }

    private JButton createActionButton(String skillName, int index) {
        int manaCost = player.getActionCost(index);
        int damage   = player.getActionDamage(index);

        String displayName;
        switch (index) {
            case 0:  displayName = "BASIC";    break;
            default: displayName = skillName;  break;
        }

        String buttonText = String.format(
            "<html><center>%s<br><font size='2'>[MP: %d | DMG: %d]</font></center></html>",
            displayName, manaCost, damage);

        JButton b = new JButton(buttonText);
        if (index == 0) {
            b.setBackground(new Color(60, 60, 60));
        } else if (index == 4) {
            b.setBackground(new Color(140, 30, 30));
        } else {
            b.setBackground(new Color(30, 70, 140));
        }
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setFont(new Font("Arial", Font.BOLD, 12));
        b.setPreferredSize(new Dimension(120, 70));
        b.addActionListener(e -> handlePlayerAction(index));
        return b;
    }

    private void handlePlayerAction(int index) {
        if (!playerTurn || isProcessing || matchOver || roundOver) return;

        if (!player.canUse(index)) {
            logMessage("⚠ Not enough mana to use " + player.getActionName(index)
                + "! (Need " + player.getActionCost(index) + " MP)");
            return;
        }

        isProcessing = true;
        setActionsEnabled(false);
        updateTurnIndicator("ATTACKING...");

        String animFolder = ACTION_FOLDERS[index];
        playAnimation(true, animFolder, animFolder, 1200);

        Timer impact = new Timer(700, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int dmg = player.useAction(index);
                int actual = enemy.takeDamage(dmg);
                
                logMessage(player.getName() + " used " + player.getActionName(index) + "!");
                logMessage("  ⚔ Dealt " + actual + " damage!");
                logMessage("  💧 MP: " + player.getMana() + "/" + player.getMaxMana());
                logMessage("  " + enemy.getName() + " HP: " + enemy.getHp() + "/" + enemy.getMaxHp());
                showCombatText("-" + actual, (int)(getWidth()*0.75), (int)(getHeight()*0.25), Color.RED);
                
                playAnimation(false, "damaged", "damaged", 600);
                
                player.tickCooldowns();

                if (!enemy.isAlive()) {
                    refreshUI();
                    endRound(true);
                } else {
                    Timer transition = new Timer(1000, new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent a) {
                            playerTurn = false;
                            isProcessing = false;
                            refreshUI();
                            setActionsEnabled(true);
                            updateTurnIndicator("ENEMY TURN");
                            doAiTurn();
                        }
                    });
                    transition.setRepeats(false);
                    transition.start();
                }
            }
        });
        impact.setRepeats(false);
        impact.start();
    }

    private void doAiTurn() {
        if (matchOver || isProcessing || playerTurn || roundOver) return;

        isProcessing = true;
        setActionsEnabled(false);
        updateTurnIndicator("ENEMY ATTACKING...");

        int action = enemy.decideAction();
        String animFolder = ACTION_FOLDERS[action];
        
        playAnimation(false, animFolder, animFolder, 1200);

        Timer impact = new Timer(700, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int dmg = enemy.useAction(action);
                int actual = player.takeDamage(dmg);
                
                logMessage(enemy.getName() + " used " + enemy.getActionName(action) + "!");
                logMessage("  ⚔ Dealt " + actual + " damage!");
                logMessage("  💧 Enemy MP: " + enemy.getMana() + "/" + enemy.getMaxMana());
                logMessage("  " + player.getName() + " HP: " + player.getHp() + "/" + player.getMaxHp());
                showCombatText("-" + actual, (int)(getWidth()*0.20), (int)(getHeight()*0.45), Color.RED);
                
                playAnimation(true, "damaged", "damaged", 600);
                
                enemy.tickCooldowns();

                if (!player.isAlive()) {
                    refreshUI();
                    endRound(false);
                } else {
                    Timer transition = new Timer(1000, new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent a) {
                            playerTurn = true;
                            isProcessing = false;
                            refreshUI();
                            setActionsEnabled(true);
                            updateTurnIndicator("YOUR TURN");
                        }
                    });
                    transition.setRepeats(false);
                    transition.start();
                }
            }
        });
        impact.setRepeats(false);
        impact.start();
    }

    // ── Animation Methods (mirroring PvpBattleArena) ─────────────────────

    private void playAnimation(boolean isPlayer, String folder, String action, int duration) {
        if (folder.equals("damaged")) triggerShake(15, 350);
        setSprite(isPlayer, folder, action);

        Timer timer = new Timer(duration, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Combatant f = isPlayer ? player : enemy;
                if (f != null && f.isAlive()) setSprite(isPlayer, "idle", "idle");
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    /**
     * Mapping table matching PvpBattleArena style:
     *   folder      actualFolder   actualAction   example file
     *   ──────────  ─────────────  ─────────────  ──────────────────────────
     *   "idle"      idle           idle           ironmanidleNE.gif
     *   "basic"     basic          basic          ironmanbasicNE.gif
     *   "damaged"   damaged        damaged        ironmandamagedNE.gif
     *   "skill1"    skill1         first          ironmanfirstNE.gif
     *   "skill2"    skill2         second         ironmansecondNE.gif
     *   "skill3"    skill2         second         (falls back to skill2)
     *   "ultimate"  ultimate       ult            ironmanultNE.gif
     */
    private void setSprite(boolean isPlayer, String folder, String action) {
        Combatant f = isPlayer ? player : enemy;
        if (f == null) return;

        String name = f.getName().replace("-", "").replace(" ", "").toLowerCase();
        String side = isPlayer ? "NE" : "SW";

        String actualFolder;
        String actualAction;

        switch (folder) {
            case "idle":
                actualFolder = "idle";
                actualAction = "idle";
                break;
            case "basic":
                actualFolder = "basic";
                actualAction = "basic";
                break;
            case "damaged":
                actualFolder = "damaged";
                actualAction = "damaged";
                break;
            case "skill1":
                actualFolder = "skill1";
                actualAction = "first";
                break;
            case "skill2":
                actualFolder = "skill2";
                actualAction = "second";
                break;
            case "skill3":
                actualFolder = "skill2";
                actualAction = "second";
                break;
            case "ultimate":
                actualFolder = "ultimate";
                actualAction = "ult";
                break;
            default:
                actualFolder = "idle";
                actualAction = "idle";
                break;
        }

        // Map hero names to file names
        String mappedName = mapHeroNameToFile(name);
        String path = "gifs/" + actualFolder + "/" + mappedName + actualAction + side + ".gif";

        File file = new File(path);
        if (file.exists()) {
            ImageIcon icon = new ImageIcon(file.getPath());
            icon.getImage().flush();
            if (isPlayer) playerSprite = icon.getImage();
            else enemySprite = icon.getImage();
        } else {
            // Try without side suffix as fallback
            String altPath = "gifs/" + actualFolder + "/" + mappedName + actualAction + ".gif";
            File altFile = new File(altPath);
            if (altFile.exists()) {
                ImageIcon icon = new ImageIcon(altFile.getPath());
                icon.getImage().flush();
                if (isPlayer) playerSprite = icon.getImage();
                else enemySprite = icon.getImage();
            }
        }
        repaint();
    }

    private String mapHeroNameToFile(String name) {
        switch (name) {
            case "ironman":        return "ironman";
            case "captainamerica": return "captainamerica";
            case "spiderman":      return "spider-man";
            case "blackwidow":     return "blackwidow";
            case "thefalcon":      return "falcon";
            case "antman":         return "antman";
            case "janclark":       return "clark";
            case "reuben":         return "reuben";
            case "justine":        return "justine";
            case "thanos":         return "thanos";
            case "thor":           return "thor";
            case "hulk":           return "hulk";
            default:               return name;
        }
    }

    private void showCombatText(String text, int x, int y, Color color) {
        this.floatingText = text;
        this.floatX = x;
        this.floatY = y;
        this.floatColor = color;
        this.floatOpacity = 255;
        Timer t = new Timer(25, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                floatY -= 4;
                floatOpacity -= 8;
                if (floatOpacity <= 0) {
                    floatOpacity = 0;
                    ((Timer) e.getSource()).stop();
                }
                repaint();
            }
        });
        t.start();
    }

    private void triggerShake(int intensity, int duration) {
        long start = System.currentTimeMillis();
        Random rand = new Random();
        Timer t = new Timer(20, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (System.currentTimeMillis() - start > duration) {
                    shakeX = 0;
                    shakeY = 0;
                    ((Timer) e.getSource()).stop();
                } else {
                    shakeX = rand.nextInt(intensity * 2) - intensity;
                    shakeY = rand.nextInt(intensity * 2) - intensity;
                }
                repaint();
            }
        });
        t.start();
    }

    // ── Round and Match Management ─────────────────────────────────────────

    private void endRound(boolean win) {
        roundOver = true;
        isProcessing = false;

        if (win) {
            playerScore++;
            logMessage("");
            logMessage("★ ★ ★ ★ ★ ★ ★ ★ ★ ★ ★ ★ ★ ★ ★ ★");
            logMessage("  " + player.getName() + " WINS ROUND " + currentRound + "!");
            logMessage("★ ★ ★ ★ ★ ★ ★ ★ ★ ★ ★ ★ ★ ★ ★ ★");
        } else {
            aiScore++;
            logMessage("");
            logMessage("✘ ✘ ✘ ✘ ✘ ✘ ✘ ✘ ✘ ✘ ✘ ✘ ✘ ✘ ✘ ✘");
            logMessage("  " + enemy.getName() + " WINS ROUND " + currentRound + "!");
            logMessage("✘ ✘ ✘ ✘ ✘ ✘ ✘ ✘ ✘ ✘ ✘ ✘ ✘ ✘ ✘ ✘");
        }

        updateScoreLabel();

        if (playerScore >= 2 || aiScore >= 2) {
            endMatch();
        } else {
            btnNext.setVisible(true);
            setActionsEnabled(false);
            updateTurnIndicator("ROUND " + currentRound + " COMPLETE");
            logMessage("");
            logMessage("Press CONTINUE for Round " + (currentRound + 1));
        }

        repaint();
    }

    private void endMatch() {
        matchOver = true;
        gameOver = true;
        isVictory = (playerScore >= 2);

        if (isVictory) {
            startMatchEndFadeOut(false);
            logMessage("");
            logMessage("⚡ " + enemy.getName() + " HAS BEEN DEFEATED! ⚡");
        } else {
            startMatchEndFadeOut(true);
            logMessage("");
            logMessage("💔 " + player.getName() + " HAS FALLEN... 💔");
        }

        LeaderboardManager.recordGauntletResult(player.getName(), playerScore, aiScore, isVictory);

        String resultMsg = isVictory ? "VICTORY!" : "DEFEAT...";
        logMessage("");
        logMessage("══════════════════════════════════");
        logMessage("  MATCH OVER - " + resultMsg);
        logMessage("  Final Score: " + playerScore + " - " + aiScore);
        logMessage("══════════════════════════════════");

        Timer showButtons = new Timer(900, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnTryAgain.setVisible(true);
                btnNext.setText("RETURN TO MENU");
                btnNext.setVisible(true);
            }
        });
        showButtons.setRepeats(false);
        showButtons.start();

        setActionsEnabled(false);
        repaint();
    }

    private void startMatchEndFadeOut(boolean isPlayer) {
        final long startTime = System.currentTimeMillis();
        final int duration = 800;
        isFadingOut = true;

        if (fadeTimer != null && fadeTimer.isRunning()) fadeTimer.stop();

        fadeTimer = new Timer(16, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                long elapsed = System.currentTimeMillis() - startTime;
                float progress = Math.min(1.0f, elapsed / (float) duration);
                float newOpacity = 1.0f - progress;

                if (isPlayer) playerOpacity = Math.max(0, newOpacity);
                else enemyOpacity = Math.max(0, newOpacity);

                repaint();

                if (progress >= 1.0f) {
                    fadeTimer.stop();
                    isFadingOut = false;
                    repaint();
                }
            }
        });
        fadeTimer.start();
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

    private void handleForfeit() {
        if (matchOver) {
            mainFrame.getSelectorPanel().resetSelections();
            mainFrame.navigateTo("main");
            return;
        }

        int choice = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to forfeit?\nThis will end the entire match.",
            "Forfeit Match", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            isProcessing = false;

            logMessage("");
            logMessage("⚑ ⚑ ⚑ ⚑ ⚑ ⚑ ⚑ ⚑ ⚑ ⚑ ⚑ ⚑ ⚑ ⚑ ⚑ ⚑");
            logMessage("  MATCH FORFEITED!");
            logMessage("⚑ ⚑ ⚑ ⚑ ⚑ ⚑ ⚑ ⚑ ⚑ ⚑ ⚑ ⚑ ⚑ ⚑ ⚑ ⚑");
            matchOver = true;
            isVictory = false;

            startMatchEndFadeOut(true);

            btnTryAgain.setVisible(true);
            btnNext.setText("RETURN TO MENU");
            btnNext.setVisible(true);
            setActionsEnabled(false);

            LeaderboardManager.recordGauntletResult(player.getName(), playerScore, aiScore, false);
            repaint();
        }
    }

    private void resetFullMatch() {
        isProcessing = false;

        playerScore = 0;
        aiScore = 0;
        currentRound = 1;
        matchOver = false;
        isVictory = false;
        isFadingOut = false;
        playerTurn = true;
        roundOver = false;
        gameOver = false;

        player.resetForNewRound();
        enemy.resetForNewRound();

        playerOpacity = 1.0f;
        enemyOpacity = 1.0f;

        setSprite(true, "idle", "idle");
        setSprite(false, "idle", "idle");

        btnTryAgain.setVisible(false);
        btnNext.setVisible(false);
        btnNext.setText("CONTINUE ▶");
        setActionsEnabled(true);

        updateRoundLabel();
        updateScoreLabel();
        updateTurnIndicator("YOUR TURN");

        battleLog.setText("");
        startRound();
        refreshUI();
        repaint();
    }

    // ── UI Helper Methods ───────────────────────────────────────────────────

    private void refreshUI() {
        if (player != null && enemy != null) {
            hpBarP.setValue(player.getHp());
            hpLblP.setText("HP: " + player.getHp() + "/" + player.getMaxHp());
            hpBarE.setValue(enemy.getHp());
            hpLblE.setText("HP: " + enemy.getHp() + "/" + enemy.getMaxHp());

            manaBarP.setValue(player.getMana());
            manaLblP.setText("MP: " + player.getMana() + "/" + player.getMaxMana());
            manaBarE.setValue(enemy.getMana());
            manaLblE.setText("MP: " + enemy.getMana() + "/" + enemy.getMaxMana());

            updateSkillButtonTexts();
            updateSkillButtonStates();
        }
    }

    private void updateSkillButtonTexts() {
        btnBasic.setText(String.format("<html><center>BASIC<br><font size='2'>[MP: %d | DMG: %d]</font></center></html>",
            player.getActionCost(0), player.getActionDamage(0)));
        btnSkill1.setText(String.format("<html><center>%s<br><font size='2'>[MP: %d | DMG: %d]</font></center></html>",
            player.getSkill1Name(), player.getActionCost(1), player.getActionDamage(1)));
        btnSkill2.setText(String.format("<html><center>%s<br><font size='2'>[MP: %d | DMG: %d]</font></center></html>",
            player.getSkill2Name(), player.getActionCost(2), player.getActionDamage(2)));
       
           
        btnUlt.setText(String.format("<html><center>%s<br><font size='2'>[MP: %d | DMG: %d]</font></center></html>",
            player.getUltimateName(), player.getActionCost(4), player.getActionDamage(4)));
    }

    private void updateSkillButtonStates() {
        btnBasic.setEnabled(true);
        btnSkill1.setEnabled(player.canUse(1));
        btnSkill2.setEnabled(player.canUse(2));
       
        btnUlt.setEnabled(player.canUse(4));
    }

    private void syncBarMaximums() {
        hpBarP.setMaximum(player.getMaxHp());
        hpBarE.setMaximum(enemy.getMaxHp());
        manaBarP.setMaximum(player.getMaxMana());
        manaBarE.setMaximum(enemy.getMaxMana());
        refreshUI();
    }

    private JPanel createStatPanel(String name, boolean isPlayer) {
        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBackground(new Color(0, 0, 0, 200));
        box.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 215, 0), 2),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        box.setPreferredSize(new Dimension(260, 140));

        JLabel n = new JLabel(name.toUpperCase());
        n.setFont(new Font("Impact", Font.PLAIN, 18));
        n.setForeground(new Color(255, 215, 0));
        n.setAlignmentX(Component.CENTER_ALIGNMENT);

        JProgressBar hp = new JProgressBar();
        hp.setForeground(new Color(40, 180, 100));
        hp.setBackground(new Color(80, 40, 40));
        hp.setPreferredSize(new Dimension(220, 18));

        JLabel hpLabel = new JLabel("HP: 100/100");
        hpLabel.setFont(new Font("Arial", Font.BOLD, 12));
        hpLabel.setForeground(Color.WHITE);
        hpLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JProgressBar mn = new JProgressBar();
        mn.setForeground(new Color(40, 120, 200));
        mn.setBackground(new Color(40, 40, 80));
        mn.setPreferredSize(new Dimension(220, 18));

        JLabel manaLabel = new JLabel("MP: 0/100");
        manaLabel.setFont(new Font("Arial", Font.BOLD, 12));
        manaLabel.setForeground(new Color(100, 200, 255));
        manaLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        if (isPlayer) {
            hpBarP = hp; manaBarP = mn; hpLblP = hpLabel; manaLblP = manaLabel;
        } else {
            hpBarE = hp; manaBarE = mn; hpLblE = hpLabel; manaLblE = manaLabel;
        }

        box.add(n);
        box.add(Box.createRigidArea(new Dimension(0, 5)));
        box.add(hpLabel);
        box.add(hp);
        box.add(Box.createRigidArea(new Dimension(0, 5)));
        box.add(manaLabel);
        box.add(mn);
        return box;
    }

    private void setActionsEnabled(boolean enabled) {
        if (!enabled) {
            btnBasic.setEnabled(false);
            btnSkill1.setEnabled(false);
            btnSkill2.setEnabled(false);
            
            btnUlt.setEnabled(false);
            btnForfeit.setEnabled(false);
        } else {
            btnBasic.setEnabled(true);
            btnSkill1.setEnabled(player.canUse(1));
            btnSkill2.setEnabled(player.canUse(2));
          
            btnUlt.setEnabled(player.canUse(4));
            btnForfeit.setEnabled(true);
        }
    }

    private void updateTurnIndicator(String text) {
        turnIndicator.setText(text);
        if (text.contains("YOUR") && text.length() > 4) {
            turnIndicator.setForeground(Color.GREEN);
        } else if (text.contains("ENEMY") && text.length() > 5) {
            turnIndicator.setForeground(Color.ORANGE);
        } else if (text.equals("ATTACKING...") || text.equals("ENEMY ATTACKING...")) {
            turnIndicator.setForeground(Color.RED);
        } else {
            turnIndicator.setForeground(Color.WHITE);
        }
    }

    private void updateRoundLabel() {
        roundLabel.setText("ROUND " + currentRound + " / 3");
    }

    private void updateScoreLabel() {
        scoreLabel.setText(playerScore + " - " + aiScore);
    }

    private void logMessage(String msg) {
        battleLog.append(msg + "\n");
        battleLog.setCaretPosition(battleLog.getDocument().getLength());
    }

    private JLabel makeLabel(String t, int s, Color c) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("Impact", Font.PLAIN, s));
        l.setForeground(c);
        l.setHorizontalAlignment(SwingConstants.CENTER);
        return l;
    }
}
