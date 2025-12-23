package com.imigishalink.auth;

import com.imigishalink.common.EmailService;
import com.imigishalink.users.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class TwoFactorService {

    private final TwoFactorTokenRepository tokenRepository;
    private final EmailService emailService;
    private final Random random = new Random();

    @Value("${app.security.two-factor.expiration-minutes:10}")
    private int expirationMinutes;

    @Transactional
    public TwoFactorToken createEmailChallenge(User user) {
        return createEmailChallenge(user, user.getEmail());
    }

    @Transactional
    public TwoFactorToken createEmailChallenge(User user, String emailToSend) {
        String code = String.format("%06d", random.nextInt(1_000_000));

        // Invalidate previous unconsumed tokens for the user
        tokenRepository.findTopByUserAndConsumedFalseOrderByCreatedAtDesc(user)
                .ifPresent(existing -> {
                    existing.setConsumed(true);
                    tokenRepository.save(existing);
                });

        TwoFactorToken token = new TwoFactorToken();
        token.setUser(user);
        token.setCode(code);
        token.setExpiresAt(LocalDateTime.now().plusMinutes(expirationMinutes));
        token.setConsumed(false);
        token.setType(TwoFactorToken.TwoFactorType.EMAIL);

        TwoFactorToken saved = tokenRepository.save(token);
        
        // Log the code prominently for development/testing
        log.info("═══════════════════════════════════════════════════════════");
        log.info("🔐 OTP CODE FOR {}: {}", emailToSend, code);
        log.info("═══════════════════════════════════════════════════════════");
        log.info("If email is not configured, use this code to login.");
        log.info("Code expires in {} minutes.", expirationMinutes);
        
        // Try to send email (may fail if not configured)
        sendEmailOtp(user, code, emailToSend);
        
        return saved;
    }

    @Transactional
    public User verifyEmailCode(long challengeId, String code) {
        TwoFactorToken token = tokenRepository.findById(challengeId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid challenge id"));

        if (token.isConsumed() || token.isExpired()) {
            throw new IllegalArgumentException("Code expired or already used");
        }

        // Compare codes (trim and case-insensitive for better UX)
        String tokenCode = token.getCode() != null ? token.getCode().trim() : "";
        String providedCode = code != null ? code.trim() : "";
        
        if (!tokenCode.equals(providedCode)) {
            log.warn("OTP mismatch for challenge {}: expected '{}', got '{}'", challengeId, tokenCode, providedCode);
            throw new IllegalArgumentException("Invalid verification code");
        }

        token.setConsumed(true);
        tokenRepository.save(token);
        
        // Return the user - the caller should refresh it from repository to avoid lazy loading
        return token.getUser();
    }

    private void sendEmailOtp(User user, String code) {
        sendEmailOtp(user, code, user.getEmail());
    }

    private void sendEmailOtp(User user, String code, String emailToSend) {
        String subject = "Your ImigishaLink Login Verification Code";
        String body = String.format(
            "Hi %s,\n\n" +
            "Your one-time login verification code is: %s\n\n" +
            "This code will expire in %d minutes.\n\n" +
            "If you did not attempt to log in, please ignore this message.\n\n" +
            "Best regards,\n" +
            "ImigishaLink Team",
            user.getFirstName() != null ? user.getFirstName() : "User",
            code,
            expirationMinutes
        );
        
        try {
            log.info("Attempting to send OTP email to: {}", emailToSend);
            emailService.sendEmail(emailToSend, subject, body);
            log.info("✅ OTP email sent successfully to: {}", emailToSend);
            log.info("Please check your inbox (and spam folder) for the verification code.");
        } catch (Exception ex) {
            log.error("❌ FAILED TO SEND EMAIL to {}: {}", emailToSend, ex.getMessage());
            log.error("═══════════════════════════════════════════════════════════");
            log.error("EMAIL NOT SENT! Use the OTP code shown above: {}", code);
            log.error("═══════════════════════════════════════════════════════════");
            log.error("To fix: Set MAIL_USERNAME and MAIL_PASSWORD environment variables");
            log.error("For Gmail: Use App Password (not regular password)");
            // Don't throw - allow the challenge to be created even if email fails
            // The code is logged prominently above, so user can still test
        }
    }
}
