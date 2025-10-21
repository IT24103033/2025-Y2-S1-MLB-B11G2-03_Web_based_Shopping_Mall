-- Test data for notification system
USE SE_Project;

-- Insert test user
INSERT INTO users (user_id, first_name, last_name, email, password, phone_number, address) 
VALUES ('USER001', 'John', 'Doe', 'john.doe@email.com', 'password123', '0771234567', '123 Main St, Colombo');

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

-- Check if data was inserted correctly
SELECT 'Testing Data:' as info;
SELECT * FROM users WHERE user_id = 'USER001';
SELECT * FROM orders WHERE order_id = 'ORDER123';
SELECT * FROM products;
SELECT * FROM order_items WHERE order_id = 'ORDER123';