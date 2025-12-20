package com.imigishalink.donations;

import com.imigishalink.common.BaseEntity;
import com.imigishalink.users.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "contributions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Contribution extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "donation_id", nullable = false)
    private Donation donation;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contributor_id", nullable = false)
    private User contributor;
    
    @Column(name = "quantity")
    private Integer quantity;
    
    @Column(name = "value")
    private BigDecimal value;
    
    @Column(name = "message", length = 500)
    private String message;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private ContributionStatus status = ContributionStatus.PENDING;
    
    @Column(name = "delivery_date")
    private java.time.LocalDate deliveryDate;
    
    @Column(name = "proof_url")
    private String proofUrl;
}

enum ContributionStatus {
    PENDING,
    CONFIRMED,
    DELIVERED,
    CANCELLED
}