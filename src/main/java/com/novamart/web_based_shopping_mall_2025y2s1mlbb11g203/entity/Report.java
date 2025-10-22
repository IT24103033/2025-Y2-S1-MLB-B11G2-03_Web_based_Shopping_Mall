package com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "Report")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ReportID")
    @EqualsAndHashCode.Include
    private Integer reportId;

    @Column(name = "ReportType", length = 50)
    private String reportType;

    @Column(name = "GeneratedDate")
    private LocalDateTime generatedDate;

    @Column(name = "Format")
    private String format;

    @Column(name = "ReportData", columnDefinition = "TEXT")
    private String reportData;

    @Column(name = "PeriodStart")
    private LocalDate periodStart;

    @Column(name = "PeriodEnd")
    private LocalDate periodEnd;

    @Column(name = "UserID")
    private Integer userId;

    @Column(name = "ReportName", length = 100)
    private String reportName;

    @Column(name = "Description", columnDefinition = "TEXT")
    private String reportDescription;

    @Column(name = "ProductCategory", length = 50)
    private String productCategory;

    @Column(name = "OrderStatus", length = 50)
    private String orderStatus;

    @Column(name = "Shop_ID")
    private Integer shopId;
}
