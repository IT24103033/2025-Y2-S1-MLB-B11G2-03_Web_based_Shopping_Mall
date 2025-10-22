// Checkout page JavaScript functionality

/**
 * Toggle card details section based on payment method selection
 */
function toggleCardDetails() {
    const cardDetailsSection = document.getElementById('cardDetailsSection');
    const cardRadio = document.getElementById('paymentCard');
    const cardInputs = cardDetailsSection.querySelectorAll('input');
    
    if (cardRadio.checked) {
        cardDetailsSection.style.display = 'block';
        cardDetailsSection.classList.remove('hidden');
        // Make card fields required
        cardInputs.forEach(input => input.required = true);
    } else {
        cardDetailsSection.classList.add('hidden');
        // Remove required attribute from card fields
        cardInputs.forEach(input => {
            input.required = false;
            input.value = ''; // Clear values
        });
    }
}

/**
 * Format card number with spaces
 */
function formatCardNumber(input) {
    let value = input.value.replace(/\s/g, '');
    let formattedValue = value.replace(/(.{4})/g, '$1 ').trim();
    if (formattedValue.length > 19) {
        formattedValue = formattedValue.substring(0, 19);
    }
    input.value = formattedValue;
}

/**
 * Format expiry date as MM/YY
 */
function formatExpiryDate(input) {
    let value = input.value.replace(/\D/g, '');
    if (value.length >= 2) {
        value = value.substring(0, 2) + '/' + value.substring(2, 4);
    }
    input.value = value;
}

/**
 * Allow only numbers for CVV
 */
function formatCVV(input) {
    input.value = input.value.replace(/\D/g, '');
}

/**
 * Validate form before submission
 */
function validateForm() {
    const customerName = document.getElementById('customerName').value.trim();
    const customerEmail = document.getElementById('customerEmail').value.trim();
    const customerPhone = document.getElementById('customerPhone').value.trim();
    const customerAddress = document.getElementById('customerAddress').value.trim();
    const paymentMethod = document.querySelector('input[name="paymentMethod"]:checked').value;
    
    // Basic validation
    if (!customerName || !customerEmail || !customerPhone || !customerAddress) {
        alert('Please fill in all customer details.');
        return false;
    }
    
    // Email validation
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(customerEmail)) {
        alert('Please enter a valid email address.');
        return false;
    }
    
    // Phone validation (basic)
    const phoneRegex = /^[\d\s\-\+\(\)]{10,}$/;
    if (!phoneRegex.test(customerPhone)) {
        alert('Please enter a valid phone number.');
        return false;
    }
    
    // Card validation (if card payment selected)
    if (paymentMethod === 'card') {
        const cardNumber = document.getElementById('cardNumber').value.replace(/\s/g, '');
        const cardExpiry = document.getElementById('cardExpiry').value;
        const cardCvv = document.getElementById('cardCvv').value;
        
        if (cardNumber.length < 13 || cardNumber.length > 19) {
            alert('Please enter a valid card number.');
            return false;
        }
        
        if (!/^\d{2}\/\d{2}$/.test(cardExpiry)) {
            alert('Please enter expiry date in MM/YY format.');
            return false;
        }
        
        if (cardCvv.length < 3 || cardCvv.length > 4) {
            alert('Please enter a valid CVV.');
            return false;
        }
    }
    
    // Show loading state
    const submitButton = document.querySelector('.place-order-btn');
    submitButton.disabled = true;
    submitButton.textContent = 'Processing Order...';
    
    return true;
}

// Initialize page
document.addEventListener('DOMContentLoaded', function() {
    console.log('Checkout page loaded');
    
    // Set up card number formatting
    const cardNumberInput = document.getElementById('cardNumber');
    if (cardNumberInput) {
        cardNumberInput.addEventListener('input', function() {
            formatCardNumber(this);
        });
    }
    
    // Set up expiry date formatting
    const cardExpiryInput = document.getElementById('cardExpiry');
    if (cardExpiryInput) {
        cardExpiryInput.addEventListener('input', function() {
            formatExpiryDate(this);
        });
    }
    
    // Set up CVV formatting
    const cardCvvInput = document.getElementById('cardCvv');
    if (cardCvvInput) {
        cardCvvInput.addEventListener('input', function() {
            formatCVV(this);
        });
    }
    
    // Initialize card details visibility
    toggleCardDetails();
});