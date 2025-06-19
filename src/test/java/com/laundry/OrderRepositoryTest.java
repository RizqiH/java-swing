package com.laundry;

import com.laundry.model.Order;
import com.laundry.repository.InMemoryOrderRepository;
import com.laundry.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for OrderRepository implementations.
 */
class OrderRepositoryTest {
    
    private OrderRepository orderRepository;
    private Order testOrder;
    
    @BeforeEach
    void setUp() {
        orderRepository = new InMemoryOrderRepository();
        testOrder = new Order("ORD001");
        testOrder.setCustomerName("John Doe");
        testOrder.setPhone("081234567890");
        testOrder.setLaundryType("Regular");
        testOrder.setService("Wash & Dry");
        testOrder.setWeight(5.0);
        testOrder.setTotal(15000.0);
    }
    
    @Test
    @DisplayName("Should save order successfully")
    void testSaveOrder() {
        // When
        orderRepository.save(testOrder);
        
        // Then
        Order foundOrder = orderRepository.findById("ORD001");
        assertNotNull(foundOrder);
        assertEquals("ORD001", foundOrder.getOrderId());
        assertEquals("John Doe", foundOrder.getCustomerName());
    }
    
    @Test
    @DisplayName("Should find order by ID")
    void testFindById() {
        // Given
        orderRepository.save(testOrder);
        
        // When
        Order foundOrder = orderRepository.findById("ORD001");
        
        // Then
        assertNotNull(foundOrder);
        assertEquals(testOrder.getOrderId(), foundOrder.getOrderId());
        assertEquals(testOrder.getCustomerName(), foundOrder.getCustomerName());
        assertEquals(testOrder.getLaundryType(), foundOrder.getLaundryType());
    }
    
    @Test
    @DisplayName("Should return null for non-existent order ID")
    void testFindByIdNotFound() {
        // When
        Order foundOrder = orderRepository.findById("NONEXISTENT");
        
        // Then
        assertNull(foundOrder);
    }
    
    @Test
    @DisplayName("Should get all orders")
    void testFindAll() {
        // Given
        Order order1 = new Order("ORD001");
        order1.setCustomerName("John Doe");
        order1.setPhone("081111111111");
        order1.setLaundryType("Regular");
        order1.setService("Wash & Dry");
        order1.setWeight(3.0);
        order1.setTotal(12000.0);
        
        Order order2 = new Order("ORD002");
        order2.setCustomerName("Jane Smith");
        order2.setPhone("082222222222");
        order2.setLaundryType("Express");
        order2.setService("Dry Clean");
        order2.setWeight(2.0);
        order2.setTotal(25000.0);
        
        Order order3 = new Order("ORD003");
        order3.setCustomerName("Bob Johnson");
        order3.setPhone("083333333333");
        order3.setLaundryType("Regular");
        order3.setService("Wash Only");
        order3.setWeight(4.0);
        order3.setTotal(8000.0);
        
        orderRepository.save(order1);
        orderRepository.save(order2);
        orderRepository.save(order3);
        
        // When
        List<Order> allOrders = orderRepository.findAll();
        
        // Then
        assertEquals(3, allOrders.size());
        assertTrue(allOrders.stream().anyMatch(o -> o.getOrderId().equals("ORD001")));
        assertTrue(allOrders.stream().anyMatch(o -> o.getOrderId().equals("ORD002")));
        assertTrue(allOrders.stream().anyMatch(o -> o.getOrderId().equals("ORD003")));
    }
    
    @Test
    @DisplayName("Should find orders by customer name")
    void testFindByCustomerName() {
        // Given
        Order order1 = new Order("ORD001");
        order1.setCustomerName("John Doe");
        order1.setPhone("081111111111");
        order1.setLaundryType("Regular");
        order1.setService("Wash & Dry");
        order1.setWeight(3.0);
        order1.setTotal(12000.0);
        
        Order order2 = new Order("ORD002");
        order2.setCustomerName("John Doe");
        order2.setPhone("081111111111");
        order2.setLaundryType("Express");
        order2.setService("Dry Clean");
        order2.setWeight(2.0);
        order2.setTotal(25000.0);
        
        Order order3 = new Order("ORD003");
        order3.setCustomerName("Jane Smith");
        order3.setPhone("082222222222");
        order3.setLaundryType("Regular");
        order3.setService("Wash Only");
        order3.setWeight(4.0);
        order3.setTotal(8000.0);
        
        orderRepository.save(order1);
        orderRepository.save(order2);
        orderRepository.save(order3);
        
        // When
        List<Order> johnOrders = orderRepository.findByCustomerName("John Doe");
        
        // Then
        assertEquals(2, johnOrders.size());
        assertTrue(johnOrders.stream().allMatch(o -> o.getCustomerName().equals("John Doe")));
        assertTrue(johnOrders.stream().anyMatch(o -> o.getOrderId().equals("ORD001")));
        assertTrue(johnOrders.stream().anyMatch(o -> o.getOrderId().equals("ORD002")));
    }
    
