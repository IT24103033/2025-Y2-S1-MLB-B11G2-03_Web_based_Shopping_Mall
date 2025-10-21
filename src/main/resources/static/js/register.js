// Wait for DOM to be fully loaded
document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('registrationForm');
    const submitBtn = document.getElementById('submitBtn');
    const successAlert = document.getElementById('successAlert');

    // Auto-hide success message after 5 seconds
    if (successAlert) {
        setTimeout(() => {
            successAlert.style.opacity = '0';
            successAlert.style.transition = 'opacity 0.5s ease';
            setTimeout(() => {
                successAlert.style.display = 'none';
            }, 500);
        }, 5000);
    }

    // Get all input fields
    const firstNameInput = document.getElementById('first_name');
    const lastNameInput = document.getElementById('last_name');
    const usernameInput = document.getElementById('username');
    const emailInput = document.getElementById('email');
    const passwordInput = document.getElementById('password');
    const roleSelect = document.getElementById('role');

    // Validation patterns
    const patterns = {
        first_name: /^[a-zA-Z\s]{2,50}$/,
        last_name: /^[a-zA-Z\s]{2,50}$/,
        username: /^[a-zA-Z0-9_]{3,20}$/,
        email: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
        password: /^.{6,}$/
    };

    // Error messages
    const errorMessages = {
        first_name: {
            empty: 'First name is required',
            invalid: 'First name must be 2-50 characters and contain only letters'
        },
        last_name: {
            empty: 'Last name is required',
            invalid: 'Last name must be 2-50 characters and contain only letters'
        },
        username: {
            empty: 'Username is required',
            invalid: 'Username must be 3-20 characters (letters, numbers, underscore only)'
        },
        email: {
            empty: 'Email is required',
            invalid: 'Please enter a valid email address'
        },
        password: {
            empty: 'Password is required',
            invalid: 'Password must be at least 6 characters long'
        },
        role: {
            empty: 'Please select a role'
        }
    };

    // Add real-time validation on blur
    const inputs = [firstNameInput, lastNameInput, usernameInput, emailInput, passwordInput, roleSelect];

    inputs.forEach(input => {
        // Validate on blur (when user leaves the field)
        input.addEventListener('blur', function() {
            validateField(this);
        });

        // Clear error on input
        input.addEventListener('input', function() {
            clearError(this);
        });
    });

    // Form submission validation
    form.addEventListener('submit', function(e) {
        e.preventDefault(); // Prevent default submission first

        let isValid = true;
        let firstErrorField = null;

        // Validate all fields
        inputs.forEach(input => {
            if (!validateField(input)) {
                isValid = false;
                if (!firstErrorField) {
                    firstErrorField = input;
                }
            }
        });

        if (!isValid) {
            // Scroll to first error and focus
            if (firstErrorField) {
                firstErrorField.scrollIntoView({ behavior: 'smooth', block: 'center' });
                firstErrorField.focus();

                // Add shake animation to the form
                form.classList.add('shake');
                setTimeout(() => {
                    form.classList.remove('shake');
                }, 500);
            }
        } else {
            // All validations passed, submit the form
            submitBtn.disabled = true;
            submitBtn.textContent = 'Registering...';
            form.submit();
        }
    });

    // Validate individual field
    function validateField(field) {
        const fieldId = field.id;
        const fieldValue = field.value.trim();
        const errorElement = document.getElementById(fieldId + '_error');

        // Check if field is empty
        if (fieldValue === '' || (fieldId === 'role' && fieldValue === '')) {
            showError(field, errorElement, errorMessages[fieldId].empty);
            return false;
        }

        // Check if field has a pattern and validate it
        if (patterns[fieldId]) {
            if (!patterns[fieldId].test(fieldValue)) {
                showError(field, errorElement, errorMessages[fieldId].invalid);
                return false;
            }
        }

        // If validation passes, mark as success
        markSuccess(field);
        clearError(field);
        return true;
    }

    // Show error message
    function showError(field, errorElement, message) {
        field.classList.remove('success');
        field.classList.add('error');

        if (errorElement) {
            errorElement.textContent = message;
            errorElement.classList.add('show');
        }
    }

    // Clear error message
    function clearError(field) {
        field.classList.remove('error');
        const errorElement = document.getElementById(field.id + '_error');

        if (errorElement) {
            errorElement.textContent = '';
            errorElement.classList.remove('show');
        }
    }

    // Mark field as success
    function markSuccess(field) {
        field.classList.remove('error');
        field.classList.add('success');
    }

    // Prevent form resubmission on refresh
    if (window.history.replaceState) {
        window.history.replaceState(null, null, window.location.href);
    }

    // Prevent spaces in username
    if (usernameInput) {
        usernameInput.addEventListener('keypress', function(e) {
            if (e.key === ' ') {
                e.preventDefault();
            }
        });
    }

    // Email validation on input
    if (emailInput) {
        emailInput.addEventListener('input', function() {
            this.value = this.value.toLowerCase().trim();
        });
    }
});