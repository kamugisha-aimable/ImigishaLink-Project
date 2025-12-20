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
        log.info("DEV MODE - OTP Code for {}: {}", user.getEmail(), code);
        sendEmailOtp(user, code);
        return saved;
    }

    @Transactional
    public User verifyEmailCode(long challengeId, String code) {
        TwoFactorToken token = tokenRepository.findById(challengeId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid challenge id"));

        if (token.isConsumed() || token.isExpired()) {
            throw new IllegalArgumentException("Code expired or already used");
        }

        if (!token.getCode().equals(code)) {
            throw new IllegalArgumentException("Invalid verification code");
        }

        token.setConsumed(true);
        tokenRepository.save(token);
        return token.getUser();
    }

    private void sendEmailOtp(User user, String code) {
        String subject = "Your ImigishaLink login verification code";
        String body = """
                Hi %s,

                Your one-time login code is: %s
                It expires in %d minutes.

                If you did not attempt to log in, you can ignore this message.
                """.formatted(user.getFirstName(), code, expirationMinutes);
        emailService.sendEmail(user.getEmail(), subject, body);
    }
}
