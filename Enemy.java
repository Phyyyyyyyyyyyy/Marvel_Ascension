import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Enemy implements Interfaces.Combatant {
    // Properties
    private String name;
    private int hp;
    private int maxHp;
    private int attack;
    private String skill1;
    private String skill2;
    private String skill3;
    private int sk1Cost;
    private int sk2Cost;
    private int sk3Cost;
    private int sk1Damage;
    private int sk2Damage;
    private int sk3Damage;
    private int mana;

    // Constructor
    public Enemy(String name, int hp, int maxHp, int attack,
                 String skill1, String skill2, String skill3,
                 int sk1Cost, int sk2Cost, int sk3Cost,
                 int sk1Damage, int sk2Damage, int sk3Damage,
                 int mana) {
        this.name = name;
        this.hp = hp;
        this.maxHp = maxHp;
        this.attack = attack;
        this.skill1 = skill1;
        this.skill2 = skill2;
        this.skill3 = skill3;
        this.sk1Cost = sk1Cost;
        this.sk2Cost = sk2Cost;
        this.sk3Cost = sk3Cost;
        this.sk1Damage = sk1Damage;
        this.sk2Damage = sk2Damage;
        this.sk3Damage = sk3Damage;
        this.mana = mana;
    }

    // Display Methods
    public void displayIntro() {
        System.out.println("\n\t\t\t\t\t\t\tEnemy: " + getName());
        System.out.println("\t\t\t\t\t\t\tStats - HP: " + getHp() + " | Attack: " + getAttack() + " | Mana: " + getMana());
        System.out.println("\t\t\t\t\t\t\tSkills:");
        System.out.println("\t\t\t\t\t\t\t" + getSkill1() + " - " + getSk1Cost() + " mana (Damage: " + getSk1Damage() + ")");
        System.out.println("\t\t\t\t\t\t\t" + getSkill2() + " - " + getSk2Cost() + " mana (Damage: " + getSk2Damage() + ")");
        System.out.println("\t\t\t\t\t\t\t" + getSkill3() + " - " + getSk3Cost() + " mana (Damage: " + getSk3Damage() + ")");
        System.out.println();
    }

    public void displayStats() {
        System.out.println("\t\t\t\t" + getName() + " - HP: " + getHp() + "/" + getMaxHp() + " | Mana: " + getMana());
    }

    // Combat Methods
    public boolean isAlive() {
        return hp > 0;
    }

    public void takeDamage(int damage) {
        hp = Math.max(0, hp - damage);
        System.out.println("\t\t\t\t" + name + " takes " + damage + " damage! HP: " + hp + "/" + maxHp);
    }

    public void heal(int amount) {
        hp = Math.min(maxHp, hp + amount);
        System.out.println("\t\t\t\t" + name + " heals " + amount + " HP! HP: " + hp + "/" + maxHp);
    }

    public void restoreMana(int amount) {
        mana = Math.min(100, mana + amount);
    }

    public boolean canUseSkill(int skillNumber) {
        switch(skillNumber) {
            case 1: return mana >= sk1Cost;
            case 2: return mana >= sk2Cost;
            case 3: return mana >= sk3Cost;
            default: return false;
        }
    }

    public int useSkill(int skillNumber) {
        int damage = 0;
        String skillName = "";
        
        switch(skillNumber) {
            case 1:
                if(canUseSkill(1)) {
                    mana -= sk1Cost;
                    damage = sk1Damage;
                    skillName = skill1;
                }
                break;
            case 2:
                if(canUseSkill(2)) {
                    mana -= sk2Cost;
                    damage = sk2Damage;
                    skillName = skill2;
                }
                break;
            case 3:
                if(canUseSkill(3)) {
                    mana -= sk3Cost;
                    damage = sk3Damage;
                    skillName = skill3;
                }
                break;
        }
        
        if(damage > 0) {
            System.out.println("\t\t\t\t" + name + " uses " + skillName + "! Deals " + damage + " damage!");
        } else {
            System.out.println("\t\t\t\t" + name + " tries to use a skill but doesn't have enough mana!");
        }
        
        return damage;
    }

    public int basicAttack() {
        int damage = attack;
        System.out.println("\t\t\t\t" + name + " performs a basic attack! Deals " + damage + " damage!");
        return damage;
    }

    /**
     * Enemy AI decision making - chooses an action
     * @return 0 = basic attack, 1 = skill 1, 2 = skill 2, 3 = skill 3
     */
    public int chooseAction() {
        Random rand = new Random();
        
        // 70% chance to use a skill, 30% chance to basic attack
        if(rand.nextInt(100) < 70) {
            return chooseSkill();
        } else {
            return 0; // Basic attack
        }
    }

    /**
     * Enemy AI decision making - chooses a skill to use
     * @return The index of the skill to use (1, 2, or 3)
     */
    private int chooseSkill() {
        Random rand = new Random();
        
        // Check which skills are available
        List<Integer> availableSkills = new ArrayList<>();
        if(canUseSkill(1)) availableSkills.add(1);
        if(canUseSkill(2)) availableSkills.add(2);
        if(canUseSkill(3)) availableSkills.add(3);
        
        // If no skills available, return 0 for basic attack
        if(availableSkills.isEmpty()) {
            return 0;
        }
        
        // Simple AI: If mana is low, prefer cheaper skills
        if(getMana() < 20) {
            // Prefer skill 1 (usually cheapest)
            if(availableSkills.contains(1)) return 1;
        } else if(getMana() < 25) {
            // Prefer skill 1 or 2
            if(availableSkills.contains(1) && availableSkills.contains(2)) {
                return rand.nextBoolean() ? 1 : 2;
            } else if(availableSkills.contains(1)) {
                return 1;
            } else if(availableSkills.contains(2)) {
                return 2;
            }
        }
        
        // Random skill from available ones
        return availableSkills.get(rand.nextInt(availableSkills.size()));
    }

    // Static Factory Methods
    public static Enemy getRandomEnemy() {
        List<Enemy> enemies = new ArrayList<>();
        Random rand = new Random();

        // Tier 1 enemies (easier)
        enemies.add(new Enemy("Loki", 100, 100, 15,
                "Illusion Sneak Attack", "Scepter Strike", "Mind Control",
                15, 20, 25, 15, 20, 25, 100));

        enemies.add(new Enemy("Mystique", 100, 100, 16,
                "Shape Shift Strike", "Mimic Attack", "Invisible Hit",
                15, 20, 25, 15, 20, 25, 100));

        enemies.add(new Enemy("Green Goblin", 100, 100, 16,
                "Pumpkin Bomb", "Glider Attack", "Goblin's Rage",
                15, 20, 25, 15, 20, 25, 100));

        // Tier 2 enemies (medium difficulty)
        enemies.add(new Enemy("Ultron", 110, 110, 18,
                "Laser Blast", "Metal Punch", "Flight Thrust Attack",
                15, 15, 20, 15, 15, 20, 100));

        enemies.add(new Enemy("Red Skull", 110, 110, 16,
                "Cosmic Blast", "Tactical Strike", "Rally Troops",
                15, 15, 20, 15, 15, 20, 100));

        enemies.add(new Enemy("Electro", 110, 110, 17,
                "Electric Shock", "Thunderbolt", "Overcharge",
                15, 15, 20, 15, 15, 20, 100));

        enemies.add(new Enemy("Sabretooth", 110, 110, 18,
                "Ferocious Claw", "Savage Bite", "Howling Vengeance",
                15, 15, 20, 15, 15, 20, 100));

        enemies.add(new Enemy("Sandman", 110, 110, 18,
                "Sand Blast", "Morphing Strike", "Sand Smash",
                15, 15, 20, 15, 15, 20, 100));

        // Tier 3 enemies (harder)
        enemies.add(new Enemy("Venom", 120, 120, 17,
                "Symbiote Strike", "Web Trap", "Rage",
                12, 13, 15, 12, 13, 15, 100));

        enemies.add(new Enemy("Doctor Octopus", 120, 120, 17,
                "Tentacle Slam", "Mechanical Grab", "Overload",
                12, 13, 15, 12, 13, 15, 100));

        enemies.add(new Enemy("Magneto", 120, 120, 18,
                "Magnetic Pulse", "Metal Manipulation", "Force Field Attack",
                12, 13, 15, 12, 13, 15, 100));

        enemies.add(new Enemy("Hela", 120, 120, 19,
                "Necrosword Slash", "Minions Attack", "Asgardian Fury",
                12, 13, 15, 12, 13, 15, 100));

        enemies.add(new Enemy("Kingpin", 120, 120, 19,
                "Heavy Punch", "Ground Slam", "Intimidate",
                12, 13, 15, 12, 13, 15, 100));

        enemies.add(new Enemy("Juggernaut", 120, 120, 20,
                "Unstoppable Charge", "Ground Pound", "Rage Mode",
                12, 13, 15, 12, 13, 15, 100));

        // Tier 4 enemies (boss level)
        enemies.add(new Enemy("Thanos", 130, 130, 20,
                "Power Stone Punch", "Space Stone Snap", "Reality Warp",
                18, 20, 22, 18, 20, 22, 100));

        enemies.add(new Enemy("Galactus", 150, 150, 25,
                "Cosmic Blast", "Planet Devourer", "Universal Destruction",
                20, 25, 30, 20, 25, 30, 100));

        enemies.add(new Enemy("Dormammu", 140, 140, 22,
                "Dark Dimension", "Flame Wave", "Mind Possession",
                18, 22, 25, 18, 22, 25, 100));

        return enemies.get(rand.nextInt(enemies.size()));
    }
    
    /**
     * Get a random enemy based on difficulty level
     * @param level 1 = easy, 2 = medium, 3 = hard, 4 = boss
     * @return a random enemy from the specified tier
     */
    public static Enemy getRandomEnemyByLevel(int level) {
        List<Enemy> enemies = new ArrayList<>();
        Random rand = new Random();
        
        switch(level) {
            case 1: // Easy enemies
                enemies.add(new Enemy("Loki", 100, 100, 15,
                        "Illusion Sneak Attack", "Scepter Strike", "Mind Control",
                        15, 20, 25, 15, 20, 25, 100));
                enemies.add(new Enemy("Mystique", 100, 100, 16,
                        "Shape Shift Strike", "Mimic Attack", "Invisible Hit",
                        15, 20, 25, 15, 20, 25, 100));
                enemies.add(new Enemy("Green Goblin", 100, 100, 16,
                        "Pumpkin Bomb", "Glider Attack", "Goblin's Rage",
                        15, 20, 25, 15, 20, 25, 100));
                break;
                
            case 2: // Medium enemies
                enemies.add(new Enemy("Ultron", 110, 110, 18,
                        "Laser Blast", "Metal Punch", "Flight Thrust Attack",
                        15, 15, 20, 15, 15, 20, 100));
                enemies.add(new Enemy("Red Skull", 110, 110, 16,
                        "Cosmic Blast", "Tactical Strike", "Rally Troops",
                        15, 15, 20, 15, 15, 20, 100));
                enemies.add(new Enemy("Electro", 110, 110, 17,
                        "Electric Shock", "Thunderbolt", "Overcharge",
                        15, 15, 20, 15, 15, 20, 100));
                enemies.add(new Enemy("Sabretooth", 110, 110, 18,
                        "Ferocious Claw", "Savage Bite", "Howling Vengeance",
                        15, 15, 20, 15, 15, 20, 100));
                enemies.add(new Enemy("Sandman", 110, 110, 18,
                        "Sand Blast", "Morphing Strike", "Sand Smash",
                        15, 15, 20, 15, 15, 20, 100));
                break;
                
            case 3: // Hard enemies
                enemies.add(new Enemy("Venom", 120, 120, 17,
                        "Symbiote Strike", "Web Trap", "Rage",
                        12, 13, 15, 12, 13, 15, 100));
                enemies.add(new Enemy("Doctor Octopus", 120, 120, 17,
                        "Tentacle Slam", "Mechanical Grab", "Overload",
                        12, 13, 15, 12, 13, 15, 100));
                enemies.add(new Enemy("Magneto", 120, 120, 18,
                        "Magnetic Pulse", "Metal Manipulation", "Force Field Attack",
                        12, 13, 15, 12, 13, 15, 100));
                enemies.add(new Enemy("Hela", 120, 120, 19,
                        "Necrosword Slash", "Minions Attack", "Asgardian Fury",
                        12, 13, 15, 12, 13, 15, 100));
                enemies.add(new Enemy("Kingpin", 120, 120, 19,
                        "Heavy Punch", "Ground Slam", "Intimidate",
                        12, 13, 15, 12, 13, 15, 100));
                enemies.add(new Enemy("Juggernaut", 120, 120, 20,
                        "Unstoppable Charge", "Ground Pound", "Rage Mode",
                        12, 13, 15, 12, 13, 15, 100));
                break;
                
            case 4: // Boss enemies
                enemies.add(new Enemy("Thanos", 130, 130, 20,
                        "Power Stone Punch", "Space Stone Snap", "Reality Warp",
                        18, 20, 22, 18, 20, 22, 100));
                enemies.add(new Enemy("Galactus", 150, 150, 25,
                        "Cosmic Blast", "Planet Devourer", "Universal Destruction",
                        20, 25, 30, 20, 25, 30, 100));
                enemies.add(new Enemy("Dormammu", 140, 140, 22,
                        "Dark Dimension", "Flame Wave", "Mind Possession",
                        18, 22, 25, 18, 22, 25, 100));
                break;
                
            default:
                return getRandomEnemy();
        }
        
        return enemies.get(rand.nextInt(enemies.size()));
    }
    
    /**
     * Get a specific enemy by name
     * @param name The enemy name to search for
     * @return The enemy if found, otherwise a random enemy
     */
    public static Enemy getEnemyByName(String name) {
        List<Enemy> allEnemies = getAllEnemies();
        
        for(Enemy enemy : allEnemies) {
            if(enemy.getName().equalsIgnoreCase(name)) {
                // Create a fresh copy
                return new Enemy(enemy.getName(), enemy.getMaxHp(), enemy.getMaxHp(), 
                               enemy.getAttack(), enemy.getSkill1(), enemy.getSkill2(), 
                               enemy.getSkill3(), enemy.getSk1Cost(), enemy.getSk2Cost(), 
                               enemy.getSk3Cost(), enemy.getSk1Damage(), enemy.getSk2Damage(), 
                               enemy.getSk3Damage(), 100);
            }
        }
        
        return getRandomEnemy();
    }
    
    /**
     * Get all available enemies
     * @return List of all enemies
     */
    public static List<Enemy> getAllEnemies() {
        List<Enemy> allEnemies = new ArrayList<>();
        
        // Tier 1
        allEnemies.add(new Enemy("Loki", 100, 100, 15,
                "Illusion Sneak Attack", "Scepter Strike", "Mind Control",
                15, 20, 25, 15, 20, 25, 100));
        allEnemies.add(new Enemy("Mystique", 100, 100, 16,
                "Shape Shift Strike", "Mimic Attack", "Invisible Hit",
                15, 20, 25, 15, 20, 25, 100));
        allEnemies.add(new Enemy("Green Goblin", 100, 100, 16,
                "Pumpkin Bomb", "Glider Attack", "Goblin's Rage",
                15, 20, 25, 15, 20, 25, 100));
        
        // Tier 2
        allEnemies.add(new Enemy("Ultron", 110, 110, 18,
                "Laser Blast", "Metal Punch", "Flight Thrust Attack",
                15, 15, 20, 15, 15, 20, 100));
        allEnemies.add(new Enemy("Red Skull", 110, 110, 16,
                "Cosmic Blast", "Tactical Strike", "Rally Troops",
                15, 15, 20, 15, 15, 20, 100));
        allEnemies.add(new Enemy("Electro", 110, 110, 17,
                "Electric Shock", "Thunderbolt", "Overcharge",
                15, 15, 20, 15, 15, 20, 100));
        allEnemies.add(new Enemy("Sabretooth", 110, 110, 18,
                "Ferocious Claw", "Savage Bite", "Howling Vengeance",
                15, 15, 20, 15, 15, 20, 100));
        allEnemies.add(new Enemy("Sandman", 110, 110, 18,
                "Sand Blast", "Morphing Strike", "Sand Smash",
                15, 15, 20, 15, 15, 20, 100));
        
        // Tier 3
        allEnemies.add(new Enemy("Venom", 120, 120, 17,
                "Symbiote Strike", "Web Trap", "Rage",
                12, 13, 15, 12, 13, 15, 100));
        allEnemies.add(new Enemy("Doctor Octopus", 120, 120, 17,
                "Tentacle Slam", "Mechanical Grab", "Overload",
                12, 13, 15, 12, 13, 15, 100));
        allEnemies.add(new Enemy("Magneto", 120, 120, 18,
                "Magnetic Pulse", "Metal Manipulation", "Force Field Attack",
                12, 13, 15, 12, 13, 15, 100));
        allEnemies.add(new Enemy("Hela", 120, 120, 19,
                "Necrosword Slash", "Minions Attack", "Asgardian Fury",
                12, 13, 15, 12, 13, 15, 100));
        allEnemies.add(new Enemy("Kingpin", 120, 120, 19,
                "Heavy Punch", "Ground Slam", "Intimidate",
                12, 13, 15, 12, 13, 15, 100));
        allEnemies.add(new Enemy("Juggernaut", 120, 120, 20,
                "Unstoppable Charge", "Ground Pound", "Rage Mode",
                12, 13, 15, 12, 13, 15, 100));
        
        // Tier 4 - Bosses
        allEnemies.add(new Enemy("Thanos", 130, 130, 20,
                "Power Stone Punch", "Space Stone Snap", "Reality Warp",
                18, 20, 22, 18, 20, 22, 100));
        allEnemies.add(new Enemy("Galactus", 150, 150, 25,
                "Cosmic Blast", "Planet Devourer", "Universal Destruction",
                20, 25, 30, 20, 25, 30, 100));
        allEnemies.add(new Enemy("Dormammu", 140, 140, 22,
                "Dark Dimension", "Flame Wave", "Mind Possession",
                18, 22, 25, 18, 22, 25, 100));
        
        return allEnemies;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getHp() { return hp; }
    public void setHp(int hp) { this.hp = Math.max(0, Math.min(hp, maxHp)); }

    public int getMaxHp() { return maxHp; }
    public void setMaxHp(int maxHp) { this.maxHp = maxHp; }

    public int getAttack() { return attack; }
    public void setAttack(int attack) { this.attack = attack; }

    public String getSkill1() { return skill1; }
    public String getSkill2() { return skill2; }
    public String getSkill3() { return skill3; }

    public int getSk1Cost() { return sk1Cost; }
    public int getSk2Cost() { return sk2Cost; }
    public int getSk3Cost() { return sk3Cost; }

    public int getSk1Damage() { return sk1Damage; }
    public int getSk2Damage() { return sk2Damage; }
    public int getSk3Damage() { return sk3Damage; }

    public int getMana() { return mana; }
    public void setMana(int mana) { this.mana = Math.max(0, Math.min(mana, 100)); }
}
