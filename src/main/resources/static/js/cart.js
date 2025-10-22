// Cart page JavaScript functionality

/**
 * Update item quantity in cart
 */
function updateQuantity(button, change) {
    const productId = button.getAttribute('data-product-id');
    const quantitySpan = button.parentElement.querySelector('.quantity');
    const currentQuantity = parseInt(quantitySpan.textContent);
    const newQuantity = currentQuantity + change;
    
    // Don't allow quantity below 1
    if (newQuantity < 1) {
        if (confirm('Remove this item from cart?')) {
            removeItem(button);
        }
        return;
    }
    
    // Show loading state
    quantitySpan.textContent = '...';
    
    // Make AJAX call to update quantity using product ID
    fetch(`/api/cart/update-by-product/${productId}?quantity=${newQuantity}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
        }
    })
    .then(response => response.text())
    .then(data => {
        console.log('Update response:', data);
        // Reload page to show updated totals
        window.location.reload();
    })
    .catch(error => {
        console.error('Error updating quantity:', error);
        // Restore original quantity on error
        quantitySpan.textContent = currentQuantity;
        alert('Failed to update quantity. Please try again.');
    });
}

/**
 * Remove item from cart
 */
function removeItem(button) {
    const productId = button.getAttribute('data-product-id');
    
    // Show confirmation
    if (!confirm('Are you sure you want to remove this item?')) {
        return;
    }
    
    // Show loading state
    button.textContent = 'Removing...';
    button.disabled = true;
    
    // Make AJAX call to remove item using product ID
    fetch(`/api/cart/remove-by-product/${productId}`, {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json',
        }
    })
    .then(response => response.text())
    .then(data => {
        console.log('Remove response:', data);
        // Reload page to show updated cart
        window.location.reload();
    })
    .catch(error => {
        console.error('Error removing item:', error);
        // Restore button state on error
        button.textContent = 'Remove';
        button.disabled = false;
        alert('Failed to remove item. Please try again.');
    });
}

/**
 * Clear entire cart (if needed)
 */
function clearCart() {
    if (!confirm('Are you sure you want to clear your entire cart?')) {
        return;
    }
    
    fetch('/api/cart/clear', {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json',
        }
    })
    .then(response => response.text())
    .then(data => {
        console.log('Clear cart response:', data);
        window.location.reload();
    })
    .catch(error => {
        console.error('Error clearing cart:', error);
        alert('Failed to clear cart. Please try again.');
    });
}

/**
 * Add test item to cart (for testing purposes)
 */
function addTestItem(productId, quantity) {
    const button = event.target;
    button.textContent = 'Adding...';
    button.disabled = true;
    
    fetch(`/api/cart/add/${productId}?quantity=${quantity}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        }
    })
    .then(response => response.text())
    .then(data => {
        console.log('Add item response:', data);
        // Reload page to show new item
        window.location.reload();
    })
    .catch(error => {
        console.error('Error adding item:', error);
        button.textContent = `Add Product ${productId.split('_')[1]}`;
        button.disabled = false;
        alert('Failed to add item. Please try again.');
    });
}

// Initialize page
document.addEventListener('DOMContentLoaded', function() {
    console.log('Cart page loaded');
    
    // Add any initialization code here if needed
    // For example, setting up event listeners, etc.
});