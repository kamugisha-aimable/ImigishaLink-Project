# Setup Database Right Now - Step by Step

## Quick Setup (Choose One Method)

### Method 1: Using pgAdmin (Easiest - GUI)

1. **Open pgAdmin** (PostgreSQL GUI tool)
2. **Connect to PostgreSQL Server** (enter your password)
3. **Right-click on "Databases"** → **"Create"** → **"Database"**
4. **Database name**: `imigishalink`
5. **Click "Save"**
6. ✅ Database created!

### Method 2: Using Command Line

1. **Open Command Prompt** (not PowerShell)
2. **Navigate to PostgreSQL bin folder** (usually):
   ```cmd
   cd "C:\Program Files\PostgreSQL\XX\bin"
   ```
   (Replace XX with your PostgreSQL version number)

3. **Create database**:
   ```cmd
   psql -U postgres -c "CREATE DATABASE imigishalink;"
   ```

4. **Enter password** when prompted
5. ✅ Database created!

### Method 3: Using SQL Script

1. **Open Command Prompt**
2. **Navigate to project**:
   ```cmd
   cd "K:\new\AUCA\Semester 7\Web Technology and Internet\Web Tech\ImigishaLink Project\imigishalink-backend"
   ```

3. **Run setup script**:
   ```cmd
   setup_database.bat
   ```

## Step 2: Update Password

**IMPORTANT**: Update the database password in `application.yml`

1. Open: `imigishalink-backend/src/main/resources/application.yml`
2. Find line 8: `password: VAO123@ntagombizi`
3. Change it to your PostgreSQL password:
   ```yaml
   password: YOUR_ACTUAL_POSTGRES_PASSWORD
   ```

## Step 3: Start Backend

The application will automatically:
- ✅ Connect to database
- ✅ Create all tables
- ✅ Create test users

```cmd
cd imigishalink-backend
mvn spring-boot:run
```

## Step 4: Verify

After backend starts, check the logs for:
- ✅ "Initializing sample data..."
- ✅ "Created admin user: admin@imigishalink.rw"
- ✅ "Data initialization completed!"

## Test Login

Go to: `http://localhost:3000/login`

Use:
- Email: `admin@imigishalink.rw`
- Password: `Admin@123`

## If Database Already Exists

If you get "database already exists" error, that's OK! The database is ready to use.

## Need Help Finding PostgreSQL?

**Common Installation Paths:**
- `C:\Program Files\PostgreSQL\15\bin\psql.exe`
- `C:\Program Files\PostgreSQL\16\bin\psql.exe`
- `C:\Program Files (x86)\PostgreSQL\XX\bin\psql.exe`

**Or use pgAdmin:**
- Search "pgAdmin" in Windows Start Menu

