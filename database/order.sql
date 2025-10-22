USE report_db;

-- ========================================
-- REPORTING SYSTEM DATABASE SCHEMA
-- ========================================

-- 1. User table (for authentication)
CREATE TABLE user (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(100) UNIQUE,
    password VARCHAR(255),
    email VARCHAR(255),
    phone_number VARCHAR(50),
    role ENUM('ADMIN', 'SHOP_OWNER'),
    created_at DATETIME,
    updated_at DATETIME
);

-- 2. Category table
CREATE TABLE category (
    category_id INT PRIMARY KEY AUTO_INCREMENT,
    category_name VARCHAR(255)
);

-- 3. Shop table
CREATE TABLE shop (
    shop_id INT PRIMARY KEY AUTO_INCREMENT,
    shop_name VARCHAR(255),
    status VARCHAR(50),
    description TEXT,
    policies TEXT,
    contact VARCHAR(255),
    registration_date DATE
);

-- 4. Product table
CREATE TABLE product (
    product_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255),
    description TEXT,
    price DECIMAL(10,2),
    stock_quantity INT,
    category_id INT,
    FOREIGN KEY (category_id) REFERENCES Category(category_id)
);

-- 5. Customer table
CREATE TABLE customer (
    customer_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    FOREIGN KEY (user_id) REFERENCES User(user_id)
);

-- 6. Order table
CREATE TABLE `order` (
    order_id INT PRIMARY KEY AUTO_INCREMENT,
    total_amount DECIMAL(10,2),
    order_date DATETIME,
    status VARCHAR(50),
    customer_id INT,
    shop_id INT,
    FOREIGN KEY (customer_id) REFERENCES Customer (customer_id),
    FOREIGN KEY (shop_id) REFERENCES Shop (shop_id)
);

-- 7. Order_Item table
CREATE TABLE order_item (
    order_item_id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT,
    product_id INT,
    quantity INT,
    unit_price DECIMAL(10,2),
    subtotal DECIMAL(10,2),
    FOREIGN KEY (order_id) REFERENCES `Order`(order_id),
    FOREIGN KEY (product_id) REFERENCES Product(product_id)
);


-- 8. Report Table
CREATE TABLE Report (
    ReportID INT PRIMARY KEY AUTO_INCREMENT,
    ReportName VARCHAR(100) NULL,
    Description TEXT NULL,
    ReportType VARCHAR(50),
    GeneratedDate DATETIME,
    Format VARCHAR(50),
    ReportData TEXT,
    PeriodStart DATE,
    PeriodEnd DATE,
    ProductCategory VARCHAR(50) NULL,
    OrderStatus VARCHAR(50) NULL,
    Shop_ID INT,
    UserID INT,
    FOREIGN KEY (Shop_ID) REFERENCES Shop(shop_id),
    FOREIGN KEY (UserID) REFERENCES User(user_id)
);

-- 9. Shop_Report table (many-to-many relationship)
CREATE TABLE shop_report (
    shop_id INT,
    report_id INT,
    PRIMARY KEY (shop_id, report_id),
    FOREIGN KEY (shop_id) REFERENCES Shop(shop_id),
    FOREIGN KEY (report_id) REFERENCES Report(ReportID)
);




