# User Roles and Access Guide

## User Roles

The system supports three user roles:

1. **USER** (Donor/Regular User)
   - Can create donation requests
   - Can contribute to donations
   - Can join communities
   - Default role for new registrations

2. **NGO** (NGO Member)
   - All USER permissions
   - Can create and manage NGO profiles
   - Can receive donations
   - Can be assigned as NGO admin

3. **ADMIN** (Administrator)
   - Full system access
   - Can verify users
   - Can create other admin accounts
   - Can manage all resources
   - Cannot be created through registration (must be created by existing admin)

## Registration Process

### Regular Users (USER or NGO)

1. Go to `/signup`
2. Fill in registration form
3. Select account type:
   - **Donor / Regular User** → Creates USER role
   - **NGO Member** → Creates NGO role
4. Submit form
5. **Email Verification Required**:
   - User receives verification code via email
   - Must verify email before login
   - Verification code is valid for 24 hours

### Admin Users

Admin accounts **cannot** be created through registration. They must be created by an existing admin using:
- Admin panel (if available)
- Direct database access
- Admin API endpoint: `POST /api/v1/auth/admin/create-admin` (requires admin authentication)

## Login Process

### Step 1: Email & Password
- Enter email and password
- System validates credentials
- **Note**: Users must be verified before login (except admins)

### Step 2: OTP Verification (2FA)
- System sends 6-digit OTP to user's email
- Enter OTP code
- Complete login and receive JWT tokens

### Admin Login
- Admins can login even if not verified (for initial setup)
- Still requires 2FA OTP verification

## Email Verification

### Method 1: Via Email Link
1. Check email after registration
2. Click verification link
3. Automatically verified

### Method 2: Via Verification Code
1. Check email for 6-digit code
2. Go to `/verify-email?code=XXXXXX` or enter code manually
3. Code expires after 24 hours

### Method 3: Admin Verification
- Admin can verify users manually via:
  - `POST /api/v1/auth/admin/verify-user?email=user@example.com`
  - Requires admin authentication

## Pre-created Test Accounts

The system creates these accounts on startup (if they don't exist):

### Admin Account
- **Email**: `admin@imigishalink.rw`
- **Password**: `Admin@123`
- **Role**: ADMIN
- **Status**: Verified (can login immediately)

### NGO Account
- **Email**: `ngo@test.rw`
- **Password**: `Ngo@123`
- **Role**: NGO
- **Status**: Verified (can login immediately)

### Donor Account
- **Email**: `donor@test.rw`
- **Password**: `Donor@123`
- **Role**: USER
- **Status**: Verified (can login immediately)

## Troubleshooting

### "Please verify your email before logging in"
- **Solution**: Verify your email using one of the methods above
- Check spam folder for verification email
- Request admin to verify your account manually

### "Admin accounts cannot be created through registration"
- **Solution**: Contact existing admin to create admin account
- Or use pre-created admin account: `admin@imigishalink.rw`

### Can't receive verification email
- Check spam folder
- Verify email address is correct
- Contact admin for manual verification
- Check email service configuration in backend

## Role-Based Access

### Protected Routes
- `/dashboard` - Requires authentication (all roles)
- Admin-only routes require ADMIN role

### Public Routes
- `/` - Home page
- `/ngos` - NGOs listing
- `/donations` - Donations listing
- `/community` - Community page
- `/contact` - Contact page

### Auth Routes
- `/login` - Login page
- `/signup` - Registration page
- `/forgot-password` - Password reset
- `/verify-email` - Email verification

## API Endpoints

### Registration
```
POST /api/v1/auth/register
Body: {
  "firstName": "John",
  "lastName": "Doe",
  "email": "john@example.com",
  "password": "password123",
  "phoneNumber": "+250788123456",
  "role": "USER" // or "NGO"
}
```

### Email Verification
```
GET /api/v1/auth/verify?code=123456
```

### Admin: Verify User
```
POST /api/v1/auth/admin/verify-user?email=user@example.com
Headers: Authorization: Bearer <admin_token>
```

### Admin: Create Admin
```
POST /api/v1/auth/admin/create-admin
Headers: Authorization: Bearer <admin_token>
Body: {
  "firstName": "Admin",
  "lastName": "User",
  "email": "newadmin@example.com",
  "password": "password123",
  "phoneNumber": "+250788123456"
}
```

## Next Steps

1. **For Regular Users**: Register → Verify Email → Login
2. **For Admins**: Use pre-created account or have existing admin create one
3. **For Testing**: Use pre-created test accounts (all verified)

