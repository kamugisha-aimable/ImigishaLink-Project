package com.imigishalink.auth;

import com.imigishalink.common.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginOTPService {

    private final LoginOTPRepository otpRepository;
    private final EmailService emailService;
    private final Random random = new Random();

    @Value("${app.security.otp.expiration-minutes:5}")
    private int expirationMinutes;

    /**
     * Generate and send 6-digit OTP to email
     */
    @Transactional
    public LoginOTP generateAndSendOTP(String email) {
        // Invalidate all previous unused OTPs for this email
        otpRepository.markAllAsUsedByEmail(email);

        // Generate 6-digit OTP
        String otp = String.format("%06d", random.nextInt(1_000_000));

        // Create OTP entity
        LoginOTP loginOTP = new LoginOTP();
        loginOTP.setEmail(email);
        loginOTP.setOtp(otp);
        loginOTP.setExpiresAt(LocalDateTime.now().plusMinutes(expirationMinutes));
        loginOTP.setUsed(false);

        LoginOTP saved = otpRepository.save(loginOTP);

        // Log OTP for development/testing
        log.info("═══════════════════════════════════════════════════════════");
        log.info("🔐 OTP CODE FOR {}: {}", email, otp);
        log.info("═══════════════════════════════════════════════════════════");
        log.info("Code expires in {} minutes.", expirationMinutes);

        // Send email
        sendOTPEmail(email, otp);

        return saved;
    }

    /**
     * Verify OTP code
     */
    @Transactional
    public boolean verifyOTP(String email, String otp) {
        Optional<LoginOTP> loginOTP = otpRepository.findByEmailAndOtpAndUsedFalse(email, otp);

        if (loginOTP.isEmpty()) {
            log.warn("Invalid OTP attempt for email: {}", email);
            return false;
        }

        LoginOTP otpEntity = loginOTP.get();

        // Check if expired
        if (otpEntity.isExpired()) {
            log.warn("Expired OTP attempt for email: {}", email);
            otpEntity.setUsed(true);
            otpRepository.save(otpEntity);
            return false;
        }

        // Mark as used
        otpEntity.setUsed(true);
        otpRepository.save(otpEntity);

        log.info("✅ OTP verified successfully for email: {}", email);
        return true;
    }

    /**
     * Get latest valid OTP for email (for resend functionality)
     */
    public Optional<LoginOTP> getLatestValidOTP(String email) {
        return otpRepository.findLatestValidByEmail(email, LocalDateTime.now());
    }

    /**
     * Send OTP email to the specified email address
     */
    private void sendOTPEmail(String email, String otp) {
        String subject = "ImigishaLink Login OTP";
        String body = String.format(
            "Your ImigishaLink login verification code is: %s\n\n" +
            "This code will expire in %d minutes.\n\n" +
            "If you did not attempt to log in, please ignore this message.\n\n" +
            "Best regards,\n" +
            "ImigishaLink Team",
            otp,
            expirationMinutes
        );

        try {
            log.info("═══════════════════════════════════════════════════════════");
            log.info("📧 SENDING OTP EMAIL TO: {}", email);
            log.info("═══════════════════════════════════════════════════════════");
            
            emailService.sendEmail(email, subject, body);
            
            log.info("✅ OTP email sent successfully to: {}", email);
            log.info("Please check inbox and spam folder for the code.");
            log.info("═══════════════════════════════════════════════════════════");
        } catch (Exception ex) {
            log.error("═══════════════════════════════════════════════════════════");
            log.error("❌ FAILED TO SEND EMAIL to {}: {}", email, ex.getMessage());
            log.error("═══════════════════════════════════════════════════════════");
            log.error("EMAIL NOT SENT! Use the OTP code shown above: {}", otp);
            log.error("Check email configuration: MAIL_USERNAME and MAIL_PASSWORD");
            log.error("═══════════════════════════════════════════════════════════");
            // Don't throw - OTP is still created and logged for testing
        }
    }
}

