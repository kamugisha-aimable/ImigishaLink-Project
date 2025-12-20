# Quick Database Setup for ImigishaLink

## 🚀 Fast Setup (3 Steps)

### Step 1: Ensure PostgreSQL is Running

**Windows:**
```cmd
# Check if PostgreSQL service is running
sc query postgresql-x64-XX

# If not running, start it
net start postgresql-x64-XX
```

**Or use Services:**
- Press `Win + R`
- Type `services.msc`
- Find "PostgreSQL" service
- Right-click → Start

### Step 2: Create Database

**Option A: Using Command Line (Recommended)**
```cmd
psql -U postgres
```

Then in psql:
```sql
CREATE DATABASE imigishalink;
\q
```

**Option B: Using Batch Script**
```cmd
cd imigishalink-backend
setup_database.bat
```

### Step 3: Update Password in application.yml

Edit `imigishalink-backend/src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    password: YOUR_POSTGRES_PASSWORD  # Change this line
```

## ✅ Verify Setup

### Test Database Connection
```cmd
psql -U postgres -d imigishalink
```

If you can connect, you're good to go!

### Start Application

The application will:
1. ✅ Connect to database
2. ✅ Create all tables automatically
3. ✅ Create test users automatically

```cmd
cd imigishalink-backend
mvn spring-boot:run
```

Look for these messages in logs:
- ✅ "Initializing sample data..."
- ✅ "Created admin user: admin@imigishalink.rw"
- ✅ "Data initialization completed!"

## 🧪 Test Login

After application starts, use these accounts:

**Admin:**
- Email: `admin@imigishalink.rw`
- Password: `Admin@123`

**NGO:**
- Email: `ngo@test.rw`
- Password: `Ngo@123`

**Donor:**
- Email: `donor@test.rw`
- Password: `Donor@123`

## 🔍 Verify Users in Database

```sql
psql -U postgres -d imigishalink

-- Check users
SELECT email, role, is_verified FROM users;

-- Should show 3 test users, all verified
```

## ❌ Common Issues

### "Database does not exist"
→ Run Step 2 to create database

### "Password authentication failed"
→ Update password in `application.yml`

### "Connection refused"
→ Start PostgreSQL service

### "Tables not created"
→ Check application logs for errors
→ Verify `ddl-auto: update` in `application.yml`

## 📝 Database Configuration

Current settings in `application.yml`:
- **Database**: `imigishalink`
- **Host**: `localhost`
- **Port**: `5432`
- **Username**: `postgres`
- **Password**: Update this!
- **Auto-create tables**: Yes (`ddl-auto: update`)

## 🎯 That's It!

Once database is set up and application starts:
- ✅ Users can register
- ✅ Users are saved to database
- ✅ Users can login
- ✅ All data persists

