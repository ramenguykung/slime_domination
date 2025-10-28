package com.player;

public abstract class Player {
    private int health;
    private double Damege, attackSpeed;
    private String name;

    /**
     * 
     * @return String of the entity's name
     */
    public String getName() {
        return this.name;
    }

    /**
     *
     * @param name the name to set for the entity as a String
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 
     * @return Damege of the entity as a double
     */
    public double getDamege() {
        return this.Damege;
    }

    /**
     * 
     * @param Damege the Damege to set for the entity as a double
     */
    public void setDamege(double Damege) {
        this.Damege = Damege;
    }

    /**
     * 
     * @return the speed of attack as a double of the entity
     */
    public double getAttackSpeed() {
        return this.attackSpeed;
    }

    /**
     * 
     * @param attackSpeed the speed of attack to set for the entity as a double
     */
    public void setAttackSpeed(double attackSpeed) {
        this.attackSpeed = attackSpeed;
    }

    /**
     * 
     * @return health of the entity as an int
     */
    public int getHealth() {
        return this.health;
    }

    /**
     * 
     * @param health the health to set for the entity as an int
     */
    public void setHealth(int health) {
        this.health = health;
    }

    public Player() {
        this.health = 0;
        this.Damege = 0;
        this.attackSpeed = 0;
        this.name = "";
    }
}
