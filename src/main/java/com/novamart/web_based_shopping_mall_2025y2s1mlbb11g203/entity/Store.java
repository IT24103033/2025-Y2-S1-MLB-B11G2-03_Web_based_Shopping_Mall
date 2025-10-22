package com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Store Entity - Represents stores/vendors in the shopping mall
 * Each store can have multiple products
 */
@Entity
@Table(name = "stores")
public class Store {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_id")
    private Long storeId;
    
    @NotBlank(message = "Store name is required")
    @Size(min = 3, max = 100, message = "Store name must be between 3 and 100 characters")
    @Column(name = "store_name", nullable = false, unique = true, length = 100)
    private String storeName;
    
    @Size(max = 50, message = "Category must be at most 50 characters")
    @Column(name = "category", length = 50)
    private String category;
    
    @Size(max = 255, message = "Description must be at most 255 characters")
    @Column(name = "description", length = 255)
    private String description;
    
    @Column(name = "owner_id")
    private String ownerId;
    
    @Column(name = "created_date")
    private LocalDateTime createdDate;
    
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;
    
    // Relationship to User (store owner)
    @ManyToOne
    @JoinColumn(name = "owner_id", insertable = false, updatable = false)
    private User owner;
    
    // Relationship to Products (one store has many products)
    @OneToMany(mappedBy = "store")
    private List<Product> products;
    
    // Getters and setters
    public Long getStoreId() {
        return storeId;
    }
    
    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }
    
    public String getStoreName() {
        return storeName;
    }
    
    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getOwnerId() {
        return ownerId;
    }
    
    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }
    
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }
    
    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
    
    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }
    
    public void setUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }
    
    public User getOwner() {
        return owner;
    }
    
    public void setOwner(User owner) {
        this.owner = owner;
    }
    
    public List<Product> getProducts() {
        return products;
    }
    
    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
