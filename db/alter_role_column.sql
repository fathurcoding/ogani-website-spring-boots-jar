-- FIX: Change role column from ENUM to VARCHAR
-- This allows JPA to work properly with EnumType.STRING

ALTER TABLE ogani_app.users 
MODIFY COLUMN role VARCHAR(20) NOT NULL DEFAULT 'CUSTOMER';

-- Now update all existing data to uppercase
UPDATE ogani_app.users SET role = 'CUSTOMER' WHERE role = 'customer';
UPDATE ogani_app.users SET role = 'ADMIN' WHERE role = 'admin';

-- Verify results
SELECT user_id, username, role FROM ogani_app.users ORDER BY user_id;
