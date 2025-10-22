package com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.repository;

import com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

// Repository for cart operations
@Repository
public interface CartItemRepository extends JpaRepository<CartItem, String> {
    
    // Find all cart items for a specific user
    List<CartItem> findByUserId(Long userId);
    
    // Find specific item in user's cart
    Optional<CartItem> findByUserIdAndProductId(Long userId, String productId);
    
    // Delete all items from user's cart (used after checkout)
    void deleteByUserId(Long userId);
}