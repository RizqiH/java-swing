package com.laundry.model;

/**
 * User entity class representing a user in the laundry system.
 * Supports both ADMIN and MEMBER roles with proper encapsulation.
 */
public class User {
    private String username;
    private String password;
    private String fullName;
    private String phone;
    private String address;
    private String role;
    private int points;
    
    public User(String username, String password, String fullName, String phone, String address, String role) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.phone = phone;
        this.address = address;
        this.role = role;
        this.points = 0;
    }
    
    // Getters
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getFullName() { return fullName; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }
    public String getRole() { return role; }
    public int getPoints() { return points; }
    
    // Setters with validation
    public void setPassword(String password) {
        if (password != null && !password.trim().isEmpty()) {
            this.password = password;
        }
    }
    
    public void setFullName(String fullName) {
        if (fullName != null && !fullName.trim().isEmpty()) {
            this.fullName = fullName;
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
    
    public void addPoints(int points) {
        if (points > 0) {
            this.points += points;
        }
    }
    
    public boolean deductPoints(int points) {
        if (points > 0 && this.points >= points) {
            this.points -= points;
            return true;
        }
        return false;
    }
}