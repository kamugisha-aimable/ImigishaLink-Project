package com.imigishalink.auth;

import com.imigishalink.users.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TwoFactorTokenRepository extends JpaRepository<TwoFactorToken, Long> {

    Optional<TwoFactorToken> findTopByUserAndConsumedFalseOrderByCreatedAtDesc(User user);
}

