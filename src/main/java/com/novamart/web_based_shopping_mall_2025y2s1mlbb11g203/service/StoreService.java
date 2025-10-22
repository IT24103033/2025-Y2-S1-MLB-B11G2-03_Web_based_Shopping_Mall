package com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.service;

import com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.entity.Store;
import com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.repository.StoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Store Service - Business logic for store management
 * Handles CRUD operations and validation for stores
 */
@Service
public class StoreService {
    
    @Autowired
    private StoreRepository storeRepository;
    
    // Get all stores in the shopping mall
    public List<Store> getAllStores() {
        return storeRepository.findAll();
    }
    
    // Get store by ID
    public Store getStoreById(Long storeId) {
        return storeRepository.findById(storeId)
            .orElseThrow(() -> new IllegalArgumentException("Store not found with ID: " + storeId));
    }
    
    /**
     * Create new store
     * Validates that store name is unique
     */
    public Store createStore(Store store) {
        // Check if store name already exists
        if (storeRepository.existsByStoreName(store.getStoreName())) {
            throw new IllegalArgumentException("Store name already exists: " + store.getStoreName());
        }
        
        // Set timestamps
        store.setCreatedDate(LocalDateTime.now());
        store.setUpdatedDate(LocalDateTime.now());
        
        return storeRepository.save(store);
    }
    
    /**
     * Update existing store
     * Validates unique store name if changed
     */
    public Store updateStore(Long storeId, Store updatedStore) {
        Store existingStore = storeRepository.findById(storeId)
            .orElseThrow(() -> new IllegalArgumentException("Store not found with ID: " + storeId));
        
        // Check if store name is being changed
        String newName = updatedStore.getStoreName();
        if (newName != null && !newName.equals(existingStore.getStoreName())) {
            // Check if new name already exists
            if (storeRepository.existsByStoreName(newName)) {
                throw new IllegalArgumentException("Store name already exists: " + newName);
            }
            existingStore.setStoreName(newName);
        }
        
        // Update other fields
        existingStore.setCategory(updatedStore.getCategory());
        existingStore.setDescription(updatedStore.getDescription());
        existingStore.setUpdatedDate(LocalDateTime.now());
        
        return storeRepository.save(existingStore);
    }

    // Delete store by ID
    public void deleteStore(Long storeId) {
        if (!storeRepository.existsById(storeId)) {
            throw new IllegalArgumentException("Store not found with ID: " + storeId);
        }
        storeRepository.deleteById(storeId);
    }

    // Get all stores owned by a specific user
    public List<Store> getStoresByOwnerId(String ownerId) {
        return storeRepository.findByOwnerId(ownerId);
    }
    
    // Get stores by category
    public List<Store> getStoresByCategory(String category) {
        return storeRepository.findByCategory(category);
    }
}
