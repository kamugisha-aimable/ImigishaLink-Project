package com.imigishalink.auth;

import com.imigishalink.common.BaseEntity;
import com.imigishalink.users.User;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "two_factor_tokens")
@Getter
@Setter
public class TwoFactorToken extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String code;

    private LocalDateTime expiresAt;

    private boolean consumed = false;

    @Enumerated(EnumType.STRING)
    private TwoFactorType type = TwoFactorType.EMAIL;

    public boolean isExpired() {
        return expiresAt != null && expiresAt.isBefore(LocalDateTime.now());
    }

    public enum TwoFactorType {
        EMAIL,
        TOTP
    }
}

