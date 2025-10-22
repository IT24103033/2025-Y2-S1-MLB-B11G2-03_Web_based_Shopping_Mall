package com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Report Entity - represents generated reports for sales and inventory analysis
 * Supports multiple report types with flexible filtering and data visualization
 */
@Entity
@Table(name = "report")
public class Report {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Integer reportId;

    @Column(name = "report_type", length = 50)
    private String reportType;  // "Sales", "Inventory", etc.

    @Column(name = "generated_date")
    private LocalDateTime generatedDate;

    @Column(name = "format", length = 20)
    private String format;  // "CSV", "PDF", "CSV/PDF"

    @Column(name = "report_data", columnDefinition = "TEXT")
    private String reportData;  // The actual report content

    @Column(name = "period_start")
    private LocalDate periodStart;

    @Column(name = "period_end")
    private LocalDate periodEnd;

    @Column(name = "user_id")
    private Long userId;  // Changed to Long to match our User entity migration

    @Column(name = "report_name", length = 100)
    private String reportName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String reportDescription;

    @Column(name = "product_category", length = 50)
    private String productCategory;  // Filter: categories to include

    @Column(name = "order_status", length = 50)
    private String orderStatus;  // Filter: order statuses to include

    @Column(name = "shop_id")
    private Integer shopId;  // For multi-shop support
    
    @Column(name = "created_date")
    private LocalDateTime createdDate;
    
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    // Constructors
    public Report() {
        this.createdDate = LocalDateTime.now();
        this.updatedDate = LocalDateTime.now();
    }

    // Getters and Setters
    public Integer getReportId() {
        return reportId;
    }

    public void setReportId(Integer reportId) {
        this.reportId = reportId;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public LocalDateTime getGeneratedDate() {
        return generatedDate;
    }

    public void setGeneratedDate(LocalDateTime generatedDate) {
        this.generatedDate = generatedDate;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getReportData() {
        return reportData;
    }

    public void setReportData(String reportData) {
        this.reportData = reportData;
    }

    public LocalDate getPeriodStart() {
        return periodStart;
    }

    public void setPeriodStart(LocalDate periodStart) {
        this.periodStart = periodStart;
    }

    public LocalDate getPeriodEnd() {
        return periodEnd;
    }

    public void setPeriodEnd(LocalDate periodEnd) {
        this.periodEnd = periodEnd;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public String getReportDescription() {
        return reportDescription;
    }

    public void setReportDescription(String reportDescription) {
        this.reportDescription = reportDescription;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Integer getShopId() {
        return shopId;
    }

    public void setShopId(Integer shopId) {
        this.shopId = shopId;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }
}