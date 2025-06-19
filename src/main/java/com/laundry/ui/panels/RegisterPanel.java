package com.laundry.ui.panels;

import com.laundry.config.AppConfig;
import com.laundry.service.AuthenticationService;
import com.laundry.ui.components.UIComponentFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Registration panel for new user account creation.
 * Provides form fields for user information input.
 */
public class RegisterPanel extends JPanel {
    private static final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    
    private final JTextField nameField, phoneField, usernameField, addressField;
    private final JPasswordField passwordField, confirmPasswordField;
    private final AuthenticationService authService;
    private ActionListener panelSwitchListener;
    
    public RegisterPanel() {
        this.authService = AppConfig.getInstance().getAuthenticationService();
        
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);
        
        // Initialize fields
        nameField = new JTextField(25);
        phoneField = new JTextField(25);
        usernameField = new JTextField(25);
        passwordField = new JPasswordField(25);
        confirmPasswordField = new JPasswordField(25);
        addressField = new JTextField(25);
        
        setupRegisterForm();
    }
    
    /**
     * Sets the listener for panel switching events.
     * @param listener ActionListener that handles panel switching
     */
    public void setPanelSwitchListener(ActionListener listener) {
        this.panelSwitchListener = listener;
    }
    
    private void setupRegisterForm() {
        // Header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(UIComponentFactory.getPrimaryColor());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("REGISTER NEW ACCOUNT");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        
        // Form Panel
        JPanel formPanel = createFormPanel();
        
        // Layout
        add(headerPanel, BorderLayout.NORTH);
        add(new JScrollPane(formPanel), BorderLayout.CENTER);
    }
    
    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Form fields
        String[] labels = {"Full Name:", "Phone Number:", "Username:", 
                          "Password:", "Confirm Password:", "Address:"};
        JComponent[] fields = {nameField, phoneField, usernameField, 
                              passwordField, confirmPasswordField, addressField};
        
        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i;
            JLabel label = new JLabel(labels[i]);
            label.setFont(new Font("Arial", Font.PLAIN, 14));
            formPanel.add(label, gbc);
            
            gbc.gridx = 1;
            fields[i].setFont(new Font("Arial", Font.PLAIN, 14));
            formPanel.add(fields[i], gbc);
        }
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton registerButton = UIComponentFactory.createStyledButton("Register", UIComponentFactory.getSuccessColor());
        registerButton.addActionListener(e -> handleRegister());
        
        JButton backButton = UIComponentFactory.createStyledButton("Back to Login", UIComponentFactory.getSecondaryColor());
        backButton.addActionListener(e -> {
            if (panelSwitchListener != null) {
                panelSwitchListener.actionPerformed(e);
            }
        });
        
        buttonPanel.add(registerButton);
        buttonPanel.add(backButton);
        
        gbc.gridx = 0; gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(buttonPanel, gbc);
        
        return formPanel;
    }
    
    private void handleRegister() {
        String fullName = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String address = addressField.getText().trim();
        
        // Validation
        if (fullName.isEmpty() || phone.isEmpty() || username.isEmpty() || 
            password.isEmpty() || address.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields!", 
                "Registration Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match!", 
                "Registration Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (authService.registerUser(username, password, fullName, phone, address)) {
            JOptionPane.showMessageDialog(this, "Registration successful! Please login.", 
                "Success", JOptionPane.INFORMATION_MESSAGE);
            
            // Clear fields
            clearFields();
            
            // Switch to login panel
            if (panelSwitchListener != null) {
                panelSwitchListener.actionPerformed(null);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Username already exists!", 
                "Registration Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void clearFields() {
        nameField.setText("");
        phoneField.setText("");
        usernameField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
        addressField.setText("");
    }
}