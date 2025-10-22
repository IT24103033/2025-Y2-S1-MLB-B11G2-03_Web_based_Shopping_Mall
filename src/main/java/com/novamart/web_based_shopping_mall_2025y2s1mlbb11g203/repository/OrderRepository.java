package com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.repository;

import com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
    
    // Find orders by user ID
    List<Order> findByUserId(String userId);

    // Find orders created between two dates
    @Query("SELECT o FROM Order o WHERE o.createdDate BETWEEN :startDate AND :endDate ORDER BY o.createdDate DESC")
    List<Order> findByOrderDateBetween(@Param("startDate") LocalDateTime startDate, 
                                       @Param("endDate") LocalDateTime endDate);
    
    // Find orders by status
    List<Order> findByStatus(String status);
}