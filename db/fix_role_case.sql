-- Quick fix for all users with lowercase role
UPDATE ogani_app.users 
SET role = 'CUSTOMER' 
WHERE role = 'customer';

UPDATE ogani_app.users 
SET role = 'ADMIN' 
WHERE role = 'admin';
