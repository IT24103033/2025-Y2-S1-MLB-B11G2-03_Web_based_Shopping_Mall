-- Users table
CREATE TABLE users (
    user_id VARCHAR(50) PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20),
    address TEXT,
    role ENUM('customer', 'store_owner', 'admin') DEFAULT 'customer',
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Stores table
CREATE TABLE stores (
    store_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    store_name VARCHAR(100) NOT NULL UNIQUE,
    category VARCHAR(50),
    description VARCHAR(255),
    owner_id VARCHAR(50),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (owner_id) REFERENCES users(user_id)
);

-- Orders table
CREATE TABLE orders (
    order_id VARCHAR(50) PRIMARY KEY,
    user_id VARCHAR(50) NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('processing', 'shipped', 'delivered', 'cancelled') DEFAULT 'processing',
    is_email BOOLEAN DEFAULT TRUE,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- Products table (needed for order items)
CREATE TABLE products (
    product_id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    stock_quantity INT DEFAULT 0,
    store_id BIGINT,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (store_id) REFERENCES stores(store_id)
);

-- Order Items table
CREATE TABLE order_items (
    order_item_id VARCHAR(50) PRIMARY KEY,
    order_id VARCHAR(50) NOT NULL,
    product_id VARCHAR(50) NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(product_id)
);

-- Cart Items table (for shopping cart - temporary items before checkout)
CREATE TABLE cart_items (
    cart_item_id VARCHAR(50) PRIMARY KEY,
    user_id VARCHAR(50) NOT NULL,  -- Can be temporary user ID for anonymous users
    product_id VARCHAR(50) NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(product_id),
    UNIQUE KEY unique_user_product (user_id, product_id)  -- One product per user in cart
);

-- Notifications table
CREATE TABLE notifications (
    notification_id VARCHAR(50) PRIMARY KEY,
    user_id VARCHAR(50) NOT NULL,
    message TEXT NOT NULL,
    status ENUM('sent', 'pending', 'failed') DEFAULT 'pending',
    delivery_method ENUM('email', 'in_app') DEFAULT 'in_app',
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    read_date TIMESTAMP NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- Payments table
CREATE TABLE payments (
    payment_id VARCHAR(50) PRIMARY KEY,
    order_id VARCHAR(50) NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    payment_status ENUM('pending', 'completed', 'failed') DEFAULT 'pending',
    transaction_id VARCHAR(100),
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(order_id)
);