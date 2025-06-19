package com.laundry.ui.panels;

import com.laundry.model.User;
import com.laundry.model.Order;
import com.laundry.service.OrderService;
import com.laundry.repository.UserRepository;
import com.laundry.config.AppConfig;
import com.laundry.ui.components.UIComponentFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Member dashboard panel for customer interface.
 * Provides access to member features like orders, points, and profile.
 */
public class MemberDashboardPanel extends BasePanel {
    private User currentUser;
    
    public MemberDashboardPanel() {
        super();
        initializePanel();
    }
    
    /**
     * Sets the current user for the dashboard.
     * @param user Current logged-in user
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;
        updateDashboard();
    }
    

    
    @Override
    protected void initializePanel() {
        setupMemberDashboard();
    }
    
    @Override
    protected String getPanelTitle() {
        return "MEMBER DASHBOARD - Welcome " + 
            (currentUser != null ? currentUser.getFullName() : "Guest");
    }
    
    private void setupMemberDashboard() {
        // Header
        JPanel headerPanel = createHeaderPanel();
        
        // Content
        JPanel contentPanel = createContentPanel();
        
        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }
    

    
    private JPanel createContentPanel() {
        JPanel contentPanel = createStyledPanel(new GridLayout(2, 2, 20, 20));
        
        // Add menu cards
        contentPanel.add(createMenuCard("New Order", "Place a new laundry order", 
            e -> showNewOrderDialog()));
        contentPanel.add(createMenuCard("Order History", "View your order history", 
            e -> showOrderHistoryDialog()));
        contentPanel.add(createMenuCard("Points", "Check your loyalty points: " + 
            (currentUser != null ? currentUser.getPoints() : 0), 
            e -> showPointsDialog()));
        contentPanel.add(createMenuCard("Profile", "Update your profile information", 
            e -> showProfileDialog()));
        
        return contentPanel;
    }
    
    private JPanel createMenuCard(String title, String description, ActionListener action) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(41, 128, 185));
        
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        descLabel.setForeground(Color.GRAY);
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(descLabel, BorderLayout.CENTER);
        
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                action.actionPerformed(new ActionEvent(card, ActionEvent.ACTION_PERFORMED, ""));
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(245, 245, 245));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(Color.WHITE);
            }
        });
        
        return card;
    }
    
    /**
     * Updates the dashboard with current user information.
     */
    public void updateDashboard() {
        // Remove all components and recreate
        removeAll();
        setupMemberDashboard();
        revalidate();
        repaint();
    }
    
    /**
     * Gets the current user.
     * @return Current logged-in user
     */
    public User getCurrentUser() {
        return currentUser;
    }
    
    private void showNewOrderDialog() {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "New Order", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Form fields
        JTextField phoneField = new JTextField(currentUser != null ? currentUser.getPhone() : "", 20);
        JTextField addressField = new JTextField(currentUser != null ? currentUser.getAddress() : "", 20);
        JComboBox<String> serviceCombo = new JComboBox<>(new String[]{"Cuci Setrika", "Cuci Kering", "Setrika Saja"});
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"Regular", "Express", "Premium"});
        JTextField weightField = new JTextField("1.0", 20);
        
        // Add components
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        panel.add(phoneField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1;
        panel.add(addressField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Service:"), gbc);
        gbc.gridx = 1;
        panel.add(serviceCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Type:"), gbc);
        gbc.gridx = 1;
        panel.add(typeCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Weight (kg):"), gbc);
        gbc.gridx = 1;
        panel.add(weightField, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton createButton = new JButton("Create Order");
        JButton cancelButton = new JButton("Cancel");
        
        createButton.addActionListener(e -> {
            try {
                if (currentUser == null) {
                    JOptionPane.showMessageDialog(dialog, "Please log in to create an order!", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                double weight = Double.parseDouble(weightField.getText());
                Order order = orderService.createOrderForUser(
                    currentUser,
                    phoneField.getText(),
                    addressField.getText(),
                    (String) serviceCombo.getSelectedItem(),
                    (String) typeCombo.getSelectedItem(),
                    weight
                );
                
                JOptionPane.showMessageDialog(dialog, 
                    "Order created successfully!\nOrder ID: " + order.getOrderId() + 
                    "\nTotal: Rp " + String.format("%,.0f", order.getTotal()),
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                
                // Refresh the dashboard to show updated points and order count
                updateDashboard();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid weight!", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(createButton);
        buttonPanel.add(cancelButton);
        
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(buttonPanel, gbc);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void showOrderHistoryDialog() {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Order History", true);
        dialog.setSize(700, 400);
        dialog.setLocationRelativeTo(this);
        
        String[] columns = {"Order ID", "Service", "Type", "Weight", "Status", "Total", "Date"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        
        if (currentUser != null) {
            List<Order> orders = orderService.getOrdersByCustomer(currentUser.getUsername());
            for (Order order : orders) {
                Object[] row = {
                    order.getOrderId(),
                    order.getLaundryType(),
                    order.getService(),
                    order.getWeight() + " kg",
                    order.getStatus(),
                    "Rp " + String.format("%,.0f", order.getTotal()),
                    order.getOrderTime().toLocalDate().toString()
                };
                model.addRow(row);
            }
        }
        
        JScrollPane scrollPane = new JScrollPane(table);
        dialog.add(scrollPane, BorderLayout.CENTER);
        
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(closeButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }
    
    private void showPointsDialog() {
        String message = "Current Points: " + (currentUser != null ? currentUser.getPoints() : 0) + "\n\n" +
                       "Points are earned with each order:\n" +
                       "- Regular orders: 10 points\n" +
                       "- Express orders: 15 points\n" +
                       "- Premium orders: 20 points\n\n" +
                       "Use points for discounts on future orders!";
        
        JOptionPane.showMessageDialog(this, message, "Loyalty Points", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showProfileDialog() {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Update Profile", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Form fields
        JTextField nameField = new JTextField(currentUser != null ? currentUser.getFullName() : "", 20);
        JTextField phoneField = new JTextField(currentUser != null ? currentUser.getPhone() : "", 20);
        JTextField addressField = new JTextField(currentUser != null ? currentUser.getAddress() : "", 20);
        JPasswordField passwordField = new JPasswordField(20);
        
        // Add components
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        panel.add(phoneField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1;
        panel.add(addressField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("New Password:"), gbc);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton updateButton = new JButton("Update");
        JButton cancelButton = new JButton("Cancel");
        
        updateButton.addActionListener(e -> {
            if (currentUser != null) {
                try {
                    currentUser.setFullName(nameField.getText().trim());
                    currentUser.setPhone(phoneField.getText().trim());
                    currentUser.setAddress(addressField.getText().trim());
                    
                    String newPassword = new String(passwordField.getPassword());
                    if (!newPassword.trim().isEmpty()) {
                        currentUser.setPassword(newPassword);
                    }
                    
                    // Save changes to database
                    userRepository.updateUser(currentUser);
                    
                    JOptionPane.showMessageDialog(dialog, "Profile updated successfully!", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    
                    // Refresh the dashboard to show updated info
                    updateDashboard();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Failed to update profile: " + ex.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(updateButton);
        buttonPanel.add(cancelButton);
        
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(buttonPanel, gbc);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
}