-- Migration Script: Add Enhanced User Profile Fields
-- Date: 2025-12-16
-- Description: Add fullName, phoneNumber, birthDate, age, and address to users table

-- Add full_name if not exists
ALTER TABLE users 
ADD COLUMN IF NOT EXISTS full_name VARCHAR(255) AFTER email;

-- Add phone_number if not exists (mungkin sudah ada)
ALTER TABLE users 
ADD COLUMN IF NOT EXISTS phone_number VARCHAR(20) AFTER full_name;

-- Add birth_date if not exists
ALTER TABLE users 
ADD COLUMN IF NOT EXISTS birth_date DATE AFTER phone_number;

-- Add age if not exists
ALTER TABLE users 
ADD COLUMN IF NOT EXISTS age INT AFTER birth_date;

-- Add address if not exists
ALTER TABLE users 
ADD COLUMN IF NOT EXISTS address TEXT AFTER age;

-- Add unique constraint for phone number (skip if error)
ALTER TABLE users 
ADD CONSTRAINT unique_phone UNIQUE (phone_number);

-- Add unique constraint for email (skip if already exists)
ALTER TABLE users 
ADD CONSTRAINT unique_email UNIQUE (email);

-- Add indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_users_full_name ON users(full_name);
CREATE INDEX IF NOT EXISTS idx_users_phone ON users(phone_number);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
