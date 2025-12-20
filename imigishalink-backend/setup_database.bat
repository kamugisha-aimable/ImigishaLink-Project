@echo off
echo ========================================
echo ImigishaLink Database Setup
echo ========================================
echo.

REM Check if PostgreSQL is installed
where psql >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: PostgreSQL is not installed or not in PATH
    echo Please install PostgreSQL first
    pause
    exit /b 1
)

echo Step 1: Creating database 'imigishalink'...
echo.
psql -U postgres -c "CREATE DATABASE imigishalink;" 2>nul

if %ERRORLEVEL% EQU 0 (
    echo Database created successfully!
) else (
    echo.
    echo Database might already exist or there was an error.
    echo You can ignore this if database already exists.
)

echo.
echo Step 2: Verifying database...
psql -U postgres -d imigishalink -c "\dt" >nul 2>&1

if %ERRORLEVEL% EQU 0 (
    echo Database connection successful!
) else (
    echo Database connection failed. Please check your PostgreSQL credentials.
    echo.
    echo Update application.yml with correct credentials:
    echo   username: postgres
    echo   password: YOUR_PASSWORD
)

echo.
echo ========================================
echo Setup Complete!
echo ========================================
echo.
echo Next steps:
echo 1. Update application.yml with your PostgreSQL password
echo 2. Start the Spring Boot application: mvn spring-boot:run
echo 3. Tables will be created automatically
echo 4. Test users will be created automatically
echo.
pause

