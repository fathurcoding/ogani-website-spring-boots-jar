-- Force update SEMUA users dengan direct UPDATE
-- Tidak peduli case apapun di database

-- Update user 7 dan 8 specifically
UPDATE ogani_app.users SET role = 'CUSTOMER' WHERE user_id = 7;
UPDATE ogani_app.users SET role = 'CUSTOMER' WHERE user_id = 8;

-- Update SEMUA users lainnya
UPDATE ogani_app.users SET role = 'CUSTOMER' WHERE user_id > 0 AND role != 'ADMIN';
UPDATE ogani_app.users SET role = 'ADMIN' WHERE role LIKE '%admin%';

-- Verify results
SELECT user_id, username, role FROM ogani_app.users ORDER BY user_id;
