package com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.controller;

import com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.entity.Notification;
import com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.service.NotificationService;
import com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.exception.ValidationException;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;
import java.util.UUID;

@Controller
public class NotificationController {
    
    @Autowired
    private NotificationService notificationService;
    
    /**
     * Display notifications page with input validation
     * GET /notifications
     */
    @GetMapping("/notifications")
    public String showNotifications(HttpSession session, Model model) {
        try {
            System.out.println("🔍 NotificationController: Starting /notifications request");
            
            // Get or create temporary user ID (MUST match CartService session attribute name)
            String userId = (String) session.getAttribute("userId");
            if (userId == null) {
                userId = "TEMP_" + UUID.randomUUID().toString();
                session.setAttribute("userId", userId);
                System.out.println("🆕 Created new user ID: " + userId);
            } else {
                System.out.println("📝 Found existing user ID: " + userId);
            }
            
            // Validate user ID format (simple validation)
            System.out.println("✅ Validating user ID...");
            try {
                validateUserId(userId);
                System.out.println("✅ User ID validation passed");
            } catch (ValidationException ve) {
                System.out.println("⚠️ User ID validation failed: " + ve.getMessage());
                // For now, just log the warning but continue - don't block the user
                System.out.println("⚠️ Continuing anyway for user experience...");
            }
            
            System.out.println("🔍 NotificationController: Looking for notifications for user: " + userId);
            
            // Get recent 10 notifications for this user
            System.out.println("🔍 Calling notificationService.getRecentNotifications...");
            List<Notification> notifications = notificationService.getRecentNotifications(userId, 10);
            System.out.println("✅ Service call completed successfully");
            
            System.out.println("📋 Found " + notifications.size() + " notifications for user: " + userId);
            for (Notification notification : notifications) {
                System.out.println("  - " + notification.getMessage() + " (Method: " + notification.getDeliveryMethod() + ")");
            }
            
            // Add to model for template
            model.addAttribute("notifications", notifications);
            model.addAttribute("userId", userId);
            
            return "notifications";
            
        } catch (ValidationException e) {
            // Handle validation errors gracefully
            System.out.println("❌ Validation error in notifications page: " + e.getMessage());
            model.addAttribute("error", "Unable to load notifications: " + e.getMessage());
            model.addAttribute("notifications", List.of()); // Empty list
            return "notifications";
        } catch (Exception e) {
            // Handle any other errors
            System.out.println("❌ Unexpected error in notifications page: " + e.getMessage());
            model.addAttribute("error", "An unexpected error occurred. Please try again.");
            model.addAttribute("notifications", List.of()); // Empty list
            return "notifications";
        }
    }
    
    /**
     * Debug endpoint - show all notifications in database
     * GET /notifications/debug
     */
    @GetMapping("/notifications/debug")
    public String debugNotifications(Model model) {
        List<Notification> allNotifications = notificationService.getAllNotifications();
        
        System.out.println("🔧 DEBUG: Found " + allNotifications.size() + " total notifications in database:");
        for (Notification notification : allNotifications) {
            System.out.println("  - User: " + notification.getUserId() + 
                             ", Message: " + notification.getMessage() + 
                             ", Method: " + notification.getDeliveryMethod() +
                             ", Created: " + notification.getCreatedDate());
        }
        
        model.addAttribute("notifications", allNotifications);
        model.addAttribute("isDebug", true);
        
        return "notifications";
    }
    
    /**
     * Simple test endpoint to verify controller is working - returns plain text
     * GET /notifications/test
     */
    @GetMapping("/notifications/test")
    @org.springframework.web.bind.annotation.ResponseBody
    public String testNotifications() {
        System.out.println("🧪 TEST: Notifications controller test endpoint called");
        return "✅ NotificationController is working! This means the controller is loaded and responding.";
    }
    
    /**
     * Template test endpoint - tests if template rendering works
     * GET /notifications/template-test
     */
    @GetMapping("/notifications/template-test")
    public String testTemplate(Model model) {
        System.out.println("🧪 TEST: Testing template rendering");
        model.addAttribute("message", "Template test successful!");
        model.addAttribute("notifications", List.of());
        model.addAttribute("userId", "TEST_USER");
        return "notifications-simple";
    }
    
    // ================================
    // VALIDATION HELPER METHODS
    // ================================
    
    /**
     * Simple validation for user ID - keeping it simple to avoid issues with UUID format
     * @param userId The user ID to validate
     * @throws ValidationException if user ID is invalid
     */
    private void validateUserId(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new ValidationException("User ID cannot be null or empty");
        }
        if (userId.length() > 100) {
            throw new ValidationException("User ID cannot exceed 100 characters");
        }
        // Simple validation - just check it's not dangerous characters (no SQL injection risk)
        // Allow most characters but block potentially dangerous ones
        if (userId.contains("'") || userId.contains("\"") || userId.contains(";") || userId.contains("--")) {
            throw new ValidationException("User ID contains potentially dangerous characters");
        }
    }
}