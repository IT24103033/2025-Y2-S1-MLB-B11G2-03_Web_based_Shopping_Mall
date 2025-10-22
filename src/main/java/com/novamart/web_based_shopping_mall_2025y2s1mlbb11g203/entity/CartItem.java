package com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;

/**
 * CartItem entity - represents items in a user's shopping cart
 * This is separate from OrderItem (which is for completed orders)
 */
@Entity
@Table(name = "cart_items")
public class CartItem {
    
    @Id
    @Column(name = "cart_item_id")
    private String cartItemId;
    
    @Column(name = "user_id")
    private Long userId;  // Changed from String to Long after database migration
    
    @Column(name = "product_id")
    private String productId;
    
    @Column(name = "quantity")
    private int quantity;
    
    @Column(name = "created_date")
    private LocalDateTime createdDate;
    
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;
    
    // Relationship to get product details (EAGER for cart display)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Product product;
    
    // getters and setters
    public String getCartItemId() { return cartItemId; }
    public void setCartItemId(String cartItemId) { this.cartItemId = cartItemId; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    
    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }
    
    public LocalDateTime getUpdatedDate() { return updatedDate; }
    public void setUpdatedDate(LocalDateTime updatedDate) { this.updatedDate = updatedDate; }
    
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
}