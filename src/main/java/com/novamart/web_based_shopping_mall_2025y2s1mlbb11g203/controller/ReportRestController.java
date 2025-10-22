package com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.controller;

import com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.entity.Report;
import com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.entity.User;
import com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.repository.UserRepository;
import com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ReportRestController - REST API endpoints for report generation and management
 * Provides AJAX endpoints for dynamic report operations
 */
@RestController
@RequestMapping("/api/reports")
public class ReportRestController {

    @Autowired
    private ReportService reportService;
    
    @Autowired
    private UserRepository userRepository;

    // Get current authenticated user ID
    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
            String username = auth.getName();
            return userRepository.findByUsername(username)
                    .map(User::getUserId)
                    .orElse(null);
        }
        return null;
    }

    // Generate Inventory Report via AJAX
    @PostMapping("/generate/inventory")
    public ResponseEntity<?> generateInventoryReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate reportDate,
            @RequestParam(required = false) Integer shopId) {
        try {
            Long userId = getCurrentUserId();
            if (userId == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Authentication required");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            }
            
            // Validate report date
            if (reportDate == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Report date is required to check stock levels");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            // Validate date is not in the future
            if (reportDate.isAfter(LocalDate.now())) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Invalid parameters: Report date cannot be in the future");
                errorResponse.put("invalidParams", true);
                return ResponseEntity.badRequest().body(errorResponse);
            }

            Report report = reportService.generateInventoryReport(userId, shopId, reportDate);

            String shopInfo = shopId != null ? " - Shop " + shopId : "";
            report.setReportName("Inventory Report - Stock as of " + reportDate + shopInfo);
            report.setReportDescription("Comprehensive inventory report showing available stock levels as of " + reportDate + shopInfo);

            Report savedReport = reportService.saveReport(report);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Inventory report generated successfully for " + reportDate + shopInfo);
            response.put("report", savedReport);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error generating inventory report: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Generate Sales Report via AJAX
    @PostMapping("/generate/sales")
    public ResponseEntity<?> generateSalesReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam String reportName,
            @RequestParam(required = false) String reportDescription,
            @RequestParam(required = false) String productCategory,
            @RequestParam(required = false) String orderStatus,
            @RequestParam(required = false) Integer shopId) {

        try {
            Long userId = getCurrentUserId();
            if (userId == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Authentication required");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            }
            
            // Validate parameters - dates must be provided
            if (startDate == null || endDate == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Invalid parameters: Both start date and end date are required");
                errorResponse.put("invalidParams", true);
                return ResponseEntity.badRequest().body(errorResponse);
            }

            // Validate report name
            if (reportName == null || reportName.trim().isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Invalid parameters: Report name is required");
                errorResponse.put("invalidParams", true);
                return ResponseEntity.badRequest().body(errorResponse);
            }

            // Validate date range - start must be before end
            if (startDate.isAfter(endDate)) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Invalid parameters: Start date must be before or equal to end date");
                errorResponse.put("invalidParams", true);
                return ResponseEntity.badRequest().body(errorResponse);
            }

            // Validate dates are not in the future
            if (startDate.isAfter(LocalDate.now()) || endDate.isAfter(LocalDate.now())) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Invalid parameters: Dates cannot be in the future");
                errorResponse.put("invalidParams", true);
                return ResponseEntity.badRequest().body(errorResponse);
            }

            String finalReportName = reportName;
            String finalReportDescription = reportDescription;
            
            if (shopId != null) {
                finalReportName = reportName + " - Shop " + shopId;
                if (reportDescription != null && !reportDescription.trim().isEmpty()) {
                    finalReportDescription = reportDescription + " (Shop " + shopId + ")";
                } else {
                    finalReportDescription = "Sales report for Shop " + shopId;
                }
            }

            Report report = reportService.generateSalesReport(userId, shopId, startDate, endDate, finalReportName,
                    finalReportDescription, productCategory, orderStatus);
            Report savedReport = reportService.saveReport(report);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Sales report generated successfully" + (shopId != null ? " for Shop " + shopId : "") + "!");
            response.put("report", savedReport);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "System error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Get report by ID via AJAX
    @GetMapping("/{reportId}")
    public ResponseEntity<?> getReport(@PathVariable Integer reportId) {
        try {
            Report report = reportService.getReportById(reportId);
            if (report == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Report not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("report", report);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error retrieving report: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Export report to CSV
    @GetMapping("/export/{reportId}/csv")
    public ResponseEntity<byte[]> exportReportToCsv(@PathVariable Integer reportId) {
        try {
            Report report = reportService.getReportById(reportId);
            if (report == null) {
                return ResponseEntity.notFound().build();
            }

            String csvContent = generateCsvContent(report);
            byte[] csvBytes = csvContent.getBytes("UTF-8");

            String filename = String.format("%s_Report_%s.csv",
                    report.getReportType(),
                    LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("text/csv"));
            headers.setContentDispositionFormData("attachment", filename);
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            return new ResponseEntity<>(csvBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get all reports for current user
    @GetMapping("/user")
    public ResponseEntity<?> getUserReports() {
        try {
            List<Report> reports = reportService.getAllReports();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("reports", reports);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error retrieving reports: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Update report via AJAX
    @PutMapping("/{reportId}")
    public ResponseEntity<?> updateReport(
            @PathVariable Integer reportId,
            @RequestParam(required = false) String reportName,
            @RequestParam(required = false) String reportDescription) {
        try {
            // Validate that at least one field is provided
            if ((reportName == null || reportName.trim().isEmpty()) &&
                    (reportDescription == null || reportDescription.trim().isEmpty())) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "At least one field (name or description) must be provided");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            Report updatedReport = reportService.updateReport(reportId, reportName, reportDescription);

            if (updatedReport == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Report not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Report updated successfully");
            response.put("report", updatedReport);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error updating report: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Delete report via AJAX
    @DeleteMapping("/{reportId}")
    public ResponseEntity<?> deleteReport(@PathVariable Integer reportId) {
        try {
            reportService.deleteReport(reportId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Report deleted successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error deleting report: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Helper method to generate CSV content from report
    private String generateCsvContent(Report report) {
        StringBuilder csv = new StringBuilder();

        // Header
        csv.append("Report Type,Generated Date,Period Start,Period End,Format\n");
        csv.append(String.format("%s,%s,%s,%s,%s\n",
                escapeCsv(report.getReportType()),
                report.getGeneratedDate(),
                report.getPeriodStart(),
                report.getPeriodEnd(),
                escapeCsv(report.getFormat())));

        csv.append("\nReport Data:\n");
        csv.append(escapeCsv(report.getReportData()));

        return csv.toString();
    }

    // Escape CSV special characters
    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    // Get analytics data for a sales report
    @GetMapping("/{reportId}/analytics")
    public ResponseEntity<?> getReportAnalytics(@PathVariable Integer reportId) {
        try {
            Map<String, Object> analytics = reportService.getSalesAnalytics(reportId);

            if (analytics == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Report not found or not a sales report");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("analytics", analytics);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error retrieving analytics: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
