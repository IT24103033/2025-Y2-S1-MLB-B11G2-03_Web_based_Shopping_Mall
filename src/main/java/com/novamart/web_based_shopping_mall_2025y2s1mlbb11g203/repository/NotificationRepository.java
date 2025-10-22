package com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.repository;

import com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.entity.Notification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, String> {
    
    /**
     * Find notifications by user ID ordered by creation date descending
     * @param userId The user ID to search for
     * @param pageable Pagination information
     * @return List of notifications
     */
    List<Notification> findByUserIdOrderByCreatedDateDesc(String userId, Pageable pageable);
}