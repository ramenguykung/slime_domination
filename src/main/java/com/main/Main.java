package com.main;

import com.player.PlayerSlime;
import java.awt.*;
import java.awt.event.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;

public class Main extends JFrame {
    private static final int WINDOW_WIDTH = 1600;
    private static final int WINDOW_HEIGHT = 900;
    
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private PlayerSlime player;
    
    public Main() {
        setTitle("Slime Domination");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        // Initialize player
        player = new PlayerSlime();
        
        // Create menu panel
        MenuPanel menuPanel = new MenuPanel(this);
        mainPanel.add(menuPanel, "MENU");
        
        add(mainPanel);
        
        cardLayout.show(mainPanel, "MENU");
    }
    
    public void showPanel(String panelName) {
        cardLayout.show(mainPanel, panelName);
    }
    
    public void startGame() {
        // Remove old game panel if exists
        Component[] components = mainPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof GamePanel) {
                mainPanel.remove(comp);
            }
        }
        
        // Create new game panel
        GamePanel gamePanel = new GamePanel(this, player);
        mainPanel.add(gamePanel, "GAME");
        cardLayout.show(mainPanel, "GAME");
    }
    
    public PlayerSlime getPlayer() {
        return player;
    }
    
    public void resetPlayer() {
        player = new PlayerSlime();
    }
    
    public static void main(String[] args) {
        Main game = new Main();
        game.setVisible(true);
    }
}

class MenuPanel extends JPanel {
    private BufferedImage backgroundImage;
    private BufferedImage slimeImage;
    private Main mainFrame;
    
    public MenuPanel(Main mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(null);
        setPreferredSize(new Dimension(1024, 768));
        
        loadImages();
        createMenuButtons();
    }
    
    private void loadImages() {
        try {
            // Load background image
            backgroundImage = ImageIO.read(getClass().getResourceAsStream("/images/background/no_pillars/1920X1080/Full.png"));
            
            // Load slime idle image
            slimeImage = ImageIO.read(getClass().getResourceAsStream("/images/player/idle/idle_00.png"));
        } catch (Exception e) {
            System.err.println("Error loading images: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void createMenuButtons() {
        // Button styling
        Font buttonFont = new Font("Minecraft", Font.BOLD, 36);
        Color buttonColor = new Color(255, 255, 255, 255);
        Color borderColor = new Color(210, 210, 140);
        
        // Create buttons
        JButton playButton = createStyledButton("PLAY", buttonFont, buttonColor, borderColor);
        JButton statsButton = createStyledButton("STATS", buttonFont, buttonColor, borderColor);
        JButton upgradeButton = createStyledButton("UPGRADE", buttonFont, buttonColor, borderColor);
        JButton exitButton = createStyledButton("EXIT", buttonFont, buttonColor, borderColor);
        
        // Position buttons (right side of screen)
        int buttonWidth = 480;
        int buttonHeight = 80;
        int buttonX = 1000;
        int startY = 180;
        int spacing = 110;
        
        playButton.setBounds(buttonX, startY, buttonWidth, buttonHeight);
        statsButton.setBounds(buttonX, startY + spacing, buttonWidth, buttonHeight);
        upgradeButton.setBounds(buttonX, startY + spacing * 2, buttonWidth, buttonHeight);
        exitButton.setBounds(buttonX, startY + spacing * 3, buttonWidth, buttonHeight);
        
        // Add action listeners
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startGame();
            }
        });
        statsButton.addActionListener(e -> showStats());
        upgradeButton.addActionListener(e -> showUpgrade());
        exitButton.addActionListener(e -> System.exit(0));
        
