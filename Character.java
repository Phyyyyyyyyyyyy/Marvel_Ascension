/**
 * ABSTRACTION + INHERITANCE BASE
 *
 * Abstract class representing any combatant in Marvel Ascension.
 * Both Hero and Enemy extend this class, enforcing a shared contract
 * while allowing each to implement behavior differently (Polymorphism).
 *
 * OOP Concepts demonstrated here:
 *  - Abstraction:   Abstract methods force subclasses to define their own behavior.
 *  - Encapsulation: All fields are private; accessed only through getters/setters.
 *  - Inheritance:   Hero and Enemy both extend Character, inheriting shared logic.
 *  - Polymorphism:  displayStats(), useSkill(), chooseAction() are overridden per subclass.
 */
public abstract class Character {

    // --- Encapsulation: private fields, controlled via getters/setters ---
    private String name;
    private int hp;
    private int maxHp;
    private int attack;
    private int mana;

    protected String skill1;
    protected String skill2;
    protected String skill3;

    // --- Constructor ---
    public Character(String name, int hp, int maxHp, int attack, int mana,
                     String skill1, String skill2, String skill3) {
        this.name   = name;
        this.hp     = hp;
        this.maxHp  = maxHp;
        this.attack = attack;
        this.mana   = mana;
        this.skill1 = skill1;
        this.skill2 = skill2;
        this.skill3 = skill3;
    }

    // =========================================================
    // ABSTRACTION — subclasses MUST implement these methods
    // =========================================================

    /**
     * Display character stats to console.
     * Each subclass formats this differently (Polymorphism via override).
     */
    public abstract void displayStats();

    /**
     * Use a numbered skill (1, 2, or 3).
     * @return damage dealt, or 0 if skill couldn't be used
     */
    public abstract int useSkill(int skillNumber);

    /**
     * AI or logic-based action selection.
     * @return 0 = basic attack, 1/2/3 = skill index
     */
    public abstract int chooseAction();

    // =========================================================
    // CONCRETE SHARED BEHAVIOUR (Inheritance — available to all subclasses)
    // =========================================================

    /** @return true if this character still has HP remaining */
    public boolean isAlive() {
        return hp > 0;
    }

    /** Apply damage, clamped to 0 */
    public void takeDamage(int damage) {
        hp = Math.max(0, hp - damage);
        System.out.println("\t\t\t\t" + name + " takes " + damage + " damage! HP: " + hp + "/" + maxHp);
    }

    /** Restore HP, clamped to maxHp */
    public void heal(int amount) {
        hp = Math.min(maxHp, hp + amount);
        System.out.println("\t\t\t\t" + name + " heals " + amount + " HP! HP: " + hp + "/" + maxHp);
    }

    /** Restore mana, clamped to 100 */
    public void restoreMana(int amount) {
        mana = Math.min(100, mana + amount);
    }

    /** Perform a basic attack — returns damage equal to attack stat */
    public int basicAttack() {
        System.out.println("\t\t\t\t" + name + " performs a basic attack! Deals " + attack + " damage!");
        return attack;
    }

    /** Display an intro block for this character */
    public void displayIntro() {
        System.out.println("\n\t\t\t\t\t\t\tCharacter: " + name);
        System.out.println("\t\t\t\t\t\t\tHP: " + hp + " | ATK: " + attack + " | Mana: " + mana);
        System.out.println("\t\t\t\t\t\t\tSkills: " + skill1 + " | " + skill2 + " | " + skill3);
    }

    // =========================================================
    // ENCAPSULATION — Getters and Setters
    // =========================================================

    public String getName()             { return name; }
    public void   setName(String name)  { this.name = name; }

    public int  getHp()                 { return hp; }
    public void setHp(int hp)           { this.hp = Math.max(0, Math.min(hp, maxHp)); }

    public int  getMaxHp()              { return maxHp; }
    public void setMaxHp(int maxHp)     { this.maxHp = maxHp; }

    public int  getAttack()             { return attack; }
    public void setAttack(int attack)   { this.attack = attack; }

    public int  getMana()               { return mana; }
    public void setMana(int mana)       { this.mana = Math.max(0, Math.min(mana, 100)); }

    public String getSkill1()           { return skill1; }
    public String getSkill2()           { return skill2; }
    public String getSkill3()           { return skill3; }

    /** Convenience: return all three skill names as an array */
    public String[] getSkills()         { return new String[]{skill1, skill2, skill3}; }

    @Override
    public String toString() {
        return "[" + getClass().getSimpleName() + "] " + name +
               " | HP: " + hp + "/" + maxHp +
               " | ATK: " + attack +
               " | Mana: " + mana;
    }
}
