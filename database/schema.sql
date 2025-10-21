CREATE DATABASE IF NOT EXISTS shopping_mall;
-- user: admin password: strong_pass hostname: 206.189.139.192
USE shopping_mall;

-- Drop table if it already exists (for testing)
DROP TABLE IF EXISTS user;

-- Create user table
CREATE TABLE User (
                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      first_name VARCHAR(50) NOT NULL,
                      last_name VARCHAR(50) NOT NULL,
                      username VARCHAR(100) UNIQUE NOT NULL,
                      address VARCHAR(255),
                      email VARCHAR(100) UNIQUE NOT NULL,
                      password VARCHAR(255) NOT NULL,
                      role ENUM('CUSTOMER', 'SHOP_OWNER', 'ADMIN') NOT NULL DEFAULT 'CUSTOMER'
);


-- 2. UserPhone Table (multivalued attribute)
CREATE TABLE UserPhone (
                           phone_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           user_id BIGINT NOT NULL,
                           phone_number VARCHAR(20) NOT NULL,
                           FOREIGN KEY (user_id) REFERENCES User(id) ON DELETE CASCADE
);

-- 3. Customer Table (subclass of User)
CREATE TABLE if not exists Customer (
                          customer_id BIGINT PRIMARY KEY,
                          FOREIGN KEY (customer_id) REFERENCES User(id) ON DELETE CASCADE
);

-- 4. ShopOwner Table (subclass of User)
CREATE TABLE if not exists ShopOwner (
                           shop_owner_id BIGINT PRIMARY KEY,
                           bank_account_no VARCHAR(50) NOT NULL,
                           tax_id VARCHAR(50) NOT NULL,
                           business_registration_no VARCHAR(50) NOT NULL,
                           FOREIGN KEY (shop_owner_id) REFERENCES User(id) ON DELETE CASCADE
);

-- 5. Admin Table (subclass of User)
CREATE TABLE if not exists Admin (
                       admin_id BIGINT PRIMARY KEY,
                       admin_level INT NOT NULL,
                       FOREIGN KEY (admin_id) REFERENCES User(id) ON DELETE CASCADE
);

-- 6. AdminPermission Table (multivalued attribute for Admin)
CREATE TABLE if not exists AdminPermission (
                                 permission_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                 admin_id BIGINT NOT NULL,
                                 permission_name VARCHAR(100) NOT NULL,
                                 FOREIGN KEY (admin_id) REFERENCES Admin(admin_id) ON DELETE CASCADE
);