-- Admin, Shop Owner, and 3 customers
INSERT INTO user (user_id, username, password, email, phone_number, role, created_at, updated_at) VALUES
     (1,'admin','$2a$10$15eGFY5gKBJtezn6NyMjT.7p8T9en2LeGWwLYO1/WddozpaMWeWDq.','admin@novamall.com','1234567890','ADMIN','2025-10-01 16:51:09','2025-10-01 16:51:09'),
     (2,'john','$2a$10$VE0Y34X06N85t1TOhlJoR.NTGZoZabNgYZUwQXIDSF14zgfaeb5k.','john@novamall.com','0987654321','SHOP_OWNER','2025-10-01 16:51:10','2025-10-01 16:51:10'),
     (3, 'alice_c', '$2a$10$VE0Y34X06N77t1WHNEJoR.NTGZoZabNgYZUwQXIDSF88zgfaeb5k.', 'alice@example.com', '1112223332', NULL, '2025-01-10 09:00:00', '2025-01-10 09:00:00'),
     (4, 'bob_c', '$2a$10$VE0Y34X06N55t1TOhlBcD.CSQLoZabNgYZUwQXIDSF99zgfaeb5k.', 'bob@example.com', '1112223333', NULL, '2025-01-15 14:00:00', '2025-01-15 14:00:00'),
     (5, 'charlie_c', '$2a$10$VE0Y34X06N44t1TOhlQcR.NRGEoBabNgPOUwQXIDSF14zgasdb5k.', 'charlie@example.com', '1112223334', NULL, '2025-01-20 16:00:00', '2025-01-20 16:00:00'),
     (6, 'david_c', '$2a$10$QWzY34X06N88t1TOhlJoR.NTGZoZabNgYZUwQXIDSF14zgfaeb5k.', 'david@example.com', '1112223335', NULL, '2025-01-25 08:00:00', '2025-01-25 08:00:00'),
     (7, 'eve_c', '$2a$10$ERvY34X06N88t1TOhlJoR.NTGZoZabNgYZUwQXIDSF14zgfaeb5k.', 'eve@example.com', '1112223336', NULL, '2025-02-01 10:00:00', '2025-02-01 10:00:00'),
     (8, 'frank_c', '$2a$10$FGhY34X06N88t1TOhlJoR.NTGZoZabNgYZUwQXIDSF14zgfaeb5k.', 'frank@example.com', '1112223337', NULL, '2025-02-05 12:00:00', '2025-02-05 12:00:00'),
     (9, 'grace_c', '$2a$10$JKLm34X06N88t1TOhlJoR.NTGZoZabNgYZUwQXIDSF14zgfaeb5k.', 'grace@example.com', '1112223338', NULL, '2025-02-10 14:00:00', '2025-02-10 14:00:00'),
     (10, 'helen_c', '$2a$10$POiu34X06N88t1TOhlJoR.NTGZoZabNgYZUwQXIDSF14zgfaeb5k.', 'helen@example.com', '1112223339', NULL, '2025-02-15 16:00:00', '2025-02-15 16:00:00');

-- Shop table (ONLY ONE SHOP)
INSERT INTO shop (shop_id, shop_name, status, description, policies, contact, registration_date) VALUES
    (1, 'Nova Mall Store', 'ACTIVE', 'Your one-stop shop for everything!', 'Returns within 30 days.', 'contact@novamallstore.com', '2025-01-05');

-- Product table
INSERT INTO product (product_id, name, description, price, stock_quantity, category_id) VALUES
     (1, 'Wireless Mouse', 'Ergonomic wireless mouse', 7797.00, 50, 101),
     (2, 'Gaming Headset', 'High-fidelity gaming headset', 10997.00, 0, 101),
     (3, 'Cotton T-Shirt (M)', 'Comfortable plain cotton t-shirt, size M', 2500.00, 120, 102),
     (4, 'Jeans (L)', 'Stylish denim jeans, size L', 4850.00, 30, 102),
     (5, 'Coffee Maker', 'Automatic drip coffee maker', 18000.00, 15, 103),
     (6, 'Bestseller Novel', 'Fiction novel by a popular author', 1897.00, 5, 104),
     (7, 'Organic Apples (1kg)', 'Fresh organic apples', 1047.00, 0, 105),
     (8, 'Bluetooth Speaker', 'Portable waterproof speaker', 13500.00, 25, 101),
     (9, 'Summer Dress (S)', 'Light and airy summer dress, size S', 5500.00, 10, 102),
     (10, 'Yoga Mat', 'Premium non-slip yoga mat', 2997.00, 0, 103),
     (11, 'Smart Watch', 'Fitness tracker and notification display', 25997.00, 10, 101),
     (12, 'Designer Scarf', 'Silk blend scarf for all seasons', 2700.00, 0, 102),
     (13, 'Portable Charger', '10000mAh power bank', 5000.00, 40, 101),
     (14, 'Cookbook: Italian Delights', 'Classic Italian recipes', 3750.00, 8, 104),
     (15, 'Desk Lamp', 'Adjustable LED desk lamp', 8000.00, 0, 103),
     (16, 'Running Shorts (M)', 'Lightweight athletic shorts, size M', 3000.00, 60, 102),
     (17, 'Blender', 'High-speed kitchen blender', 10997.00, 5, 103),
     (18, 'Action Camera', '4K waterproof action camera', 20000.00, 0, 101),
     (19, 'Face Masks (50-pack)', 'Disposable protective face masks', 3000.00, 200, 105),
     (20, 'Kids Puzzle (500pc)', 'Colorful animal puzzle', 5400.00, 12, 104);

