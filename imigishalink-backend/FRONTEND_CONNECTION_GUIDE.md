# Frontend-Backend Connection Guide

## Backend Configuration

**Base URL:** `http://localhost:8080`  
**API Base Path:** `/api/v1`  
**CORS:** Configured for `http://localhost:3000` and `http://localhost:3001`

## 1. Frontend Environment Setup

### Create `.env` file in your frontend project:

```env
REACT_APP_API_BASE_URL=http://localhost:8080/api/v1
REACT_APP_BACKEND_URL=http://localhost:8080
```

## 2. API Service Setup (React/JavaScript Example)

### Create `src/services/api.js`:

```javascript
const API_BASE_URL = process.env.REACT_APP_API_BASE_URL || 'http://localhost:8080/api/v1';

class ApiService {
  constructor() {
    this.baseURL = API_BASE_URL;
  }

  // Get auth token from localStorage
  getAuthToken() {
    return localStorage.getItem('accessToken');
  }

  // Set auth tokens
  setAuthTokens(accessToken, refreshToken) {
    localStorage.setItem('accessToken', accessToken);
    localStorage.setItem('refreshToken', refreshToken);
  }

  // Clear auth tokens
  clearAuthTokens() {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
  }

  // Make authenticated request
  async request(endpoint, options = {}) {
    const url = `${this.baseURL}${endpoint}`;
    const token = this.getAuthToken();

    const config = {
      ...options,
      headers: {
        'Content-Type': 'application/json',
        ...(token && { Authorization: `Bearer ${token}` }),
        ...options.headers,
      },
    };

    try {
      const response = await fetch(url, config);
      
      // Handle 401 Unauthorized - token expired
      if (response.status === 401) {
        this.clearAuthTokens();
        window.location.href = '/login';
        throw new Error('Unauthorized - Please login again');
      }

      const data = await response.json();
      
      if (!response.ok) {
        throw new Error(data.message || 'Request failed');
      }

      return data;
    } catch (error) {
      console.error('API Error:', error);
      throw error;
    }
  }

  // GET request
  async get(endpoint, params = {}) {
    const queryString = new URLSearchParams(params).toString();
    const url = queryString ? `${endpoint}?${queryString}` : endpoint;
    return this.request(url, { method: 'GET' });
  }

  // POST request
  async post(endpoint, data) {
    return this.request(endpoint, {
      method: 'POST',
      body: JSON.stringify(data),
    });
  }

  // PUT request
  async put(endpoint, data) {
    return this.request(endpoint, {
      method: 'PUT',
      body: JSON.stringify(data),
    });
  }

  // DELETE request
  async delete(endpoint) {
    return this.request(endpoint, { method: 'DELETE' });
  }

  // PATCH request
  async patch(endpoint, data) {
    return this.request(endpoint, {
      method: 'PATCH',
      body: JSON.stringify(data),
    });
  }
}

export default new ApiService();
```

## 3. Authentication Service

### Create `src/services/authService.js`:

