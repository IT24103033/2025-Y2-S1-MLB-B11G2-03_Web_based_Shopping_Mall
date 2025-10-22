package com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.repository;

import com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

// Repository for order item operations
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, String> {
    
    // Find all items for a specific order
    List<OrderItem> findByOrderId(String orderId);
    
    // Find top selling products within a date range
    @Query("SELECT p.name, SUM(oi.quantity), SUM(oi.subtotal) " +
           "FROM OrderItem oi " +
           "JOIN oi.product p " +
           "JOIN Order o ON oi.orderId = o.orderId " +
           "WHERE o.createdDate BETWEEN :startDate AND :endDate " +
           "AND o.status = 'completed' " +
           "GROUP BY p.productId, p.name " +
           "ORDER BY SUM(oi.quantity) DESC")
    List<Object[]> findTopSellingProducts(@Param("startDate") LocalDateTime startDate,
                                          @Param("endDate") LocalDateTime endDate);
}