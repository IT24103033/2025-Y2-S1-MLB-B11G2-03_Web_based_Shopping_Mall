package com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.service;

import com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.entity.User;
import com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.exception.ValidationException;
import com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service for user operations
 * Handles user registration, authentication, and management
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Register a new user
     * Validates username and email uniqueness, encrypts password
     */
    public User registerUser(User user) {
        // Check if username already exists
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new ValidationException("Username already exists");
        }
        
        // Check if email already exists
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new ValidationException("Email already exists");
        }
        
        // Encrypt password using BCrypt
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // Set default role if not set
        if (user.getRole() == null) {
            user.setRole(User.Role.CUSTOMER);
        }
        
        return userRepository.save(user);
    }

    /**
     * Authenticate user by username and password
     * Returns user if credentials are valid
     */
    public User authenticate(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ValidationException("Invalid username or password"));
        
        // Check if password matches
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new ValidationException("Invalid username or password");
        }
        
        return user;
    }

    // Find user by username
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // Find user by email
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // Find user by ID
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ValidationException("User not found"));
    }

    /**
     * Get all users
     * Used by admin for user management
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Update user information
    public void updateUser(User user) {
        // Check if email is being changed to one that already exists
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser.isPresent() && !existingUser.get().getUserId().equals(user.getUserId())) {
            throw new ValidationException("Email already in use");
        }
        
        userRepository.save(user);
    }

    /**
     * Change user password
     * Verifies old password before setting new one
     */
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = getUserById(userId);
        
        // Verify old password
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new ValidationException("Old password is incorrect");
        }
        
        // Encrypt and save new password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    /**
     * Delete user by ID
     * Used by admin
     */
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    /**
     * Get total number of users
     * Used for statistics
     */
    public long getTotalUsers() {
        return userRepository.count();
    }

    // Check if email exists
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    // Check if username exists
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Save user
     * Generic save method
     */
    public void saveUser(User user) {
        userRepository.save(user);
    }
}
