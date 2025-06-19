package com.laundry.repository;

import com.laundry.model.User;
import java.util.Collection;

/**
 * Repository interface for User data access operations.
 * Defines the contract for user data persistence.
 */
public interface UserRepository {
    void addUser(User user);
    User getUser(String username);
    boolean userExists(String username);
    Collection<User> getAllMembers();
    void updateUser(User user);
}