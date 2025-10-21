package com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.controller;

import com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.util.Date;

@RestController
@RequestMapping("/api/test")
public class TestController {
    
    @Autowired
    private NotificationService notificationService;
    
    /**
     * Test notification system
     * GET /api/test/notification/{orderId}
     */
    @GetMapping("/notification/{orderId}")
    public String testNotification(@PathVariable String orderId) {
        try {
            notificationService.getNotification(orderId);
            return "Notification sent successfully for order: " + orderId;
        } catch (Exception e) {
            return "Failed to send notification: " + e.getMessage();
        }
    }
    
    /**
     * Health check endpoint
     * GET /api/test/health
     */
    @GetMapping("/health")
    public String healthCheck() {
        return "Notification system is running!";
    }
    
    /**
     * Debug session information
     * GET /api/test/session
     */
    @GetMapping("/session")
    public String sessionDebug(HttpSession session) {
        return "Session ID: " + session.getId() + 
               " | Created: " + new Date(session.getCreationTime()) +
               " | User ID: " + session.getAttribute("userId");
    }
}