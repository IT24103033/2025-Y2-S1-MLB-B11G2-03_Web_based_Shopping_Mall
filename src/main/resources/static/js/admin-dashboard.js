// Wait for DOM to be fully loaded
document.addEventListener('DOMContentLoaded', function() {

    // ============ SIDEBAR NAVIGATION ============
    const sidebar = document.getElementById('sidebar');
    const menuToggle = document.getElementById('menuToggle');
    const closeSidebar = document.getElementById('closeSidebar');
    const navItems = document.querySelectorAll('.nav-item[data-section]');
    const contentSections = document.querySelectorAll('.content-section');
    const pageTitle = document.getElementById('pageTitle');

    // Toggle sidebar on mobile
    if (menuToggle) {
        menuToggle.addEventListener('click', function() {
            sidebar.classList.add('active');
        });
    }

    if (closeSidebar) {
        closeSidebar.addEventListener('click', function() {
            sidebar.classList.remove('active');
        });
    }

    // Navigation item click handler
    navItems.forEach(item => {
        item.addEventListener('click', function(e) {
            e.preventDefault();

            // Get the section to show
            const sectionName = this.getAttribute('data-section');

            // Remove active class from all nav items
            navItems.forEach(nav => nav.classList.remove('active'));

            // Add active class to clicked item
            this.classList.add('active');

            // Hide all content sections
            contentSections.forEach(section => section.classList.remove('active'));

            // Show the selected section
            const targetSection = document.getElementById(sectionName + '-section');
            if (targetSection) {
                targetSection.classList.add('active');
            }

            // Update page title
            updatePageTitle(sectionName);

            // Close sidebar on mobile after selection
            if (window.innerWidth <= 768) {
                sidebar.classList.remove('active');
            }

            // Load users when Users tab is opened
            if (sectionName === 'users') {
                loadUsersTable();
            }
        });
    });

    // Update page title based on active section
    function updatePageTitle(section) {
        const titles = {
            'overview': 'Dashboard Overview',
            'users': 'User Management',
            'products': 'Product Management',
            'orders': 'Order Management',
            'settings': 'Settings',
            'reports': 'Reports & Analytics'
        };

        pageTitle.textContent = titles[section] || 'Dashboard';
    }

    // ============ LOGOUT FUNCTIONALITY ============
    const logoutBtn = document.getElementById('logoutBtn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', function(e) {
            e.preventDefault();
            if (confirm('Are you sure you want to logout?')) {
                window.location.href = '/logout';
            }
        });
    }

    // ============ TABLE ACTION BUTTONS ============
    
    // Function to attach event listeners to user action buttons
    function attachUserActionListeners() {
        // Edit button handlers
        const editBtns = document.querySelectorAll('.edit-btn');
        editBtns.forEach(btn => {
            btn.addEventListener('click', function() {
                const userId = this.getAttribute('data-user-id');
                if (userId) {
                    window.location.href = `/admin/editUser?userId=${userId}`;
                }
            });
        });

        // Delete button handlers
        const deleteBtns = document.querySelectorAll('.delete-btn');
        deleteBtns.forEach(btn => {
            btn.addEventListener('click', function() {
                const userId = this.getAttribute('data-user-id');
                if (userId && confirm('Are you sure you want to delete this user?')) {
                    deleteUser(userId);
                }
            });
        });
    }
    
    // Function to delete user via AJAX
    async function deleteUser(userId) {
        try {
            // Get CSRF token from meta tag
            const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
            const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');
            
            const headers = {
                'Content-Type': 'application/json',
                'X-Requested-With': 'XMLHttpRequest'
            };
            
            if (csrfToken && csrfHeader) {
                headers[csrfHeader] = csrfToken;
            }
            
            const response = await fetch(`/admin/users/${userId}`, {
                method: 'DELETE',
                headers: headers
            });
            
            if (response.ok) {
                showNotification('User deleted successfully', 'success');
                // Reload the users table
                loadUsersTable();
            } else {
                const errorText = await response.text();
                showNotification('Error deleting user: ' + errorText, 'error');
            }
        } catch (error) {
            showNotification('Error deleting user: ' + error.message, 'error');
            console.error('Delete error:', error);
        }
    }

    // Edit button handlers (for other tables)
    const editBtns = document.querySelectorAll('.edit-btn:not([data-user-id])');
    editBtns.forEach(btn => {
        btn.addEventListener('click', function() {
            const row = this.closest('tr');
            const id = row.querySelector('td:first-child').textContent;
            alert('Edit item with ID: ' + id);
            // You can implement edit modal or redirect to edit page
        });
    });

    // Delete button handlers (for other tables)
    const deleteBtns = document.querySelectorAll('.delete-btn:not([data-user-id])');
    deleteBtns.forEach(btn => {
        btn.addEventListener('click', function() {
            const row = this.closest('tr');
            const id = row.querySelector('td:first-child').textContent;

            if (confirm('Are you sure you want to delete this item?')) {
                // Implement delete functionality here
                row.style.opacity = '0';
                setTimeout(() => {
                    row.remove();
                    showNotification('Item deleted successfully', 'success');
                }, 300);
            }
        });
    });

    // View button handlers
    const viewBtns = document.querySelectorAll('.view-btn');
    viewBtns.forEach(btn => {
        btn.addEventListener('click', function() {
            const row = this.closest('tr');
            const id = row.querySelector('td:first-child').textContent;
            alert('View details for ID: ' + id);
            // Implement view modal or redirect to detail page
        });
    });

    // ============ ADD BUTTONS ============
    const addUserBtn = document.getElementById('addUserBtn');
    if (addUserBtn) {
        addUserBtn.addEventListener('click', function() {
            alert('Add new user functionality');
            // Redirect to add user page or open modal
            // window.location.href = '/admin/users/add';
        });
    }

    const addProductBtn = document.getElementById('addProductBtn');
    if (addProductBtn) {
        addProductBtn.addEventListener('click', function() {
            alert('Add new product functionality');
            // Redirect to add product page or open modal
            // window.location.href = '/admin/products/add';
        });
    }

    // ============ NOTIFICATION SYSTEM ============
    function showNotification(message, type = 'info') {
        const notification = document.createElement('div');
        notification.className = `notification notification-${type}`;
        notification.textContent = message;

        // Style the notification
        notification.style.cssText = `
      position: fixed;
      top: 80px;
      right: 20px;
      padding: 16px 24px;
      background: ${type === 'success' ? '#48bb78' : type === 'error' ? '#e53e3e' : '#667eea'};
      color: white;
      border-radius: 8px;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
      z-index: 9999;
      animation: slideInRight 0.4s ease;
    `;

        document.body.appendChild(notification);

        // Remove notification after 3 seconds
        setTimeout(() => {
            notification.style.animation = 'slideOutRight 0.4s ease';
            setTimeout(() => {
                notification.remove();
            }, 400);
        }, 3000);
    }

    // Add animation styles
    const style = document.createElement('style');
    style.textContent = `
    @keyframes slideInRight {
      from {
        transform: translateX(400px);
        opacity: 0;
      }
      to {
        transform: translateX(0);
        opacity: 1;
      }
    }
    
    @keyframes slideOutRight {
      from {
        transform: translateX(0);
        opacity: 1;
      }
      to {
        transform: translateX(400px);
        opacity: 0;
      }
    }
  `;
    document.head.appendChild(style);

    // ============ SEARCH FUNCTIONALITY ============
    const searchInput = document.querySelector('.search-input');
    if (searchInput) {
        searchInput.addEventListener('input', function() {
            const searchTerm = this.value.toLowerCase();
            const table = this.closest('section').querySelector('.data-table tbody');

            if (table) {
                const rows = table.querySelectorAll('tr');
                rows.forEach(row => {
                    const text = row.textContent.toLowerCase();
                    row.style.display = text.includes(searchTerm) ? '' : 'none';
                });
            }
        });
    }

    // ============ CHART INITIALIZATION ============
    const salesChart = document.getElementById('salesChart');
    if (salesChart) {
        // Simple chart implementation (you can use Chart.js library for better charts)
        const ctx = salesChart.getContext('2d');

        // Sample data
        const months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun'];
        const sales = [12000, 19000, 15000, 25000, 22000, 30000];

        // Set canvas size
        salesChart.width = salesChart.parentElement.offsetWidth;
        salesChart.height = 300;

        // Draw simple bar chart
        drawBarChart(ctx, months, sales, salesChart.width, salesChart.height);
    }

    function drawBarChart(ctx, labels, data, width, height) {
        const padding = 40;
        const chartWidth = width - padding * 2;
        const chartHeight = height - padding * 2;
        const barWidth = chartWidth / data.length / 1.5;
        const maxValue = Math.max(...data);

        // Clear canvas
        ctx.clearRect(0, 0, width, height);

        // Draw bars
        data.forEach((value, index) => {
            const barHeight = (value / maxValue) * chartHeight;
            const x = padding + (index * (chartWidth / data.length)) + (chartWidth / data.length - barWidth) / 2;
            const y = height - padding - barHeight;

            // Create gradient
            const gradient = ctx.createLinearGradient(0, y, 0, y + barHeight);
            gradient.addColorStop(0, '#667eea');
            gradient.addColorStop(1, '#764ba2');

            ctx.fillStyle = gradient;
            ctx.fillRect(x, y, barWidth, barHeight);

            // Draw labels
            ctx.fillStyle = '#4a5568';
            ctx.font = '12px sans-serif';
            ctx.textAlign = 'center';
            ctx.fillText(labels[index], x + barWidth / 2, height - padding + 20);

            // Draw values
            ctx.fillStyle = '#2d3748';
            ctx.font = 'bold 12px sans-serif';
            //ctx.fillText(' + value.toLocaleString(), x + barWidth / 2, y - 5);
        });

        // Draw axes
        ctx.strokeStyle = '#e2e8f0';
        ctx.lineWidth = 2;
        ctx.beginPath();
        ctx.moveTo(padding, padding);
        ctx.lineTo(padding, height - padding);
        ctx.lineTo(width - padding, height - padding);
        ctx.stroke();
    }

    // ============ FORM VALIDATION ============
    const forms = document.querySelectorAll('form');
    forms.forEach(form => {
        form.addEventListener('submit', function(e) {
            e.preventDefault();

            const inputs = this.querySelectorAll('.form-control');
            let isValid = true;

            inputs.forEach(input => {
                if (input.value.trim() === '') {
                    isValid = false;
                    input.style.borderColor = '#e53e3e';
                } else {
                    input.style.borderColor = '#e2e8f0';
                }
            });

            if (isValid) {
                showNotification('Settings saved successfully!', 'success');
                // Submit form data here
            } else {
                showNotification('Please fill in all required fields', 'error');
            }
        });
    });

    // ============ RESPONSIVE HANDLING ============
    window.addEventListener('resize', function() {
        if (window.innerWidth > 768) {
            sidebar.classList.remove('active');
        }

        // Redraw chart on resize
        if (salesChart) {
            const ctx = salesChart.getContext('2d');
            const months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun'];
            const sales = [12000, 19000, 15000, 25000, 22000, 30000];

            salesChart.width = salesChart.parentElement.offsetWidth;
            salesChart.height = 300;

            drawBarChart(ctx, months, sales, salesChart.width, salesChart.height);
        }
    });

    // ============ INITIALIZE ============
    // Show initial notification (optional)
    setTimeout(() => {
        showNotification('Welcome to Admin Dashboard!', 'info');
    }, 500);

    // Update real-time data (example)
    setInterval(() => {
        // You can implement real-time updates here
        // For example, update notification badge count
    }, 30000); // Every 30 seconds
    // Load users initially if Users tab is active by default
    const initiallyActive = document.querySelector('.content-section.active');
    if (initiallyActive && initiallyActive.id === 'users-section') {
        loadUsersTable();
    }

    async function loadUsersTable() {
        const tbody = document.getElementById('users-table-body');
        if (!tbody) return;
        tbody.innerHTML = '<tr><td colspan="6">Loading...</td></tr>';
        try {
            const res = await fetch('/admin/users', { headers: { 'Accept': 'application/json' } });
            if (!res.ok) throw new Error('Failed to load users');
            const users = await res.json();
            if (!Array.isArray(users) || users.length === 0) {
                tbody.innerHTML = '<tr><td colspan="6">No users found</td></tr>';
                return;
            }
            tbody.innerHTML = users.map(u => `
              <tr>
                <td>${u.id ?? ''}</td>
                <td>${[u.first_name, u.last_name].filter(Boolean).join(' ')}</td>
                <td>${u.email ?? ''}</td>
                <td>${u.role ?? ''}</td>
                <td><span class="badge badge-success">Active</span></td>
                <td>
                  <button class="btn-icon edit-btn" title="Edit" data-user-id="${u.id}">✏️</button>
                  <button class="btn-icon delete-btn" title="Delete" data-user-id="${u.id}">🗑️</button>
                </td>
              </tr>
            `).join('');
            
            // Re-attach event listeners for the new buttons
            attachUserActionListeners();
        } catch (e) {
            tbody.innerHTML = '<tr><td colspan="6">Error loading users</td></tr>';
            console.error(e);
        }
    }

});