package com.laundry.ui.panels;

import com.laundry.config.AppConfig;
import com.laundry.model.Order;
import com.laundry.repository.UserRepository;
import com.laundry.service.OrderService;
import com.laundry.ui.components.UIComponentFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.event.TableModelListener;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.Timer;

/**
 * Admin dashboard panel for managing orders and viewing statistics.
 * Provides order management interface and system overview.
 */
public class AdminDashboardPanel extends BasePanel {
    private final DefaultTableModel tableModel;
    private JTable orderTable;
    private DefaultTableCellRenderer cellRenderer;
    private Timer autoRefreshTimer;
    
    public AdminDashboardPanel() {
        super();
        
        // Initialize table components
        String[] columns = {"Order ID", "Customer", "Phone", "Service", "Status", "Total"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4; // Only status column is editable
            }
        };
        
        // Initialize orderTable first
        orderTable = new JTable(tableModel);
        
        setupTable();
        
        initializePanel();
        
        // Initialize auto-refresh timer (refresh every 5 seconds)
        autoRefreshTimer = new Timer(5000, e -> {
            refreshTable();
            updateStats();
        });
        autoRefreshTimer.start();
    }
    

    
    @Override
    protected void initializePanel() {
        setupAdminDashboard();
    }
    
    @Override
    protected String getPanelTitle() {
        return "ADMIN DASHBOARD";
    }
    
    private void setupAdminDashboard() {
        // Header
        JPanel headerPanel = createHeaderPanel();
        
        // Stats Panel
        JPanel statsPanel = createStatsPanel();
        statsPanel.setPreferredSize(new Dimension(0, 120)); // Set fixed height
        
        // Table and buttons
        JScrollPane tableScrollPane = createOrderTable();
        JPanel buttonPanel = createButtonPanel();
        
        // Combine table and buttons
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.add(buttonPanel, BorderLayout.NORTH);
        tablePanel.add(tableScrollPane, BorderLayout.CENTER);
        
        // Create a container for header and stats
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(headerPanel, BorderLayout.NORTH);
        topPanel.add(statsPanel, BorderLayout.CENTER);
        
        // Layout
        add(topPanel, BorderLayout.NORTH);
        add(tablePanel, BorderLayout.CENTER);
        
        refreshTable();
    }
    

    
    private JPanel createStatsPanel() {
        JPanel statsPanel = createStyledPanel(new GridLayout(1, 4, 20, 0));
        
        List<Order> allOrders = orderService.getAllOrders();
        
        statsPanel.add(UIComponentFactory.createCard("Total Orders", String.valueOf(allOrders.size())));
        statsPanel.add(UIComponentFactory.createCard("Active Orders", String.valueOf(
            allOrders.stream().filter(o -> "Pending".equals(o.getStatus()) || "In Progress".equals(o.getStatus())).count())));
        statsPanel.add(UIComponentFactory.createCard("Total Customers", String.valueOf(
            userRepository.getAllMembers().size())));
        statsPanel.add(UIComponentFactory.createCard("Revenue", "Rp " + String.format("%,.0f", 
            allOrders.stream().mapToDouble(Order::getTotal).sum())));
        
        return statsPanel;
    }
    
    private void setupTable() {
        orderTable.setFont(new Font("Arial", Font.PLAIN, 14));
        orderTable.setRowHeight(30);
        
        // Konfigurasi warna header table
        orderTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        orderTable.getTableHeader().setBackground(new Color(41, 128, 185)); // Biru
        orderTable.getTableHeader().setForeground(Color.WHITE);
        
        // Konfigurasi warna background table
        orderTable.setBackground(Color.WHITE); // Background putih
        orderTable.setForeground(Color.BLACK); // Text hitam
        
        // Konfigurasi warna selection
        orderTable.setSelectionBackground(new Color(52, 152, 219)); // Biru muda saat dipilih
        orderTable.setSelectionForeground(Color.WHITE); // Text putih saat dipilih
        
        // Konfigurasi warna grid lines
        orderTable.setGridColor(new Color(200, 200, 200)); // Abu-abu muda
        orderTable.setShowGrid(true);
        
        // Table already initialized in constructor
        
        // Pastikan text berwarna hitam dengan cara sederhana
        orderTable.setForeground(Color.BLACK);
        orderTable.setBackground(Color.WHITE);
        
        // Set warna untuk cell renderer default untuk semua tipe kolom
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setForeground(Color.BLACK);
                    c.setBackground(Color.WHITE);
                }
                return c;
            }
        };
        
        // Simpan renderer untuk diterapkan nanti
        this.cellRenderer = renderer;
        
        // Status combo box for editing
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{
            "Pending", "Processing", "Ready", "Completed", "Cancelled"
        });
        DefaultCellEditor statusEditor = new DefaultCellEditor(statusCombo);
        orderTable.getColumnModel().getColumn(4).setCellEditor(statusEditor);
        
        // Add table model listener to save changes automatically
        tableModel.addTableModelListener(e -> {
            if (e.getColumn() == 4) { // Status column
                int row = e.getFirstRow();
                String orderId = (String) tableModel.getValueAt(row, 0);
                String newStatus = (String) tableModel.getValueAt(row, 4);
                
                if (orderService.updateOrderStatus(orderId, newStatus)) {
                    updateStats(); // Refresh statistics
                    JOptionPane.showMessageDialog(this, 
                        "Order status updated successfully!", 
                        "Success", 
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Failed to update order status!", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                    refreshTable(); // Revert changes
                }
            }
        });
    }
    
    private JScrollPane createOrderTable() {
        return new JScrollPane(orderTable);
    }
    
    private JPanel createButtonPanel() {
        JPanel buttonPanel = createStyledPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        JButton refreshButton = UIComponentFactory.createStyledButton("Refresh", new Color(52, 152, 219));
        refreshButton.addActionListener(e -> {
            refreshTable();
            updateStats();
            JOptionPane.showMessageDialog(this, 
                "Data refreshed successfully!", 
                "Refresh", 
                JOptionPane.INFORMATION_MESSAGE);
        });
        
        JButton autoRefreshButton = UIComponentFactory.createStyledButton("Auto-Refresh: ON", new Color(46, 204, 113));
        autoRefreshButton.addActionListener(e -> {
            toggleAutoRefresh();
            if (autoRefreshTimer.isRunning()) {
                autoRefreshButton.setText("Auto-Refresh: ON");
                autoRefreshButton.setBackground(new Color(46, 204, 113)); // Green
            } else {
                autoRefreshButton.setText("Auto-Refresh: OFF");
                autoRefreshButton.setBackground(new Color(231, 76, 60)); // Red
            }
        });
        
        buttonPanel.add(autoRefreshButton);
        buttonPanel.add(refreshButton);
        return buttonPanel;
    }
    
    /**
     * Refreshes the order table with current data.
     */
    public void refreshTable() {
        tableModel.setRowCount(0);
        List<Order> orders = orderService.getAllOrders();
        
        for (Order order : orders) {
            Object[] row = {
                order.getOrderId(),
                order.getCustomerName(),
                order.getPhone(),
                order.getLaundryType() + " - " + order.getService(),
                order.getStatus(),
                "Rp " + String.format("%,.0f", order.getTotal())
            };
            tableModel.addRow(row);
        }
        
        // Terapkan renderer ke semua kolom setelah data dimuat
        if (cellRenderer != null) {
            for (int i = 0; i < orderTable.getColumnCount(); i++) {
                orderTable.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
            }
        }
    }
    
    /**
     * Updates the statistics panel with current data.
     */
    public void updateStats() {
        // Find and update the stats panel within topPanel
        Component[] components = getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                JPanel topPanel = (JPanel) comp;
                Component[] topComponents = topPanel.getComponents();
                for (Component topComp : topComponents) {
                    if (topComp instanceof JPanel) {
                        JPanel panel = (JPanel) topComp;
                        if (panel.getLayout() instanceof GridLayout) {
                            // Found the stats panel, replace it
                            topPanel.remove(panel);
                            JPanel newStatsPanel = createStatsPanel();
                            newStatsPanel.setPreferredSize(new Dimension(0, 120));
                            topPanel.add(newStatsPanel, BorderLayout.CENTER);
                            topPanel.revalidate();
                            topPanel.repaint();
                            return;
                        }
                    }
                }
            }
        }
        revalidate();
        repaint();
    }
    
    /**
     * Stops the auto-refresh timer.
     */
    public void stopAutoRefresh() {
        if (autoRefreshTimer != null && autoRefreshTimer.isRunning()) {
            autoRefreshTimer.stop();
        }
    }
    
    /**
     * Starts the auto-refresh timer.
     */
    public void startAutoRefresh() {
        if (autoRefreshTimer != null && !autoRefreshTimer.isRunning()) {
            autoRefreshTimer.start();
        }
    }
    
    /**
     * Toggles auto-refresh on/off.
     */
    public void toggleAutoRefresh() {
        if (autoRefreshTimer != null) {
            if (autoRefreshTimer.isRunning()) {
                stopAutoRefresh();
            } else {
                startAutoRefresh();
            }
        }
    }
}