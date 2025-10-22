function goLogin() {
    // Redirect to login page
    window.location.href = "login.html";
}
// ========================================
// NOVA SHOPPING MALL - REPORTING SYSTEM
// Frontend Integration for Report Generation
// ========================================

document.addEventListener('DOMContentLoaded', function() {
    initializeReportGeneration();
    initializeReportDisplay();
    initializeCsvExport();
});

/**
 * Initialize AJAX-based report generation forms
 */
function initializeReportGeneration() {
    // Inventory Report Generation
    const inventoryForm = document.getElementById('inventory-report-form');
    if (inventoryForm) {
        inventoryForm.addEventListener('submit', function(e) {
            e.preventDefault();
            generateInventoryReport();
        });
    }

    // Sales Report Generation
    const salesForm = document.getElementById('sales-report-form');
    if (salesForm) {
        salesForm.addEventListener('submit', function(e) {
            e.preventDefault();
            generateSalesReport();
        });
    }
}

/**
 * Generate Inventory Report via AJAX
 */
function generateInventoryReport() {
    // Get the report date from the form
    const reportDateInput = document.getElementById('inventory-report-date');
    const reportDate = reportDateInput ? reportDateInput.value : null;

    // Get user ID from the form
    const userIdInput = document.getElementById('inventory-user-id');
    const userId = userIdInput ? userIdInput.value : null;

    // Get shop ID from the form
    const shopIdInput = document.getElementById('inventory-shop-id');
    const shopId = shopIdInput ? shopIdInput.value : null;

    if (!reportDate) {
        alert('Please select a report date to check stock levels.');
        return;
    }

    if (!userId) {
        alert('Please enter a user ID. User ID is mandatory.');
        return;
    }

    if (!shopId) {
        alert('Please enter a shop ID. Shop ID is mandatory.');
        return;
    }

    const shopInfo = shopId ? ' for Shop ' + shopId : ' (All Shops)';
    showLoadingMessage('Generating inventory report for ' + reportDate + shopInfo + '...');

    // Create form data with the report date, user ID, and shop ID
    const formData = new URLSearchParams();
    formData.append('reportDate', reportDate);
    formData.append('userId', userId);
    formData.append('shopId', shopId);

    fetch('/api/reports/generate/inventory', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            'X-Requested-With': 'XMLHttpRequest'
        },
        credentials: 'same-origin',
        body: formData
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            hideLoadingMessage();

            if (data.success) {
                displaySuccessMessage(data.message);
                displayReportScreen(data.report);
                refreshReportsList();
            } else {
                displayErrorMessage(data.message);
            }
        })
        .catch(error => {
            hideLoadingMessage();
            displayErrorMessage('System error: ' + error.message);
        });
}

/**
 * Generate Sales Report via AJAX
 */
