-- ========================================
-- DATABASE MIGRATION: VARCHAR to BIGINT
-- Migration from VARCHAR(50) user_id to BIGINT AUTO_INCREMENT
-- Run this BEFORE using the new authentication system
-- ========================================

-- STEP 1: Drop existing foreign key constraints
-- This allows us to modify the user_id column type
-- Using IF EXISTS to avoid errors if constraints don't exist

-- Drop foreign keys from orders table
SET @drop_fk_orders = (
    SELECT CONCAT('ALTER TABLE orders DROP FOREIGN KEY ', CONSTRAINT_NAME, ';')
    FROM information_schema.TABLE_CONSTRAINTS
    WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'orders'
    AND CONSTRAINT_TYPE = 'FOREIGN KEY'
    AND CONSTRAINT_NAME LIKE '%user%'
    LIMIT 1
);
SET @drop_fk_orders = IFNULL(@drop_fk_orders, 'SELECT "No FK on orders" as info;');
PREPARE stmt FROM @drop_fk_orders;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Drop foreign keys from notifications table
SET @drop_fk_notifications = (
    SELECT CONCAT('ALTER TABLE notifications DROP FOREIGN KEY ', CONSTRAINT_NAME, ';')
    FROM information_schema.TABLE_CONSTRAINTS
    WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'notifications'
    AND CONSTRAINT_TYPE = 'FOREIGN KEY'
    AND CONSTRAINT_NAME LIKE '%user%'
    LIMIT 1
);
SET @drop_fk_notifications = IFNULL(@drop_fk_notifications, 'SELECT "No FK on notifications" as info;');
PREPARE stmt FROM @drop_fk_notifications;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Drop foreign keys from stores table
SET @drop_fk_stores = (
    SELECT CONCAT('ALTER TABLE stores DROP FOREIGN KEY ', CONSTRAINT_NAME, ';')
    FROM information_schema.TABLE_CONSTRAINTS
    WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'stores'
    AND CONSTRAINT_TYPE = 'FOREIGN KEY'
    AND CONSTRAINT_NAME LIKE '%owner%'
    LIMIT 1
);
SET @drop_fk_stores = IFNULL(@drop_fk_stores, 'SELECT "No FK on stores" as info;');
PREPARE stmt FROM @drop_fk_stores;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- STEP 2: Create mapping table for old IDs to new IDs
-- This preserves relationships during migration
CREATE TEMPORARY TABLE user_id_mapping (
    old_user_id VARCHAR(50),
    new_user_id BIGINT AUTO_INCREMENT PRIMARY KEY
);

-- STEP 3: Insert existing users into mapping table
INSERT INTO user_id_mapping (old_user_id)
SELECT user_id FROM users ORDER BY created_date;

-- STEP 4: Add new BIGINT column to users table
ALTER TABLE users ADD COLUMN user_id_new BIGINT;

-- STEP 5: Populate new user_id column from mapping
UPDATE users u
JOIN user_id_mapping m ON u.user_id = m.old_user_id
SET u.user_id_new = m.new_user_id;

-- STEP 6: Add new BIGINT columns to related tables
ALTER TABLE orders ADD COLUMN user_id_new BIGINT;
ALTER TABLE notifications ADD COLUMN user_id_new BIGINT;
ALTER TABLE stores ADD COLUMN owner_id_new BIGINT;

-- STEP 7: Populate new columns in related tables
UPDATE orders o
JOIN user_id_mapping m ON o.user_id = m.old_user_id
SET o.user_id_new = m.new_user_id;

UPDATE notifications n
JOIN user_id_mapping m ON n.user_id = m.old_user_id
SET n.user_id_new = m.new_user_id;

UPDATE stores s
JOIN user_id_mapping m ON s.owner_id = m.old_user_id
SET s.owner_id_new = m.new_user_id;

-- STEP 7.5: Drop any remaining foreign keys that reference users.user_id
-- This catches any FKs we might have missed in Step 1
SET @drop_fk_products = (
    SELECT CONCAT('ALTER TABLE products DROP FOREIGN KEY ', CONSTRAINT_NAME, ';')
    FROM information_schema.TABLE_CONSTRAINTS
    WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'products'
    AND CONSTRAINT_TYPE = 'FOREIGN KEY'
    AND CONSTRAINT_NAME LIKE '%store%'
    LIMIT 1
);
SET @drop_fk_products = IFNULL(@drop_fk_products, 'SELECT "No FK on products" as info;');
PREPARE stmt FROM @drop_fk_products;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Drop any FK from stores that references users
SET @drop_fk_stores2 = (
    SELECT CONCAT('ALTER TABLE stores DROP FOREIGN KEY ', CONSTRAINT_NAME, ';')
    FROM information_schema.KEY_COLUMN_USAGE
    WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'stores'
    AND REFERENCED_TABLE_NAME = 'users'
    AND REFERENCED_COLUMN_NAME = 'user_id'
    LIMIT 1
);
SET @drop_fk_stores2 = IFNULL(@drop_fk_stores2, 'SELECT "No additional FK on stores" as info;');
PREPARE stmt FROM @drop_fk_stores2;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- STEP 8: Drop old VARCHAR columns
ALTER TABLE users DROP COLUMN user_id;
ALTER TABLE orders DROP COLUMN user_id;
ALTER TABLE notifications DROP COLUMN user_id;
ALTER TABLE stores DROP COLUMN owner_id;

