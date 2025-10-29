package com.player;

public class PlayerSlime extends Player {
    private int skillPoints;
    private int roundsWon;
    
    public PlayerSlime() {
        super();
        setName("Player Slime");
        setHealth(100);
        setMaxHealth(100);
        setDamege(10);
        setAttackSpeed(1.1);
        this.skillPoints = 0;
        this.roundsWon = 0;
    }
    
    public int getSkillPoints() {
        return skillPoints;
    }
    
    public void addSkillPoints(int points) {
        this.skillPoints += points;
    }
    
    public boolean spendSkillPoints(int points) {
        if (this.skillPoints >= points) {
            this.skillPoints -= points;
            return true;
        }
        return false;
    }
    
    public int getRoundsWon() {
        return roundsWon;
    }
    
    public void incrementRoundsWon() {
        this.roundsWon++;
    }
    
    public void resetForNewRound() {
        setHealth(getMaxHealth());
    }
    
    public void takeDamage(double damage) {
        int newHealth = (int) Math.max(0, getHealth() - damage);
        setHealth(newHealth);
    }
    
    // Upgrade methods
    public void upgradeMaxHealth(int amount) {
        setMaxHealth(getMaxHealth() + amount);
        setHealth(getHealth() + amount); // Also increase current health
    }
    
    public void upgradeAttack(double amount) {
        setDamege(getDamege() + amount);
    }
    
    public void upgradeAttackSpeed(double amount) {
        setAttackSpeed(getAttackSpeed() + amount);
    }
}
