package com.laundry.repository;

import com.laundry.model.User;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * In-memory implementation of UserRepository.
 * Stores user data in a HashMap for demonstration purposes.
 */
public class InMemoryUserRepository implements UserRepository {
    private final Map<String, User> users = new HashMap<>();
    
    @Override
    public void addUser(User user) {
        users.put(user.getUsername(), user);
    }
    
    @Override
    public User getUser(String username) {
        return users.get(username);
    }
    
    @Override
    public boolean userExists(String username) {
        return users.containsKey(username);
    }
    
    @Override
    public Collection<User> getAllMembers() {
        return users.values().stream()
                .filter(u -> "MEMBER".equals(u.getRole()))
                .toList();
    }
    
    @Override
    public void updateUser(User user) {
        users.put(user.getUsername(), user);
    }
}