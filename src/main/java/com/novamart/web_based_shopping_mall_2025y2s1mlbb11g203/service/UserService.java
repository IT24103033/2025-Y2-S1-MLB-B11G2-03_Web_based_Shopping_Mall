package com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.service;

import com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.entity.User;
import com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.exception.ValidationException;
import com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.repository.UserRepository;
import com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.util.EmailUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private EmailUtil emailUtil;

    public User registerUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new ValidationException("Email already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User login(String email, String password) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ValidationException("Invalid credentials"));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new ValidationException("Invalid credentials");
        }
        return user;
    }

    public void recoverPassword(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ValidationException("User not found"));
        String resetToken = UUID.randomUUID().toString();
        // Save token (e.g., in a separate entity or field - implement as needed)
        emailUtil.sendEmail(email, "Password Reset", "Click to reset: http://localhost:8080/reset?token=" + resetToken);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
    }

    public void updateUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent() &&
                !userRepository.findByEmail(user.getEmail()).get().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Email already in use");
        }
        userRepository.save(user);
    }

    public void changePassword(String email, String oldPassword, String newPassword) {
        User user = findByEmail(email);
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public long getTotalUsers() {
        return userRepository.count();
    }


    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new ValidationException("User not found"));
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }
}