        // Add buttons to panel
        add(playButton);
        add(statsButton);
        add(upgradeButton);
        add(exitButton);
    }
    
    private JButton createStyledButton(String text, Font font, Color bgColor, Color borderColor) {
        JButton button = new JButton(text);
        button.setFont(font);
        button.setBackground(bgColor);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderColor, 4),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        
        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(255, 255, 255, 255));
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // Draw background
        if (backgroundImage != null) {
            // Scale background to fit window
            g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            // Fallback gradient background
            GradientPaint gradient = new GradientPaint(
                0, 0, new Color(255, 182, 193),
                0, getHeight(), new Color(210, 180, 140)
            );
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
        
        // Draw slime character (left side)
        if (slimeImage != null) {
            int slimeSize = 280;
            int slimeX = 300;
            int slimeY = 280;
            g2d.drawImage(slimeImage, slimeX, slimeY, slimeSize, slimeSize, this);
        }
        
        // Draw sun (top left)
        g2d.setColor(new Color(255, 255, 255, 200));
        g2d.fillOval(30, 80, 120, 120);
    }
    
    private void startGame() {
        mainFrame.startGame();
    }
    
    private void showStats() {
        PlayerSlime player = mainFrame.getPlayer();
        JOptionPane.showMessageDialog(this, 
            "=== Player Stats ===\n" +
            "HP: " + player.getHealth() + "/" + player.getMaxHealth() + "\n" +
            "Attack: " + (int)player.getDamege() + "\n" +
            "Attack Speed: " + player.getAttackSpeed() + "\n" +
            "Rounds Won: " + player.getRoundsWon() + "\n" +
            "Skill Points: " + player.getSkillPoints(),
            "Stats", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showUpgrade() {
        PlayerSlime player = mainFrame.getPlayer();
        UpgradeDialog upgradeDialog = new UpgradeDialog(mainFrame, player);
        upgradeDialog.setVisible(true);
    }
}

class UpgradeDialog extends JDialog {
    private PlayerSlime player;
    private JLabel skillPointsLabel;
    private JLabel healthStatLabel;
    private JLabel attackStatLabel;
    private JLabel attackSpeedStatLabel;
    
    // Upgrade costs
    private static final int HEALTH_COST = 1;
    private static final int ATTACK_COST = 2;
    private static final int ATTACK_SPEED_COST = 2;
    
    // Upgrade amounts
    private static final int HEALTH_UPGRADE = 20;
    private static final double ATTACK_UPGRADE = 5.0;
    private static final double ATTACK_SPEED_UPGRADE = 0.1;
    
    public UpgradeDialog(Main mainFrame, PlayerSlime player) {
        super(mainFrame, "Upgrade Your Slime", true);
        this.player = player;
        
        setSize(600, 500);
        setLocationRelativeTo(mainFrame);
        setResizable(false);
        
        setupUI();
    }
    
    private void setupUI() {
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(240, 240, 240));
        
        // Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(100, 149, 237));
        
        // Skill Points Panel
        JPanel skillPointsPanel = new JPanel();
        skillPointsPanel.setBackground(new Color(240, 240, 240));
        skillPointsLabel = new JLabel("Available Skill Points: " + player.getSkillPoints());
        skillPointsLabel.setFont(new Font("Arial", Font.BOLD, 18));
        skillPointsPanel.add(skillPointsLabel);
        
        // Center Panel with upgrades
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(new Color(240, 240, 240));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        centerPanel.add(skillPointsPanel);
        centerPanel.add(Box.createVerticalStrut(20));
        
        // Health Upgrade
        JPanel healthPanel = createUpgradePanel(
            "Max Health",
            "Current: " + player.getMaxHealth() + " → +" + HEALTH_UPGRADE + " HP",
            HEALTH_COST,
            new Color(255, 99, 71),
            () -> upgradeHealth()
        );
        healthStatLabel = (JLabel) ((JPanel) healthPanel.getComponent(0)).getComponent(1);
        centerPanel.add(healthPanel);
        centerPanel.add(Box.createVerticalStrut(15));
        
        // Attack Upgrade
        JPanel attackPanel = createUpgradePanel(
            "Attack Damage",
            "Current: " + (int)player.getDamege() + " → +" + (int)ATTACK_UPGRADE + " DMG",
            ATTACK_COST,
            new Color(255, 165, 0),
            () -> upgradeAttack()
        );
        attackStatLabel = (JLabel) ((JPanel) attackPanel.getComponent(0)).getComponent(1);
        centerPanel.add(attackPanel);
        centerPanel.add(Box.createVerticalStrut(15));
        
        // Attack Speed Upgrade
        JPanel attackSpeedPanel = createUpgradePanel(
            "Attack Speed",
            String.format("Current: %.1f → +%.1f SPD", player.getAttackSpeed(), ATTACK_SPEED_UPGRADE),
            ATTACK_SPEED_COST,
            new Color(50, 205, 50),
            () -> upgradeAttackSpeed()
        );
        attackSpeedStatLabel = (JLabel) ((JPanel) attackSpeedPanel.getComponent(0)).getComponent(1);
        centerPanel.add(attackSpeedPanel);
        
        add(centerPanel, BorderLayout.CENTER);
        
        // Close Button
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(240, 240, 240));
        JButton closeButton = new JButton("Close");
        closeButton.setFont(new Font("Arial", Font.BOLD, 16));
        closeButton.setPreferredSize(new Dimension(150, 40));
        closeButton.addActionListener(e -> dispose());
        bottomPanel.add(closeButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createUpgradePanel(String title, String upgradeAmount, int cost, Color color, Runnable upgradeAction) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(10, 5));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 3),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        panel.setMaximumSize(new Dimension(550, 80));
        
        // Left side - Info
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(color);
        
        JLabel statLabel = new JLabel(upgradeAmount);
        statLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        infoPanel.add(titleLabel);
        infoPanel.add(statLabel);
        panel.add(infoPanel, BorderLayout.WEST);
        
        // Right side - Button
        JButton upgradeButton = new JButton("Upgrade (" + cost + " SP)");
        upgradeButton.setFont(new Font("Arial", Font.BOLD, 14));
        upgradeButton.setBackground(color);
        upgradeButton.setForeground(Color.WHITE);
        upgradeButton.setFocusPainted(false);
        upgradeButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        upgradeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        upgradeButton.addActionListener(e -> {
            upgradeAction.run();
        });
        
        panel.add(upgradeButton, BorderLayout.EAST);
        
        return panel;
    }
    
    private void upgradeHealth() {
        if (player.spendSkillPoints(HEALTH_COST)) {
            player.upgradeMaxHealth(HEALTH_UPGRADE);
            updateLabels();
            JOptionPane.showMessageDialog(this, 
                "Max Health increased by " + HEALTH_UPGRADE + "!\nNew Max Health: " + player.getMaxHealth(),
                "Upgrade Successful", 
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                "Not enough skill points!\nRequired: " + HEALTH_COST + " | Available: " + player.getSkillPoints(),
                "Insufficient Skill Points",
                JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void upgradeAttack() {
        if (player.spendSkillPoints(ATTACK_COST)) {
            player.upgradeAttack(ATTACK_UPGRADE);
            updateLabels();
            JOptionPane.showMessageDialog(this,
                "Attack increased by " + (int)ATTACK_UPGRADE + "!\nNew Attack: " + (int)player.getDamege(),
                "Upgrade Successful",
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                "Not enough skill points!\nRequired: " + ATTACK_COST + " | Available: " + player.getSkillPoints(),
                "Insufficient Skill Points",
                JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void upgradeAttackSpeed() {
        if (player.spendSkillPoints(ATTACK_SPEED_COST)) {
            player.upgradeAttackSpeed(ATTACK_SPEED_UPGRADE);
            updateLabels();
            JOptionPane.showMessageDialog(this,
                String.format("Attack Speed increased by %.1f!\nNew Attack Speed: %.1f", 
                    ATTACK_SPEED_UPGRADE, player.getAttackSpeed()),
                "Upgrade Successful",
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                "Not enough skill points!\nRequired: " + ATTACK_SPEED_COST + " | Available: " + player.getSkillPoints(),
                "Insufficient Skill Points",
                JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void updateLabels() {
        skillPointsLabel.setText("Available Skill Points: " + player.getSkillPoints());
        healthStatLabel.setText("Current: " + player.getMaxHealth() + " → +" + HEALTH_UPGRADE + " HP");
        attackStatLabel.setText("Current: " + (int)player.getDamege() + " → +" + (int)ATTACK_UPGRADE + " DMG");
        attackSpeedStatLabel.setText(String.format("Current: %.1f → +%.1f SPD", player.getAttackSpeed(), ATTACK_SPEED_UPGRADE));
        // Force the dialog to refresh and show updated stats
        revalidate();
        repaint();
    }
}