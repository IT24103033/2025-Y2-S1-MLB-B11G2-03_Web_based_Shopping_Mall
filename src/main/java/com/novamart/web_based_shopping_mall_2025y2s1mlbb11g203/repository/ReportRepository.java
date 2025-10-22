package com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.repository;

import com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

// ReportRepository - Data access layer for Report entity
@Repository
public interface ReportRepository extends JpaRepository<Report, Integer> {
    
    // Find all reports by type (Sales, Inventory, etc.)
    List<Report> findByReportType(String reportType);

    // Find reports generated within a date range
    List<Report> findByGeneratedDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // Find reports by user ID
    List<Report> findByUserId(Long userId);

    // Find reports by shop ID
    List<Report> findByShopId(Integer shopId);
    
    /**
     * Find expired reports (older than cutoff date)
     * Useful for automatic cleanup/archiving
     */
    @Query("SELECT r FROM Report r WHERE r.generatedDate < :cutoffDate ORDER BY r.generatedDate ASC")
    List<Report> findExpiredReports(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    // Find recent reports (limit)
    @Query("SELECT r FROM Report r ORDER BY r.generatedDate DESC")
    List<Report> findRecentReports();

    // Find reports by type and user
    List<Report> findByReportTypeAndUserId(String reportType, Long userId);
    
    // Find reports by period range
    @Query("SELECT r FROM Report r WHERE r.periodStart >= :startDate AND r.periodEnd <= :endDate ORDER BY r.generatedDate DESC")
    List<Report> findByPeriodRange(@Param("startDate") java.time.LocalDate startDate, 
                                    @Param("endDate") java.time.LocalDate endDate);
}