function generateSalesReport() {
    const startDate = document.getElementById('sales-start-date').value;
    const endDate = document.getElementById('sales-end-date').value;
    const reportName = document.getElementById('sales-report-name').value;
    const reportDescription = document.getElementById('sales-report-description').value;

    // Get user ID from the form
    const userIdInput = document.getElementById('sales-user-id');
    const userId = userIdInput ? userIdInput.value : null;

    // Get shop ID from the form
    const shopIdInput = document.getElementById('sales-shop-id');
    const shopId = shopIdInput ? shopIdInput.value : null;

    // Get selected product categories
    const categoriesSelect = document.getElementById('sales-product-categories');
    const selectedCategories = Array.from(categoriesSelect.selectedOptions).map(opt => opt.value);
    const productCategories = selectedCategories.join(',');

    // Get selected order statuses
    const statusesSelect = document.getElementById('sales-order-statuses');
    const selectedStatuses = Array.from(statusesSelect.selectedOptions).map(opt => opt.value);
    const orderStatuses = selectedStatuses.join(',');

    // Validate parameters
    if (!startDate || !endDate) {
        displayErrorMessage('Please select both start and end dates');
        return;
    }

    if (!reportName || reportName.trim() === '') {
        displayErrorMessage('Please enter a report name');
        return;
    }

    if (!userId) {
        displayErrorMessage('Please enter a user ID. User ID is mandatory.');
        return;
    }

    if (!shopId) {
        displayErrorMessage('Please enter a shop ID. Shop ID is mandatory.');
        return;
    }

    if (new Date(startDate) > new Date(endDate)) {
        displayErrorMessage('Start date must be before end date');
        return;
    }

    const shopInfo = shopId ? ' for Shop ' + shopId : '';
    showLoadingMessage('Generating sales report' + shopInfo + '...');

    const params = new URLSearchParams({
        startDate: startDate,
        endDate: endDate,
        reportName: reportName,
        reportDescription: reportDescription,
        productCategory: productCategories,
        orderStatus: orderStatuses,
        userId: userId,
        shopId: shopId
    });

    fetch('/api/reports/generate/sales?' + params, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'X-Requested-With': 'XMLHttpRequest'
        },
        credentials: 'same-origin'
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            hideLoadingMessage();

            if (data.success) {
                displaySuccessMessage(data.message);
                displayReportScreen(data.report);
                refreshReportsList();
            } else if (data.invalidParams) {
                // Invalid parameters - highlight error and show message
                displayErrorMessage(data.message);
            } else if (data.noData) {
                displayEmptySalesDataList();
            } else {
                displayErrorMessage(data.message);
            }
        })
        .catch(error => {
            hideLoadingMessage();
            displayErrorMessage('System error: ' + error.message);
        });
}

/**
 * Display generated report on screen with charts and tables
 */
function displayReportScreen(report) {
    const reportDisplay = document.getElementById('report-display-area');
    if (!reportDisplay) return;

    reportDisplay.style.display = 'block';
    reportDisplay.innerHTML = `
        <div class="report-result">
            <div class="report-header">
                <h2>📊 ${report.reportType} Report</h2>
                <div class="report-meta">
                    <span><strong>Generated:</strong> ${formatDateTime(report.generatedDate)}</span>
                    <span><strong>Period:</strong> ${report.periodStart} to ${report.periodEnd}</span>
                    <span><strong>Format:</strong> ${report.format}</span>
                </div>
            </div>
            
            <div class="report-actions">
                <button onclick="exportReportToCsv(${report.reportId})" class="btn btn-export">
                    📥 Export to CSV
                </button>
                <button onclick="printReport()" class="btn btn-print">
                    🖨️ Print Report
                </button>
                <a href="/reports/view/${report.reportId}" class="btn btn-view">
                    👁️ View Full Details
                </a>
            </div>
            
            <div class="report-content">
                <h3>Report Summary</h3>
                <div class="report-data-table">
                    ${parseReportDataToTable(report.reportData)}
                </div>
            </div>
            
            <div class="report-chart-container">
                <canvas id="report-chart-${report.reportId}"></canvas>
            </div>
        </div>
    `;

    // Scroll to report display
    reportDisplay.scrollIntoView({ behavior: 'smooth' });

    // Generate chart if data is available
    generateReportChart(report);
}

/**
 * Parse report data string into HTML table
 */