-- STEP 9: Rename new columns to original names
ALTER TABLE users CHANGE COLUMN user_id_new user_id BIGINT;
ALTER TABLE orders CHANGE COLUMN user_id_new user_id BIGINT NOT NULL;
ALTER TABLE notifications CHANGE COLUMN user_id_new user_id BIGINT NOT NULL;
ALTER TABLE stores CHANGE COLUMN owner_id_new owner_id BIGINT;

-- STEP 10: Set user_id as PRIMARY KEY and AUTO_INCREMENT
-- First, check if primary key exists and drop it if it does
SET @drop_pk = (
    SELECT IF(COUNT(*) > 0, 'ALTER TABLE users DROP PRIMARY KEY;', 'SELECT "No PK to drop" as info;')
    FROM information_schema.TABLE_CONSTRAINTS
    WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'users'
    AND CONSTRAINT_TYPE = 'PRIMARY KEY'
);
PREPARE stmt FROM @drop_pk;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Add primary key on user_id and set AUTO_INCREMENT
ALTER TABLE users ADD PRIMARY KEY (user_id);
ALTER TABLE users MODIFY COLUMN user_id BIGINT AUTO_INCREMENT;

-- STEP 11: Add new authentication fields to users table (only if they don't exist)
-- Check and add username column
SET @add_username = (
    SELECT IF(COUNT(*) = 0, 
        'ALTER TABLE users ADD COLUMN username VARCHAR(100) UNIQUE;', 
        'SELECT "username column already exists" as info;')
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'users'
    AND COLUMN_NAME = 'username'
);
PREPARE stmt FROM @add_username;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Check and add role column
SET @add_role = (
    SELECT IF(COUNT(*) = 0, 
        'ALTER TABLE users ADD COLUMN role ENUM(''CUSTOMER'', ''SHOP_OWNER'', ''ADMIN'') DEFAULT ''CUSTOMER'';', 
        'SELECT "role column already exists" as info;')
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'users'
    AND COLUMN_NAME = 'role'
);
PREPARE stmt FROM @add_role;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- STEP 12: Generate username for existing users (email prefix)
UPDATE users SET username = SUBSTRING_INDEX(email, '@', 1) WHERE username IS NULL;

-- STEP 13: Update existing role column to match new enum values
-- Convert lowercase to uppercase to match new enum
UPDATE users SET role = 'CUSTOMER' WHERE role = 'customer' OR role IS NULL;
UPDATE users SET role = 'SHOP_OWNER' WHERE role = 'store_owner';
UPDATE users SET role = 'ADMIN' WHERE role = 'admin';

-- STEP 14: Re-add foreign key constraints
ALTER TABLE orders 
ADD CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES users(user_id);

ALTER TABLE notifications 
ADD CONSTRAINT fk_notifications_user FOREIGN KEY (user_id) REFERENCES users(user_id);

ALTER TABLE stores 
ADD CONSTRAINT fk_stores_owner FOREIGN KEY (owner_id) REFERENCES users(user_id);

-- STEP 15: Update cart_items table (migrate user_id from VARCHAR to BIGINT)
-- First, add new BIGINT column
ALTER TABLE cart_items ADD COLUMN user_id_new BIGINT;

-- Populate new column from mapping (for existing users)
UPDATE cart_items c
JOIN user_id_mapping m ON c.user_id = m.old_user_id
SET c.user_id_new = m.new_user_id;

-- Delete cart items that belong to non-existent users (like TEMP users)
DELETE FROM cart_items WHERE user_id_new IS NULL;

-- Drop old VARCHAR column
ALTER TABLE cart_items DROP COLUMN user_id;

-- Rename new column to original name
ALTER TABLE cart_items CHANGE COLUMN user_id_new user_id BIGINT NOT NULL;

-- STEP 16: Clean up
DROP TEMPORARY TABLE IF EXISTS user_id_mapping;

-- ========================================
-- VERIFICATION QUERIES
-- Run these to verify migration was successful
-- ========================================

-- Check users table structure
DESCRIBE users;

-- Check that all users have IDs
SELECT COUNT(*) as total_users FROM users;

-- Check that all relationships are intact
SELECT 
    (SELECT COUNT(*) FROM orders) as total_orders,
    (SELECT COUNT(*) FROM orders WHERE user_id IS NOT NULL) as orders_with_users,
    (SELECT COUNT(*) FROM notifications) as total_notifications,
    (SELECT COUNT(*) FROM stores) as total_stores,
    (SELECT COUNT(*) FROM stores WHERE owner_id IS NOT NULL) as stores_with_owners;

-- Display sample data
SELECT user_id, first_name, last_name, email, username, role FROM users LIMIT 5;

SELECT 'Migration completed successfully!' as status;
