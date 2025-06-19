package com.laundry.repository;

import com.laundry.config.DatabaseConfig;
import com.laundry.model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Database implementation of UserRepository.
 * Uses MySQL database with prepared statements for secure data access.
 */
public class DatabaseUserRepository implements UserRepository {
    private final DatabaseConfig databaseConfig;
    
    public DatabaseUserRepository() {
        this.databaseConfig = DatabaseConfig.getInstance();
    }
    
    @Override
    public void addUser(User user) {
        String sql = "INSERT INTO users (username, password, full_name, phone, address, role, points) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getFullName());
            stmt.setString(4, user.getPhone());
            stmt.setString(5, user.getAddress());
            stmt.setString(6, user.getRole());
            stmt.setInt(7, user.getPoints());
            
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("Error adding user: " + e.getMessage());
            throw new RuntimeException("Failed to add user", e);
        }
    }
    
    @Override
    public User getUser(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting user: " + e.getMessage());
            throw new RuntimeException("Failed to get user", e);
        }
        
        return null;
    }
    
    @Override
    public boolean userExists(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error checking user existence: " + e.getMessage());
            throw new RuntimeException("Failed to check user existence", e);
        }
        
        return false;
    }
    
    @Override
    public Collection<User> getAllMembers() {
        String sql = "SELECT * FROM users WHERE role = 'MEMBER' ORDER BY full_name";
        List<User> members = new ArrayList<>();
        
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                members.add(mapResultSetToUser(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting all members: " + e.getMessage());
            throw new RuntimeException("Failed to get all members", e);
        }
        
        return members;
    }
    
    /**
     * Updates user information in the database.
     * @param user User to update
     */
    public void updateUser(User user) {
        String sql = "UPDATE users SET password = ?, full_name = ?, phone = ?, address = ?, points = ? WHERE username = ?";
        
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, user.getPassword());
            stmt.setString(2, user.getFullName());
            stmt.setString(3, user.getPhone());
            stmt.setString(4, user.getAddress());
            stmt.setInt(5, user.getPoints());
            stmt.setString(6, user.getUsername());
            
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
            throw new RuntimeException("Failed to update user", e);
        }
    }
    
    /**
     * Deletes a user from the database.
     * @param username Username of user to delete
     */
    public void deleteUser(String username) {
        String sql = "DELETE FROM users WHERE username = ?";
        
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
            throw new RuntimeException("Failed to delete user", e);
        }
    }
    
    /**
     * Maps a ResultSet row to a User object.
     * @param rs ResultSet containing user data
     * @return User object
     * @throws SQLException if database access error occurs
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User(
            rs.getString("username"),
            rs.getString("password"),
            rs.getString("full_name"),
            rs.getString("phone"),
            rs.getString("address"),
            rs.getString("role")
        );
        
        // Set points separately since it's not in the constructor
        user.addPoints(rs.getInt("points"));
        
        return user;
    }
}