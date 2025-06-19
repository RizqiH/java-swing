package com.laundry;

import com.laundry.model.User;
import com.laundry.repository.InMemoryUserRepository;
import com.laundry.repository.UserRepository;
import com.laundry.service.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for AuthenticationService.
 */
class AuthenticationServiceTest {
    
    private AuthenticationService authService;
    private UserRepository userRepository;
    
    @BeforeEach
    void setUp() {
        userRepository = new InMemoryUserRepository();
        authService = new AuthenticationService(userRepository);
        
        // Add a test user
        User testUser = new User("testuser", "password123", "Test User", "081234567890", "Test Address", "MEMBER");
        userRepository.addUser(testUser);
    }
    
    @Test
    @DisplayName("Should authenticate user with correct credentials")
    void testAuthenticateSuccess() {
        // When
        User authenticatedUser = authService.authenticate("testuser", "password123");
        
        // Then
        assertNotNull(authenticatedUser);
        assertEquals("testuser", authenticatedUser.getUsername());
        assertEquals("Test User", authenticatedUser.getFullName());
    }
    
    @Test
    @DisplayName("Should return null for incorrect username")
    void testAuthenticateWrongUsername() {
        // When
        User authenticatedUser = authService.authenticate("wronguser", "password123");
        
        // Then
        assertNull(authenticatedUser);
    }
    
    @Test
    @DisplayName("Should return null for incorrect password")
    void testAuthenticateWrongPassword() {
        // When
        User authenticatedUser = authService.authenticate("testuser", "wrongpassword");
        
        // Then
        assertNull(authenticatedUser);
    }
    
    @Test
    @DisplayName("Should return null for empty username")
    void testAuthenticateEmptyUsername() {
        // When
        User authenticatedUser = authService.authenticate("", "password123");
        
        // Then
        assertNull(authenticatedUser);
    }
    
    @Test
    @DisplayName("Should return null for empty password")
    void testAuthenticateEmptyPassword() {
        // When
        User authenticatedUser = authService.authenticate("testuser", "");
        
        // Then
        assertNull(authenticatedUser);
    }
    
    @Test
    @DisplayName("Should return null for null credentials")
    void testAuthenticateNullCredentials() {
        // When & Then
        assertNull(authService.authenticate(null, "password123"));
        assertNull(authService.authenticate("testuser", null));
        assertNull(authService.authenticate(null, null));
    }
    
    @Test
    @DisplayName("Should register new user successfully")
    void testRegisterUserSuccess() {
        // When
        boolean result = authService.registerUser("newuser", "newpass123", 
                                                 "New User", "089876543210", "New Address");
        
        // Then
        assertTrue(result);
        
        // Verify user was saved
        User savedUser = userRepository.getUser("newuser");
        assertNotNull(savedUser);
        assertEquals("newuser", savedUser.getUsername());
        assertEquals("New User", savedUser.getFullName());
        assertEquals("089876543210", savedUser.getPhone());
        assertEquals("New Address", savedUser.getAddress());
    }
    
    @Test
    @DisplayName("Should not register user with existing username")
    void testRegisterUserExistingUsername() {
        // When
        boolean result = authService.registerUser("testuser", "newpass123", 
                                                 "Another User", "089876543210", "Another Address");
        
        // Then
        assertFalse(result);
        
        // Verify original user data is unchanged
        User existingUser = userRepository.getUser("testuser");
        assertEquals("Test User", existingUser.getFullName());
        assertEquals("081234567890", existingUser.getPhone());
    }
    
    @Test
    @DisplayName("Should not register user with empty username")
    void testRegisterUserEmptyUsername() {
        // When
        boolean result = authService.registerUser("", "newpass123", 
                                                 "New User", "089876543210", "New Address");
        
        // Then
        assertFalse(result);
    }
    
    @Test
    @DisplayName("Should not register user with empty password")
    void testRegisterUserEmptyPassword() {
        // When
        boolean result = authService.registerUser("newuser", "", 
                                                 "New User", "089876543210", "New Address");
        
        // Then
        assertFalse(result);
    }
    
    @Test
    @DisplayName("Should not register user with empty full name")
    void testRegisterUserEmptyFullName() {
        // When
        boolean result = authService.registerUser("newuser", "newpass123", 
                                                 "", "089876543210", "New Address");
        
        // Then
        assertFalse(result);
    }
    
    @Test
    @DisplayName("Should not register user with null parameters")
    void testRegisterUserNullParameters() {
        // When & Then
        assertFalse(authService.registerUser(null, "password", "Name", "Phone", "Address"));
        assertFalse(authService.registerUser("username", null, "Name", "Phone", "Address"));
        assertFalse(authService.registerUser("username", "password", null, "Phone", "Address"));
        assertFalse(authService.registerUser("username", "password", "Name", null, "Address"));
        assertFalse(authService.registerUser("username", "password", "Name", "Phone", null));
    }
    
    @Test
    @DisplayName("Should authenticate newly registered user")
    void testAuthenticateNewlyRegisteredUser() {
        // Given
        boolean registered = authService.registerUser("newuser", "newpass123", 
                                                     "New User", "089876543210", "New Address");
        assertTrue(registered);
        
        // When
        User authenticatedUser = authService.authenticate("newuser", "newpass123");
        
        // Then
        assertNotNull(authenticatedUser);
        assertEquals("newuser", authenticatedUser.getUsername());
        assertEquals("New User", authenticatedUser.getFullName());
    }
    
    @Test
    @DisplayName("Should handle case-sensitive usernames")
    void testCaseSensitiveUsernames() {
        // When
        User authenticatedUser1 = authService.authenticate("TestUser", "password123");
        User authenticatedUser2 = authService.authenticate("TESTUSER", "password123");
        User authenticatedUser3 = authService.authenticate("testuser", "password123");
        
        // Then
        assertNull(authenticatedUser1); // Different case
        assertNull(authenticatedUser2); // Different case
        assertNotNull(authenticatedUser3); // Exact match
    }
    
    @Test
    @DisplayName("Should handle multiple users registration and authentication")
    void testMultipleUsersRegistrationAndAuthentication() {
        // Given - Register multiple users
        assertTrue(authService.registerUser("user1", "pass1", "User One", "081111111111", "Address 1"));
        assertTrue(authService.registerUser("user2", "pass2", "User Two", "082222222222", "Address 2"));
        assertTrue(authService.registerUser("user3", "pass3", "User Three", "083333333333", "Address 3"));
        
        // When & Then - Authenticate each user
        User user1 = authService.authenticate("user1", "pass1");
        User user2 = authService.authenticate("user2", "pass2");
        User user3 = authService.authenticate("user3", "pass3");
        
        assertNotNull(user1);
        assertNotNull(user2);
        assertNotNull(user3);
        
        assertEquals("User One", user1.getFullName());
        assertEquals("User Two", user2.getFullName());
        assertEquals("User Three", user3.getFullName());
        
        // Test cross-authentication (wrong password for different user)
        assertNull(authService.authenticate("user1", "pass2"));
        assertNull(authService.authenticate("user2", "pass3"));
        assertNull(authService.authenticate("user3", "pass1"));
    }
    
    @Test
    @DisplayName("Should initialize new user with zero points")
    void testNewUserInitialPoints() {
        // Given
        authService.registerUser("pointsuser", "password", "Points User", "081111111111", "Address");
        
        // When
        User user = authService.authenticate("pointsuser", "password");
        
        // Then
        assertNotNull(user);
        assertEquals(0, user.getPoints());
    }
}