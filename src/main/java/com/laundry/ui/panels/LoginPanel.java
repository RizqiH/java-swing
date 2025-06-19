package com.laundry.ui.panels;

import com.laundry.config.AppConfig;
import com.laundry.model.User;
import com.laundry.service.AuthenticationService;
import com.laundry.ui.components.UIComponentFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Login panel for user authentication.
 * Provides username/password input and navigation to registration.
 */
public class LoginPanel extends JPanel {
    private static final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final AuthenticationService authService;
    private ActionListener panelSwitchListener;
    private ActionListener loginSuccessListener;
    private User authenticatedUser; // Store the authenticated user temporarily
    
    public LoginPanel() {
        this.authService = AppConfig.getInstance().getAuthenticationService();
        
        setLayout(new GridBagLayout());
        setBackground(BACKGROUND_COLOR);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Create login form
        JPanel loginCard = createLoginCard();
        add(loginCard);
        
        // Initialize fields
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        
        setupLoginForm(loginCard, gbc);
    }
    
    /**
     * Sets the listener for panel switching events.
     * @param listener ActionListener that handles panel switching
     */
    public void setPanelSwitchListener(ActionListener listener) {
        this.panelSwitchListener = listener;
    }
    
    /**
     * Sets the listener for successful login events.
     * @param listener ActionListener that handles successful login
     */
    public void setLoginSuccessListener(ActionListener listener) {
        this.loginSuccessListener = listener;
    }
    
    private JPanel createLoginCard() {
        JPanel loginCard = new JPanel(new GridBagLayout());
        loginCard.setBackground(Color.WHITE);
        loginCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(40, 40, 40, 40)
        ));
        return loginCard;
    }
    
    private void setupLoginForm(JPanel loginCard, GridBagConstraints gbc) {
        // Logo/Title
        JLabel logoLabel = new JLabel("LAUNDRY SYSTEM", JLabel.CENTER);
        logoLabel.setFont(new Font("Arial", Font.BOLD, 32));
        logoLabel.setForeground(UIComponentFactory.getPrimaryColor());
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        loginCard.add(logoLabel, gbc);
        
        // Username field
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        loginCard.add(new JLabel("Username:"), gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        loginCard.add(usernameField, gbc);
        
        // Password field
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        loginCard.add(new JLabel("Password:"), gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        loginCard.add(passwordField, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton loginButton = UIComponentFactory.createStyledButton("Login", UIComponentFactory.getSuccessColor());
        loginButton.addActionListener(e -> handleLogin());
        
        JButton registerButton = UIComponentFactory.createStyledButton("Register", UIComponentFactory.getSecondaryColor());
        registerButton.addActionListener(e -> {
            if (panelSwitchListener != null) {
                panelSwitchListener.actionPerformed(e);
            }
        });
        
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        loginCard.add(buttonPanel, gbc);
    }
    
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter username and password!", 
                "Login Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        User user = authService.authenticate(username, password);
        if (user != null) {
            // Store the authenticated user before clearing fields
            this.authenticatedUser = user;
            
            JOptionPane.showMessageDialog(this, "Welcome " + user.getFullName() + "!", 
                "Login Success", JOptionPane.INFORMATION_MESSAGE);
            
            // Clear fields
            usernameField.setText("");
            passwordField.setText("");
            
            // Notify success listener
            if (loginSuccessListener != null) {
                loginSuccessListener.actionPerformed(null);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password!", 
                "Login Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Gets the currently authenticated user.
     * @return User object if authentication successful, null otherwise
     */
    public User getCurrentUser() {
        return authenticatedUser;
    }
    
    /**
     * Clears the authenticated user (used during logout).
     */
    public void clearAuthenticatedUser() {
        this.authenticatedUser = null;
    }
}