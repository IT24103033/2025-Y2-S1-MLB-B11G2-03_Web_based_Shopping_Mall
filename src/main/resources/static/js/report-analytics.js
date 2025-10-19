// Report Analytics - Chart.js Integration
// This script loads and renders analytics charts for sales reports

let charts = {}; // Store chart instances for cleanup

// Load analytics data when page loads
document.addEventListener('DOMContentLoaded', function() {
    if (reportType === 'Sales') {
        loadAnalytics();
    }
});

async function loadAnalytics() {
    try {
        const response = await fetch(`/api/reports/${reportId}/analytics`);
        const data = await response.json();

        if (data.success) {
            renderAnalytics(data.analytics);
        } else {
            showError(data.message || 'Failed to load analytics');
        }
    } catch (error) {
        console.error('Error loading analytics:', error);
        showError('Failed to load analytics data');
    }
}

function showError(message) {
    document.getElementById('loadingAnalytics').style.display = 'none';
    const errorDiv = document.getElementById('analyticsError');
    errorDiv.textContent = message;
    errorDiv.style.display = 'block';
}

function renderAnalytics(analytics) {
    // Hide loading
    document.getElementById('loadingAnalytics').style.display = 'none';

    // Show summary stats
    renderSummaryStats(analytics.summary);

    // Show charts
    document.getElementById('chartsGrid').style.display = 'grid';

    // Render all charts
    renderOrderStatusChart(analytics.salesByStatus);
    renderRevenueStatusChart(analytics.revenueByStatus);
    renderCategoryChart(analytics.salesByCategory);
    renderRevenueCategoryChart(analytics.revenueByCategory);
    renderTopProductsChart(analytics.topProducts);
}

function renderSummaryStats(summary) {
    document.getElementById('summaryStats').style.display = 'block';

    const totalOrders = summary.totalOrders || 0;
    document.getElementById('totalOrders').textContent = totalOrders;
    document.getElementById('totalRevenue').textContent = `LKR ${formatNumber(summary.totalRevenue)}`;
    document.getElementById('avgOrderValue').textContent = `LKR ${formatNumber(summary.averageOrderValue)}`;

    const completed = summary.completedOrders || 0;
    const pending = summary.pendingOrders || 0;
    const cancelled = summary.cancelledOrders || 0;

    document.getElementById('completedOrders').textContent = completed;
    document.getElementById('pendingOrders').textContent = pending;
    document.getElementById('cancelledOrders').textContent = cancelled;
}

function renderOrderStatusChart(salesByStatus) {
    const ctx = document.getElementById('orderStatusChart').getContext('2d');

    // Destroy existing chart if it exists
    if (charts.orderStatus) {
        charts.orderStatus.destroy();
    }

    charts.orderStatus = new Chart(ctx, {
        type: 'pie',
        data: {
            labels: Object.keys(salesByStatus),
            datasets: [{
                data: Object.values(salesByStatus),
                backgroundColor: [
                    '#4CAF50', // Completed - Green
                    '#FFC107', // Pending - Yellow
                    '#F44336'  // Cancelled - Red
                ],
                borderColor: '#222',
                borderWidth: 2
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: 'bottom',
                    labels: {
                        color: '#fff',
                        font: {
                            size: 12
                        }
                    }
                },
                tooltip: {
                    callbacks: {
                        label: function(context) {
                            const label = context.label || '';
                            const value = context.parsed || 0;
                            const total = context.dataset.data.reduce((a, b) => a + b, 0);
                            const percentage = ((value / total) * 100).toFixed(1);
                            return `${label}: ${value} (${percentage}%)`;
                        }
                    }
                }
            }
        }
    });
}

function renderRevenueStatusChart(revenueByStatus) {
    const ctx = document.getElementById('revenueStatusChart').getContext('2d');

    if (charts.revenueStatus) {
        charts.revenueStatus.destroy();
    }

    charts.revenueStatus = new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: Object.keys(revenueByStatus),
            datasets: [{
                data: Object.values(revenueByStatus),
                backgroundColor: [
                    '#4CAF50',
                    '#FFC107',
                    '#F44336'
                ],
                borderColor: '#222',
                borderWidth: 2
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: 'bottom',
                    labels: {
                        color: '#fff',
                        font: {
                            size: 12
                        }
                    }
                },
                tooltip: {
                    callbacks: {
                        label: function(context) {
                            const label = context.label || '';
                            const value = context.parsed || 0;
                            return `${label}: LKR ${formatNumber(value)}`;
                        }
                    }
                }
            }
        }
    });
}


