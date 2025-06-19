package com.laundry;

import com.laundry.model.Order;
import com.laundry.model.User;
import com.laundry.repository.InMemoryOrderRepository;
import com.laundry.repository.InMemoryUserRepository;
import com.laundry.repository.OrderRepository;
import com.laundry.repository.UserRepository;
import com.laundry.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for OrderService.
 */
class OrderServiceTest {
    
    private OrderService orderService;
    private OrderRepository orderRepository;
    private UserRepository userRepository;
    private User testUser;
    
    @BeforeEach
    void setUp() {
        orderRepository = new InMemoryOrderRepository();
        userRepository = new InMemoryUserRepository();
        orderService = new OrderService(orderRepository, userRepository);
        
        // Create test user
        testUser = new User("testuser", "password", "Test User", "081234567890", "Test Address", "MEMBER");
        userRepository.addUser(testUser);
    }
    
    @Test
    @DisplayName("Should create order successfully")
    void testCreateOrderSuccess() {
        // When
        Order order = orderService.createOrder("Test User", "081234567890", "Test Address",
                                              "Regular", "Wash & Dry", 5.0);
        
        // Then
        assertNotNull(order);
        assertEquals("Test User", order.getCustomerName());
        assertEquals("081234567890", order.getPhone());
        assertEquals("Regular", order.getLaundryType());
        assertEquals("Wash & Dry", order.getService());
        assertEquals(5.0, order.getWeight());
        assertEquals("Pending", order.getStatus());
        assertTrue(order.getOrderId().startsWith("ORD"));
    }
    
    @Test
    @DisplayName("Should calculate price correctly for regular wash & dry")
    void testCalculatePriceRegularWashDry() {
        // When
        double price = orderService.calculatePrice("Regular", "Wash & Dry", 5.0);
        
        // Then
        assertEquals(25000.0, price); // 5kg * 5000 per kg
    }
    
    @Test
    @DisplayName("Should calculate price correctly for express wash & dry")
    void testCalculatePriceExpressWashDry() {
        // When
        double price = orderService.calculatePrice("Express", "Wash & Dry", 3.0);
        
        // Then
        assertEquals(24000.0, price); // 3kg * 8000 per kg
    }
    
    @Test
    @DisplayName("Should calculate price correctly for dry clean")
    void testCalculatePriceDryClean() {
        // When
        double price = orderService.calculatePrice("Regular", "Dry Clean", 2.0);
        
        // Then
        assertEquals(30000.0, price); // 2kg * 15000 per kg
    }
    
    @Test
    @DisplayName("Should calculate price correctly for wash only")
    void testCalculatePriceWashOnly() {
        // When
        double price = orderService.calculatePrice("Regular", "Wash Only", 4.0);
        
        // Then
        assertEquals(12000.0, price); // 4kg * 3000 per kg
    }
    
    @Test
    @DisplayName("Should return default price for unknown service")
    void testCalculatePriceUnknownService() {
        // When
        double price = orderService.calculatePrice("Regular", "Unknown Service", 3.0);
        
        // Then
        assertEquals(15000.0, price); // 3kg * 5000 per kg (default)
    }
    
    @Test
    @DisplayName("Should get all orders")
    void testGetAllOrders() {
        // Given
        Order order1 = orderService.createOrder("Customer 1", "081111111111", "Address 1",
                                               "Regular", "Wash & Dry", 3.0);
        Order order2 = orderService.createOrder("Customer 2", "082222222222", "Address 2",
                                               "Express", "Dry Clean", 2.0);
        Order order3 = orderService.createOrder("Customer 3", "083333333333", "Address 3",
                                               "Regular", "Wash Only", 4.0);
        
        // When
        List<Order> allOrders = orderService.getAllOrders();
        
        // Then
        assertEquals(3, allOrders.size());
        assertTrue(allOrders.contains(order1));
        assertTrue(allOrders.contains(order2));
        assertTrue(allOrders.contains(order3));
    }
    
    @Test
    @DisplayName("Should get orders by customer name")
    void testGetOrdersByCustomer() {
        // Given
        Order order1 = orderService.createOrder("John Doe", "081111111111", "John's Address",
                                               "Regular", "Wash & Dry", 3.0);
        Order order2 = orderService.createOrder("John Doe", "081111111111", "John's Address",
                                               "Express", "Dry Clean", 2.0);
        Order order3 = orderService.createOrder("Jane Smith", "082222222222", "Jane's Address",
                                               "Regular", "Wash Only", 4.0);
        
        // When
        List<Order> johnOrders = orderService.getOrdersByCustomer("John Doe");
        
        // Then
        assertEquals(2, johnOrders.size());
        assertTrue(johnOrders.contains(order1));
        assertTrue(johnOrders.contains(order2));
        assertFalse(johnOrders.contains(order3));
    }
    
