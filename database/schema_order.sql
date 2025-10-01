CREATE DATABASE IF NOT EXISTS orderprocessing;

USE orderprocessing;

DROP TABLE IF EXISTS user;

CREATE TABLE users (
                       id INT AUTO_INCREMENT PRIMARY KEY,         -- Unique ID for each user
                       username VARCHAR(50) NOT NULL UNIQUE,      -- Username must be unique
                       email VARCHAR(100) NOT NULL UNIQUE,        -- Email must be unique
                       password_hash VARCHAR(255) NOT NULL,       -- Store hashed password
                       full_name VARCHAR(100),                    -- Optional: full name
                       role ENUM('customer', 'admin', 'seller') DEFAULT 'customer', -- User role
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Creation time
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

INSERT INTO users (username, email, password_hash, full_name, role)
VALUES
    ('john_doe', 'john@example.com', 'hashed_password_123', 'John Doe', 'customer'),
    ('jane_admin', 'jane@example.com', 'hashed_password_456', 'Jane Smith', 'admin'),
    ('seller_bob', 'bob@example.com', 'hashed_password_789', 'Bob Seller','seller');