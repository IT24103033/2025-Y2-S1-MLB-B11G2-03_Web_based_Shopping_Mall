// Profile Page JavaScript

document.addEventListener('DOMContentLoaded', function() {
    
    // Form Validation
    const forms = document.querySelectorAll('form');
    
    forms.forEach(form => {
        form.addEventListener('submit', function(e) {
            // Add loading state
            form.classList.add('loading');
            
            // Validate form fields
            const inputs = form.querySelectorAll('input[required]');
            let isValid = true;
            
            inputs.forEach(input => {
                if (!input.value.trim()) {
                    isValid = false;
                    input.style.borderColor = '#dc3545';
                }
            });
            
            if (!isValid) {
                e.preventDefault();
                form.classList.remove('loading');
                showMessage('Please fill in all required fields', 'error');
            }
        });
    });
    
    // Real-time input validation
    const inputs = document.querySelectorAll('input');
    
    inputs.forEach(input => {
        input.addEventListener('input', function() {
            if (this.validity.valid) {
                this.style.borderColor = '#28a745';
            } else if (this.value) {
                this.style.borderColor = '#dc3545';
            } else {
                this.style.borderColor = '#e0e0e0';
            }
        });
        
        // Clear error styling on focus
        input.addEventListener('focus', function() {
            if (this.style.borderColor === 'rgb(220, 53, 69)') {
                this.style.borderColor = '#667eea';
            }
        });
    });
    
    // Email validation
    const emailInput = document.querySelector('input[type="email"]');
    if (emailInput) {
        emailInput.addEventListener('blur', function() {
            const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            if (!emailPattern.test(this.value) && this.value) {
                this.style.borderColor = '#dc3545';
                showMessage('Please enter a valid email address', 'error');
            }
        });
    }
    
    // Password strength checker
    const newPasswordInput = document.querySelector('input[name="newPassword"]');
    if (newPasswordInput) {
        const strengthIndicator = document.createElement('div');
        strengthIndicator.className = 'password-strength';
        strengthIndicator.style.cssText = 'margin-top: -10px; margin-bottom: 15px; font-size: 0.85rem;';
        newPasswordInput.parentNode.insertBefore(strengthIndicator, newPasswordInput.nextSibling);
        
        newPasswordInput.addEventListener('input', function() {
            const strength = checkPasswordStrength(this.value);
            strengthIndicator.textContent = strength.text;
            strengthIndicator.style.color = strength.color;
        });
    }
    
    // Password confirmation (if needed in future)
    const oldPasswordInput = document.querySelector('input[name="oldPassword"]');
    if (oldPasswordInput && newPasswordInput) {
        const changePasswordForm = oldPasswordInput.closest('form');
        changePasswordForm.addEventListener('submit', function(e) {
            if (oldPasswordInput.value === newPasswordInput.value) {
                e.preventDefault();
                showMessage('New password must be different from old password', 'error');
                changePasswordForm.classList.remove('loading');
            }
        });
    }
    
    // Auto-hide success/error messages after 5 seconds
    const messages = document.querySelectorAll('.success, .error');
    messages.forEach(message => {
        setTimeout(() => {
            message.style.transition = 'opacity 0.5s ease';
            message.style.opacity = '0';
            setTimeout(() => message.remove(), 500);
        }, 5000);
    });
    
    // Confirm before logout
    const logoutLink = document.querySelector('a[href*="logout"]');
    if (logoutLink) {
        logoutLink.addEventListener('click', function(e) {
            if (!confirm('Are you sure you want to logout?')) {
                e.preventDefault();
            }
        });
    }
    
    // Smooth scroll to error messages
    if (document.querySelector('.error')) {
        document.querySelector('.error').scrollIntoView({ 
            behavior: 'smooth', 
            block: 'center' 
        });
    }
    
    // Add animations to form elements
    const formElements = document.querySelectorAll('form');
    formElements.forEach((form, index) => {
        form.style.animationDelay = `${index * 0.1}s`;
    });
    
});

// Helper Functions

function checkPasswordStrength(password) {
    let strength = 0;
    
    if (password.length >= 8) strength++;
    if (password.length >= 12) strength++;
    if (/[a-z]/.test(password)) strength++;
    if (/[A-Z]/.test(password)) strength++;
    if (/[0-9]/.test(password)) strength++;
    if (/[^a-zA-Z0-9]/.test(password)) strength++;
    
    if (strength <= 2) {
        return { text: 'Weak password', color: '#dc3545' };
    } else if (strength <= 4) {
        return { text: 'Medium password', color: '#ffc107' };
    } else {
        return { text: 'Strong password', color: '#28a745' };
    }
}

function showMessage(text, type) {
    // Remove existing dynamic messages
    const existingMsg = document.querySelector('.dynamic-message');
    if (existingMsg) existingMsg.remove();
    
    const message = document.createElement('p');
    message.className = `${type} dynamic-message`;
    message.textContent = text;
    message.style.cssText = 'max-width: 500px; margin: 0 auto 20px;';
    
    const firstForm = document.querySelector('form');
    if (firstForm) {
        firstForm.parentNode.insertBefore(message, firstForm);
    }
    
    // Auto-hide after 5 seconds
    setTimeout(() => {
        message.style.transition = 'opacity 0.5s ease';
        message.style.opacity = '0';
        setTimeout(() => message.remove(), 500);
    }, 5000);
}

// Prevent double form submission
let formSubmitted = false;
document.querySelectorAll('form').forEach(form => {
    form.addEventListener('submit', function() {
        if (formSubmitted) {
            return false;
        }
        formSubmitted = true;
        
        // Reset after 3 seconds (in case submission fails)
        setTimeout(() => {
            formSubmitted = false;
        }, 3000);
    });
});

// Add keyboard shortcuts
document.addEventListener('keydown', function(e) {
    // Ctrl/Cmd + S to submit profile form
    if ((e.ctrlKey || e.metaKey) && e.key === 's') {
        e.preventDefault();
        const profileForm = document.querySelector('form');
        if (profileForm) {
            profileForm.requestSubmit();
        }
    }
});