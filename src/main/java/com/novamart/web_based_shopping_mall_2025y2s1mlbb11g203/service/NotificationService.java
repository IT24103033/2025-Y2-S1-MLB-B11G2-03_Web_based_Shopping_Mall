package com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.service;

import com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.entity.Order;
import com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.entity.Notification;
import com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.repository.OrderRepository;
import com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.repository.NotificationRepository;
import com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.exception.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class NotificationService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    @Autowired(required = false)
    private JavaMailSender mailSender;
    
    /**
     * Create and send notifications for an order
     * Validates input and creates both in-app and email notifications
     * @param orderId The order ID to create notifications for
     * @throws ValidationException if orderId is invalid or order not found
     */
    public void getNotification(String orderId) {
        // Validate order ID input
        validateOrderId(orderId);
        // Find the order by ID
        Order order = orderRepository.findById(orderId).orElse(null);
        
        if (order == null) {
            System.out.println("Order not found: " + orderId);
            throw new ValidationException("Order not found with ID: " + orderId);
        }
        
        // Validate the order has required data
        validateOrder(order);
        
        // Create IN-APP notification for notifications page
        Notification inAppNotification = createNotification(
            order.getUserId(), 
            createSimpleMessage(order), 
            "in-app", 
            "sent"
        );
        
        // Validate notification before saving
        validateNotification(inAppNotification);
        
        // Save in-app notification
        notificationRepository.save(inAppNotification);
        System.out.println("✅ In-app notification created for user: " + order.getUserId());
        
        // Also create EMAIL notification if email is configured
        Notification emailNotification = createNotification(
            order.getUserId(), 
            createMessage(order), 
            "email", 
            "pending"
        );
        
        // Validate email notification before saving
        validateNotification(emailNotification);
        
        // Send email notification
        sendEmailNotification(order);
        emailNotification.setStatus("sent");
        
        // Save email notification record
        notificationRepository.save(emailNotification);
        System.out.println("📧 Email notification created for user: " + order.getUserId());
    }
    
    private void sendEmailNotification(Order order) {
        String message = "Dear " + order.getUser().getFirstName() + 
                        ",\n\nYour order " + order.getOrderId() + 
                        " is now " + order.getStatus().toUpperCase() + 
                        ".\nOrder Total: $" + order.getTotalAmount() + 
                        "\n\nThank you for shopping with NovaMart!";
        
        // Check if email is configured
        if (mailSender == null) {
            System.out.println("Email not configured. Would send email to: " + order.getUser().getEmail());
            System.out.println("Email content: " + message);
            return;
        }
        
        try {
            SimpleMailMessage email = new SimpleMailMessage();
            email.setTo(order.getUser().getEmail());
            email.setSubject("Order Update - " + order.getOrderId());
            email.setText(message);
            
            mailSender.send(email);
            System.out.println("Email sent to: " + order.getUser().getEmail());
        } catch (Exception e) {
            System.out.println("Failed to send email: " + e.getMessage());
        }
    }
    
    private String createSimpleMessage(Order order) {
        return "Your order " + order.getOrderId() + " has been completed successfully! Total: $" + order.getTotalAmount();
    }
    
    private String createMessage(Order order) {
        return "Dear " + order.getUser().getFirstName() + 
               ",\n\nYour order " + order.getOrderId() + 
               " is now " + order.getStatus().toUpperCase() + 
               ".\nOrder Total: $" + order.getTotalAmount() + 
               "\n\nThank you for shopping with NovaMart!";
    }
    
    /**
     * Get recent notifications for a user with validation
     * @param userId The user ID to get notifications for
     * @param limit Maximum number of notifications to return
     * @return List of recent notifications
     * @throws ValidationException if parameters are invalid
     */
    public List<Notification> getRecentNotifications(String userId, int limit) {
        // Validate input parameters
        if (userId == null || userId.trim().isEmpty()) {
            throw new ValidationException("User ID cannot be null or empty when fetching notifications");
        }
        if (limit <= 0) {
            throw new ValidationException("Limit must be greater than 0");
        }
        if (limit > 100) {
            throw new ValidationException("Limit cannot exceed 100 notifications");
        }
        
        return notificationRepository.findByUserIdOrderByCreatedDateDesc(userId, PageRequest.of(0, limit));
    }
    
    /**
     * Debug method - get all notifications in database
     * @return List of all notifications
     */
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }
    
    // ================================
    // VALIDATION METHODS (Private)
    // ================================
    
    /**
     * Validate order ID input
     * @param orderId The order ID to validate
     * @throws ValidationException if order ID is invalid
     */
    private void validateOrderId(String orderId) {
        if (orderId == null || orderId.trim().isEmpty()) {
            throw new ValidationException("Order ID cannot be null or empty");
        }
        if (orderId.length() > 100) {
            throw new ValidationException("Order ID cannot exceed 100 characters");
        }
    }
    
    /**
     * Validate order has required data for notifications
     * @param order The order to validate
     * @throws ValidationException if order is missing required data
     */
    private void validateOrder(Order order) {
        if (order.getUserId() == null || order.getUserId().trim().isEmpty()) {
            throw new ValidationException("Order must have a valid user ID");
        }
        if (order.getOrderId() == null || order.getOrderId().trim().isEmpty()) {
            throw new ValidationException("Order must have a valid order ID");
        }
        if (order.getTotalAmount() == null) {
            throw new ValidationException("Order must have a total amount");
        }
    }
    
    /**
     * Create a new notification with validation
     * @param userId User ID for the notification
     * @param message Message content
     * @param deliveryMethod email or in-app
     * @param status sent, pending, or failed
     * @return Created notification object
     */
    private Notification createNotification(String userId, String message, String deliveryMethod, String status) {
        Notification notification = new Notification();
        notification.setNotificationId(UUID.randomUUID().toString());
        notification.setUserId(userId);
        notification.setMessage(message);
        notification.setDeliveryMethod(deliveryMethod);
        notification.setStatus(status);
        notification.setCreatedDate(LocalDateTime.now());
        return notification;
    }
    
    /**
     * Validate notification data before saving
     * @param notification The notification to validate
     * @throws ValidationException if notification has invalid data
     */
    private void validateNotification(Notification notification) {
        // Validate user ID
        if (notification.getUserId() == null || notification.getUserId().trim().isEmpty()) {
            throw new ValidationException("Notification must have a valid user ID");
        }
        if (notification.getUserId().length() > 50) {
            throw new ValidationException("User ID cannot exceed 50 characters");
        }
        
        // Validate message
        if (notification.getMessage() == null || notification.getMessage().trim().isEmpty()) {
            throw new ValidationException("Notification message cannot be empty");
        }
        if (notification.getMessage().length() > 1000) {
            throw new ValidationException("Notification message cannot exceed 1000 characters");
        }
        
        // Validate delivery method
        if (notification.getDeliveryMethod() == null || 
            (!notification.getDeliveryMethod().equals("email") && 
             !notification.getDeliveryMethod().equals("in-app"))) {
            throw new ValidationException("Delivery method must be 'email' or 'in-app'");
        }
        
        // Validate status
        if (notification.getStatus() == null || 
            (!notification.getStatus().equals("sent") && 
             !notification.getStatus().equals("pending") && 
             !notification.getStatus().equals("failed"))) {
            throw new ValidationException("Status must be 'sent', 'pending', or 'failed'");
        }
        
        // Validate created date
        if (notification.getCreatedDate() == null) {
            throw new ValidationException("Notification must have a created date");
        }
    }
}