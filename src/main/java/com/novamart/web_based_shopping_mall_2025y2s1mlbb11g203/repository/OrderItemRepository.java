package com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.repository;

import com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repository for order item operations
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, String> {
    
    // Find all items for a specific order
    List<OrderItem> findByOrderId(String orderId);
}