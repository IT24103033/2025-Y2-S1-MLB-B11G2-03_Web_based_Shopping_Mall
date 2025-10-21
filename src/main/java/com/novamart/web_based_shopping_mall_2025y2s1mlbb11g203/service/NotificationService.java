package com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.service;

import com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.entity.Order;
import com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.entity.Notification;
import com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.repository.OrderRepository;
import com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class NotificationService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    @Autowired(required = false)
    private JavaMailSender mailSender;
    
    public void getNotification(String orderId) {
        // Find the order by ID
        Order order = orderRepository.findById(orderId).orElse(null);
        
        if (order == null) {
            System.out.println("Order not found: " + orderId);
            return;
        }
        
        // Create notification record in database
        Notification notification = new Notification();
        notification.setNotificationId(UUID.randomUUID().toString());
        notification.setUserId(order.getUserId());
        notification.setMessage(createMessage(order));
        notification.setDeliveryMethod("email"); // Default to email, can be changed based on user preference
        notification.setStatus("pending");
        notification.setCreatedDate(LocalDateTime.now());
        
        // Send notification based on delivery_method
        if ("email".equals(notification.getDeliveryMethod())) {
            sendEmailNotification(order);
            notification.setStatus("sent");
        } else {
            sendInAppNotification(order);
            notification.setStatus("sent");
        }
        
        // Save notification record
        notificationRepository.save(notification);
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
    
    private void sendInAppNotification(Order order) {
        String message = "Order " + order.getOrderId() + " is now " + order.getStatus().toUpperCase();
        System.out.println("In-App Notification for user " + order.getUserId() + ": " + message);
    }
    
    private String createMessage(Order order) {
        return "Dear " + order.getUser().getFirstName() + 
               ",\n\nYour order " + order.getOrderId() + 
               " is now " + order.getStatus().toUpperCase() + 
               ".\nOrder Total: $" + order.getTotalAmount() + 
               "\n\nThank you for shopping with NovaMart!";
    }
}