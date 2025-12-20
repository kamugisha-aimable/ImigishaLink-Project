# Quick Start Guide - ImigishaLink System

## 🚀 System Status

Both servers are starting:
- **Backend**: http://localhost:8080
- **Frontend**: http://localhost:3000 (or check terminal output)

## 📋 Step-by-Step Instructions

### 1. Wait for Servers to Start

**Backend (Spring Boot)**:
- Look for: `Started ImigishalinkApplication in X.XXX seconds`
- Should be running on: `http://localhost:8080`

**Frontend (Vite)**:
- Look for: `➜  Local:   http://localhost:XXXX/`
- Usually runs on: `http://localhost:3000` or `http://localhost:5173`

### 2. Open in Browser

Once both servers are running:
1. Open your web browser (Chrome, Firefox, Edge)
2. Navigate to: **http://localhost:3000** (or the port shown in terminal)
3. You should see the ImigishaLink homepage

### 3. Test the System

#### Option A: Use Pre-created Test Accounts

**Admin Account** (Full Access):
- Email: `admin@imigishalink.rw`
- Password: `Admin@123`
- Role: ADMIN
- Status: ✅ Verified (can login immediately)

**NGO Account**:
- Email: `ngo@test.rw`
- Password: `Ngo@123`
- Role: NGO
- Status: ✅ Verified (can login immediately)

**Donor Account**:
- Email: `donor@test.rw`
- Password: `Donor@123`
- Role: USER
- Status: ✅ Verified (can login immediately)

#### Option B: Create New Account

1. Click **"Sign Up"** or go to: `http://localhost:3000/signup`
2. Fill in the form:
   - First Name
   - Last Name
   - Email
   - Phone Number (optional)
   - **Select Account Type**: Donor/Regular User or NGO Member
   - Password
   - Confirm Password
3. Click **"Sign up"**
4. **Important**: You'll receive a verification code
   - Check the success message for the verification code (for testing)
   - Or check your email
5. Verify your email:
   - Go to: `http://localhost:3000/verify-email?code=XXXXXX`
   - Or manually enter the code
6. After verification, go to login page

### 4. Login Process

1. Go to: `http://localhost:3000/login`
2. Enter email and password
3. Click **"Log in"**
4. **Step 1**: You'll see "OTP sent to your email"
5. Check your email for 6-digit OTP code
6. Enter the OTP code
7. Click **"Verify OTP"**
8. ✅ You'll be redirected to the home page

## 🔍 Verify System is Running

### Check Backend
Open in browser: `http://localhost:8080/actuator/health`
- Should return JSON with status

Or test API:
```bash
curl http://localhost:8080/api/v1/auth/verify?code=test
```

### Check Frontend
- Open: `http://localhost:3000`
- Should see ImigishaLink homepage

## 🐛 Troubleshooting

### Backend Not Starting

**Port 8080 in use:**
```cmd
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

**Database connection error:**
- Check PostgreSQL is running
- Verify database credentials in `application.yml`
- Ensure database `imigishalink` exists

**Maven issues:**
```cmd
cd imigishalink-backend
mvn clean install
mvn spring-boot:run
```

### Frontend Not Starting

**Port in use:**
```cmd
netstat -ano | findstr :3000
taskkill /PID <PID> /F
```

**npm issues:**
```cmd
cd ImigishaLink
npm install
npm run dev
```

**PowerShell execution policy:**
- Use Command Prompt (cmd.exe) instead
- Or run: `Set-ExecutionPolicy -ExecutionPolicy Bypass -Scope Process`

### Login Issues

**"Please verify your email before logging in"**
- Solution: Verify your email first
- Use pre-created accounts (already verified)
- Or check database for verification code

**"Network Error" or "Failed to fetch"**
- Check backend is running: `http://localhost:8080`
- Check browser console (F12) for errors
- Verify CORS is configured correctly

**OTP not received**
- Check email configuration in `application.yml`
- Check spam folder
- For testing, use verification code shown after registration

## 📱 Quick Test Flow

1. **Start Backend**: `cd imigishalink-backend && mvn spring-boot:run`
2. **Start Frontend**: `cd ImigishaLink && npm run dev`
3. **Open Browser**: `http://localhost:3000`
4. **Login as Admin**: 
   - Email: `admin@imigishalink.rw`
   - Password: `Admin@123`
5. **Complete 2FA**: Enter OTP from email
6. **✅ Success**: You're logged in!

## 🎯 Next Steps

After successful login:
- Explore the dashboard
- Test creating donations
- Test NGO features
- Test community features
- Check user roles and permissions

## 📞 Need Help?

- Check terminal output for errors
- Check browser console (F12)
- Check backend logs: `logs/imigishalink.log`
- Verify both servers are running
- Check network tab in browser DevTools