```javascript
import apiService from './api';

class AuthService {
  // Register new user
  async register(userData) {
    const response = await apiService.post('/auth/register', {
      firstName: userData.firstName,
      lastName: userData.lastName,
      email: userData.email,
      password: userData.password,
      phoneNumber: userData.phoneNumber,
    });
    
    if (response.success && response.data?.accessToken) {
      apiService.setAuthTokens(
        response.data.accessToken,
        response.data.refreshToken
      );
    }
    
    return response;
  }

  // Login Step 1: Password authentication (returns challengeId for 2FA)
  async loginStep1(email, password) {
    const response = await apiService.post('/auth/login', {
      email,
      password,
    });
    
    // Returns: { success: true, data: { challengeId: 123, message: "..." } }
    return response;
  }

  // Login Step 2: Verify OTP (completes login, returns tokens)
  async loginStep2(challengeId, code) {
    const response = await apiService.post('/auth/login/verify-otp', {
      challengeId,
      code,
    });
    
    if (response.success && response.data?.accessToken) {
      apiService.setAuthTokens(
        response.data.accessToken,
        response.data.refreshToken
      );
    }
    
    return response;
  }

  // Complete login flow (handles 2FA)
  async login(email, password, otpCode) {
    try {
      // Step 1: Authenticate password
      const step1Response = await this.loginStep1(email, password);
      
      if (!step1Response.success) {
        return step1Response;
      }

      // Step 2: Verify OTP if provided
      if (otpCode) {
        const challengeId = step1Response.data.challengeId;
        return await this.loginStep2(challengeId, otpCode);
      }

      // Return step 1 response (needs OTP)
      return step1Response;
    } catch (error) {
      throw error;
    }
  }

  // Forgot password
  async forgotPassword(email) {
    return apiService.post('/auth/forgot-password', {
      email,
    });
  }

  // Reset password
  async resetPassword(token, newPassword) {
    return apiService.post('/auth/reset-password', {
      token,
      newPassword,
    });
  }

  // Refresh token
  async refreshToken() {
    const refreshToken = localStorage.getItem('refreshToken');
    if (!refreshToken) {
      throw new Error('No refresh token available');
    }

    const response = await apiService.post('/auth/refresh', {
      refreshToken,
    });

    if (response.success && response.data?.accessToken) {
      apiService.setAuthTokens(
        response.data.accessToken,
        response.data.refreshToken
      );
    }

    return response;
  }

  // Logout
  logout() {
    apiService.clearAuthTokens();
  }

  // Check if user is authenticated
  isAuthenticated() {
    return !!apiService.getAuthToken();
  }

  // Get current user from token (decode JWT)
  getCurrentUser() {
    const token = apiService.getAuthToken();
    if (!token) return null;

    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload;
    } catch (error) {
      return null;
    }
  }
}

export default new AuthService();
```

## 4. Example: Login Component (React)

```javascript
import React, { useState } from 'react';
import authService from '../services/authService';

function Login() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [otpCode, setOtpCode] = useState('');
  const [needsOtp, setNeedsOtp] = useState(false);
  const [challengeId, setChallengeId] = useState(null);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleLogin = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      if (!needsOtp) {
        // Step 1: Password authentication
        const response = await authService.loginStep1(email, password);
        
        if (response.success) {
          setNeedsOtp(true);
          setChallengeId(response.data.challengeId);
          // OTP sent to email
        } else {
          setError(response.message || 'Login failed');
        }
      } else {
        // Step 2: Verify OTP
        const response = await authService.loginStep2(challengeId, otpCode);
        
        if (response.success) {
          // Login successful, redirect to dashboard
          window.location.href = '/dashboard';
        } else {
          setError(response.message || 'Invalid OTP code');
        }
      }
    } catch (err) {
      setError(err.message || 'An error occurred');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-container">
      <form onSubmit={handleLogin}>
        <h2>Login</h2>
        
        {error && <div className="error">{error}</div>}
        
        <input
          type="email"
          placeholder="Email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
          disabled={needsOtp}
        />
        
        <input
          type="password"
          placeholder="Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
          disabled={needsOtp}
        />
        
        {needsOtp && (
          <div>
            <p>Enter the OTP code sent to your email:</p>
            <input
              type="text"
              placeholder="OTP Code"
              value={otpCode}
              onChange={(e) => setOtpCode(e.target.value)}
              required
              maxLength={6}
            />
          </div>
        )}
        
        <button type="submit" disabled={loading}>
          {loading ? 'Loading...' : needsOtp ? 'Verify OTP' : 'Login'}
        </button>
      </form>
    </div>
  );
}

export default Login;
```

## 5. Example: Making Authenticated API Calls

```javascript
import apiService from '../services/api';

// Get all donations
async function getDonations(page = 0, size = 10) {
  try {
    const response = await apiService.get('/donations', { page, size });
    return response.data; // { content: [...], totalElements: 100, ... }
  } catch (error) {
    console.error('Failed to fetch donations:', error);
    throw error;
  }
}

// Create a donation (requires authentication)
async function createDonation(donationData) {
  try {
    const response = await apiService.post('/donations', donationData);
    return response.data;
  } catch (error) {
    console.error('Failed to create donation:', error);
    throw error;
  }
}

// Search donations
async function searchDonations(query, page = 0, size = 10) {
  try {
    const response = await apiService.get('/search/donations', {
      search: query,
      page,
      size,
    });
    return response.data;
  } catch (error) {
    console.error('Search failed:', error);
    throw error;
  }
}
```

