package com.laundry.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Database configuration manager that reads settings from properties file.
 * Provides centralized configuration management for database connections.
 */
public class DatabaseConfigManager {
    private static DatabaseConfigManager instance;
    private Properties properties;
    
    private DatabaseConfigManager() {
        loadProperties();
    }
    
    /**
     * Gets the singleton instance of DatabaseConfigManager.
     * @return DatabaseConfigManager instance
     */
    public static synchronized DatabaseConfigManager getInstance() {
        if (instance == null) {
            instance = new DatabaseConfigManager();
        }
        return instance;
    }
    
    /**
     * Loads database properties from the configuration file.
     */
    private void loadProperties() {
        properties = new Properties();
        
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("database.properties")) {
            if (input != null) {
                properties.load(input);
                System.out.println("Database configuration loaded from properties file.");
            } else {
                System.out.println("Properties file not found, using default configuration.");
                setDefaultProperties();
            }
        } catch (IOException e) {
            System.err.println("Error loading database properties: " + e.getMessage());
            setDefaultProperties();
        }
    }
    
    /**
     * Sets default database properties if file is not found.
     */
    private void setDefaultProperties() {
        properties.setProperty("db.url", "jdbc:mysql://localhost:3306/laundry_system");
        properties.setProperty("db.username", "root");
        properties.setProperty("db.password", "");
        properties.setProperty("db.driver", "com.mysql.cj.jdbc.Driver");
        properties.setProperty("db.pool.maximum", "10");
        properties.setProperty("db.pool.minimum", "2");
        properties.setProperty("db.connection.timeout", "30000");
        properties.setProperty("db.idle.timeout", "600000");
        properties.setProperty("db.max.lifetime", "1800000");
        properties.setProperty("db.auto.create.tables", "true");
        properties.setProperty("db.auto.insert.sample.data", "true");
        properties.setProperty("db.ssl.enabled", "false");
    }
    
    // Getter methods for database configuration
    public String getDatabaseUrl() {
        return properties.getProperty("db.url");
    }
    
    public String getDatabaseUsername() {
        return properties.getProperty("db.username");
    }
    
    public String getDatabasePassword() {
        return properties.getProperty("db.password");
    }
    
    public String getDatabaseDriver() {
        return properties.getProperty("db.driver");
    }
    
    public int getMaximumPoolSize() {
        return Integer.parseInt(properties.getProperty("db.pool.maximum", "10"));
    }
    
    public int getMinimumIdle() {
        return Integer.parseInt(properties.getProperty("db.pool.minimum", "2"));
    }
    
    public long getConnectionTimeout() {
        return Long.parseLong(properties.getProperty("db.connection.timeout", "30000"));
    }
    
    public long getIdleTimeout() {
        return Long.parseLong(properties.getProperty("db.idle.timeout", "600000"));
    }
    
    public long getMaxLifetime() {
        return Long.parseLong(properties.getProperty("db.max.lifetime", "1800000"));
    }
    
    public boolean isAutoCreateTables() {
        return Boolean.parseBoolean(properties.getProperty("db.auto.create.tables", "true"));
    }
    
    public boolean isAutoInsertSampleData() {
        return Boolean.parseBoolean(properties.getProperty("db.auto.insert.sample.data", "true"));
    }
    
    public boolean isSslEnabled() {
        return Boolean.parseBoolean(properties.getProperty("db.ssl.enabled", "false"));
    }
    
    /**
     * Gets the complete JDBC URL with SSL settings.
     * @return Complete JDBC URL
     */
    public String getCompleteJdbcUrl() {
        String baseUrl = getDatabaseUrl();
        
        if (!isSslEnabled()) {
            if (baseUrl.contains("?")) {
                baseUrl += "&useSSL=false&allowPublicKeyRetrieval=true";
            } else {
                baseUrl += "?useSSL=false&allowPublicKeyRetrieval=true";
            }
        }
        
        return baseUrl;
    }
    
    /**
     * Prints current configuration for debugging.
     */
    public void printConfiguration() {
        System.out.println("=== Database Configuration ===");
        System.out.println("URL: " + getDatabaseUrl());
        System.out.println("Username: " + getDatabaseUsername());
        System.out.println("Driver: " + getDatabaseDriver());
        System.out.println("Max Pool Size: " + getMaximumPoolSize());
        System.out.println("Min Idle: " + getMinimumIdle());
        System.out.println("Auto Create Tables: " + isAutoCreateTables());
        System.out.println("Auto Insert Sample Data: " + isAutoInsertSampleData());
        System.out.println("SSL Enabled: " + isSslEnabled());
        System.out.println("==============================");
    }
}