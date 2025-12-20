-- Verify Database Setup
-- Run this in psql: psql -U postgres -d imigishalink -f verify_database.sql

-- Check if database exists and is accessible
SELECT current_database();

-- List all tables
\dt

-- Check users table (if exists)
SELECT 
    id, 
    email, 
    first_name, 
    last_name, 
    role, 
    is_verified, 
    is_active,
    created_at
FROM users 
ORDER BY created_at DESC 
LIMIT 10;

-- Count users by role
SELECT 
    role, 
    COUNT(*) as count,
    COUNT(CASE WHEN is_verified = true THEN 1 END) as verified_count
FROM users 
GROUP BY role;

-- Check if test users exist
SELECT 
    email, 
    role, 
    is_verified,
    CASE 
        WHEN is_verified = true THEN 'Ready to login'
        ELSE 'Needs verification'
    END as status
FROM users 
WHERE email IN (
    'admin@imigishalink.rw',
    'ngo@test.rw',
    'donor@test.rw'
);

