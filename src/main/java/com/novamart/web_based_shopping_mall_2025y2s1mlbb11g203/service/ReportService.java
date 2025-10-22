package com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.service;

import com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.entity.*;
import com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ReportService - Business logic for generating and managing reports
 * Supports Sales Reports and Inventory Reports with real database data
 */
@Service
public class ReportService {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    // ==================== CRUD Operations ====================
    
    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }

    public List<Report> getReportsByType(String reportType) {
        return reportRepository.findByReportType(reportType);
    }

    public List<Report> getReportsByDateRange(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
        return reportRepository.findByGeneratedDateBetween(startDateTime, endDateTime);
    }

    public List<Report> getExpiredReports(LocalDate cutoffDate) {
        LocalDateTime cutoff = cutoffDate.atStartOfDay();
        return reportRepository.findExpiredReports(cutoff);
    }

    public Report saveReport(Report report) {
        report.setUpdatedDate(LocalDateTime.now());
        return reportRepository.save(report);
    }

    public Report updateReport(Integer reportId, String reportName, String reportDescription) {
        Report report = reportRepository.findById(reportId).orElse(null);
        if (report != null) {
            if (reportName != null && !reportName.trim().isEmpty()) {
                report.setReportName(reportName);
            }
            if (reportDescription != null && !reportDescription.trim().isEmpty()) {
                report.setReportDescription(reportDescription);
            }
            report.setUpdatedDate(LocalDateTime.now());
            return reportRepository.save(report);
        }
        return null;
    }

    public void deleteReport(Integer reportId) {
        reportRepository.deleteById(reportId);
    }

    public void deleteReports(List<Integer> reportIds) {
        reportRepository.deleteAllById(reportIds);
    }

    public Report getReportById(Integer reportId) {
        return reportRepository.findById(reportId).orElse(null);
    }

    // ==================== Report Generation ====================

    /**
     * Generate Inventory Report
     * Shows current stock levels, out of stock items, low stock alerts
     */
    public Report generateInventoryReport(Long userId, Integer shopId, LocalDate reportDate) {
        Report report = new Report();
        report.setReportType("Inventory");
        report.setUserId(userId);
        report.setShopId(shopId);
        report.setGeneratedDate(LocalDateTime.now());
        report.setFormat("CSV/PDF");

        // FETCH REAL DATA FROM DATABASE
        List<Product> allProducts = productRepository.findAll();
        List<Product> outOfStock = productRepository.findByStockQuantity(0);
        List<Product> lowStock = allProducts.stream()
                .filter(p -> p.getStockQuantity() > 0 && p.getStockQuantity() < 10)
                .collect(Collectors.toList());

        // Calculate total stock value
        BigDecimal totalStockValue = allProducts.stream()
                .map(p -> p.getPrice().multiply(new BigDecimal(p.getStockQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Get top 5 products by stock value
        List<Product> topProducts = allProducts.stream()
                .sorted((p1, p2) -> {
                    BigDecimal val1 = p1.getPrice().multiply(new BigDecimal(p1.getStockQuantity()));
                    BigDecimal val2 = p2.getPrice().multiply(new BigDecimal(p2.getStockQuantity()));
                    return val2.compareTo(val1);
                })
                .limit(5)
                .collect(Collectors.toList());

        // Generate professional inventory report data
        StringBuilder reportData = new StringBuilder();
        reportData.append("═══════════════════════════════════════════════════════════════\n");
        reportData.append("                    INVENTORY REPORT\n");
        reportData.append("═══════════════════════════════════════════════════════════════\n\n");
        reportData.append("Report Period: ").append(reportDate).append("\n");
        reportData.append("Generated: ").append(LocalDateTime.now()).append("\n");
        reportData.append("Generated By: User ID ").append(userId).append("\n\n");
        reportData.append("───────────────────────────────────────────────────────────────\n");
        reportData.append("INVENTORY SUMMARY\n");
        reportData.append("───────────────────────────────────────────────────────────────\n\n");
        reportData.append("Total Products in Stock:        ").append(allProducts.size()).append(" items\n");
        reportData.append("Total Stock Value:              LKR ").append(String.format("%,.2f", totalStockValue)).append("\n");
        reportData.append("Low Stock Items:                ").append(lowStock.size()).append(" items\n");
        reportData.append("Out of Stock Items:             ").append(outOfStock.size()).append(" items\n\n");
        reportData.append("───────────────────────────────────────────────────────────────\n");
        reportData.append("TOP 5 PRODUCTS BY STOCK VALUE\n");
        reportData.append("───────────────────────────────────────────────────────────────\n\n");

        int rank = 1;
        for (Product p : topProducts) {
            BigDecimal stockValue = p.getPrice().multiply(new BigDecimal(p.getStockQuantity()));
            reportData.append(String.format("%d. %-25s - Qty: %-4d - Value: LKR %,.2f\n",
                    rank++, p.getName(), p.getStockQuantity(), stockValue));
        }

        reportData.append("\n───────────────────────────────────────────────────────────────\n");
        reportData.append("ALERTS & RECOMMENDATIONS\n");
        reportData.append("───────────────────────────────────────────────────────────────\n\n");

        if (outOfStock.size() > 0) {
            reportData.append("⚠ CRITICAL: ").append(outOfStock.size()).append(" products are out of stock\n");
        }
        if (lowStock.size() > 0) {
            reportData.append("⚠ WARNING: ").append(lowStock.size()).append(" products have low stock levels\n");
        }
        if (outOfStock.isEmpty() && lowStock.isEmpty()) {
            reportData.append("✓ All products are adequately stocked\n");
        }

        reportData.append("\n═══════════════════════════════════════════════════════════════\n");
        reportData.append("                    END OF REPORT\n");
        reportData.append("═══════════════════════════════════════════════════════════════\n");

        report.setReportData(reportData.toString());
        report.setPeriodStart(reportDate);
        report.setPeriodEnd(reportDate);
        return report;
    }

    /**
     * Generate Sales Report with filters
     * Shows sales data with category and status filtering
     */
    public Report generateSalesReport(Long userId, Integer shopId, LocalDate startDate, LocalDate endDate,
                                      String reportName, String reportDescription,
                                      String productCategory, String orderStatus) {
        Report report = new Report();
        report.setReportType("Sales");
        report.setUserId(userId);
        report.setShopId(shopId);
        report.setGeneratedDate(LocalDateTime.now());
        report.setFormat("CSV/PDF");
        report.setReportName(reportName);
        report.setReportDescription(reportDescription);
        report.setProductCategory(productCategory);
        report.setOrderStatus(orderStatus);

        // FETCH REAL DATA FROM DATABASE
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        // Get filtered orders
        List<Order> allOrders = getFilteredOrders(startDateTime, endDateTime, productCategory, orderStatus);

        long completedOrders = allOrders.stream().filter(o -> "completed".equalsIgnoreCase(o.getStatus())).count();
        long cancelledOrders = allOrders.stream().filter(o -> "cancelled".equalsIgnoreCase(o.getStatus())).count();

        BigDecimal totalRevenue = allOrders.stream()
                .filter(o -> "completed".equalsIgnoreCase(o.getStatus()))
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal avgOrderValue = allOrders.size() > 0 ?
                totalRevenue.divide(new BigDecimal(allOrders.size()), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;

        // Get top selling products
        List<Object[]> topProducts = orderItemRepository.findTopSellingProducts(startDateTime, endDateTime);

        // Generate professional sales report data
        StringBuilder reportData = new StringBuilder();
        reportData.append("═══════════════════════════════════════════════════════════════\n");
        reportData.append("                      SALES REPORT\n");
        reportData.append("═══════════════════════════════════════════════════════════════\n\n");

        if (reportName != null && !reportName.trim().isEmpty()) {
            reportData.append("Report Name: ").append(reportName).append("\n");
        }
        if (reportDescription != null && !reportDescription.trim().isEmpty()) {
            reportData.append("Description: ").append(reportDescription).append("\n");
        }

        reportData.append("Report Period: ").append(startDate).append(" to ").append(endDate).append("\n");
        reportData.append("Generated: ").append(LocalDateTime.now()).append("\n");
        reportData.append("Generated By: User ID ").append(userId).append("\n");

        if (productCategory != null && !productCategory.trim().isEmpty()) {
            reportData.append("Product Categories: ").append(productCategory).append("\n");
        }
        if (orderStatus != null && !orderStatus.trim().isEmpty()) {
            reportData.append("Order Statuses: ").append(orderStatus).append("\n");
        }

        reportData.append("\n");
        reportData.append("───────────────────────────────────────────────────────────────\n");
        reportData.append("SALES SUMMARY\n");
        reportData.append("───────────────────────────────────────────────────────────────\n\n");
        reportData.append("Total Orders:                   ").append(allOrders.size()).append(" orders\n");
        reportData.append("Total Revenue:                  LKR ").append(String.format("%,.2f", totalRevenue)).append("\n");
        reportData.append("Average Order Value:            LKR ").append(String.format("%,.2f", avgOrderValue)).append("\n");
        reportData.append("Completed Orders:               ").append(completedOrders).append("\n");
        reportData.append("Cancelled Orders:               ").append(cancelledOrders).append("\n\n");

        reportData.append("───────────────────────────────────────────────────────────────\n");
        reportData.append("TOP 5 BEST SELLING PRODUCTS\n");
        reportData.append("───────────────────────────────────────────────────────────────\n\n");

        int rank = 1;
        for (Object[] row : topProducts) {
            if (rank > 5) break;
            String productName = (String) row[0];
            Long quantity = ((Number) row[1]).longValue();
            BigDecimal revenue = (BigDecimal) row[2];
            reportData.append(String.format("%d. %-25s - Units: %-4d - Revenue: LKR %,.2f\n",
                    rank++, productName, quantity, revenue));
        }

        if (topProducts.isEmpty()) {
            reportData.append("No sales data available for this period.\n");
        }

        reportData.append("\n───────────────────────────────────────────────────────────────\n");
        reportData.append("PERFORMANCE INSIGHTS\n");
        reportData.append("───────────────────────────────────────────────────────────────\n\n");

        if (allOrders.size() > 0) {
            reportData.append("✓ Total orders processed: ").append(allOrders.size()).append("\n");
            reportData.append("✓ Revenue generated: LKR ").append(String.format("%,.2f", totalRevenue)).append("\n");
        } else {
            reportData.append("ℹ No orders found for the selected period\n");
        }

        reportData.append("\n═══════════════════════════════════════════════════════════════\n");
        reportData.append("                    END OF REPORT\n");
        reportData.append("═══════════════════════════════════════════════════════════════\n");

        report.setReportData(reportData.toString());
        report.setPeriodStart(startDate);
        report.setPeriodEnd(endDate);
        return report;
    }

    // Helper method to get filtered orders based on category and status
    private List<Order> getFilteredOrders(LocalDateTime startDateTime, LocalDateTime endDateTime,
                                          String productCategory, String orderStatus) {
        List<Order> allOrders;

        // Filter by status
        if (orderStatus != null && !orderStatus.trim().isEmpty()) {
            String[] statusArray = orderStatus.split(",");
            allOrders = orderRepository.findByOrderDateBetween(startDateTime, endDateTime).stream()
                    .filter(o -> {
                        for (String status : statusArray) {
                            if (o.getStatus().equalsIgnoreCase(status.trim())) {
                                return true;
                            }
                        }
                        return false;
                    })
                    .collect(Collectors.toList());
        } else {
            allOrders = orderRepository.findByOrderDateBetween(startDateTime, endDateTime);
        }

        // Filter by category (if needed)
        if (productCategory != null && !productCategory.trim().isEmpty()) {
            List<String> categoryFilter = Arrays.stream(productCategory.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());

            allOrders = allOrders.stream()
                    .filter(order -> order.getOrderItems().stream()
                            .anyMatch(item -> {
                                Product product = item.getProduct();
                                if (product != null) {
                                    String categoryName = getCategoryNameById(product.getCategoryId());
                                    return categoryFilter.stream()
                                            .anyMatch(cat -> cat.equalsIgnoreCase(categoryName));
                                }
                                return false;
                            }))
                    .collect(Collectors.toList());
        }

        return allOrders;
    }

    // Helper method to map category ID to category name
    private String getCategoryNameById(Integer categoryId) {
        if (categoryId == null) return "Unknown";

        return switch (categoryId) {
            case 101 -> "Electronics";
            case 102 -> "Clothing";
            case 103 -> "Home Goods";
            case 104 -> "Books";
            case 105 -> "Groceries";
            default -> "Other";
        };
    }

    // Helper method to get sales analytics data for visualization (charts)
    public Map<String, Object> getSalesAnalytics(Integer reportId) {
        Report report = reportRepository.findById(reportId).orElse(null);
        if (report == null || !"Sales".equals(report.getReportType())) {
            return null;
        }

        LocalDateTime startDateTime = report.getPeriodStart().atStartOfDay();
        LocalDateTime endDateTime = report.getPeriodEnd().atTime(23, 59, 59);

        String productCategory = report.getProductCategory();
        String orderStatus = report.getOrderStatus();

        List<Order> orders = getFilteredOrders(startDateTime, endDateTime, productCategory, orderStatus);

        Map<String, Object> analytics = new HashMap<>();

        // 1. Sales by Status (Pie Chart)
        Map<String, Long> salesByStatus = new HashMap<>();
        salesByStatus.put("COMPLETED", orders.stream().filter(o -> "completed".equalsIgnoreCase(o.getStatus())).count());
        salesByStatus.put("PENDING", orders.stream().filter(o -> "pending".equalsIgnoreCase(o.getStatus())).count());
        salesByStatus.put("CANCELLED", orders.stream().filter(o -> "cancelled".equalsIgnoreCase(o.getStatus())).count());
        analytics.put("salesByStatus", salesByStatus);

        // 2. Revenue by Status (Pie Chart)
        Map<String, BigDecimal> revenueByStatus = new HashMap<>();
        revenueByStatus.put("COMPLETED", orders.stream().filter(o -> "completed".equalsIgnoreCase(o.getStatus()))
                .map(Order::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
        revenueByStatus.put("PENDING", orders.stream().filter(o -> "pending".equalsIgnoreCase(o.getStatus()))
                .map(Order::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
        revenueByStatus.put("CANCELLED", orders.stream().filter(o -> "cancelled".equalsIgnoreCase(o.getStatus()))
                .map(Order::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
        analytics.put("revenueByStatus", revenueByStatus);

        // 3. Top Products (Bar Chart)
        List<Object[]> topProducts = orderItemRepository.findTopSellingProducts(startDateTime, endDateTime);
        Map<String, Object> topProductsData = new HashMap<>();
        topProductsData.put("labels", topProducts.stream().limit(10).map(row -> (String) row[0]).collect(Collectors.toList()));
        topProductsData.put("quantities", topProducts.stream().limit(10).map(row -> ((Number) row[1]).longValue()).collect(Collectors.toList()));
        topProductsData.put("revenues", topProducts.stream().limit(10).map(row -> (BigDecimal) row[2]).collect(Collectors.toList()));
        analytics.put("topProducts", topProductsData);

        // 4. Summary Statistics
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalOrders", orders.size());
        summary.put("totalRevenue", orders.stream().filter(o -> "completed".equalsIgnoreCase(o.getStatus()))
                .map(Order::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
        summary.put("averageOrderValue", orders.size() > 0 ?
                ((BigDecimal) summary.get("totalRevenue")).divide(new BigDecimal(orders.size()), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
        summary.put("completedOrders", orders.stream().filter(o -> "completed".equalsIgnoreCase(o.getStatus())).count());
        summary.put("pendingOrders", orders.stream().filter(o -> "pending".equalsIgnoreCase(o.getStatus())).count());
        summary.put("cancelledOrders", orders.stream().filter(o -> "cancelled".equalsIgnoreCase(o.getStatus())).count());
        analytics.put("summary", summary);

        return analytics;
    }
}
