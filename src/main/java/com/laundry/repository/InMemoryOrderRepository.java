package com.laundry.repository;

import com.laundry.model.Order;
import java.util.ArrayList;
import java.util.List;

/**
 * In-memory implementation of OrderRepository.
 * Stores order data in an ArrayList for demonstration purposes.
 */
public class InMemoryOrderRepository implements OrderRepository {
    private final List<Order> orders = new ArrayList<>();
    private int orderIdCounter = 1;
    
    @Override
    public void addOrder(Order order) {
        orders.add(order);
    }
    
    @Override
    public void save(Order order) {
        orders.add(order);
    }
    
    @Override
    public void updateOrder(Order order) {
        for (int i = 0; i < orders.size(); i++) {
            if (orders.get(i).getOrderId().equals(order.getOrderId())) {
                orders.set(i, order);
                return;
            }
        }
    }
    
    @Override
    public List<Order> getAllOrders() {
        return new ArrayList<>(orders);
    }
    
    @Override
    public List<Order> findAll() {
        return new ArrayList<>(orders);
    }
    
    @Override
    public List<Order> getOrdersByCustomer(String username) {
        return orders.stream()
                .filter(o -> username.equals(o.getCustomerName()))
                .toList();
    }
    
    @Override
    public Order findById(String orderId) {
        return orders.stream()
                .filter(o -> orderId.equals(o.getOrderId()))
                .findFirst()
                .orElse(null);
    }
    
    @Override
    public List<Order> findByCustomerName(String customerName) {
        return orders.stream()
                .filter(o -> customerName.equals(o.getCustomerName()))
                .toList();
    }
    
    @Override
    public String generateOrderId() {
        return "ORD" + String.format("%03d", orderIdCounter++);
    }
}