-- Customer table (3 customers, linked to User IDs 3, 4, 5)
INSERT INTO customer (customer_id) VALUES
     (3), -- Alice
     (4), -- Bob
     (5), -- Charlie
     (6), -- David
     (7), -- Eve
     (8), -- Frank
     (9), -- Grace
     (10); -- Helen

-- Order table
INSERT INTO `order` (order_id, total_amount, order_date, status, customer_id, shop_id) VALUES
    (1001, 7797.00, '2025-03-01 10:15:00', 'COMPLETED', 3, 1),
    (1002, 2500.00, '2025-03-02 11:30:00', 'COMPLETED', 4, 1),
    (1003, 22850.00, '2025-03-03 14:00:00', 'COMPLETED', 3, 1),
    (1004, 1897.00, '2025-03-05 09:00:00', 'COMPLETED', 5, 1),
    (1005, 13500.00, '2025-03-07 16:45:00', 'PENDING', 4, 1),
    (1006, 5500.00, '2025-03-10 13:00:00', 'COMPLETED', 5, 1),
    (1007, 10997.00, '2025-03-12 10:00:00', 'CANCELLED', 3, 1),
    (1008, 7797.00, '2025-04-01 10:30:00', 'COMPLETED', 4, 1),
    (1009, 5000.00, '2025-04-02 11:00:00', 'COMPLETED', 5, 1),
    (1010, 4850.00, '2025-04-03 14:10:00', 'PENDING', 3, 1),
    (1011, 25997.00, '2025-04-05 15:00:00', 'COMPLETED', 6, 1),
    (1012, 5000.00, '2025-04-06 10:45:00', 'COMPLETED', 7, 1),
    (1013, 8000.00, '2025-04-08 11:00:00', 'PENDING', 8, 1),
    (1014, 3000.00, '2025-04-10 14:30:00', 'COMPLETED', 9, 1),
    (1015, 10997.00, '2025-04-12 17:00:00', 'COMPLETED', 10, 1),
    (1016, 3000.00, '2025-04-15 09:15:00', 'COMPLETED', 6, 1),
    (1017, 5400.00, '2025-04-18 10:00:00', 'COMPLETED', 7, 1),
    (1018, 13500.00, '2025-05-01 11:30:00', 'COMPLETED', 8, 1),
    (1019, 1897.00, '2025-05-02 14:00:00', 'COMPLETED', 9, 1),
    (1020, 7797.00, '2025-05-03 16:00:00', 'PENDING', 10, 1);

-- Order_Item table
INSERT INTO order_item (order_item_id, order_id, product_id, quantity, unit_price, subtotal) VALUES
    (1, 1001, 1, 1, 7797.00, 7797.00),
    (2, 1002, 3, 1, 2500.00, 2500.00),
    (3, 1003, 4, 1, 4850.00, 4850.00),
    (4, 1003, 5, 1, 18000.00, 18000.00),
    (5, 1004, 6, 1, 1897.00, 1897.00),
    (6, 1005, 8, 1, 13500.00, 13500.00),
    (7, 1006, 9, 1, 5500.00, 5500.00),
    (8, 1007, 2, 1, 10997.00, 10997.00),
    (9, 1008, 1, 1, 7797.00, 7797.00),
    (10, 1009, 3, 2, 2500.00, 5000.00),
    (11, 1010, 4, 1, 4850.00, 4850.00),
    (12, 1011, 11, 1, 25997.00, 25997.00),
    (13, 1012, 13, 1, 5000.00, 5000.00),
    (14, 1013, 15, 1, 8000.00, 8000.00),
    (15, 1014, 16, 1, 3000.00, 3000.00),
    (16, 1015, 17, 1, 10997.00, 10997.00),
    (17, 1016, 19, 1, 3000.00, 3000.00),
    (18, 1017, 20, 1, 5400.00, 5400.00),
    (19, 1018, 8, 1, 13500.00, 13500.00),
    (20, 1019, 6, 1, 1897.00, 1897.00),
    (21, 1020, 1, 1, 7797.00, 7797.00);


-- Category table
INSERT INTO category (category_id, category_name) VALUES
    (101, 'Electronics'),
    (102, 'Clothing'),
    (103, 'Home Goods'),
    (104, 'Books'),
    (105, 'Groceries');

SET FOREIGN_KEY_CHECKS = 0;
SET FOREIGN_KEY_CHECKS = 1;

ALTER TABLE Report AUTO_INCREMENT = 1;

