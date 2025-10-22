package com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * Notification entity with validation annotations
 * Represents a notification sent to a user about order updates
 */
@Entity
@Table(name = "notifications")
public class Notification {
    
    @Id
    @Column(name = "notification_id")
    @NotBlank(message = "Notification ID cannot be blank")
    @Size(max = 100, message = "Notification ID cannot exceed 100 characters")
    private String notificationId;
    
    @Column(name = "user_id")
    @NotBlank(message = "User ID is required")
    @Size(max = 50, message = "User ID cannot exceed 50 characters")
    private String userId;
    
    @Column(name = "message")
    @NotBlank(message = "Message cannot be empty")
    @Size(max = 1000, message = "Message cannot exceed 1000 characters")
    private String message;
    
    @Column(name = "status")
    @NotBlank(message = "Status is required")
    @Pattern(regexp = "^(sent|pending|failed)$", 
             message = "Status must be one of: sent, pending, failed")
    private String status;
    
    @Column(name = "delivery_method")
    @NotBlank(message = "Delivery method is required")
    @Pattern(regexp = "^(email|in-app)$", 
             message = "Delivery method must be either 'email' or 'in-app'")
    private String deliveryMethod;
    
    @Column(name = "created_date")
    @NotNull(message = "Created date is required")
    private LocalDateTime createdDate;
    
    @Column(name = "read_date")
    // read_date can be null (not read yet), so no validation needed
    private LocalDateTime readDate;
    
    // getters and setters
    public String getNotificationId() { return notificationId; }
    public void setNotificationId(String notificationId) { this.notificationId = notificationId; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getDeliveryMethod() { return deliveryMethod; }
    public void setDeliveryMethod(String deliveryMethod) { this.deliveryMethod = deliveryMethod; }
    
    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }
    
    public LocalDateTime getReadDate() { return readDate; }
    public void setReadDate(LocalDateTime readDate) { this.readDate = readDate; }
}