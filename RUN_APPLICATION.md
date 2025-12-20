# How to Run ImigishaLink in Browser - Step by Step Guide

## Prerequisites Check

Before starting, make sure you have:
- ✅ Java JDK 17 or higher installed
- ✅ Maven installed
- ✅ Node.js and npm installed
- ✅ PostgreSQL database running
- ✅ Database created (imigishalink)

## Step 1: Start the Backend Server

### Option A: Using Maven (Recommended)

1. Open a terminal/command prompt
2. Navigate to the backend directory:
   ```bash
   cd "K:\new\AUCA\Semester 7\Web Technology and Internet\Web Tech\ImigishaLink Project\imigishalink-backend"
   ```

3. Start the Spring Boot application:
   ```bash
   mvn spring-boot:run
   ```

   **OR** if you have the JAR file:
   ```bash
   java -jar target/imigishalink-backend-0.0.1-SNAPSHOT.jar
   ```

4. Wait for the server to start. You should see:
   ```
   Started ImigishalinkApplication in X.XXX seconds
   ```

5. The backend is now running on: **http://localhost:8080**

### Option B: Using IDE (IntelliJ IDEA / Eclipse)

1. Open the `imigishalink-backend` project in your IDE
2. Find `ImigishalinkApplication.java`
3. Right-click → Run 'ImigishalinkApplication'
4. Wait for the application to start

### Verify Backend is Running

Open your browser and go to:
- **http://localhost:8080/api/v1/auth/verify?code=test** (should return an error, but confirms server is running)
- Or check: **http://localhost:8080/actuator/health** (if actuator is enabled)

## Step 2: Start the Frontend Server

### Open a NEW Terminal/Command Prompt

1. Navigate to the frontend directory:
   ```bash
   cd "K:\new\AUCA\Semester 7\Web Technology and Internet\Web Tech\ImigishaLink Project\ImigishaLink"
   ```

2. Install dependencies (if not already done):
   ```bash
   npm install
   ```

3. Start the development server:
   ```bash
   npm run dev
   ```

4. You should see output like:
   ```
   VITE v7.x.x  ready in XXX ms

   ➜  Local:   http://localhost:3000/
   ➜  Network: use --host to expose
   ```

5. The frontend is now running on: **http://localhost:3000**

## Step 3: Access the Application in Browser

1. Open your web browser (Chrome, Firefox, Edge, etc.)
2. Navigate to: **http://localhost:3000**
3. You should see the ImigishaLink homepage

## Step 4: Test the Login Flow

### Create a Test Account (if needed)

If you don't have an account yet:

1. Click "Sign Up" or go to: **http://localhost:3000/signup**
2. Fill in the registration form
3. Submit the form
4. Check your email for verification code
5. Verify your email

### Login Process

1. Go to: **http://localhost:3000/login**
2. Enter your email and password
3. Click "Log in"
4. **Step 1**: You'll see a message that OTP was sent to your email
5. Check your email for the 6-digit OTP code
6. Enter the OTP code in the form
7. Click "Verify OTP"
8. You'll be redirected to the dashboard: **http://localhost:3000/dashboard**

## Troubleshooting

### Backend Won't Start

**Problem**: Port 8080 is already in use
```bash
# Windows - Find process using port 8080
netstat -ano | findstr :8080

# Kill the process (replace PID with actual process ID)
taskkill /PID <PID> /F
```

**Problem**: Database connection error
- Check PostgreSQL is running
- Verify database credentials in `application.yml`
- Ensure database `imigishalink` exists

**Problem**: Maven build fails
```bash
# Clean and rebuild
mvn clean install
mvn spring-boot:run
```

### Frontend Won't Start

**Problem**: Port 3000 is already in use
```bash
# Windows - Find process using port 3000
netstat -ano | findstr :3000

# Kill the process
taskkill /PID <PID> /F
```

**Problem**: npm install fails
```bash
# Clear cache and reinstall
npm cache clean --force
rm -rf node_modules package-lock.json
npm install
```

**Problem**: CORS errors in browser console
- Verify backend is running on port 8080
- Check `SecurityConfig.java` allows `http://localhost:3000`
- Restart both servers

### Login Issues

**Problem**: "Network Error" or "Failed to fetch"
- Check backend is running: http://localhost:8080
- Check browser console for errors
- Verify API URL in `src/services/api.js` is correct

**Problem**: OTP not received
- Check email configuration in `application.yml`
- Check spam folder
- Verify email service is configured correctly
- Check backend logs for email sending errors

**Problem**: "Invalid credentials"
- Verify user exists in database
- Check user is verified (email verification completed)
- Verify password is correct

## Quick Start Commands Summary

### Terminal 1 (Backend):
```bash
cd "K:\new\AUCA\Semester 7\Web Technology and Internet\Web Tech\ImigishaLink Project\imigishalink-backend"
mvn spring-boot:run
```

### Terminal 2 (Frontend):
```bash
cd "K:\new\AUCA\Semester 7\Web Technology and Internet\Web Tech\ImigishaLink Project\ImigishaLink"
npm run dev
```

### Browser:
Open: **http://localhost:3000**

## Testing Checklist

- [ ] Backend starts without errors
- [ ] Frontend starts without errors
- [ ] Homepage loads correctly
- [ ] Can navigate to login page
- [ ] Can enter email and password
- [ ] OTP is sent to email
- [ ] Can enter OTP code
- [ ] Login completes successfully
- [ ] Redirected to dashboard
- [ ] User info appears in topbar
- [ ] Logout works correctly

## Default Test Accounts

If you used the `DataInitializer`, these accounts should exist:

1. **Admin Account**:
   - Email: `admin@imigishalink.rw`
   - Password: `Admin@123`

2. **NGO Account**:
   - Email: `ngo@test.rw`
   - Password: `Ngo@123`

3. **Donor Account**:
   - Email: `donor@test.rw`
   - Password: `Donor@123`

**Note**: These accounts need email verification before they can login. Check the database or use the verification endpoint.

## Next Steps

Once everything is running:
1. Explore the dashboard
2. Test protected routes
3. Try creating donations
4. Test NGO features
5. Explore community features

## Need Help?

- Check browser console (F12) for errors
- Check backend logs in `logs/imigishalink.log`
- Verify both servers are running
- Check network tab in browser DevTools for API calls

