package com.imigishalink.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String mailUsername;

    @Value("${spring.mail.password}")
    private String mailPassword;

    public void sendEmail(String to, String subject, String body) {
        if (!StringUtils.hasText(to)) {
            log.warn("Skipped sending email because recipient was empty. subject={}", subject);
            return;
        }

        // Email is configured in application.yml
        log.info("Email service configured. From: {}", mailUsername);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailUsername); // Set from address
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        
        try {
            log.info("Attempting to send email to: {}", to);
            log.debug("Email from: {}, subject: {}", mailUsername, subject);
            mailSender.send(message);
            log.info("✓ Email sent successfully to: {}", to);
        } catch (Exception ex) {
            log.error("✗ Failed to send email to {}: {}", to, ex.getMessage());
            log.error("Error details: ", ex);
            
            // Provide helpful error messages
            if (ex.getMessage() != null) {
                if (ex.getMessage().contains("Authentication failed")) {
                    log.error("Email authentication failed. Check your MAIL_USERNAME and MAIL_PASSWORD.");
                    log.error("For Gmail, make sure you're using an App Password, not your regular password.");
                } else if (ex.getMessage().contains("Connection")) {
                    log.error("Cannot connect to email server. Check your internet connection and SMTP settings.");
                }
            }
            
            // Re-throw to allow caller to handle the error
            throw new RuntimeException("Failed to send email: " + ex.getMessage(), ex);
        }
    }
}

