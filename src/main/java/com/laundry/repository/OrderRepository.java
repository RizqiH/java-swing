package com.laundry.repository;

import com.laundry.model.Order;
import java.util.List;

/**
 * Repository interface for Order data access operations.
 * Defines the contract for order data persistence.
 */
public interface OrderRepository {
    void addOrder(Order order);
    void save(Order order);
    void updateOrder(Order order);
    List<Order> getAllOrders();
    List<Order> findAll();
    List<Order> getOrdersByCustomer(String username);
    Order findById(String orderId);
    List<Order> findByCustomerName(String customerName);
    String generateOrderId();
}