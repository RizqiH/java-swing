package com.laundry.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Database configuration class for managing database connections
 * and initialization using HikariCP connection pooling.
 */
public class DatabaseConfig {
    private static DatabaseConfig instance;
    private HikariDataSource dataSource;
    
    private final DatabaseConfigManager configManager;
    
    private DatabaseConfig() {
        this.configManager = DatabaseConfigManager.getInstance();
        configManager.printConfiguration();
        initializeDataSource();
        if (configManager.isAutoCreateTables()) {
            createTables();
        }
    }
    
    /**
     * Gets the singleton instance of DatabaseConfig.
     * @return DatabaseConfig instance
     */
    public static synchronized DatabaseConfig getInstance() {
        if (instance == null) {
            instance = new DatabaseConfig();
        }
        return instance;
    }
    
    /**
     * Initializes the HikariCP data source using configuration manager.
     */
    private void initializeDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(configManager.getCompleteJdbcUrl());
        config.setUsername(configManager.getDatabaseUsername());
        config.setPassword(configManager.getDatabasePassword());
        config.setDriverClassName(configManager.getDatabaseDriver());
        
        // Connection pool settings from configuration
        config.setMaximumPoolSize(configManager.getMaximumPoolSize());
        config.setMinimumIdle(configManager.getMinimumIdle());
        config.setConnectionTimeout(configManager.getConnectionTimeout());
        config.setIdleTimeout(configManager.getIdleTimeout());
        config.setMaxLifetime(configManager.getMaxLifetime());
        
        this.dataSource = new HikariDataSource(config);
    }
    
    /**
     * Gets a database connection from the pool.
     * @return Database connection
     * @throws SQLException if connection fails
     */
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
    
    /**
     * Creates database tables if they don't exist.
     */
    private void createTables() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Create users table
            String createUsersTable = "CREATE TABLE IF NOT EXISTS users (" +
                "username VARCHAR(50) PRIMARY KEY," +
                "password VARCHAR(255) NOT NULL," +
                "full_name VARCHAR(100) NOT NULL," +
                "phone VARCHAR(20) NOT NULL," +
                "address TEXT NOT NULL," +
                "role ENUM('ADMIN', 'MEMBER') NOT NULL," +
                "points INT DEFAULT 0," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";
            stmt.executeUpdate(createUsersTable);
            
            // Create orders table
            String createOrdersTable = "CREATE TABLE IF NOT EXISTS orders (" +
                "order_id VARCHAR(20) PRIMARY KEY," +
                "customer_name VARCHAR(100) NOT NULL," +
                "phone VARCHAR(20) NOT NULL," +
                "address TEXT NOT NULL," +
                "laundry_type VARCHAR(50) NOT NULL," +
                "service VARCHAR(50) NOT NULL," +
                "status VARCHAR(20) DEFAULT 'Pending'," +
                "weight DECIMAL(5,2) NOT NULL," +
                "total DECIMAL(10,2) NOT NULL," +
                "pickup_time DATETIME," +
                "order_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "customer_id INT" +
                ")";
            stmt.executeUpdate(createOrdersTable);
            
            // Insert sample data if enabled in configuration
            if (configManager.isAutoInsertSampleData()) {
                // Insert default admin user if not exists
                String insertAdmin = "INSERT IGNORE INTO users (username, password, full_name, phone, address, role) " +
                    "VALUES ('admin', 'admin', 'Administrator', '081234567890', 'Admin Office', 'ADMIN')";
                stmt.executeUpdate(insertAdmin);
                
                // Insert sample member if not exists
                String insertMember = "INSERT IGNORE INTO users (username, password, full_name, phone, address, role) " +
                    "VALUES ('john', '123', 'John Doe', '081234567891', 'Jl. Merdeka No. 1', 'MEMBER')";
                stmt.executeUpdate(insertMember);
                
                System.out.println("Sample data inserted successfully!");
            }
            
            System.out.println("Database tables created successfully!");
            
        } catch (SQLException e) {
            System.err.println("Error creating database tables: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Closes the data source and all connections.
     */
    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
    
    /**
     * Gets the data source for advanced usage.
     * @return HikariDataSource instance
     */
    public DataSource getDataSource() {
        return dataSource;
    }
}