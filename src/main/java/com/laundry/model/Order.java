package com.laundry.model;

import java.time.LocalDateTime;

/**
 * Order entity class representing a laundry order in the system.
 * Contains all order details with proper encapsulation and validation.
 */
public class Order {
    private String orderId;
    private String customerName;
    private String phone;
    private String address;
    private String laundryType;
    private String service;
    private String status;
    private double weight;
    private double total;
    private LocalDateTime pickupTime;
    private LocalDateTime orderTime;
    private int customerId;
    
    public Order(String orderId) {
        this.orderId = orderId;
        this.orderTime = LocalDateTime.now();
        this.status = "Pending";
    }
    
    // Getters
    public String getOrderId() { return orderId; }
    public String getCustomerName() { return customerName; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }
    public String getLaundryType() { return laundryType; }
    public String getService() { return service; }
    public String getStatus() { return status; }
    public double getWeight() { return weight; }
    public double getTotal() { return total; }
    public LocalDateTime getPickupTime() { return pickupTime; }
    public LocalDateTime getOrderTime() { return orderTime; }
    public int getCustomerId() { return customerId; }
    
    // Setters with validation
    public void setCustomerName(String customerName) {
        if (customerName != null && !customerName.trim().isEmpty()) {
            this.customerName = customerName;
        }
    }
    
    public void setPhone(String phone) {
        if (phone != null && !phone.trim().isEmpty()) {
            this.phone = phone;
        }
    }
    
    public void setAddress(String address) {
        if (address != null && !address.trim().isEmpty()) {
            this.address = address;
        }
    }
    
    public void setLaundryType(String laundryType) {
        if (laundryType != null && !laundryType.trim().isEmpty()) {
            this.laundryType = laundryType;
        }
    }
    
    public void setService(String service) {
        if (service != null && !service.trim().isEmpty()) {
            this.service = service;
        }
    }
    
    public void setStatus(String status) {
        if (status != null && !status.trim().isEmpty()) {
            this.status = status;
        }
    }
    
    public void setWeight(double weight) {
        this.weight = weight;
    }
    
    public void setTotal(double total) {
        this.total = total;
    }
    
    public void setPickupTime(LocalDateTime pickupTime) {
        this.pickupTime = pickupTime;
    }
    
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }
}