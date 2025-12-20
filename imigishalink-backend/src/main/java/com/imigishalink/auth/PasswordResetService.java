package com.imigishalink.auth;

import com.imigishalink.common.EmailService;
import com.imigishalink.users.User;
import com.imigishalink.users.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetService {

    private final PasswordResetTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Value("${app.email.password-reset.expiration-hours:1}")
    private int expirationHours;

    @Transactional
    public void sendResetLink(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Clean previous tokens
        tokenRepository.deleteByUser(user);

        PasswordResetToken token = new PasswordResetToken();
        token.setUser(user);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiresAt(LocalDateTime.now().plusHours(expirationHours));
        token.setConsumed(false);
        tokenRepository.save(token);

        String subject = "ImigishaLink password reset";
        String body = """
                Hi %s,

                We received a request to reset your password.
                Use the token below in the app or follow the reset link:

                Token: %s
                Link: https://app.imigishalink.rw/reset-password?token=%s

                This token expires in %d hour(s).
                If you didn't request this, you can ignore this email.
                """.formatted(user.getFirstName(), token.getToken(), token.getToken(), expirationHours);

        emailService.sendEmail(user.getEmail(), subject, body);
    }

    @Transactional
    public void resetPassword(String tokenValue, String newPassword) {
        PasswordResetToken token = tokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new RuntimeException("Invalid reset token"));

        if (token.isConsumed() || token.isExpired()) {
            throw new RuntimeException("Token expired or already used");
        }

        User user = token.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        token.setConsumed(true);
        tokenRepository.save(token);
    }
}

