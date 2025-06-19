package com.laundry;

import com.laundry.config.AppConfig;
import com.laundry.model.User;
import com.laundry.service.AuthenticationService;
import com.laundry.ui.panels.AdminDashboardPanel;
import com.laundry.ui.panels.LoginPanel;
import com.laundry.ui.panels.MemberDashboardPanel;
import com.laundry.ui.panels.RegisterPanel;

import javax.swing.*;
import java.awt.*;

/**
 * Main application class for the Laundry Management System.
 * Manages the main window and panel navigation.
 */
public class LaundrySystemApp extends JFrame {
    private static final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    
    private final CardLayout cardLayout;
    private final JPanel mainPanel;
    private final AuthenticationService authService;
    
    // Panels
    private final LoginPanel loginPanel;
    private final RegisterPanel registerPanel;
    private final AdminDashboardPanel adminPanel;
    private final MemberDashboardPanel memberPanel;
    
    private User currentUser;
    
    public LaundrySystemApp() {
        // Initialize services
        this.authService = AppConfig.getInstance().getAuthenticationService();
        
        // Initialize layout
        this.cardLayout = new CardLayout();
        this.mainPanel = new JPanel(cardLayout);
        
        // Initialize panels
        this.loginPanel = new LoginPanel();
        this.registerPanel = new RegisterPanel();
        this.adminPanel = new AdminDashboardPanel();
        this.memberPanel = new MemberDashboardPanel();
        
        setupFrame();
        setupPanels();
        setupEventHandlers();
        
        // Show login panel initially
        showPanel("LOGIN");
    }
    
    private void setupFrame() {
        setTitle("Laundry Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setBackground(BACKGROUND_COLOR);
        
        add(mainPanel);
    }
    
    private void setupPanels() {
        mainPanel.add(loginPanel, "LOGIN");
        mainPanel.add(registerPanel, "REGISTER");
        mainPanel.add(adminPanel, "ADMIN");
        mainPanel.add(memberPanel, "MEMBER");
    }
    
    private void setupEventHandlers() {
        // Login panel events
        loginPanel.setPanelSwitchListener(e -> showPanel("REGISTER"));
        loginPanel.setLoginSuccessListener(e -> handleLoginSuccess());
        
        // Register panel events
        registerPanel.setPanelSwitchListener(e -> showPanel("LOGIN"));
        
        // Admin panel events
        adminPanel.setLogoutListener(e -> handleLogout());
        
        // Member panel events
        memberPanel.setLogoutListener(e -> handleLogout());
    }
    
    private void handleLoginSuccess() {
        currentUser = loginPanel.getCurrentUser();
        if (currentUser != null) {
            if ("admin".equals(currentUser.getUsername())) {
                showPanel("ADMIN");
            } else {
                memberPanel.setCurrentUser(currentUser);
                showPanel("MEMBER");
            }
        }
    }
    
    private void handleLogout() {
        currentUser = null;
        memberPanel.setCurrentUser(null);
        loginPanel.clearAuthenticatedUser();
        showPanel("LOGIN");
    }
    
    /**
     * Shows the specified panel.
     * @param panelName Name of the panel to show
     */
    public void showPanel(String panelName) {
        cardLayout.show(mainPanel, panelName);
        
        // Update panels when shown
        switch (panelName) {
            case "ADMIN":
                adminPanel.refreshTable();
                adminPanel.updateStats();
                break;
            case "MEMBER":
                memberPanel.updateDashboard();
                break;
        }
    }
    
    /**
     * Gets the current logged-in user.
     * @return Current user or null if not logged in
     */
    public User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Main method to start the application.
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Use cross-platform look and feel to avoid system overrides
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                
                // Set custom table styling
                UIManager.put("Table.foreground", Color.BLACK);
                UIManager.put("Table.background", Color.WHITE);
                UIManager.put("Table.selectionForeground", Color.WHITE);
                UIManager.put("Table.selectionBackground", new Color(173, 216, 230));
                UIManager.put("Table.gridColor", Color.LIGHT_GRAY);
                UIManager.put("TableHeader.foreground", Color.WHITE);
                UIManager.put("TableHeader.background", new Color(52, 152, 219));
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            new LaundrySystemApp().setVisible(true);
        });
    }
}