package com.laundry.service;

import com.laundry.model.User;
import com.laundry.repository.UserRepository;

/**
 * Service class for handling user authentication and registration.
 * Contains business logic for user management operations.
 */
public class AuthenticationService {
    private final UserRepository userRepository;
    
    public AuthenticationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    /**
     * Authenticates a user with username and password.
     * @param username The username to authenticate
     * @param password The password to verify
     * @return User object if authentication successful, null otherwise
     */
    public User authenticate(String username, String password) {
        User user = userRepository.getUser(username);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }
    
    /**
     * Registers a new user in the system.
     * @param username The desired username
     * @param password The user's password
     * @param fullName The user's full name
     * @param phone The user's phone number
     * @param address The user's address
     * @return true if registration successful, false if username already exists
     */
    public boolean registerUser(String username, String password, String fullName, 
                               String phone, String address) {
        // Validate input parameters
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        if (password == null || password.trim().isEmpty()) {
            return false;
        }
        if (fullName == null || fullName.trim().isEmpty()) {
            return false;
        }
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        if (address == null || address.trim().isEmpty()) {
            return false;
        }
        
        if (userRepository.userExists(username)) {
            return false;
        }
        
        User newUser = new User(username, password, fullName, phone, address, "MEMBER");
        userRepository.addUser(newUser);
        return true;
    }
}