    @Test
    @DisplayName("Should return empty list for non-existent customer")
    void testGetOrdersByNonExistentCustomer() {
        // When
        List<Order> orders = orderService.getOrdersByCustomer("Non Existent Customer");
        
        // Then
        assertTrue(orders.isEmpty());
    }
    
    @Test
    @DisplayName("Should generate unique order IDs")
    void testUniqueOrderIds() {
        // When
        Order order1 = orderService.createOrder("Customer 1", "081111111111", "Address 1",
                                               "Regular", "Wash & Dry", 3.0);
        Order order2 = orderService.createOrder("Customer 2", "082222222222", "Address 2",
                                               "Express", "Dry Clean", 2.0);
        Order order3 = orderService.createOrder("Customer 3", "083333333333", "Address 3",
                                               "Regular", "Wash Only", 4.0);
        
        // Then
        assertNotEquals(order1.getOrderId(), order2.getOrderId());
        assertNotEquals(order2.getOrderId(), order3.getOrderId());
        assertNotEquals(order1.getOrderId(), order3.getOrderId());
    }
    
    @Test
    @DisplayName("Should award points to existing user after order creation")
    void testAwardPointsToExistingUser() {
        // Given
        assertEquals(0, testUser.getPoints()); // Initial points
        
        // When
        Order order = orderService.createOrder("Test User", "081234567890", "Test Address",
                                              "Regular", "Wash & Dry", 5.0);
        
        // Then
        User updatedUser = userRepository.getUser("testuser");
        assertTrue(updatedUser.getPoints() > 0); // Points should be awarded
        
        // Points calculation: total / 1000 = 25000 / 1000 = 25 points
        assertEquals(25, updatedUser.getPoints());
    }
    
    @Test
    @DisplayName("Should not award points to non-existent user")
    void testNoPointsForNonExistentUser() {
        // When
        Order order = orderService.createOrder("Non Existent User", "089999999999", "Test Address",
                                              "Regular", "Wash & Dry", 3.0);
        
        // Then
        assertNotNull(order); // Order should still be created
        assertEquals("Non Existent User", order.getCustomerName());
        
        // Original user points should remain unchanged
        assertEquals(0, testUser.getPoints());
    }
    
    @Test
    @DisplayName("Should handle zero weight orders")
    void testZeroWeightOrder() {
        // When
        Order order = orderService.createOrder("Test User", "081234567890", "Test Address",
                                              "Regular", "Wash & Dry", 0.0);
        
        // Then
        assertNotNull(order);
        assertEquals(0.0, order.getWeight());
        assertEquals(0.0, order.getTotal());
    }
    
    @Test
    @DisplayName("Should handle negative weight orders")
    void testNegativeWeightOrder() {
        // When
        Order order = orderService.createOrder("Test User", "081234567890", "Test Address",
                                              "Regular", "Wash & Dry", -1.0);
        
        // Then
        assertNotNull(order);
        assertEquals(-1.0, order.getWeight());
        assertTrue(order.getTotal() < 0); // Negative total
    }
    
    @Test
    @DisplayName("Should handle large weight orders")
    void testLargeWeightOrder() {
        // When
        Order order = orderService.createOrder("Test User", "081234567890", "Test Address",
                                              "Regular", "Wash & Dry", 100.0);
        
        // Then
        assertNotNull(order);
        assertEquals(100.0, order.getWeight());
        assertEquals(500000.0, order.getTotal()); // 100kg * 5000 per kg
    }
    
    @Test
    @DisplayName("Should accumulate points from multiple orders")
    void testAccumulatePointsFromMultipleOrders() {
        // Given
        assertEquals(0, testUser.getPoints());
        
        // When - Create multiple orders
        orderService.createOrder("Test User", "081234567890", "Test Address", "Regular", "Wash & Dry", 2.0); // 10000 -> 10 points
        orderService.createOrder("Test User", "081234567890", "Test Address", "Express", "Dry Clean", 1.0); // 15000 -> 15 points
        orderService.createOrder("Test User", "081234567890", "Test Address", "Regular", "Wash Only", 3.0); // 9000 -> 9 points
        
        // Then
        User updatedUser = userRepository.getUser("testuser");
        assertEquals(34, updatedUser.getPoints()); // 10 + 15 + 9 = 34 points
    }
}