    @Test
    @DisplayName("Should return empty list for non-existent customer")
    void testFindByCustomerNameNotFound() {
        // When
        List<Order> orders = orderRepository.findByCustomerName("Nonexistent Customer");
        
        // Then
        assertTrue(orders.isEmpty());
    }
    
    @Test
    @DisplayName("Should update existing order")
    void testUpdateOrder() {
        // Given
        orderRepository.save(testOrder);
        
        // When
        testOrder.setStatus("Processing");
        testOrder.setTotal(20000.0);
        orderRepository.save(testOrder); // Save again to update
        
        // Then
        Order updatedOrder = orderRepository.findById("ORD001");
        assertEquals("Processing", updatedOrder.getStatus());
        assertEquals(20000.0, updatedOrder.getTotal());
    }
    
    @Test
    @DisplayName("Should handle order status changes")
    void testOrderStatusChanges() {
        // Given
        orderRepository.save(testOrder);
        
        // When & Then - Initial status
        Order order = orderRepository.findById("ORD001");
        assertEquals("Pending", order.getStatus());
        
        // When & Then - Update to Processing
        order.setStatus("Processing");
        orderRepository.save(order);
        Order processingOrder = orderRepository.findById("ORD001");
        assertEquals("Processing", processingOrder.getStatus());
        
        // When & Then - Update to Completed
        processingOrder.setStatus("Completed");
        orderRepository.save(processingOrder);
        Order completedOrder = orderRepository.findById("ORD001");
        assertEquals("Completed", completedOrder.getStatus());
    }
    
    @Test
    @DisplayName("Should handle multiple orders with different statuses")
    void testMultipleOrdersWithDifferentStatuses() {
        // Given
        Order pendingOrder = new Order("ORD001");
        pendingOrder.setCustomerName("John Doe");
        pendingOrder.setPhone("081111111111");
        pendingOrder.setLaundryType("Regular");
        pendingOrder.setService("Wash & Dry");
        pendingOrder.setWeight(3.0);
        pendingOrder.setTotal(12000.0);
        
        Order processingOrder = new Order("ORD002");
        processingOrder.setCustomerName("Jane Smith");
        processingOrder.setPhone("082222222222");
        processingOrder.setLaundryType("Express");
        processingOrder.setService("Dry Clean");
        processingOrder.setWeight(2.0);
        processingOrder.setTotal(25000.0);
        
        Order completedOrder = new Order("ORD003");
        completedOrder.setCustomerName("Bob Johnson");
        completedOrder.setPhone("083333333333");
        completedOrder.setLaundryType("Regular");
        completedOrder.setService("Wash Only");
        completedOrder.setWeight(4.0);
        completedOrder.setTotal(8000.0);
        
        processingOrder.setStatus("Processing");
        completedOrder.setStatus("Completed");
        
        orderRepository.save(pendingOrder);
        orderRepository.save(processingOrder);
        orderRepository.save(completedOrder);
        
        // When
        List<Order> allOrders = orderRepository.findAll();
        
        // Then
        assertEquals(3, allOrders.size());
        
        long pendingCount = allOrders.stream().filter(o -> "Pending".equals(o.getStatus())).count();
        long processingCount = allOrders.stream().filter(o -> "Processing".equals(o.getStatus())).count();
        long completedCount = allOrders.stream().filter(o -> "Completed".equals(o.getStatus())).count();
        
        assertEquals(1, pendingCount);
        assertEquals(1, processingCount);
        assertEquals(1, completedCount);
    }
    
    @Test
    @DisplayName("Should calculate total revenue correctly")
    void testTotalRevenue() {
        // Given
        Order order1 = new Order("ORD001");
        order1.setCustomerName("John Doe");
        order1.setPhone("081111111111");
        order1.setLaundryType("Regular");
        order1.setService("Wash & Dry");
        order1.setWeight(3.0);
        order1.setTotal(15000.0);
        
        Order order2 = new Order("ORD002");
        order2.setCustomerName("Jane Smith");
        order2.setPhone("082222222222");
        order2.setLaundryType("Express");
        order2.setService("Dry Clean");
        order2.setWeight(2.0);
        order2.setTotal(25000.0);
        
        Order order3 = new Order("ORD003");
        order3.setCustomerName("Bob Johnson");
        order3.setPhone("083333333333");
        order3.setLaundryType("Regular");
        order3.setService("Wash Only");
        order3.setWeight(4.0);
        order3.setTotal(10000.0);
        
        orderRepository.save(order1);
        orderRepository.save(order2);
        orderRepository.save(order3);
        
        // When
        List<Order> allOrders = orderRepository.findAll();
        double totalRevenue = allOrders.stream().mapToDouble(Order::getTotal).sum();
        
        // Then
        assertEquals(50000.0, totalRevenue);
    }
}