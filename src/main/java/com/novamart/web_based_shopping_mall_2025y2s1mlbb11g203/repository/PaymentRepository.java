package com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.repository;

import com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Payment Repository - Database operations for Payment entity
 * Provides methods to retrieve payment records by order ID
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {
    
    // Find payment by order ID
    Optional<Payment> findByOrderId(String orderId);
    
    // Find all payments for a specific order
    List<Payment> findAllByOrderId(String orderId);
}
