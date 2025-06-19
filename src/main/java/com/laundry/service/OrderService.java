package com.laundry.service;

import com.laundry.model.Order;
import com.laundry.model.User;
import com.laundry.repository.OrderRepository;
import com.laundry.repository.UserRepository;
import java.util.List;

/**
 * Service class for handling order operations.
 * Contains business logic for order management and pricing.
 */
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    
    public OrderService(OrderRepository orderRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }
    
    /**
     * Creates a new order with the given details.
     * @param customerName Name of the customer
     * @param phone Customer's phone number
     * @param address Customer's address
     * @param laundryType Type of laundry service
     * @param service Service level (Regular/Express)
     * @param weight Weight of laundry in kg
     * @return The created Order object
     */
    public Order createOrder(String customerName, String phone, String address,
                           String laundryType, String service, double weight) {
        String orderId = orderRepository.generateOrderId();
        Order order = new Order(orderId);
        order.setCustomerName(customerName);
        order.setPhone(phone);
        order.setAddress(address);
        order.setLaundryType(laundryType);
        order.setService(service);
        order.setWeight(weight);
        
        // Calculate total based on service and weight
        double pricePerKg = getPricePerKg(laundryType, service);
        order.setTotal(weight * pricePerKg);
        
        // Award points to existing user if found
        awardPointsToUser(phone, order.getTotal());
        
        orderRepository.addOrder(order);
        return order;
    }
    
    /**
     * Creates a new order for a logged-in user.
     * @param user The logged-in user
     * @param phone Customer's phone number
     * @param address Customer's address
     * @param laundryType Type of laundry service
     * @param service Service level (Regular/Express)
     * @param weight Weight of laundry in kg
     * @return Created order
     */
    public Order createOrderForUser(User user, String phone, String address,
                                  String laundryType, String service, double weight) {
        String orderId = orderRepository.generateOrderId();
        Order order = new Order(orderId);
        order.setCustomerName(user.getUsername()); // Store username for proper linking
        order.setPhone(phone);
        order.setAddress(address);
        order.setLaundryType(laundryType);
        order.setService(service);
        order.setWeight(weight);
        
        // Calculate total based on service and weight
        double pricePerKg = getPricePerKg(laundryType, service);
        order.setTotal(weight * pricePerKg);
        
        // Award points to the user
        awardPointsToUser(phone, order.getTotal());
        
        orderRepository.addOrder(order);
        return order;
    }
    
    /**
     * Calculates price per kg based on laundry type and service level.
     * @param laundryType Type of laundry service
     * @param service Service level
     * @return Price per kg in Rupiah
     */
    private double getPricePerKg(String laundryType, String service) {
        // Pricing logic based on service type
        switch (service) {
            case "Wash & Dry":
                if ("Express".equals(laundryType)) {
                    return 8000; // Express Wash & Dry
                } else {
                    return 5000; // Regular Wash & Dry
                }
            case "Dry Clean":
                return 15000; // Dry Clean service
            case "Wash Only":
                return 3000; // Wash Only service
            default:
                return 5000; // Default price for unknown services
        }
    }
    
    /**
     * Awards points to a user based on order total.
     * Points are calculated as total / 1000.
     * @param phone User's phone number
     * @param total Order total amount
     */
    private void awardPointsToUser(String phone, double total) {
        // Find user by phone number
        for (com.laundry.model.User user : userRepository.getAllMembers()) {
            if (phone.equals(user.getPhone())) {
                int points = (int) (total / 1000);
                user.addPoints(points);
                // Save the updated user with new points to the database
                userRepository.updateUser(user);
                break;
            }
        }
    }
    
    /**
     * Calculates the total price for a given laundry order.
     * @param laundryType Type of laundry service
     * @param service Service level
     * @param weight Weight of laundry in kg
     * @return Total price in Rupiah
     */
    public double calculatePrice(String laundryType, String service, double weight) {
        double pricePerKg = getPricePerKg(laundryType, service);
        return weight * pricePerKg;
    }

    /**
     * Retrieves all orders in the system.
     * @return List of all orders
     */
    public List<Order> getAllOrders() {
        return orderRepository.getAllOrders();
    }
    
    /**
     * Retrieves orders for a specific customer.
     * @param username Customer's username
     * @return List of orders for the customer
     */
    public List<Order> getOrdersByCustomer(String username) {
        return orderRepository.getOrdersByCustomer(username);
    }
    
    /**
     * Updates the status of an existing order.
     * @param orderId ID of the order to update
     * @param newStatus New status for the order
     * @return true if update was successful, false otherwise
     */
    public boolean updateOrderStatus(String orderId, String newStatus) {
        Order order = orderRepository.findById(orderId);
        if (order != null) {
            order.setStatus(newStatus);
            orderRepository.updateOrder(order);
            return true;
        }
        return false;
    }
}