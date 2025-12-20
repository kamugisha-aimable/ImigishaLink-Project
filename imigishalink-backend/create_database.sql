-- Create ImigishaLink Database
-- Run this script as PostgreSQL superuser (postgres)

-- Create database
CREATE DATABASE imigishalink;

-- Connect to the database (run this in psql after creating database)
\c imigishalink

-- Note: Tables will be created automatically by Hibernate when the application starts
-- This script only creates the database itself

-- To verify database was created:
-- \l

-- To connect to the database:
-- psql -U postgres -d imigishalink

