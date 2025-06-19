package com.laundry.config;

import com.laundry.repository.DatabaseOrderRepository;
import com.laundry.repository.DatabaseUserRepository;
import com.laundry.repository.OrderRepository;
import com.laundry.repository.UserRepository;
import com.laundry.service.AuthenticationService;
import com.laundry.service.OrderService;

/**
 * Application configuration class that manages dependency injection
 * and initialization of repositories and services.
 */
public class AppConfig {
    private static AppConfig instance;
    
    // Repositories
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    
    // Services
    private final AuthenticationService authenticationService;
    private final OrderService orderService;
    
    private AppConfig() {
        System.out.println("Initializing application with database configuration...");
        
        UserRepository tempUserRepo = null;
        OrderRepository tempOrderRepo = null;
        
        try {
            // Try to initialize database repositories
            tempUserRepo = new DatabaseUserRepository();
            tempOrderRepo = new DatabaseOrderRepository();
            System.out.println("Successfully connected to database.");
        } catch (Exception e) {
            System.err.println("Failed to connect to database: " + e.getMessage());
            System.out.println("Falling back to in-memory repositories...");
            
            // Use fallback configuration
            FallbackAppConfig fallback = FallbackAppConfig.getInstance();
            tempUserRepo = fallback.getUserRepository();
            tempOrderRepo = fallback.getOrderRepository();
        }
        
        this.userRepository = tempUserRepo;
        this.orderRepository = tempOrderRepo;
        
        // Initialize services with dependency injection
        this.authenticationService = new AuthenticationService(userRepository);
        this.orderService = new OrderService(orderRepository, userRepository);
        
        System.out.println("Application configuration completed.");
    }
    
    /**
     * Gets the singleton instance of AppConfig.
     * @return AppConfig instance
     */
    public static synchronized AppConfig getInstance() {
        if (instance == null) {
            instance = new AppConfig();
        }
        return instance;
    }
    

    
    // Getters for dependency injection
    public UserRepository getUserRepository() {
        return userRepository;
    }
    
    public OrderRepository getOrderRepository() {
        return orderRepository;
    }
    
    public AuthenticationService getAuthenticationService() {
        return authenticationService;
    }
    
    public OrderService getOrderService() {
        return orderService;
    }
}