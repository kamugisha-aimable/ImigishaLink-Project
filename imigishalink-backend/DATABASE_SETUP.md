# Database Setup Guide for ImigishaLink

## Prerequisites

1. **PostgreSQL** must be installed and running
2. **PostgreSQL** should be accessible on `localhost:5432`
3. Default PostgreSQL superuser credentials

## Step 1: Install PostgreSQL (if not installed)

### Windows
1. Download from: https://www.postgresql.org/download/windows/
2. Install PostgreSQL
3. Remember the password you set for the `postgres` user
4. Default port: `5432`

### Verify Installation
```cmd
psql --version
```

## Step 2: Create the Database

### Option A: Using psql Command Line

1. Open Command Prompt or PowerShell
2. Connect to PostgreSQL:
   ```cmd
   psql -U postgres
   ```
3. Enter your PostgreSQL password when prompted
4. Create the database:
   ```sql
   CREATE DATABASE imigishalink;
   ```
5. Verify database was created:
   ```sql
   \l
   ```
6. Exit psql:
   ```sql
   \q
   ```

### Option B: Using pgAdmin (GUI)

1. Open pgAdmin
2. Connect to PostgreSQL server
3. Right-click on "Databases" → "Create" → "Database"
4. Name: `imigishalink`
5. Click "Save"

### Option C: Using SQL Script

Run the provided SQL script:
```cmd
psql -U postgres -f create_database.sql
```

## Step 3: Update Database Credentials (if needed)

Edit `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/imigishalink
    username: postgres          # Change if different
    password: YOUR_PASSWORD      # Change to your PostgreSQL password
```

## Step 4: Verify Database Connection

### Test Connection
```cmd
psql -U postgres -d imigishalink
```

If successful, you'll see:
```
psql (XX.X)
Type "help" for help.

imigishalink=#
```

## Step 5: Start the Application

The application will automatically:
1. Connect to the database
2. Create all tables (via Hibernate `ddl-auto: update`)
3. Run `DataInitializer` to create:
   - Sample locations
   - Sample categories
   - Pre-created test users (admin, NGO, donor)

### Check Tables Created

After starting the application, verify tables:

```sql
\dt
```

You should see tables like:
- users
- categories
- locations
- donations
- ngos
- communities
- messages
- etc.

## Step 6: Verify Test Users

Check if test users were created:

```sql
SELECT email, role, is_verified FROM users;
```

You should see:
- `admin@imigishalink.rw` (ADMIN, verified)
- `ngo@test.rw` (NGO, verified)
- `donor@test.rw` (USER, verified)

## Troubleshooting

### Error: "FATAL: database 'imigishalink' does not exist"

**Solution**: Create the database (Step 2)

### Error: "FATAL: password authentication failed"

**Solution**: 
1. Check password in `application.yml`
2. Reset PostgreSQL password if needed

### Error: "Connection refused"

**Solution**:
1. Check PostgreSQL is running:
   ```cmd
   # Windows
   net start postgresql-x64-XX
   ```
2. Verify port 5432 is not blocked
3. Check PostgreSQL is listening on localhost

### Tables Not Created

**Solution**:
1. Check `application.yml` has `ddl-auto: update`
2. Check application logs for errors
3. Verify database connection is successful
4. Check Hibernate logs (enable `show-sql: true` temporarily)

### Users Not Being Saved

**Solution**:
1. Check database connection
2. Verify tables exist: `\dt` in psql
3. Check application logs for errors
4. Verify `UserRepository.save()` is being called
5. Check if transaction is committed

## Quick Setup Script

Create a file `setup_database.bat` (Windows):

```batch
@echo off
echo Setting up ImigishaLink database...
psql -U postgres -c "CREATE DATABASE imigishalink;"
echo Database created successfully!
echo.
echo Now start the Spring Boot application to create tables.
pause
```

## Manual Database Setup (Alternative)

If automatic setup doesn't work, you can manually create tables using the SQL script (if provided) or let Hibernate create them on first run.

## Verify Everything Works

1. **Start Backend**: `mvn spring-boot:run`
2. **Check Logs**: Look for:
   - "Initializing sample data..."
   - "Created admin user..."
   - "Data initialization completed!"
3. **Test Login**: Use pre-created accounts
4. **Check Database**: Verify users exist in database

## Database Schema

The application uses JPA/Hibernate with `ddl-auto: update`, which means:
- Tables are created automatically on first run
- Schema is updated automatically when entities change
- No manual SQL scripts needed (unless you prefer them)

## Next Steps

After database is set up:
1. Start the backend application
2. Tables will be created automatically
3. Test users will be created automatically
4. You can now register and login users

