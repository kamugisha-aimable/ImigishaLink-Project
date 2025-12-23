package com.imigishalink.donations;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.imigishalink.common.BaseEntity;
import com.imigishalink.ngos.NGO;
import com.imigishalink.users.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "donation_approval_requests")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class DonationApprovalRequest extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "donation_id", nullable = false)
    private Donation donation;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requested_by_id", nullable = false)
    private User requestedBy;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ngo_id", nullable = false)
    private NGO ngo;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "request_type", nullable = false)
    private RequestType requestType; // APPROVE or REJECT
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private RequestStatus status = RequestStatus.PENDING;
    
    @Column(name = "reason", length = 1000)
    private String reason; // Optional reason for approval/rejection
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by_id")
    private User reviewedBy; // Admin who reviewed the request
    
    @Column(name = "admin_notes", length = 1000)
    private String adminNotes; // Admin's notes on the decision
    
    public enum RequestType {
        APPROVE,
        REJECT
    }
    
    public enum RequestStatus {
        PENDING,
        APPROVED,
        REJECTED,
        CANCELLED
    }
}

