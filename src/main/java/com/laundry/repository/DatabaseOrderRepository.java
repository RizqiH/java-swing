package com.laundry.repository;

import com.laundry.config.DatabaseConfig;
import com.laundry.model.Order;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Database implementation of OrderRepository.
 * Uses MySQL database with prepared statements for secure data access.
 */
public class DatabaseOrderRepository implements OrderRepository {
    private final DatabaseConfig databaseConfig;
    
    public DatabaseOrderRepository() {
        this.databaseConfig = DatabaseConfig.getInstance();
    }
    
    @Override
    public void addOrder(Order order) {
        save(order);
    }
    
    @Override
    public void save(Order order) {
        String sql = "INSERT INTO orders (order_id, customer_name, phone, address, laundry_type, service, status, weight, total, pickup_time, order_time, customer_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, order.getOrderId());
            stmt.setString(2, order.getCustomerName());
            stmt.setString(3, order.getPhone());
            stmt.setString(4, order.getAddress());
            stmt.setString(5, order.getLaundryType());
            stmt.setString(6, order.getService());
            stmt.setString(7, order.getStatus());
            stmt.setDouble(8, order.getWeight());
            stmt.setDouble(9, order.getTotal());
            
            // Handle pickup time (can be null)
            if (order.getPickupTime() != null) {
                stmt.setTimestamp(10, Timestamp.valueOf(order.getPickupTime()));
            } else {
                stmt.setTimestamp(10, null);
            }
            
            stmt.setTimestamp(11, Timestamp.valueOf(order.getOrderTime()));
            stmt.setInt(12, order.getCustomerId());
            
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("Error saving order: " + e.getMessage());
            throw new RuntimeException("Failed to save order", e);
        }
    }
    
    @Override
    public List<Order> getAllOrders() {
        return findAll();
    }
    
    @Override
    public List<Order> findAll() {
        String sql = "SELECT * FROM orders ORDER BY order_time DESC";
        List<Order> orders = new ArrayList<>();
        
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                orders.add(mapResultSetToOrder(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting all orders: " + e.getMessage());
            throw new RuntimeException("Failed to get all orders", e);
        }
        
        return orders;
    }
    
    @Override
    public List<Order> getOrdersByCustomer(String username) {
        return findByCustomerName(username);
    }
    
    @Override
    public Order findById(String orderId) {
        String sql = "SELECT * FROM orders WHERE order_id = ?";
        
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, orderId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToOrder(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding order by ID: " + e.getMessage());
            throw new RuntimeException("Failed to find order by ID", e);
        }
        
        return null;
    }
    
    @Override
    public List<Order> findByCustomerName(String customerName) {
        String sql = "SELECT * FROM orders WHERE customer_name = ? ORDER BY order_time DESC";
        List<Order> orders = new ArrayList<>();
        
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, customerName);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                orders.add(mapResultSetToOrder(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding orders by customer name: " + e.getMessage());
            throw new RuntimeException("Failed to find orders by customer name", e);
        }
        
        return orders;
    }
    
    @Override
    public String generateOrderId() {
        String sql = "SELECT COUNT(*) + 1 as next_id FROM orders";
        
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                int nextId = rs.getInt("next_id");
                return "ORD" + String.format("%03d", nextId);
            }
            
        } catch (SQLException e) {
            System.err.println("Error generating order ID: " + e.getMessage());
            throw new RuntimeException("Failed to generate order ID", e);
        }
        
        return "ORD001"; // Fallback
    }
    
    /**
     * Updates an existing order in the database.
     * @param order Order to update
     */
    public void updateOrder(Order order) {
        String sql = "UPDATE orders SET customer_name = ?, phone = ?, address = ?, laundry_type = ?, service = ?, status = ?, weight = ?, total = ?, pickup_time = ?, customer_id = ? WHERE order_id = ?";
        
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, order.getCustomerName());
            stmt.setString(2, order.getPhone());
            stmt.setString(3, order.getAddress());
            stmt.setString(4, order.getLaundryType());
            stmt.setString(5, order.getService());
            stmt.setString(6, order.getStatus());
            stmt.setDouble(7, order.getWeight());
            stmt.setDouble(8, order.getTotal());
            
            // Handle pickup time (can be null)
            if (order.getPickupTime() != null) {
                stmt.setTimestamp(9, Timestamp.valueOf(order.getPickupTime()));
            } else {
                stmt.setTimestamp(9, null);
            }
            
            stmt.setInt(10, order.getCustomerId());
            stmt.setString(11, order.getOrderId());
            
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("Error updating order: " + e.getMessage());
            throw new RuntimeException("Failed to update order", e);
        }
    }
    
    /**
     * Deletes an order from the database.
     * @param orderId ID of order to delete
     */
    public void deleteOrder(String orderId) {
        String sql = "DELETE FROM orders WHERE order_id = ?";
        
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, orderId);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("Error deleting order: " + e.getMessage());
            throw new RuntimeException("Failed to delete order", e);
        }
    }
    
    /**
     * Gets orders by status.
     * @param status Order status to filter by
     * @return List of orders with the specified status
     */
    public List<Order> findByStatus(String status) {
        String sql = "SELECT * FROM orders WHERE status = ? ORDER BY order_time DESC";
        List<Order> orders = new ArrayList<>();
        
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                orders.add(mapResultSetToOrder(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding orders by status: " + e.getMessage());
            throw new RuntimeException("Failed to find orders by status", e);
        }
        
        return orders;
    }
    
    /**
     * Maps a ResultSet row to an Order object.
     * @param rs ResultSet containing order data
     * @return Order object
     * @throws SQLException if database access error occurs
     */
    private Order mapResultSetToOrder(ResultSet rs) throws SQLException {
        Order order = new Order(rs.getString("order_id"));
        
        order.setCustomerName(rs.getString("customer_name"));
        order.setPhone(rs.getString("phone"));
        order.setAddress(rs.getString("address"));
        order.setLaundryType(rs.getString("laundry_type"));
        order.setService(rs.getString("service"));
        order.setStatus(rs.getString("status"));
        order.setWeight(rs.getDouble("weight"));
        order.setTotal(rs.getDouble("total"));
        order.setCustomerId(rs.getInt("customer_id"));
        
        // Handle pickup time (can be null)
        Timestamp pickupTimestamp = rs.getTimestamp("pickup_time");
        if (pickupTimestamp != null) {
            order.setPickupTime(pickupTimestamp.toLocalDateTime());
        }
        
        return order;
    }
}