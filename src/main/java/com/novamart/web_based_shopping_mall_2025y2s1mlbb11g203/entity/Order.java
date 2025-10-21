package com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @Column(name = "order_id")
    private String orderId;
    
    @Column(name = "user_id")
    private String userId;
    
    @Column(name = "total_amount")
    private BigDecimal totalAmount;
    
    @Column(name = "status")
    private String status;
    
    @Column(name = "is_email")
    private Boolean isEmail;
    
    // Relationship to User
    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;
    
    // getters and setters
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public Boolean getIsEmail() { return isEmail; }
    public void setIsEmail(Boolean isEmail) { this.isEmail = isEmail; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}