-- Test data for payments table
USE SE_Project;

-- Create payments table if it doesn't exist
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

-- Insert test payment for existing order ORDER123
INSERT INTO payments (payment_id, order_id, amount, payment_method, payment_status, transaction_id, payment_date)
VALUES ('PAY001', 'ORDER123', 29.99, 'card', 'completed', 'TXN_001', NOW());

-- Verify the data
SELECT * FROM payments;