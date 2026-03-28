import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Enemy — Mirror Mode Version
 * Uses existing Hero data to create AI opponents quickly.
 */
public class Enemy extends Combatant {

    public enum EnemyTier { EASY, MEDIUM, HARD, BOSS }

    private static final Random rand = new Random();
    private final EnemyTier tier;

    public Enemy(String name, int hp, int attack,
                 String skill1, String skill2, String skill3, String ultimate,
                 int sk1Cost, int sk2Cost, int sk3Cost, int ultCost,
                 int sk1Dmg, int sk2Dmg, int sk3Dmg, int ultDmg,
                 EnemyTier tier) {
        super(name, hp, attack, skill1, skill2, skill3, ultimate,
              sk1Cost, sk2Cost, sk3Cost, ultCost, sk1Dmg, sk2Dmg, sk3Dmg, ultDmg);
        this.tier = tier;
        setCooldownManager(new CooldownManager());
    }

    @Override
    public int decideAction() {
        // AI Logic: Prioritize Ultimate if health is low, otherwise random skills
        if (canUse(4) && getHp() < getMaxHp() * 0.4) return 4;
        
        if (rand.nextInt(100) < 65) {
            List<Integer> available = new ArrayList<>();
            if (canUse(3)) available.add(3);
            if (canUse(2)) available.add(2);
            if (canUse(1)) available.add(1);
            
            if (!available.isEmpty())
                return available.get(rand.nextInt(available.size()));
        }
        return 0; // Basic Attack
    }

    // ── MIRROR HERO DATABASE ──────────────────────────────────────────────
    // Complete database with all 8 heroes plus BOSS versions
    private static final List<Enemy> HERO_MIRROR_DATABASE = List.of(
        // EASY TIER (Lower stats)
        new Enemy("Iron Man", 90, 12, "Repulsor Blast", "Micro-Missiles", "Shield Flare", "Unibeam Overload",
            12, 15, 18, 35, 12, 18, 22, 40, EnemyTier.EASY),
        new Enemy("Captain America", 100, 10, "Shield Throw", "Vibranium Bash", "Tactical Command", "Avengers Assemble",
            12, 15, 0, 35, 10, 16, 0, 38, EnemyTier.EASY),
        new Enemy("Spider-Man", 85, 12, "Web Snare", "Spider-Sense Dodge", "Swing Kick", "Maximum Spider",
            12, 0, 18, 35, 12, 0, 20, 42, EnemyTier.EASY),
        new Enemy("Black Widow", 88, 14, "Widow's Bite", "Dual Pistols", "Staff Strike", "Lullaby Takedown",
            12, 15, 18, 35, 14, 18, 20, 40, EnemyTier.EASY),
        
        // MEDIUM TIER (Standard stats)
        new Enemy("Iron Man", 110, 18, "Repulsor Blast", "Micro-Missiles", "Shield Flare", "Unibeam Overload",
            15, 20, 25, 40, 18, 25, 30, 55, EnemyTier.MEDIUM),
        new Enemy("Captain America", 130, 15, "Shield Throw", "Vibranium Bash", "Tactical Command", "Avengers Assemble",
            15, 20, 20, 40, 15, 22, 25, 50, EnemyTier.MEDIUM),
        new Enemy("Thor", 140, 20, "Hammer Toss", "Lightning Strike", "Thunder Clap", "God Blast",
            15, 20, 25, 45, 20, 28, 32, 58, EnemyTier.MEDIUM),
        new Enemy("Spider-Man", 100, 14, "Web Snare", "Spider-Sense Dodge", "Swing Kick", "Maximum Spider",
            15, 0, 22, 40, 14, 0, 26, 48, EnemyTier.MEDIUM),
        
        // HARD TIER (Boosted stats)
        new Enemy("Thor", 160, 24, "Hammer Toss", "Lightning Strike", "Thunder Clap", "God Blast",
            18, 22, 28, 48, 24, 30, 38, 65, EnemyTier.HARD),
        new Enemy("Hulk", 190, 28, "Gamma Punch", "Thunderclap", "Ground Smash", "Worldbreaker Slam",
            18, 22, 30, 50, 28, 32, 40, 75, EnemyTier.HARD),
        new Enemy("The Falcon", 115, 18, "Wing Shield", "Redwing Strike", "Aerial Dive", "Flight Form Alpha",
            15, 20, 25, 45, 18, 24, 28, 52, EnemyTier.HARD),
        new Enemy("Ant-Man", 110, 15, "Size Shift", "Ant Swarm", "Pym Disk", "Giant-Man Stomp",
            15, 20, 25, 45, 15, 20, 28, 50, EnemyTier.HARD),
        
        // BOSS TIER (Very high stats)
        new Enemy("Thanos", 250, 35, "Titan Punch", "Power Stone", "Space Warp", "The Snap",
            20, 28, 35, 60, 35, 45, 50, 100, EnemyTier.BOSS),
        new Enemy("Ultron Prime", 220, 32, "Laser Pulse", "Drone Swarm", "System Hack", "Extinction Protocol",
            20, 25, 32, 55, 32, 38, 45, 85, EnemyTier.BOSS),
        new Enemy("Dormammu", 200, 30, "Dark Dimension", "Flame Wave", "Mind Possession", "Dimension Collapse",
            18, 24, 30, 50, 30, 35, 40, 80, EnemyTier.BOSS),
        new Enemy("Loki", 180, 28, "Scepter Blast", "Illusion", "Mind Control", "God of Mischief",
            18, 22, 28, 48, 28, 30, 35, 70, EnemyTier.BOSS)
    );

    public static Enemy getRandomEnemy() {
        return cloneMirror(HERO_MIRROR_DATABASE.get(rand.nextInt(HERO_MIRROR_DATABASE.size())));
    }

    public static Enemy getRandomEnemyByLevel(int level) {
        EnemyTier tier;
        switch (level) {
            case 1: tier = EnemyTier.EASY; break;
            case 2: tier = EnemyTier.MEDIUM; break;
            case 3: tier = EnemyTier.HARD; break;
            case 4: tier = EnemyTier.BOSS; break;
            default: return getRandomEnemy();
        }
        List<Enemy> filtered = new ArrayList<>();
        for (Enemy e : HERO_MIRROR_DATABASE) if (e.tier == tier) filtered.add(e);
        
        // If no enemies of that tier, return random enemy
        if (filtered.isEmpty()) return getRandomEnemy();
        
        return cloneMirror(filtered.get(rand.nextInt(filtered.size())));
    }

    public static Enemy getEnemyByName(String name) {
        for (Enemy e : HERO_MIRROR_DATABASE)
            if (e.getName().equalsIgnoreCase(name)) return cloneMirror(e);
        return getRandomEnemy();
    }

    private static Enemy cloneMirror(Enemy e) {
        return new Enemy(e.getName(), e.getMaxHp(), e.getAttack(),
            e.getSkill1Name(), e.getSkill2Name(), e.getSkill3Name(), e.getUltimateName(),
            e.getSk1Cost(), e.getSk2Cost(), e.getSk3Cost(), e.getUltCost(),
            e.getActionDamage(1), e.getActionDamage(2), e.getActionDamage(3), e.getActionDamage(4),
            e.tier);
    }
}
