package com.imigishalink.donations;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DonationApprovalRequestRepository extends JpaRepository<DonationApprovalRequest, Long> {
    
    @EntityGraph(attributePaths = {"donation", "requestedBy", "ngo", "reviewedBy"})
    Page<DonationApprovalRequest> findByStatus(DonationApprovalRequest.RequestStatus status, Pageable pageable);
    
    @EntityGraph(attributePaths = {"donation", "requestedBy", "ngo", "reviewedBy"})
    Page<DonationApprovalRequest> findByRequestedById(Long userId, Pageable pageable);
    
    @EntityGraph(attributePaths = {"donation", "requestedBy", "ngo", "reviewedBy"})
    @Query("SELECT r FROM DonationApprovalRequest r WHERE r.donation.id = :donationId AND r.status = 'PENDING'")
    List<DonationApprovalRequest> findPendingByDonationId(@Param("donationId") Long donationId);
    
    @EntityGraph(attributePaths = {"donation", "requestedBy", "ngo", "reviewedBy"})
    @Query("SELECT r FROM DonationApprovalRequest r WHERE r.ngo.id = :ngoId")
    Page<DonationApprovalRequest> findByNgoId(@Param("ngoId") Long ngoId, Pageable pageable);
}

