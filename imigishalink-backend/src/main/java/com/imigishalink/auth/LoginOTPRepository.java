package com.imigishalink.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface LoginOTPRepository extends JpaRepository<LoginOTP, Long> {

    Optional<LoginOTP> findByEmailAndOtpAndUsedFalse(String email, String otp);

    @Query("SELECT l FROM LoginOTP l WHERE l.email = :email AND l.used = false AND l.expiresAt > :now ORDER BY l.createdAt DESC")
    Optional<LoginOTP> findLatestValidByEmail(@Param("email") String email, @Param("now") LocalDateTime now);

    @Modifying
    @Query("UPDATE LoginOTP l SET l.used = true WHERE l.email = :email AND l.used = false")
    void markAllAsUsedByEmail(@Param("email") String email);

    @Modifying
    @Query("DELETE FROM LoginOTP l WHERE l.expiresAt < :now")
    void deleteExpiredOTPs(@Param("now") LocalDateTime now);
}

