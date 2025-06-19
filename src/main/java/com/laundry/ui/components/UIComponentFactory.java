package com.laundry.ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Factory class for creating styled UI components.
 * Provides consistent styling across the application.
 */
public class UIComponentFactory {
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color SECONDARY_COLOR = new Color(52, 152, 219);
    private static final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private static final Color WARNING_COLOR = new Color(241, 196, 15);
    private static final Color DANGER_COLOR = new Color(231, 76, 60);
    
    /**
     * Creates a styled button with the specified text and background color.
     * @param text Button text
     * @param bgColor Background color
     * @return Styled JButton
     */
    public static JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.darker());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }
    
    /**
     * Creates a card panel with title and content.
     * @param title Card title
     * @param content Card content
     * @return Styled card panel
     */
    public static JPanel createCard(String title, String content) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(PRIMARY_COLOR);
        
        JLabel contentLabel = new JLabel(content);
        contentLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(contentLabel, BorderLayout.CENTER);
        
        return card;
    }
    
    // Color getters for consistent theming
    public static Color getPrimaryColor() { return PRIMARY_COLOR; }
    public static Color getSecondaryColor() { return SECONDARY_COLOR; }
    public static Color getSuccessColor() { return SUCCESS_COLOR; }
    public static Color getWarningColor() { return WARNING_COLOR; }
    public static Color getDangerColor() { return DANGER_COLOR; }
}