function renderCategoryChart(salesByCategory) {
    const ctx = document.getElementById('categoryChart').getContext('2d');

    if (charts.category) {
        charts.category.destroy();
    }

    const colors = [
        '#FF6384',
        '#36A2EB',
        '#FFCE56',
        '#4BC0C0',
        '#9966FF',
        '#FF9F40'
    ];

    charts.category = new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: Object.keys(salesByCategory),
            datasets: [{
                data: Object.values(salesByCategory),
                backgroundColor: colors,
                borderColor: '#222',
                borderWidth: 2
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: 'bottom',
                    labels: {
                        color: '#fff',
                        font: {
                            size: 12
                        }
                    }
                },
                tooltip: {
                    callbacks: {
                        label: function(context) {
                            const label = context.label || '';
                            const value = context.parsed || 0;
                            const total = context.dataset.data.reduce((a, b) => a + b, 0);
                            const percentage = ((value / total) * 100).toFixed(1);
                            return `${label}: ${value} units (${percentage}%)`;
                        }
                    }
                }
            }
        }
    });
}

function renderRevenueCategoryChart(revenueByCategory) {
    const ctx = document.getElementById('revenueCategoryChart').getContext('2d');

    if (charts.revenueCategory) {
        charts.revenueCategory.destroy();
    }

    const colors = [
        '#FF6384',
        '#36A2EB',
        '#FFCE56',
        '#4BC0C0',
        '#9966FF',
        '#FF9F40'
    ];

    charts.revenueCategory = new Chart(ctx, {
        type: 'pie',
        data: {
            labels: Object.keys(revenueByCategory),
            datasets: [{
                data: Object.values(revenueByCategory),
                backgroundColor: colors,
                borderColor: '#222',
                borderWidth: 2
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: 'bottom',
                    labels: {
                        color: '#fff',
                        font: {
                            size: 12
                        }
                    }
                },
                tooltip: {
                    callbacks: {
                        label: function(context) {
                            const label = context.label || '';
                            const value = context.parsed || 0;
                            return `${label}: LKR ${formatNumber(value)}`;
                        }
                    }
                }
            }
        }
    });
}

function renderTopProductsChart(topProducts) {
    const ctx = document.getElementById('topProductsChart').getContext('2d');

    if (charts.topProducts) {
        charts.topProducts.destroy();
    }

    charts.topProducts = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: topProducts.labels,
            datasets: [{
                label: 'Revenue (LKR)',
                data: topProducts.revenues,
                backgroundColor: 'rgba(255, 215, 0, 0.8)',
                borderColor: '#FFD700',
                borderWidth: 1
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            indexAxis: 'y',
            plugins: {
                legend: {
                    display: false
                },
                tooltip: {
                    callbacks: {
                        label: function(context) {
                            const index = context.dataIndex;
                            const revenue = context.parsed.x;
                            const quantity = topProducts.quantities[index];
                            return [
                                `Revenue: LKR ${formatNumber(revenue)}`,
                                `Units Sold: ${quantity}`
                            ];
                        }
                    }
                }
            },
            scales: {
                x: {
                    ticks: {
                        color: '#fff',
                        callback: function(value) {
                            return 'LKR ' + formatNumber(value);
                        }
                    },
                    grid: {
                        color: 'rgba(255, 255, 255, 0.1)'
                    }
                },
                y: {
                    ticks: {
                        color: '#fff'
                    },
                    grid: {
                        color: 'rgba(255, 255, 255, 0.1)'
                    }
                }
            }
        }
    });
}

// Helper function to format numbers
function formatNumber(num) {
    if (num === null || num === undefined) return '0.00';
    return parseFloat(num).toLocaleString('en-US', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    });
}

// Cleanup charts on page unload
window.addEventListener('beforeunload', function() {
    Object.values(charts).forEach(chart => {
        if (chart) chart.destroy();
    });
});
