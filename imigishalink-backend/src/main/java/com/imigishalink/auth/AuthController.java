package com.imigishalink.auth;

import com.imigishalink.common.ApiResponse;
import com.imigishalink.config.JwtService;
import com.imigishalink.location.Location;
import com.imigishalink.location.LocationRepository;
import com.imigishalink.users.Role;
import com.imigishalink.users.User;
import com.imigishalink.users.UserRepository;
import com.imigishalink.users.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final String BEARER_TOKEN_TYPE = "Bearer";

    private final UserService userService;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TwoFactorService twoFactorService;
    private final PasswordResetService passwordResetService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        // Validate role - only USER and NGO can self-register, ADMIN must be created by
        // existing admin
        Role requestedRole = request.getRole() != null ? request.getRole() : Role.USER;
        if (requestedRole == Role.ADMIN) {
            throw new RuntimeException("Admin accounts cannot be created through registration");
        }

        // Create user from request
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(request.getPassword())
                .phoneNumber(request.getPhoneNumber())
                .role(requestedRole)
                .build();

        // Set location if provided
        if (request.getLocationId() != null) {
            Location location = locationRepository.findById(request.getLocationId())
                    .orElseThrow(() -> new RuntimeException("Location not found with ID: " + request.getLocationId()));
            user.setLocation(location);
        } else if (request.getProvince() != null && request.getDistrict() != null && request.getSector() != null) {
            // Try to find or create location from province, district, sector
            Location location = userService.findOrCreateLocation(
                    request.getProvince(),
                    request.getDistrict(),
                    request.getSector(),
                    request.getCell(),
                    request.getVillage()
            );
            user.setLocation(location);
        }

        // Register user
        User registeredUser = userService.registerUser(user);

        // Generate token (user can use it after email verification)
        String jwtToken = jwtService.generateToken(registeredUser);
        String refreshToken = jwtService.generateRefreshToken(registeredUser);

        AuthResponse authResponse = AuthResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .tokenType(BEARER_TOKEN_TYPE)
                .expiresIn(3600) // 1 hour
                .user(mapToUserResponse(registeredUser))
                .build();

        return ResponseEntity
                .ok(ApiResponse.success("Registration successful. You can now login.", authResponse));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginStepOneResponse>> login(@Valid @RequestBody LoginRequest request) {
        // Step 1: Authenticate credentials
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()));

        User user = (User) authentication.getPrincipal();

        // Step 2: Create 2FA challenge and send OTP via email
        // Use otpEmail if provided, otherwise use user's registered email
        String emailToSend = request.getOtpEmail() != null && !request.getOtpEmail().trim().isEmpty() 
                ? request.getOtpEmail().trim() 
                : user.getEmail();
        
        // Create 2FA challenge and send OTP via email
        // Email sending errors are handled in TwoFactorService, challenge is still created
        TwoFactorToken challenge = twoFactorService.createEmailChallenge(user, emailToSend);

        LoginStepOneResponse response = LoginStepOneResponse.builder()
                .challengeId(challenge.getId())
                .message("Verification code sent to " + emailToSend + ". Please check your inbox.")
                .build();

        return ResponseEntity.ok(ApiResponse.success("Verification code sent to your email", response));
    }

    // Admin endpoint to verify users manually
    @PostMapping("/admin/verify-user")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> adminVerifyUser(@RequestParam String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setVerified(true);
        user.setVerificationCode(null);
        user.setVerificationCodeExpiry(null);
        userRepository.save(user);

        return ResponseEntity.ok(ApiResponse.success("User verified successfully", null));
    }

    // Admin endpoint to create admin users
    @PostMapping("/admin/create-admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AuthResponse>> createAdmin(@Valid @RequestBody RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        User admin = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(request.getPassword())
                .phoneNumber(request.getPhoneNumber())
                .role(Role.ADMIN)
                .isVerified(true) // Admin is auto-verified
                .build();

        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        User savedAdmin = userRepository.save(admin);

        String jwtToken = jwtService.generateToken(savedAdmin);
        String refreshToken = jwtService.generateRefreshToken(savedAdmin);

        AuthResponse authResponse = AuthResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .tokenType(BEARER_TOKEN_TYPE)
                .expiresIn(3600)
                .user(mapToUserResponse(savedAdmin))
                .build();

        return ResponseEntity.ok(ApiResponse.success("Admin user created successfully", authResponse));
    }

    @PostMapping("/login/verify-otp")
    @Transactional
    public ResponseEntity<ApiResponse<AuthResponse>> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
        // Verify using TwoFactorService (old flow)
        User user = twoFactorService.verifyEmailCode(request.getChallengeId(), request.getCode());

        // Refresh user from database to avoid lazy loading issues
        // This ensures all lazy-loaded relationships are properly initialized
        User refreshedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Automatically link NGO users to their NGO on login
        userService.ensureNGOUserLinked(refreshedUser);
        
        // Refresh again after linking to get updated relationships
        refreshedUser = userRepository.findById(refreshedUser.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Initialize any lazy collections if needed (optional, but helps avoid issues)
        if (refreshedUser.getDonations() != null) {
            refreshedUser.getDonations().size(); // Force initialization
        }

        String jwtToken = jwtService.generateToken(refreshedUser);
        String refreshToken = jwtService.generateRefreshToken(refreshedUser);

        AuthResponse authResponse = AuthResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .tokenType(BEARER_TOKEN_TYPE)
                .expiresIn(3600)
                .user(mapToUserResponse(refreshedUser))
                .build();

        return ResponseEntity.ok(ApiResponse.success("Login successful", authResponse));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@RequestBody RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        // Validate refresh token
        String userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail == null) {
            throw new RuntimeException("Invalid refresh token");
        }

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!jwtService.isTokenValid(refreshToken, user)) {
            throw new RuntimeException("Invalid refresh token");
        }

        // Generate new tokens
        String newAccessToken = jwtService.generateToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        AuthResponse authResponse = AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType(BEARER_TOKEN_TYPE)
                .expiresIn(3600)
                .user(mapToUserResponse(user))
                .build();

        return ResponseEntity.ok(ApiResponse.success("Token refreshed successfully", authResponse));
    }

    @GetMapping("/verify")
    public ResponseEntity<ApiResponse<Void>> verifyEmail(@RequestParam String code) {
        boolean verified = userService.verifyUser(code);

        if (verified) {
            return ResponseEntity.ok(ApiResponse.success("Email verified successfully", null));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.error("Invalid or expired verification code"));
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        passwordResetService.sendResetLink(request.getEmail());
        return ResponseEntity.ok(ApiResponse.success("Reset password instructions sent to email", null));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        passwordResetService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok(ApiResponse.success("Password reset successfully", null));
    }

    private Map<String, Object> mapToUserResponse(User user) {
        Map<String, Object> userResponse = new HashMap<>();
        userResponse.put("id", user.getId());
        userResponse.put("firstName", user.getFirstName());
        userResponse.put("lastName", user.getLastName());
        userResponse.put("email", user.getEmail());
        userResponse.put("phoneNumber", user.getPhoneNumber());
        // Ensure role is returned as string (enum name) for consistent frontend handling
        userResponse.put("role", user.getRole() != null ? user.getRole().name() : "USER");
        userResponse.put("isVerified", user.isVerified());
        userResponse.put("profileImageUrl", user.getProfileImageUrl());
        return userResponse;
    }
}