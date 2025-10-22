package com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.controller;

import com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.entity.Report;
import com.novamart.web_based_shopping_mall_2025y2s1mlbb11g203.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    // Admin Dashboard
    @GetMapping("/admin")
    public String adminDashboard(Model model) {
        List<Report> allReports = reportService.getAllReports();
        model.addAttribute("reports", allReports);
        return "reports/admin-dashboard";
    }

    // Shop Owner Dashboard
    @GetMapping("/shop")
    public String shopDashboard(Model model) {
        List<Report> reports = reportService.getAllReports();
        model.addAttribute("reports", reports);
        return "reports/shop-dashboard";
    }

    // Generate Inventory Report
    @PostMapping("/generate/inventory")
    public String generateInventoryReport(@RequestParam(required = false) Integer shopId,
                                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate reportDate,
                                          @RequestParam(required = false) Integer userId,
                                          Model model) {
        LocalDate dateToUse = (reportDate != null) ? reportDate : LocalDate.now();
        Report report = reportService.generateInventoryReport(userId, shopId, dateToUse);
        reportService.saveReport(report);

        model.addAttribute("message", "Inventory report generated successfully!");
        return "redirect:/reports/shop";
    }

    // Generate Sales Report
    @PostMapping("/generate/sales")
    public String generateSalesReport(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                      @RequestParam(required = false) String reportName,
                                      @RequestParam(required = false) String reportDescription,
                                      @RequestParam(required = false) String productCategory,
                                      @RequestParam(required = false) String orderStatus,
                                      @RequestParam(required = false) Integer shopId,
                                      @RequestParam(required = false) Integer userId,
                                      Model model) {
        Report report = reportService.generateSalesReport(userId, shopId, startDate, endDate, reportName,
                reportDescription, productCategory, orderStatus);
        reportService.saveReport(report);

        model.addAttribute("message", "Sales report generated successfully!");
        return "redirect:/reports/shop";
    }

    // View Report Details
    @GetMapping("/view/{reportId}")
    public String viewReport(@PathVariable Integer reportId,
                             @RequestParam(required = false, defaultValue = "admin") String userRole,
                             Model model) {
        Report report = reportService.getReportById(reportId);
        if (report != null) {
            model.addAttribute("report", report);
            model.addAttribute("userRole", userRole);
            return "reports/report-details";
        }
        return "redirect:/reports/admin";
    }

    // Edit Report Page
    @GetMapping("/edit/{reportId}")
    public String editReportPage(@PathVariable Integer reportId,
                                 @RequestParam(required = false, defaultValue = "admin") String userRole,
                                 Model model) {
        Report report = reportService.getReportById(reportId);
        if (report != null) {
            model.addAttribute("report", report);
            model.addAttribute("userRole", userRole);
            return "reports/report-edit";
        }
        return "redirect:/reports/admin";
    }

    // Update Report (Form Submission)
    @PostMapping("/update/{reportId}")
    public String updateReport(@PathVariable Integer reportId,
                               @RequestParam String reportName,
                               @RequestParam String reportDescription,
                               @RequestParam(required = false, defaultValue = "admin") String userRole,
                               Model model) {
        Report updatedReport = reportService.updateReport(reportId, reportName, reportDescription);
        if (updatedReport != null) {
            model.addAttribute("message", "Report updated successfully!");
            return "redirect:/reports/view/" + reportId + "?userRole=" + userRole;
        }
        model.addAttribute("error", "Failed to update report");
        return "redirect:/reports/admin";
    }

    // Delete Report
    @PostMapping("/delete/{reportId}")
    public String deleteReport(@PathVariable Integer reportId) {
        reportService.deleteReport(reportId);
        return "redirect:/reports/admin";
    }

    // Delete Expired Reports (Admin)
    @PostMapping("/admin/delete-expired")
    public String deleteExpiredReports() {
        LocalDate cutoffDate = LocalDate.now().minusYears(1);
        List<Report> expiredReports = reportService.getExpiredReports(cutoffDate);

        for (Report report : expiredReports) {
            reportService.deleteReport(report.getReportId());
        }

        return "redirect:/reports/admin";
    }
}