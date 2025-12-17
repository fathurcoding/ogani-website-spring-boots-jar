-- Restore Admin user
UPDATE ogani_app.users SET role = 'ADMIN' WHERE user_id = 3;

-- Verify
SELECT user_id, username, email, role FROM ogani_app.users ORDER BY user_id;
