package com.laundry.config;

import com.laundry.model.Order;
import com.laundry.model.User;
import com.laundry.repository.InMemoryOrderRepository;
import com.laundry.repository.InMemoryUserRepository;
import com.laundry.repository.OrderRepository;
import com.laundry.repository.UserRepository;
import com.laundry.service.AuthenticationService;
import com.laundry.service.OrderService;
import java.time.LocalDateTime;

/**
 * Fallback application configuration that uses in-memory repositories
 * when database connection fails. Ensures application can still run.
 */
public class FallbackAppConfig {
    private static FallbackAppConfig instance;
    
    // Repositories
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    
    // Services
    private final AuthenticationService authenticationService;
    private final OrderService orderService;
    
    private FallbackAppConfig() {
        System.out.println("[FALLBACK MODE] Using in-memory repositories due to database connection failure.");
        
        // Initialize in-memory repositories
        this.userRepository = new InMemoryUserRepository();
        this.orderRepository = new InMemoryOrderRepository();
        
        // Initialize services with dependency injection
        this.authenticationService = new AuthenticationService(userRepository);
        this.orderService = new OrderService(orderRepository, userRepository);
        
        // Initialize sample data
        initializeSampleData();
        
        System.out.println("[FALLBACK MODE] Application configured with in-memory storage.");
    }
    
    /**
     * Gets the singleton instance of FallbackAppConfig.
     * @return FallbackAppConfig instance
     */
    public static synchronized FallbackAppConfig getInstance() {
        if (instance == null) {
            instance = new FallbackAppConfig();
        }
        return instance;
    }
    
    /**
     * Initializes sample data for demonstration purposes.
     */
    private void initializeSampleData() {
        // Add default admin user
        userRepository.addUser(new User("admin", "admin", "Administrator", 
                                       "081234567890", "Admin Office", "ADMIN"));
        
        // Add sample member
        userRepository.addUser(new User("john", "123", "John Doe", 
                                       "081234567891", "Jl. Merdeka No. 1", "MEMBER"));
        
        // Add sample order
        Order sampleOrder = orderService.createOrder("John Doe", "081234567891", 
                                                    "Jl. Merdeka No. 1", "Cuci Setrika", 
                                                    "Regular", 2.5);
        sampleOrder.setStatus("Processing");
        sampleOrder.setPickupTime(LocalDateTime.now().plusHours(2));
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