// products.js - batch add selected products to cart

document.addEventListener('DOMContentLoaded', function() {
    const btn = document.getElementById('addSelectedBtn');
    if (!btn) return;

    btn.addEventListener('click', async function() {
        const selected = Array.from(document.querySelectorAll('.select-product:checked'));
        if (selected.length === 0) {
            alert('Please select at least one product');
            return;
        }

        btn.textContent = 'Adding...';
        btn.disabled = true;

        try {
            for (const checkbox of selected) {
                const productId = checkbox.getAttribute('data-product-id');
                const qtyInput = document.querySelector('.product-qty[data-product-id="' + productId + '"]');
                const quantity = qtyInput ? parseInt(qtyInput.value) : 1;

                // Add to cart using existing API
                const res = await fetch(`/api/cart/add/${productId}?quantity=${quantity}`, {
                    method: 'POST',
                    credentials: 'include',
                    headers: { 'Content-Type': 'application/json' }
                });

                const text = await res.text();
                console.log('Added', productId, quantity, text);
            }

            // After adding all, reload to show cart
            window.location.href = '/cart';
        } catch (err) {
            console.error('Error adding selected products', err);
            alert('Failed to add selected products. See console for details.');
            btn.textContent = 'Add Selected to Cart';
            btn.disabled = false;
        }
    });
});
