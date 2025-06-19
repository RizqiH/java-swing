package com.laundry;

import com.laundry.model.User;
import com.laundry.repository.InMemoryUserRepository;
import com.laundry.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for UserRepository implementations.
 */
class UserRepositoryTest {
    
    private UserRepository userRepository;
    private User testUser;
    
    @BeforeEach
    void setUp() {
        userRepository = new InMemoryUserRepository();
        testUser = new User("testuser", "password", "Test User", "081234567890", "Test Address", "MEMBER");
    }
    
    @Test
    @DisplayName("Should save user successfully")
    void testSaveUser() {
        // When
        userRepository.addUser(testUser);
        
        // Then
        User foundUser = userRepository.getUser("testuser");
        assertNotNull(foundUser);
        assertEquals("testuser", foundUser.getUsername());
        assertEquals("Test User", foundUser.getFullName());
    }
    
    @Test
    @DisplayName("Should find user by username")
    void testGetUser() {
        // Given
        userRepository.addUser(testUser);
        
        // When
        User foundUser = userRepository.getUser("testuser");
        
        // Then
        assertNotNull(foundUser);
        assertEquals(testUser.getUsername(), foundUser.getUsername());
        assertEquals(testUser.getFullName(), foundUser.getFullName());
    }
    
    @Test
    @DisplayName("Should return null for non-existent username")
    void testGetUserNotFound() {
        // When
        User foundUser = userRepository.getUser("nonexistent");
        
        // Then
        assertNull(foundUser);
    }
    
    @Test
    @DisplayName("Should check if user exists by username")
    void testUserExists() {
        // Given
        userRepository.addUser(testUser);
        
        // When & Then
        assertTrue(userRepository.userExists("testuser"));
        assertFalse(userRepository.userExists("nonexistent"));
    }
    
    @Test
    @DisplayName("Should get all members (non-admin users)")
    void testGetAllMembers() {
        // Given
        User admin = new User("admin", "admin123", "Administrator", "081111111111", "Admin Address", "ADMIN");
        User member1 = new User("member1", "pass1", "Member One", "082222222222", "Address 1", "MEMBER");
        User member2 = new User("member2", "pass2", "Member Two", "083333333333", "Address 2", "MEMBER");
        
        userRepository.addUser(admin);
        userRepository.addUser(member1);
        userRepository.addUser(member2);
        
        // When
        Collection<User> members = userRepository.getAllMembers();
        
        // Then
        assertEquals(2, members.size());
        assertTrue(members.stream().anyMatch(u -> u.getUsername().equals("member1")));
        assertTrue(members.stream().anyMatch(u -> u.getUsername().equals("member2")));
        assertFalse(members.stream().anyMatch(u -> u.getUsername().equals("admin")));
    }
    
    @Test
    @DisplayName("Should update existing user")
    void testUpdateUser() {
        // Given
        userRepository.addUser(testUser);
        
        // When
        testUser.setFullName("Updated Name");
        testUser.setPhone("089999999999");
        userRepository.addUser(testUser); // Save again to update
        
        // Then
        User updatedUser = userRepository.getUser("testuser");
        assertEquals("Updated Name", updatedUser.getFullName());
        assertEquals("089999999999", updatedUser.getPhone());
    }
    
    @Test
    @DisplayName("Should handle multiple users")
    void testMultipleUsers() {
        // Given
        User user1 = new User("user1", "pass1", "User One", "081111111111", "Address 1", "MEMBER");
        User user2 = new User("user2", "pass2", "User Two", "082222222222", "Address 2", "MEMBER");
        User user3 = new User("user3", "pass3", "User Three", "083333333333", "Address 3", "MEMBER");
        
        // When
        userRepository.addUser(user1);
        userRepository.addUser(user2);
        userRepository.addUser(user3);
        
        // Then
        assertTrue(userRepository.userExists("user1"));
        assertTrue(userRepository.userExists("user2"));
        assertTrue(userRepository.userExists("user3"));
        
        assertEquals("User One", userRepository.getUser("user1").getFullName());
        assertEquals("User Two", userRepository.getUser("user2").getFullName());
        assertEquals("User Three", userRepository.getUser("user3").getFullName());
    }
    
    @Test
    @DisplayName("Should handle user points correctly")
    void testUserPoints() {
        // Given
        userRepository.addUser(testUser);
        
        // When
        testUser.addPoints(100);
        userRepository.addUser(testUser);
        
        // Then
        User foundUser = userRepository.getUser("testuser");
        assertEquals(100, foundUser.getPoints());
        
        // When
        foundUser.addPoints(50);
        userRepository.addUser(foundUser);
        
        // Then
        User updatedUser = userRepository.getUser("testuser");
        assertEquals(150, updatedUser.getPoints());
    }
}