## 6. API Endpoints Reference

### Authentication (`/api/v1/auth`)
- `POST /register` - Register new user
- `POST /login` - Login step 1 (password auth, returns challengeId)
- `POST /login/verify-otp` - Login step 2 (verify OTP, returns tokens)
- `POST /refresh` - Refresh access token
- `POST /forgot-password` - Request password reset
- `POST /reset-password` - Reset password with token
- `GET /verify?code=xxx` - Verify email

### Donations (`/api/v1/donations`)
- `GET /donations` - Get all donations (with filters)
- `GET /donations/{id}` - Get donation by ID
- `POST /donations` - Create donation (authenticated)
- `PUT /donations/{id}` - Update donation (authenticated)
- `DELETE /donations/{id}` - Delete donation (authenticated)

### Search (`/api/v1/search`)
- `GET /search/all?query=xxx` - Search all entities
- `POST /search/advanced` - Advanced search
- `GET /search/donations` - Search donations
- `GET /search/ngos` - Search NGOs
- `GET /search/suggestions?query=xxx` - Get search suggestions

### Categories (`/api/v1/categories`)
- `GET /categories` - Get all categories
- `GET /categories/{id}` - Get category by ID
- `POST /categories` - Create category (ADMIN only)
- `PUT /categories/{id}` - Update category (ADMIN only)

### Users (`/api/v1/users`)
- `GET /users` - Get all users (with filters)
- `GET /users/{id}` - Get user by ID
- `PUT /users/{id}` - Update user (authenticated)
- `GET /users/{id}/followers` - Get user followers
- `GET /users/{id}/following` - Get user following

### Messages (`/api/v1/messages`)
- `GET /messages/conversation/{userId}` - Get conversation
- `GET /messages/unread` - Get unread messages
- `POST /messages` - Send message (authenticated)

## 7. Response Format

All API responses follow this format:

```json
{
  "success": true,
  "message": "Operation successful",
  "data": { ... }
}
```

Error responses:
```json
{
  "success": false,
  "message": "Error message",
  "data": null
}
```

## 8. Testing the Connection

### Test Backend is Running:
```bash
curl http://localhost:8080/api/v1/categories
```

### Test CORS (from browser console):
```javascript
fetch('http://localhost:8080/api/v1/categories')
  .then(r => r.json())
  .then(console.log)
  .catch(console.error);
```

## 9. Common Issues & Solutions

### Issue: CORS Error
**Solution:** Ensure your frontend URL matches the CORS configuration:
- Backend allows: `http://localhost:3000` and `http://localhost:3001`
- Update `SecurityConfig.java` if using different ports

### Issue: 401 Unauthorized
**Solution:** 
- Check if token is being sent: `Authorization: Bearer <token>`
- Token might be expired - implement token refresh
- User might not be logged in

### Issue: Network Error
**Solution:**
- Verify backend is running on port 8080
- Check firewall settings
- Ensure CORS is properly configured

## 10. Security Best Practices

1. **Store tokens securely**: Use `httpOnly` cookies in production (not localStorage)
2. **Implement token refresh**: Automatically refresh tokens before expiration
3. **Handle errors gracefully**: Show user-friendly error messages
4. **Validate inputs**: Validate data before sending to API
5. **Use HTTPS in production**: Never send credentials over HTTP

## Quick Start Checklist

- [ ] Backend running on `http://localhost:8080`
- [ ] Frontend running on `http://localhost:3000` or `3001`
- [ ] Create `.env` file with API URL
- [ ] Set up `apiService.js` for HTTP requests
- [ ] Set up `authService.js` for authentication
- [ ] Implement login flow with 2FA
- [ ] Test API connection with a simple GET request
- [ ] Add token to all authenticated requests

