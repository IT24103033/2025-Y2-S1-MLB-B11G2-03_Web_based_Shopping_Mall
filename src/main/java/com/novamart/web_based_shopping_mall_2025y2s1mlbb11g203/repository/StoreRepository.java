package com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.repository;

import com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Store Repository - Database operations for Store entity
 * Provides methods to manage stores in the shopping mall
 */
@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
    
    // Check if store name already exists
    boolean existsByStoreName(String storeName);
    
    // Find store by name
    Optional<Store> findByStoreName(String storeName);
    
    // Find all stores by owner ID
    List<Store> findByOwnerId(String ownerId);
    
    // Find stores by category
    List<Store> findByCategory(String category);
}
