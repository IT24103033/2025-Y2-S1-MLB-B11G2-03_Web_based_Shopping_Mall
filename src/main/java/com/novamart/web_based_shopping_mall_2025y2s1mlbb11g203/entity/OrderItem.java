package com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * OrderItem entity - represents items in a completed order
 * This is different from CartItem (which is for shopping cart)
 */
@Entity
@Table(name = "order_items")
public class OrderItem {
    
    @Id
    @Column(name = "order_item_id")
    private String orderItemId;
    
    @Column(name = "order_id")
    private String orderId;
    
    @Column(name = "product_id")
    private String productId;
    
    @Column(name = "quantity")
    private int quantity;
    
    @Column(name = "unit_price")
    private BigDecimal unitPrice;  // Price when order was placed
    
    @Column(name = "subtotal")
    private BigDecimal subtotal;   // quantity * unit_price
    
    @Column(name = "created_date")
    private LocalDateTime createdDate;
    
    // Relationship to get product details
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private Product product;
    
    // Relationship to get order details
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", insertable = false, updatable = false)
    private Order order;
    
    // Simple getters and setters
    public String getOrderItemId() { return orderItemId; }
    public void setOrderItemId(String orderItemId) { this.orderItemId = orderItemId; }
    
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    
    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }
    
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    
    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
}