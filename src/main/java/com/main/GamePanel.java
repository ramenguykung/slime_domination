package com.main;

import com.player.PlayerSlime;
import com.player.EnemySlime;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class GamePanel extends JPanel {
    private Main mainFrame;
    private PlayerSlime player;
    private EnemySlime enemy;
    private BufferedImage backgroundImage;
    private BufferedImage playerIdleImage;
    private BufferedImage enemyIdleImage;
    
    private boolean battleInProgress;
    private String battleLog;
    private int animationFrame;
    
    // Thread-related fields
    private Thread playerAttackThread;
    private Thread enemyAttackThread;
    private final Object battleLock = new Object();
    private volatile boolean playerAnimating = false;
    private volatile boolean enemyAnimating = false;
    
    public GamePanel(Main mainFrame, PlayerSlime player) {
        this.mainFrame = mainFrame;
        this.player = player;
        this.enemy = new EnemySlime(player.getRoundsWon() + 1);
        this.battleInProgress = false;
        this.battleLog = "Press START BATTLE to begin!";
        this.animationFrame = 0;
        
        setLayout(null);
        setPreferredSize(new Dimension(1600, 900));
        
        loadImages();
        createUI();
        startBattle();
    }
    
    private void loadImages() {
        try {
            backgroundImage = ImageIO.read(getClass().getResourceAsStream("/images/background/pillars/1920X1080/Full.png"));
            playerIdleImage = ImageIO.read(getClass().getResourceAsStream("/images/player/idle/idle_00.png"));
            enemyIdleImage = ImageIO.read(getClass().getResourceAsStream("/images/enemy/idle/idle_00.png"));
        } catch (Exception e) {
            System.err.println("Error loading game images: " + e.getMessage());
        }
    }
    
    private void createUI() {
        // Back to menu button
        JButton backButton = new JButton("MENU");
        backButton.setFont(new Font("Arial", Font.BOLD, 20));
        backButton.setBounds(20, 20, 120, 40);
        backButton.addActionListener(e -> returnToMenu());
        add(backButton);
    }
    
    private void startBattle() {
        battleInProgress = true;
        battleLog = "Battle Started!";
        
        // Start separate attack threads for player and enemy
        startPlayerAttackThread();
        startEnemyAttackThread();
    }
    
    private void startPlayerAttackThread() {
        playerAttackThread = new Thread(() -> {
            try {
                // Calculate attack interval based on attack speed
                // Higher attack speed = faster attacks
                long attackInterval = (long) (1000 / player.getAttackSpeed());
                
                while (battleInProgress && player.isAlive() && enemy.isAlive()) {
                    Thread.sleep(attackInterval);
                    
                    if (battleInProgress && enemy.isAlive()) {
                        synchronized (battleLock) {
                            playerAttack();
                        }
                        
                        // Check if battle ended
                        if (!enemy.isAlive()) {
                            SwingUtilities.invokeLater(() -> endBattle(true));
                            break;
                        }
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        playerAttackThread.setDaemon(true);
        playerAttackThread.start();
    }
    
    private void startEnemyAttackThread() {
        enemyAttackThread = new Thread(() -> {
            try {
                // Calculate attack interval based on attack speed
                // Higher attack speed = faster attacks
                long attackInterval = (long) (1000 / enemy.getAttackSpeed());
                
                while (battleInProgress && player.isAlive() && enemy.isAlive()) {
                    Thread.sleep(attackInterval);
                    
                    if (battleInProgress && player.isAlive()) {
                        synchronized (battleLock) {
                            enemyAttack();
                        }
                        
                        // Check if battle ended
                        if (!player.isAlive()) {
                            SwingUtilities.invokeLater(() -> endBattle(false));
                            break;
                        }
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        enemyAttackThread.setDaemon(true);
        enemyAttackThread.start();
    }
    
    private void playerAttack() {
        if (!battleInProgress || !enemy.isAlive()) return;
        
        double damage = player.getDamege();
        enemy.takeDamage(damage);
        battleLog = "Player attacks for " + (int)damage + " damage!";
        
        playerAnimating = true;
        animationFrame++;
        
        SwingUtilities.invokeLater(() -> {
            repaint();
            // Reset animation flag after a short delay
            Timer animTimer = new Timer(300, e -> {
                playerAnimating = false;
                ((Timer)e.getSource()).stop();
            });
            animTimer.setRepeats(false);
            animTimer.start();
        });
    }
    
    private void enemyAttack() {
        if (!battleInProgress || !player.isAlive()) return;
        
        double damage = enemy.getDamege();
        player.takeDamage(damage);
        battleLog = "Enemy attacks for " + (int)damage + " damage!";
        
        enemyAnimating = true;
        animationFrame++;
        
        SwingUtilities.invokeLater(() -> {
            repaint();
            // Reset animation flag after a short delay
            Timer animTimer = new Timer(300, e -> {
                enemyAnimating = false;
                ((Timer)e.getSource()).stop();
            });
            animTimer.setRepeats(false);
            animTimer.start();
        });
    }
    
    private void endBattle(boolean playerWon) {
        if (!battleInProgress) return; // Prevent multiple calls
        
        battleInProgress = false;
        
        // Stop attack threads
        if (playerAttackThread != null && playerAttackThread.isAlive()) {
            playerAttackThread.interrupt();
        }
        if (enemyAttackThread != null && enemyAttackThread.isAlive()) {
            enemyAttackThread.interrupt();
        }
        
        if (playerWon) {
            player.incrementRoundsWon();
            int skillPointsEarned = 5 + (enemy.getLevel() * 2);
            player.addSkillPoints(skillPointsEarned);
            battleLog = "Victory! Earned " + skillPointsEarned + " skill points!";
            
            // Show victory dialog
            Timer delayTimer = new Timer(2000, e -> {
                ((Timer)e.getSource()).stop();
                
                // Calculate next enemy stats
                int nextLevel = player.getRoundsWon() + 1;
                int nextEnemyHP = 80 + (nextLevel * 15);
                int nextEnemyATK = (int)(8.0 + (nextLevel * 2));
                double nextEnemySpeed = 1.0 + (nextLevel * 0.1);
                
                int choice = JOptionPane.showConfirmDialog(this, 
                    "You won! Earned " + skillPointsEarned + " skill points!\n" +
                    "Rounds Won: " + player.getRoundsWon() + "\n" +
                    "Total Skill Points: " + player.getSkillPoints() + "\n\n" +
                    "Next Enemy Stats:\n" +
                    "HP: " + nextEnemyHP + " | ATK: " + nextEnemyATK + " | SPD: " + String.format("%.1f", nextEnemySpeed) + "\n\n" +
                    "Continue to next round?",
                    "Victory!", 
                    JOptionPane.YES_NO_OPTION);
                
                if (choice == JOptionPane.YES_OPTION) {
                    nextRound();
                } else {
                    // Reset player HP even when declining to continue
                    player.resetForNewRound();
                    returnToMenu();
                }
            });
            delayTimer.setRepeats(false);
            delayTimer.start();
        } else {
            battleLog = "Defeat! Game Over.";
            
            // Show defeat dialog
            Timer delayTimer = new Timer(2000, e -> {
                ((Timer)e.getSource()).stop();
                
                int choice = JOptionPane.showConfirmDialog(this,
                    "You were defeated!\n" +
                    "Rounds Won: " + player.getRoundsWon() + "\n" +
                    "Total Skill Points: " + player.getSkillPoints() + "\n\n" +
                    "Reset HP and try again?",
                    "Game Over",
                    JOptionPane.YES_NO_OPTION);
                
                // Always reset HP after defeat
                player.resetForNewRound();
                
                if (choice == JOptionPane.YES_OPTION) {
                    // Retry the same round
                    retryRound();
                } else {
                    returnToMenu();
                }
            });
            delayTimer.setRepeats(false);
            delayTimer.start();
        }
    }
    
    private void nextRound() {
        // Reset player HP to full for the new round
        player.resetForNewRound();
        
        // Create a stronger enemy for the next round (HP, damage, and speed all increase)
        enemy = new EnemySlime(player.getRoundsWon() + 1);
        
        battleLog = "Round " + (player.getRoundsWon() + 1) + " - Fight!";
        startBattle();
        repaint();
    }
    
    private void retryRound() {
        // Player HP already reset in endBattle
        // Create same level enemy (retry current round)
        enemy = new EnemySlime(player.getRoundsWon() + 1);
        
        battleLog = "Round " + (player.getRoundsWon() + 1) + " - Retry!";
        startBattle();
        repaint();
    }
    
    private void returnToMenu() {
        battleInProgress = false;
        
        // Interrupt attack threads
        if (playerAttackThread != null && playerAttackThread.isAlive()) {
            playerAttackThread.interrupt();
        }
        if (enemyAttackThread != null && enemyAttackThread.isAlive()) {
            enemyAttackThread.interrupt();
        }
        
        // Reset player HP when returning to menu (in case battle was interrupted)
        player.resetForNewRound();
        
        mainFrame.showPanel("MENU");
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // Draw background
        if (backgroundImage != null) {
            g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            g2d.setColor(new Color(210, 180, 140));
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
        
        // Draw player slime (left side)
        int slimeSize = 300;
        int playerX = 250;
        int playerY = 400;
        
        if (playerIdleImage != null) {
            g2d.drawImage(playerIdleImage, playerX, playerY, slimeSize, slimeSize, this);
        } else {
            g2d.setColor(new Color(100, 150, 255));
            g2d.fillOval(playerX, playerY, slimeSize, slimeSize);
        }
        
        // Draw player HP bar
        drawHealthBar(g2d, playerX + 50, playerY - 50, 200, 25, 
            player.getHealth(), player.getMaxHealth(), Color.GREEN, Color.RED);
        
        // Draw player stats
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 18));
        g2d.drawString("Player HP: " + player.getHealth() + "/" + player.getMaxHealth(), playerX + 50, playerY - 60);
        g2d.drawString("ATK: " + (int)player.getDamege() + " | SPD: " + String.format("%.1f", player.getAttackSpeed()), playerX + 50, playerY + slimeSize + 30);
        
        // Draw enemy slime (right side)
        int enemyX = 1050;
        int enemyY = 400;
        
        if (enemyIdleImage != null) {
            g2d.drawImage(enemyIdleImage, enemyX, enemyY, slimeSize, slimeSize, this);
        } else {
            g2d.setColor(new Color(180, 100, 255));
            g2d.fillOval(enemyX, enemyY, slimeSize, slimeSize);
        }
        
        // Draw enemy HP bar
        drawHealthBar(g2d, enemyX + 50, enemyY - 50, 200, 25,
            enemy.getHealth(), enemy.getMaxHealth(), Color.GREEN, Color.RED);
        
        // Draw enemy stats
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 18));
        g2d.drawString("Enemy HP: " + enemy.getHealth() + "/" + enemy.getMaxHealth(), enemyX + 50, enemyY - 60);
        g2d.drawString("ATK: " + (int)enemy.getDamege() + " | SPD: " + String.format("%.1f", enemy.getAttackSpeed()) + " | LVL: " + enemy.getLevel(), enemyX + 50, enemyY + slimeSize + 30);
        
        // Draw battle log
        g2d.setColor(new Color(0, 0, 0, 180));
        g2d.fillRect(400, 750, 800, 80);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        FontMetrics fm = g2d.getFontMetrics();
        int textX = 800 - fm.stringWidth(battleLog) / 2;
        g2d.drawString(battleLog, textX, 795);
        
        // Draw round info
        g2d.setFont(new Font("Arial", Font.BOLD, 22));
        g2d.drawString("Round: " + (player.getRoundsWon() + 1), 800 - fm.stringWidth("Round: " + (player.getRoundsWon() + 1)) / 2, 150);
        g2d.drawString("Skill Points: " + player.getSkillPoints(), 800 - fm.stringWidth("Skill Points: " + player.getSkillPoints()) / 2, 180);
        
        // Draw attack animation (simple slash effect)
        if (playerAnimating) {
            g2d.setColor(new Color(100, 150, 255, 180));
            g2d.setStroke(new BasicStroke(10));
            g2d.drawArc(400, 420, 300, 250, -30, 100);
        }
        
        if (enemyAnimating) {
            g2d.setColor(new Color(180, 100, 255, 180));
            g2d.setStroke(new BasicStroke(10));
            g2d.drawArc(900, 420, 300, 250, 210, 100);
        }
    }
    
    private void drawHealthBar(Graphics2D g2d, int x, int y, int width, int height, 
                                int currentHP, int maxHP, Color fullColor, Color emptyColor) {
        // Background (empty health)
        g2d.setColor(emptyColor);
        g2d.fillRect(x, y, width, height);
        
        // Foreground (current health)
        int currentWidth = (int) ((double) currentHP / maxHP * width);
        g2d.setColor(fullColor);
        g2d.fillRect(x, y, currentWidth, height);
        
        // Border
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRect(x, y, width, height);
    }
}
