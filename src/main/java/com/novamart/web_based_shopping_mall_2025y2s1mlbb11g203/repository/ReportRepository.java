
package com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.repository;

import com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Integer> {
    List<Report> findByReportType(String reportType);
    List<Report> findByGeneratedDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT r FROM Report r WHERE r.generatedDate < :cutoffDate")
    List<Report> findExpiredReports(@Param("cutoffDate") LocalDateTime cutoffDate);
}
