-- ABSOLUTE SIMPLEST UPDATE - No WHERE clause filtering
-- This WILL update ALL users

-- Set ALL users to CUSTOMER first
UPDATE ogani_app.users SET role = 'CUSTOMER';

-- Then update specific admin if needed  
-- UPDATE ogani_app.users SET role = 'ADMIN' WHERE username = 'admin';

-- Verify
SELECT user_id, username, email, role FROM ogani_app.users ORDER BY user_id;
