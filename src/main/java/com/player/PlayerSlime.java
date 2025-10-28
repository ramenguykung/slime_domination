package com.player;

public class PlayerSlime extends Player {
    private int maxHealth;
    private int skillPoints;
    private int roundsWon;
    
    public PlayerSlime() {
        super();
        setName("Player Slime");
        setHealth(100);
        this.maxHealth = 100;
        setDamege(10);
        setAttackSpeed(1.1);
        this.skillPoints = 0;
        this.roundsWon = 0;
    }
    
    public int getMaxHealth() {
        return maxHealth;
    }
    
    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }
    
    public int getSkillPoints() {
        return skillPoints;
    }
    
    public void addSkillPoints(int points) {
        this.skillPoints += points;
    }
    
    public int getRoundsWon() {
        return roundsWon;
    }
    
    public void incrementRoundsWon() {
        this.roundsWon++;
    }
    
    public void resetForNewRound() {
        setHealth(maxHealth);
    }
    
    public boolean isAlive() {
        return getHealth() > 0;
    }
    
    public void takeDamage(double damage) {
        int newHealth = (int) Math.max(0, getHealth() - damage);
        setHealth(newHealth);
    }
}
