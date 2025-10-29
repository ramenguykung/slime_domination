package com.player;

public class EnemySlime extends Player {
    private int level;
    
    public EnemySlime(int level) {
        super();
        this.level = level;
        setName("Enemy Slime");
        
        // Scale stats based on level
        int baseHealth = 80;
        double baseDamage = 8.0;
        double baseAttackSpeed = 1.0;

        setMaxHealth(baseHealth + (level * 15)); // +15 HP per level
        setHealth(getMaxHealth());
        setDamege(baseDamage + (level * 2)); // +2 damage per level
        setAttackSpeed(baseAttackSpeed + (level * 0.1)); // +0.1 attack speed per level
    }
    
    public int getLevel() {
        return level;
    }
    
    public void takeDamage(double damage) {
        int newHealth = (int) Math.max(0, getHealth() - damage);
        setHealth(newHealth);
    }
}
