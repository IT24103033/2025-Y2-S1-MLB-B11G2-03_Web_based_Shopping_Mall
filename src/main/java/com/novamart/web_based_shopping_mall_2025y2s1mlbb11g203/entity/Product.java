package com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.time.LocalDateTime;

// Product entity - represents products available in the store
@Entity
@Table(name = "products")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Product {
    
    @Id
    @Column(name = "product_id")
    private String productId;
    
    @Column(name = "name")
    private String name;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "price")
    private BigDecimal price;
    
    @Column(name = "stock_quantity")
    private int stockQuantity;
    
    @Column(name = "category_id")
    private Integer categoryId;
    
    @Column(name = "store_id")
    private Long storeId;
    
    @Column(name = "created_date")
    private LocalDateTime createdDate;
    
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;
    
    // Relationship to Store (many products belong to one store)
    @ManyToOne
    @JoinColumn(name = "store_id", insertable = false, updatable = false)
    private Store store;
    
    // getters and setters
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    
    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }
    
    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }
    
    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }
    
    public LocalDateTime getUpdatedDate() { return updatedDate; }
    public void setUpdatedDate(LocalDateTime updatedDate) { this.updatedDate = updatedDate; }
    
    public Long getStoreId() { return storeId; }
    public void setStoreId(Long storeId) { this.storeId = storeId; }
    
    public Store getStore() { return store; }
    public void setStore(Store store) { this.store = store; }
}