package com.imigishalink.auth;

import com.imigishalink.common.ApiResponse;
import com.imigishalink.config.JwtService;
import com.imigishalink.users.User;
import com.imigishalink.users.UserRepository;
import com.imigishalink.users.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * OTP-based Authentication Controller
 * Implements 6-digit OTP login flow
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class OTPAuthController {

    private static final String BEARER_TOKEN_TYPE = "Bearer";

    private final UserRepository userRepository;
    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final LoginOTPService loginOTPService;

    /**
     * POST /api/auth/login
     * Step 1: Validate email + password (BCrypt), then send 6-digit OTP
     * 
     * Request body:
     * {
     *   "email": "user@example.com",      // Login email (required)
     *   "password": "password",            // Login password (required)
     *   "otpEmail": "recipient@email.com" // Email to receive OTP (optional, defaults to login email)
     * }
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, String>>> login(@Valid @RequestBody LoginRequest request) {
        // Step 1: Authenticate credentials using BCrypt
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()));

        User user = (User) authentication.getPrincipal();

        // Step 2: Determine email address to send OTP to
        // Use otpEmail if provided, otherwise use login email
        String emailToSend = request.getOtpEmail() != null && !request.getOtpEmail().trim().isEmpty() 
                ? request.getOtpEmail().trim().toLowerCase() 
                : user.getEmail().toLowerCase();
        
        // Validate email format
        if (!emailToSend.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new RuntimeException("Invalid email address format: " + emailToSend);
        }
        
        // Generate and send 6-digit OTP to the specified email
        loginOTPService.generateAndSendOTP(emailToSend);

        Map<String, String> response = new HashMap<>();
        response.put("message", "OTP sent successfully to " + emailToSend);
        response.put("email", emailToSend);
        response.put("loginEmail", user.getEmail());

        return ResponseEntity.ok(ApiResponse.success("OTP sent successfully", response));
    }

    /**
     * POST /api/auth/login/verify-otp
     * Step 2: Verify 6-digit OTP and complete login (returns JWT)
     */
    @PostMapping("/login/verify-otp")
    @Transactional
    public ResponseEntity<ApiResponse<AuthResponse>> verifyOTP(@Valid @RequestBody LoginOTPRequest request) {
        // Verify OTP (6 digits, not expired, not used)
        // Use the email where OTP was sent (request.getEmail())
        boolean isValid = loginOTPService.verifyOTP(request.getEmail(), request.getOtp());
        
        if (!isValid) {
            throw new RuntimeException("Invalid or expired OTP");
        }

        // Get user and generate JWT tokens
        // CRITICAL: Use loginEmail if provided (for user lookup), otherwise use OTP email
        // This allows OTP to be sent to a different email than the login email
        String userEmail = (request.getLoginEmail() != null && !request.getLoginEmail().trim().isEmpty())
                ? request.getLoginEmail().trim().toLowerCase()
                : request.getEmail().trim().toLowerCase();
        
        // Use findById after finding by email to ensure proper entity loading
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));

        // Refresh user to avoid lazy loading issues
        User refreshedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // CRITICAL FIX: Check if user email matches an NGO email and update role if needed
        // This ensures users registered with NGO email get NGO role
        if (refreshedUser.getRole() != com.imigishalink.users.Role.NGO) {
            boolean hasNGOWithMatchingEmail = userService.hasNGOWithMatchingEmail(refreshedUser.getEmail());
            if (hasNGOWithMatchingEmail) {
                refreshedUser.setRole(com.imigishalink.users.Role.NGO);
                refreshedUser = userRepository.save(refreshedUser);
                System.out.println("CRITICAL: Updated user " + refreshedUser.getEmail() + " role to NGO (matched NGO email)");
            }
        }
        
        // Automatically link NGO users to their NGO on login
        userService.ensureNGOUserLinked(refreshedUser);
        
        // Refresh again after linking to get updated relationships
        refreshedUser = userRepository.findById(refreshedUser.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String jwtToken = jwtService.generateToken(refreshedUser);
        String refreshToken = jwtService.generateRefreshToken(refreshedUser);

        AuthResponse authResponse = AuthResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .tokenType(BEARER_TOKEN_TYPE)
                .expiresIn(3600) // 1 hour
                .user(mapToUserResponse(refreshedUser))
                .build();

        return ResponseEntity.ok(ApiResponse.success("Login successful", authResponse));
    }

    private Map<String, Object> mapToUserResponse(User user) {
        Map<String, Object> userResponse = new HashMap<>();
        userResponse.put("id", user.getId());
        userResponse.put("firstName", user.getFirstName());
        userResponse.put("lastName", user.getLastName());
        userResponse.put("email", user.getEmail());
        userResponse.put("phoneNumber", user.getPhoneNumber());
        // CRITICAL: Ensure role is returned as string (enum name) for consistent frontend handling
        // The role MUST match exactly: "NGO", "ADMIN", or "USER" (uppercase)
        String roleName = user.getRole() != null ? user.getRole().name() : "USER";
        userResponse.put("role", roleName);
        userResponse.put("isVerified", user.isVerified());
        userResponse.put("profileImageUrl", user.getProfileImageUrl());
        
        // Log role for debugging
        System.out.println("OTPAuthController - Returning user with role: " + roleName + " for email: " + user.getEmail());
        
        return userResponse;
    }
}