function parseReportDataToTable(reportData) {
    if (!reportData) return '<p>No data available</p>';

    // Simple parsing - you can enhance this based on your data format
    const lines = reportData.split('\n');
    let tableHtml = '<table class="data-table"><tbody>';

    lines.forEach(line => {
        if (line.trim()) {
            tableHtml += `<tr><td>${escapeHtml(line)}</td></tr>`;
        }
    });

function goRegister() {
    // Redirect to register page
    window.location.href = "register.html";
    tableHtml += '</tbody></table>';
    return tableHtml;
}

/**
 * Generate chart visualization for report (placeholder for Chart.js integration)
 */
function generateReportChart(report) {
    // Placeholder for chart generation
    // You can integrate Chart.js here for actual visualization
    console.log('Chart generation for report:', report.reportId);
}

/**
 * Export report to CSV
 */
function exportReportToCsv(reportId) {
    window.location.href = `/api/reports/export/${reportId}/csv`;
    displaySuccessMessage('CSV export started. Check your downloads folder.');
}

/**
 * Initialize CSV export buttons
 */
function initializeCsvExport() {
    const exportButtons = document.querySelectorAll('.btn-export-csv');
    exportButtons.forEach(button => {
        button.addEventListener('click', function() {
            const reportId = this.getAttribute('data-report-id');
            exportReportToCsv(reportId);
        });
    });
}

/**
 * Print report
 */
function printReport() {
    window.print();
}

/**
 * Initialize report display enhancements
 */
function initializeReportDisplay() {
    // Format currency values
    const currencyElements = document.querySelectorAll('.currency');
    currencyElements.forEach(el => {
        const value = parseFloat(el.textContent);
        if (!isNaN(value)) {
            el.textContent = value.toLocaleString('en-US', {
                style: 'currency',
                currency: 'USD'
            });
        }
    });

    // Format dates
    const dateElements = document.querySelectorAll('.date-format');
    dateElements.forEach(el => {
        const date = new Date(el.textContent);
        if (!isNaN(date.getTime())) {
            el.textContent = date.toLocaleDateString('en-US', {
                year: 'numeric',
                month: 'long',
                day: 'numeric'
            });
        }
    });
}

/**
 * Refresh reports list after generation
 */
function refreshReportsList() {
    // Reload the page to show updated reports list
    setTimeout(() => {
        location.reload();
    }, 2000);
}

/**
 * Display success message
 */
function displaySuccessMessage(message) {
    showNotification(message, 'success');
}

/**
 * Display error message
 */
function displayErrorMessage(message) {
    showNotification(message, 'error');
}

/**
 * Display empty sales data message
 */
function displayEmptySalesDataList() {
    showNotification('No data found for the selected period. Please try a different date range.', 'warning');
}

/**
 * Show loading message
 */
function showLoadingMessage(message) {
    const loader = document.getElementById('loading-overlay');
    if (loader) {
        loader.style.display = 'flex';
        const loaderText = loader.querySelector('.loading-text');
        if (loaderText) loaderText.textContent = message;
    }
}

/**
 * Hide loading message
 */
function hideLoadingMessage() {
    const loader = document.getElementById('loading-overlay');
    if (loader) {
        loader.style.display = 'none';
    }
}

/**
 * Show notification toast
 */
function showNotification(message, type = 'info') {
    const notification = document.createElement('div');
    notification.className = `notification notification-${type}`;
    notification.innerHTML = `
        <span class="notification-icon">${getNotificationIcon(type)}</span>
        <span class="notification-message">${escapeHtml(message)}</span>
        <button class="notification-close" onclick="this.parentElement.remove()">×</button>
    `;

    document.body.appendChild(notification);

    // Auto-remove after 5 seconds
    setTimeout(() => {
        notification.remove();
    }, 5000);
}

/**
 * Get notification icon based on type
 */
function getNotificationIcon(type) {
    const icons = {
        'success': '✓',
        'error': '✗',
        'warning': '⚠',
        'info': 'ℹ'
    };
    return icons[type] || icons['info'];
}

/**
 * Format date-time for display
 */
function formatDateTime(dateTimeString) {
    const date = new Date(dateTimeString);
    const dateStr = date.toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
    });
    const timeStr = date.toLocaleTimeString('en-US', {
        hour: '2-digit',
        minute: '2-digit'
    });
    return dateStr + ' ' + timeStr;
}

/**
 * Escape HTML to prevent XSS
 */
function escapeHtml(text) {
    const map = {
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        "'": '&#039;'
    };
    return text.replace(/[&<>"']/g, m => map[m]);
}}