package com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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
    
    @Column(name = "created_date")
    private LocalDateTime createdDate;
    
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;
    
    // Relationship to User
    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;
    
    // Relationship to OrderItems
    @OneToMany(mappedBy = "orderId", fetch = FetchType.LAZY)
    private List<OrderItem> orderItems;
    
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
    
    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }
    
    public LocalDateTime getUpdatedDate() { return updatedDate; }
    public void setUpdatedDate(LocalDateTime updatedDate) { this.updatedDate = updatedDate; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public List<OrderItem> getOrderItems() { return orderItems; }
    public void setOrderItems(List<OrderItem> orderItems) { this.orderItems = orderItems; }
}