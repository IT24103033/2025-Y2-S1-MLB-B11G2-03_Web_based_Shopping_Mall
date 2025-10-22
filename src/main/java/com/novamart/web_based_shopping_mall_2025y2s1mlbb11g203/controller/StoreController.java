package com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.controller;

import com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.entity.Store;
import com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.service.StoreService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * Store Controller - REST API endpoints for store management
 * Handles HTTP requests for store CRUD operations
 * Note: In future, add authentication to restrict access to store owners only
 */
@RestController
@RequestMapping("/api/stores")
@CrossOrigin(origins = "*")
public class StoreController {
    
    @Autowired
    private StoreService storeService;
    
    /**
     * Get all stores
     * GET /api/stores
     */
    @GetMapping
    public ResponseEntity<List<Store>> getAllStores() {
        List<Store> stores = storeService.getAllStores();
        return ResponseEntity.ok(stores);
    }
    
    /**
     * Get store by ID
     * GET /api/stores/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Store> getStoreById(@PathVariable Long id) {
        try {
            Store store = storeService.getStoreById(id);
            return ResponseEntity.ok(store);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Create new store
     * POST /api/stores
     * TODO: Add authentication to ensure only store owners can create stores
     */
    @PostMapping
    public ResponseEntity<Store> createStore(@Valid @RequestBody Store store) {
        try {
            Store createdStore = storeService.createStore(store);
            return ResponseEntity
                .created(URI.create("/api/stores/" + createdStore.getStoreId()))
                .body(createdStore);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    /**
     * Update existing store
     * PUT /api/stores/{id}
     * TODO: Add authentication to ensure only store owner can update their store
     */
    @PutMapping("/{id}")
    public ResponseEntity<Store> updateStore(@PathVariable Long id, @Valid @RequestBody Store store) {
        try {
            Store updatedStore = storeService.updateStore(id, store);
            return ResponseEntity.ok(updatedStore);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    /**
     * Delete store
     * DELETE /api/stores/{id}
     * TODO: Add authentication to ensure only store owner or admin can delete store
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStore(@PathVariable Long id) {
        try {
            storeService.deleteStore(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Get stores by owner ID
     * GET /api/stores/owner/{ownerId}
     */
    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<Store>> getStoresByOwner(@PathVariable String ownerId) {
        List<Store> stores = storeService.getStoresByOwnerId(ownerId);
        return ResponseEntity.ok(stores);
    }
    
    /**
     * Get stores by category
     * GET /api/stores/category/{category}
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Store>> getStoresByCategory(@PathVariable String category) {
        List<Store> stores = storeService.getStoresByCategory(category);
        return ResponseEntity.ok(stores);
    }
}
