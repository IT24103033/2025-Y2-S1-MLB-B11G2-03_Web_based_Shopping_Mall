// register.js - Professional Registration Form JavaScript

document.addEventListener('DOMContentLoaded', function() {
    // Password visibility toggle
    const togglePasswordBtn = document.querySelector('.toggle-password');
    const passwordInput = document.getElementById('password');

    if (togglePasswordBtn && passwordInput) {
        togglePasswordBtn.addEventListener('click', function() {
            const type = passwordInput.getAttribute('type') === 'password' ? 'text' : 'password';
            passwordInput.setAttribute('type', type);

            // Toggle eye icon (simple approach - you can enhance with different icons)
            const eyeIcon = this.querySelector('.eye-icon');
            if (type === 'text') {
                eyeIcon.style.opacity = '0.5';
            } else {
                eyeIcon.style.opacity = '1';
            }
        });
    }

    // Password strength checker
    const strengthFill = document.getElementById('strengthFill');
    const strengthText = document.getElementById('strengthText');

    if (passwordInput && strengthFill && strengthText) {
        passwordInput.addEventListener('input', function() {
            const password = this.value;
            const strength = calculatePasswordStrength(password);

            // Remove all strength classes
            strengthFill.classList.remove('weak', 'medium', 'strong');
            strengthText.classList.remove('weak', 'medium', 'strong');

            if (password.length === 0) {
                strengthFill.style.width = '0%';
                strengthText.textContent = '';
                return;
            }

            if (strength.score < 3) {
                strengthFill.classList.add('weak');
                strengthText.classList.add('weak');
                strengthText.textContent = 'Weak password';
            } else if (strength.score < 5) {
                strengthFill.classList.add('medium');
                strengthText.classList.add('medium');
                strengthText.textContent = 'Medium strength';
            } else {
                strengthFill.classList.add('strong');
                strengthText.classList.add('strong');
                strengthText.textContent = 'Strong password';
            }
        });
    }

    // Form validation enhancement
    const form = document.querySelector('.registration-form');
    if (form) {
        const inputs = form.querySelectorAll('input[required], select[required]');

        inputs.forEach(input => {
            // Real-time validation feedback
            input.addEventListener('blur', function() {
                validateField(this);
            });

            // Remove error styling on input
            input.addEventListener('input', function() {
                if (this.classList.contains('error')) {
                    this.classList.remove('error');
                }
            });
        });

        // Form submission validation
        form.addEventListener('submit', function(e) {
            let isValid = true;

            inputs.forEach(input => {
                if (!validateField(input)) {
                    isValid = false;
                }
            });

            if (!isValid) {
                e.preventDefault();
                // Scroll to first error
                const firstError = form.querySelector('.error');
                if (firstError) {
                    firstError.scrollIntoView({ behavior: 'smooth', block: 'center' });
                }
            }
        });
    }

    // Smooth animations for form groups
    const formGroups = document.querySelectorAll('.form-group');
    formGroups.forEach((group, index) => {
        group.style.opacity = '0';
        group.style.transform = 'translateY(10px)';

        setTimeout(() => {
            group.style.transition = 'all 0.4s ease';
            group.style.opacity = '1';
            group.style.transform = 'translateY(0)';
        }, 100 * index);
    });
});

/**
 * Calculate password strength
 * @param {string} password - The password to evaluate
 * @returns {object} - Object containing score and feedback
 */
function calculatePasswordStrength(password) {
    let score = 0;

    if (!password) return { score: 0 };

    // Length check
    if (password.length >= 8) score++;
    if (password.length >= 12) score++;

    // Character variety checks
    if (/[a-z]/.test(password)) score++; // lowercase
    if (/[A-Z]/.test(password)) score++; // uppercase
    if (/[0-9]/.test(password)) score++; // numbers
    if (/[^a-zA-Z0-9]/.test(password)) score++; // special characters

    return { score: score };
}

/**
 * Validate individual form field
 * @param {HTMLElement} field - The form field to validate
 * @returns {boolean} - Whether the field is valid
 */
function validateField(field) {
    const value = field.value.trim();
    let isValid = true;

    // Check if required field is empty
    if (field.hasAttribute('required') && !value) {
        isValid = false;
    }

    // Email validation
    if (field.type === 'email' && value) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(value)) {
            isValid = false;
        }
    }

    // Password validation (minimum 6 characters)
    if (field.type === 'password' && value && value.length < 6) {
        isValid = false;
    }

    // Select validation
    if (field.tagName === 'SELECT' && (!value || value === '')) {
        isValid = false;
    }

    // Add/remove error class
    if (!isValid) {
        field.classList.add('error');
        field.style.borderColor = '#f56565';
    } else {
        field.classList.remove('error');
        field.style.borderColor = '';
    }

    return isValid;
}

/**
 * Show success message (can be used after successful registration)
 * @param {string} message - The success message to display
 */
function showSuccessMessage(message) {
    const container = document.querySelector('.container');
    const successDiv = document.createElement('div');
    successDiv.className = 'success-message';
    successDiv.textContent = message;
    successDiv.style.cssText = `
    position: fixed;
    top: 20px;
    right: 20px;
    background: #48bb78;
    color: white;
    padding: 16px 24px;
    border-radius: 8px;
    box-shadow: 0 4px 12px rgba(72, 187, 120, 0.4);
    z-index: 1000;
    animation: slideIn 0.3s ease;
  `;

    document.body.appendChild(successDiv);

    setTimeout(() => {
        successDiv.style.animation = 'slideOut 0.3s ease';
        setTimeout(() => successDiv.remove(), 300);
    }, 3000);
}