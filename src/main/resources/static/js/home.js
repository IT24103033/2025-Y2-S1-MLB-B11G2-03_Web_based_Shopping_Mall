// Wait for DOM to be fully loaded
document.addEventListener('DOMContentLoaded', function() {


    // ============ SEARCH FUNCTIONALITY ============
    const searchInput = document.getElementById('searchInput');
    const searchBtn = document.querySelector('.search-btn');

    if (searchBtn) {
        searchBtn.addEventListener('click', function() {
            performSearch();
        });
    }

    if (searchInput) {
        searchInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                performSearch();
            }
        });
    }

    function performSearch() {
        const query = searchInput.value.trim();
        if (query) {
            console.log('Searching for:', query);
            // Implement search functionality here
            // window.location.href = '/search?q=' + encodeURIComponent(query);
            showNotification('Searching for: ' + query, 'info');
        }
    }

    // ============ CATEGORY CARDS ============
    const categoryCards = document.querySelectorAll('.category-card');

    categoryCards.forEach(card => {
        card.addEventListener('click', function() {
            const category = this.getAttribute('data-category');
            console.log('Category clicked:', category);
            // Redirect to category page
            // window.location.href = '/category/' + category;
            showNotification('Loading ' + category + ' products...', 'info');
        });
    });

    // ============ ADD TO CART ============
    const addToCartBtns = document.querySelectorAll('.add-to-cart-btn');

    addToCartBtns.forEach(btn => {
        btn.addEventListener('click', function(e) {
            e.preventDefault();

            const productCard = this.closest('.product-card');
            const productName = productCard.querySelector('h3').textContent;
            const productPrice = productCard.querySelector('.product-price').textContent;

            // Add animation
            this.textContent = '✓ Added';
            this.style.background = '#48bb78';

            setTimeout(() => {
                this.textContent = 'Add to Cart';
                this.style.background = '';
            }, 2000);

            // Update cart badge
            updateCartBadge();

            // Show notification
            showNotification(productName + ' added to cart!', 'success');

            // Store in cart (you can implement localStorage or send to backend)
            addToCart({
                name: productName,
                price: productPrice
            });
        });
    });

    // ============ QUICK VIEW ============
    const quickViewBtns = document.querySelectorAll('.quick-view');

    quickViewBtns.forEach(btn => {
        btn.addEventListener('click', function(e) {
            e.preventDefault();

            const productCard = this.closest('.product-card');
            const productName = productCard.querySelector('h3').textContent;

            showNotification('Quick view: ' + productName, 'info');
            // Implement quick view modal here
        });
    });

    // ============ NEWSLETTER FORM ============
    const newsletterForm = document.getElementById('newsletterForm');

    if (newsletterForm) {
        newsletterForm.addEventListener('submit', function(e) {
            e.preventDefault();

            const emailInput = this.querySelector('input[type="email"]');
            const email = emailInput.value;

            if (email) {
                // Send to backend
                console.log('Newsletter subscription:', email);

                showNotification('Thank you for subscribing!', 'success');
                emailInput.value = '';
            }
        });
    }

    // ============ SCROLL TO TOP ============
    const scrollTopBtn = document.getElementById('scrollTop');

    window.addEventListener('scroll', function() {
        if (window.scrollY > 300) {
            scrollTopBtn.classList.add('visible');
        } else {
            scrollTopBtn.classList.remove('visible');
        }
    });

    if (scrollTopBtn) {
        scrollTopBtn.addEventListener('click', function() {
            window.scrollTo({
                top: 0,
                behavior: 'smooth'
            });
        });
    }

    // ============ SMOOTH SCROLLING FOR ANCHOR LINKS ============
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function(e) {
            const href = this.getAttribute('href');

            if (href !== '#' && href.length > 1) {
                e.preventDefault();

                const target = document.querySelector(href);
                if (target) {
                    target.scrollIntoView({
                        behavior: 'smooth',
                        block: 'start'
                    });
                }
            }
        });
    });

    // ============ CART MANAGEMENT ============
    let cart = [];

    function addToCart(product) {
        cart.push(product);
        localStorage.setItem('cart', JSON.stringify(cart));
        console.log('Cart updated:', cart);
    }

    function updateCartBadge() {
        const badges = document.querySelectorAll('.badge');
        badges.forEach(badge => {
            const currentCount = parseInt(badge.textContent) || 0;
            badge.textContent = currentCount + 1;
        });
    }

    // Load cart from localStorage on page load
    function loadCart() {
        const savedCart = localStorage.getItem('cart');
        if (savedCart) {
            cart = JSON.parse(savedCart);
            // Update cart badge with saved count
            const badges = document.querySelectorAll('.badge');
            badges.forEach(badge => {
                badge.textContent = cart.length;
            });
        }
    }

    loadCart();

    // ============ NOTIFICATION SYSTEM ============
    function showNotification(message, type = 'info') {
        // Remove any existing notifications
        const existingNotification = document.querySelector('.notification');
        if (existingNotification) {
            existingNotification.remove();
        }

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
      max-width: 300px;
      font-weight: 500;
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

    // ============ LAZY LOADING IMAGES ============
    const images = document.querySelectorAll('img[data-src]');

    const imageObserver = new IntersectionObserver((entries, observer) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                const img = entry.target;
                img.src = img.dataset.src;
                img.removeAttribute('data-src');
                observer.unobserve(img);
            }
        });
    });

    images.forEach(img => imageObserver.observe(img));

    // ============ PRODUCT FILTERING (if needed) ============
    function filterProducts(category) {
        const productCards = document.querySelectorAll('.product-card');

        productCards.forEach(card => {
            const productCategory = card.querySelector('.product-category').textContent.toLowerCase();

            if (category === 'all' || productCategory === category.toLowerCase()) {
                card.style.display = 'block';
            } else {
                card.style.display = 'none';
            }
        });
    }

    // ============ INITIALIZE ============
    console.log('NovaMart Home Page Loaded Successfully! 🚀');

    // Add welcome animation
    setTimeout(() => {
        showNotification('Welcome to NovaMart! 🎉', 'info');
    }, 1000);

});