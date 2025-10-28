package com.main;

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
    
    public Main() {
        setTitle("Slime Domination");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        // Create menu panel
        MenuPanel menuPanel = new MenuPanel(this);
        mainPanel.add(menuPanel, "MENU");
        
        add(mainPanel);
        
        cardLayout.show(mainPanel, "MENU");
    }
    
    public void showPanel(String panelName) {
        cardLayout.show(mainPanel, panelName);
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
        JOptionPane.showMessageDialog(this, "Starting game...", "Play", JOptionPane.INFORMATION_MESSAGE);
        // TODO: Implement game panel
    }
    
    private void showStats() {
        JOptionPane.showMessageDialog(this, "Stats panel coming soon!", "Stats", JOptionPane.INFORMATION_MESSAGE);
        // TODO: Implement stats panel
    }
    
    private void showUpgrade() {
        JOptionPane.showMessageDialog(this, "Upgrade panel coming soon!", "Upgrade", JOptionPane.INFORMATION_MESSAGE);
        // TODO: Implement upgrade panel
    }
}