-- ========================================
-- DATABASE MIGRATION QUERIES
-- Run these first to update existing tables
-- ========================================

-- Step 1: Add role column to users table
-- If the column already exists, comment out this line or it will give an error
ALTER TABLE users 
ADD COLUMN role ENUM('customer', 'store_owner', 'admin') DEFAULT 'customer';

-- Step 2: Create stores table
CREATE TABLE IF NOT EXISTS stores (
    store_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    store_name VARCHAR(100) NOT NULL UNIQUE,
    category VARCHAR(50),
    description VARCHAR(255),
    owner_id VARCHAR(50),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (owner_id) REFERENCES users(user_id)
);

-- Step 3: Add store_id column to products table
-- If the column already exists, comment out these lines
ALTER TABLE products 
ADD COLUMN store_id BIGINT;

ALTER TABLE products
ADD CONSTRAINT fk_product_store FOREIGN KEY (store_id) REFERENCES stores(store_id);

-- Step 4: Create payments table (from earlier payment integration)
CREATE TABLE IF NOT EXISTS payments (
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

-- ========================================
-- TEST DATA INSERTS
-- ========================================

-- Insert test user
INSERT INTO users (user_id, first_name, last_name, email, password, phone_number, address, role) 
VALUES ('USER001', 'John', 'Doe', 'john.doe@email.com', 'password123', '0771234567', '123 Main St, Colombo', 'customer');

-- Insert temp store owner user for testing store management
INSERT INTO users (user_id, first_name, last_name, email, password, phone_number, address, role) 
VALUES ('OWNER001', 'Store', 'Owner', 'storeowner@novamart.com', 'StoreOwner123', '0777654321', '456 Business Ave, Colombo', 'store_owner');

-- Insert test product
INSERT INTO products (product_id, name, description, price, stock_quantity)
VALUES ('PROD001', 'Test Product', 'This is a test product', 29.99, 100);

-- Insert test order
INSERT INTO orders (order_id, user_id, total_amount, status, is_email)
VALUES ('ORDER123', 'USER001', 29.99, 'processing', TRUE);

-- Insert test order item
INSERT INTO order_items (order_item_id, order_id, product_id, quantity, unit_price, subtotal)
VALUES ('ITEM001', 'ORDER123', 'PROD001', 1, 29.99, 29.99);

-- Insert more test products for cart testing
INSERT INTO products (product_id, name, description, price, stock_quantity)
VALUES 
('PROD002', 'Laptop Computer', 'High-performance laptop for work and gaming', 1299.99, 50),
('PROD003', 'Wireless Headphones', 'Noise-canceling wireless headphones', 199.99, 200),
('PROD004', 'Smartphone', 'Latest model smartphone with great camera', 899.99, 75);

-- Insert test stores
INSERT INTO stores (store_name, category, description, owner_id)
VALUES 
('Tech Haven', 'Electronics', 'Your one-stop shop for all tech gadgets', 'OWNER001'),
('Fashion Forward', 'Fashion', 'Trendy clothing and accessories', 'OWNER001');

-- Check if data was inserted correctly
SELECT 'Testing Data:' as info;
SELECT * FROM users WHERE user_id = 'USER001';
SELECT * FROM orders WHERE order_id = 'ORDER123';
SELECT * FROM products;
SELECT * FROM order_items WHERE order_id = 'ORDER123';
select * from se_project.payments;
select * from stores;

SELECT * FROM notifications;

-- clear notification table
DELETE from notifications;

DELETE FROM cart_items WHERE product_id = 'PROD001';