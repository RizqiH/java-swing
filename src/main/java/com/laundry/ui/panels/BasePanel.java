package com.laundry.ui.panels;

import com.laundry.config.AppConfig;
import com.laundry.repository.UserRepository;
import com.laundry.service.OrderService;
import com.laundry.ui.components.UIComponentFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Base panel class that provides common functionality for all dashboard panels.
 * Implements shared features like header creation, logout handling, and common styling.
 */
public abstract class BasePanel extends JPanel {
    protected static final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    protected static final Color HEADER_COLOR = new Color(41, 128, 185);
    protected static final Color LOGOUT_BUTTON_COLOR = new Color(231, 76, 60);
    
    protected final OrderService orderService;
    protected final UserRepository userRepository;
    protected ActionListener logoutListener;
    
    /**
     * Constructor that initializes common services and layout.
     */
    public BasePanel() {
        this.orderService = AppConfig.getInstance().getOrderService();
        this.userRepository = AppConfig.getInstance().getUserRepository();
        
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);
    }
    
    /**
     * Abstract method that subclasses must implement to set up their specific content.
     */
    protected abstract void initializePanel();
    
    /**
     * Abstract method that returns the title for the header panel.
     * @return String title for the panel
     */
    protected abstract String getPanelTitle();
    
    /**
     * Sets the listener for logout events.
     * @param listener ActionListener that handles logout
     */
    public void setLogoutListener(ActionListener listener) {
        this.logoutListener = listener;
    }
    
    /**
     * Creates a standardized header panel with title and logout button.
     * @return JPanel configured header panel
     */
    protected JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(HEADER_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel(getPanelTitle());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        JButton logoutButton = UIComponentFactory.createStyledButton("Logout", LOGOUT_BUTTON_COLOR);
        logoutButton.addActionListener(e -> {
            if (logoutListener != null) {
                logoutListener.actionPerformed(e);
            }
        });
        headerPanel.add(logoutButton, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    /**
     * Creates a styled panel with consistent background and border.
     * @param layout LayoutManager for the panel
     * @return JPanel with standard styling
     */
    protected JPanel createStyledPanel(LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        return panel;
    }
    
    /**
     * Creates a styled panel with default FlowLayout.
     * @return JPanel with standard styling and FlowLayout
     */
    protected JPanel createStyledPanel() {
        return createStyledPanel(new FlowLayout());
    }
    
    /**
     * Shows a standardized information dialog.
     * @param title Dialog title
     * @param message Dialog message
     */
    protected void showInfoDialog(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Shows a standardized error dialog.
     * @param title Dialog title
     * @param message Error message
     */
    protected void showErrorDialog(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Shows a standardized success dialog.
     * @param title Dialog title
     * @param message Success message
     */
    protected void showSuccessDialog(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Refreshes the panel content. Subclasses can override this method
     * to implement specific refresh logic.
     */
    public void refresh() {
        revalidate();
        repaint();
    }
}