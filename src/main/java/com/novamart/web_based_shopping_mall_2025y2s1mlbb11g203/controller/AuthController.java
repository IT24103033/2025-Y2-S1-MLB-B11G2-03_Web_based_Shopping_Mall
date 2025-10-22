package com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.controller;

import com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.entity.User;
import com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.exception.ValidationException;
import com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * Controller for authentication operations
 * Handles login, registration, profile, and password management
 */
@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    // Show registration form
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    // Handle user registration
    @PostMapping("/register")
    public String register(@Valid @ModelAttribute User user, BindingResult result, Model model) {
        // Check for validation errors
        if (result.hasErrors()) {
            return "register";
        }
        
        try {
            // Attempt to register user
            userService.registerUser(user);
            return "redirect:/login?success=true";
        } catch (ValidationException e) {
            // Handle registration errors (duplicate username/email)
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("user", user);
            return "register";
        }
    }

    // Show login form
    @GetMapping("/login")
    public String showLoginForm(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "success", required = false) String success,
            @RequestParam(value = "logout", required = false) String logout,
            Model model) {
        
        if (error != null) {
            model.addAttribute("errorMessage", "Invalid username or password");
        }
        if (success != null) {
            model.addAttribute("successMessage", "Registration successful! Please login.");
        }
        if (logout != null) {
            model.addAttribute("successMessage", "You have been logged out successfully");
        }
        
        return "login";
    }

    /**
     * Show user profile page
     * Requires authentication
     */
    @GetMapping("/profile")
    public String showProfile(Authentication authentication, Model model) {
        try {
            // Get username from authentication (Spring Security)
            String username = authentication.getName();
            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new ValidationException("User not found"));
            
            model.addAttribute("user", user);
            return "profile";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/login";
        }
    }

    // Update user profile
    @PostMapping("/profile")
    public String updateProfile(@ModelAttribute User user, Authentication authentication, Model model) {
        try {
            // Get current user
            String username = authentication.getName();
            User existingUser = userService.findByUsername(username)
                    .orElseThrow(() -> new ValidationException("User not found"));
            
            // Update fields (keep same user ID and password)
            existingUser.setFirstName(user.getFirstName());
            existingUser.setLastName(user.getLastName());
            existingUser.setEmail(user.getEmail());
            existingUser.setPhoneNumber(user.getPhoneNumber());
            existingUser.setAddress(user.getAddress());
            
            userService.updateUser(existingUser);
            
            model.addAttribute("successMessage", "Profile updated successfully");
            model.addAttribute("user", existingUser);
            return "profile";
        } catch (ValidationException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("user", user);
            return "profile";
        }
    }

    // Change user password
    @PostMapping("/change-password")
    public String changePassword(
            @RequestParam String oldPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            Authentication authentication,
            Model model) {
        
        try {
            // Validate new password matches confirmation
            if (!newPassword.equals(confirmPassword)) {
                throw new ValidationException("New passwords do not match");
            }
            
            // Get current user
            String username = authentication.getName();
            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new ValidationException("User not found"));
            
            // Change password
            userService.changePassword(user.getUserId(), oldPassword, newPassword);
            
            model.addAttribute("successMessage", "Password changed successfully");
            model.addAttribute("user", user);
            return "profile";
        } catch (ValidationException e) {
            // Get user again to show profile
            String username = authentication.getName();
            User user = userService.findByUsername(username).orElse(null);
            
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("user", user);
            return "profile";
        }
    }
}
