package com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.repository;

import com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// Repository for product operations
@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
    
    // Find products by stock quantity
    List<Product> findByStockQuantity(Integer stockQuantity);
    
    // Find products by category ID
    List<Product> findByCategoryId(Integer categoryId);
}