package com.imigishalink.donations;

import com.imigishalink.categories.Category;
import com.imigishalink.categories.CategoryRepository;
import com.imigishalink.common.ApiResponse;
import com.imigishalink.common.PageResponse;
import com.imigishalink.location.Location;
import com.imigishalink.location.LocationRepository;
import com.imigishalink.ngos.NGO;
import com.imigishalink.ngos.NGORepository;
import com.imigishalink.users.User;
import com.imigishalink.users.UserRepository;
import com.imigishalink.common.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/donations")
@RequiredArgsConstructor
public class DonationController {
    
    private final DonationRepository donationRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final NGORepository ngoRepository;
    private final DonationApprovalRequestRepository approvalRequestRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<Donation>>> getAllDonations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) DonationStatus status,
            @RequestParam(required = false) DonationType type,
            @RequestParam(required = false) String province,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String search) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Donation> donations;
        
        // Use EntityGraph methods to eagerly load associations and avoid lazy loading issues
        if (search != null && !search.trim().isEmpty()) {
            donations = donationRepository.searchDonations(search, pageable);
        } else if (status != null) {
            donations = donationRepository.findByStatus(status, pageable);
        } else if (type != null) {
            donations = donationRepository.findByType(type, pageable);
        } else if (province != null) {
            donations = donationRepository.findByProvince(province, pageable);
        } else if (categoryId != null) {
            donations = donationRepository.findByCategoryId(categoryId, pageable);
        } else {
            // Return all donations with associations eagerly loaded
            // Use a query with EntityGraph to load all necessary associations
            donations = donationRepository.findAll(pageable);
        }
        
        // Ensure all lazy-loaded associations are initialized before serialization
        // Force initialization of createdBy, location, ngo, and categories
        donations.getContent().forEach(donation -> {
            if (donation.getCreatedBy() != null) {
                donation.getCreatedBy().getId(); // Force initialization
            }
            if (donation.getLocation() != null) {
                donation.getLocation().getId(); // Force initialization
            }
            if (donation.getNgo() != null) {
                donation.getNgo().getId(); // Force initialization
            }
            if (donation.getCategories() != null) {
                donation.getCategories().size(); // Force initialization
            }
        });
        
        PageResponse<Donation> response = new PageResponse<>(donations);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/urgent")
    public ResponseEntity<ApiResponse<PageResponse<Donation>>> getUrgentDonations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("priorityLevel").descending());
        Page<Donation> donations = donationRepository.findByStatus(DonationStatus.OPEN, pageable);
        
        PageResponse<Donation> response = new PageResponse<>(donations);
        return ResponseEntity.ok(ApiResponse.success("Urgent donations", response));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Donation>> getDonationById(@PathVariable @NotNull Long id) {
        Long donationId = Objects.requireNonNull(id, "Donation ID cannot be null");
        // Use custom method with @EntityGraph to eagerly fetch associations
        Donation donation = donationRepository.findByIdWithAssociations(donationId)
                .orElseThrow(() -> new RuntimeException("Donation not found"));
        return ResponseEntity.ok(ApiResponse.success(donation));
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<Donation>> createDonation(
            @AuthenticationPrincipal User currentUser,
            @RequestBody Donation donation) {
        
        donation.setCreatedBy(currentUser);
        donation.setStatus(DonationStatus.OPEN);
        
        // Set location if provided
        if (donation.getLocation() != null && donation.getLocation().getId() != null) {
            Long locationId = Objects.requireNonNull(donation.getLocation().getId(), "Location ID cannot be null");
            Location location = locationRepository.findById(locationId)
                    .orElseThrow(() -> new RuntimeException("Location not found"));
            donation.setLocation(location);
        }
        
        // Set NGO if provided
        if (donation.getNgo() != null && donation.getNgo().getId() != null) {
            Long ngoId = Objects.requireNonNull(donation.getNgo().getId(), "NGO ID cannot be null");
            NGO ngo = ngoRepository.findById(ngoId)
                    .orElseThrow(() -> new RuntimeException("NGO not found"));
            donation.setNgo(ngo);
        }
        
        // Set categories
        if (donation.getCategories() != null && !donation.getCategories().isEmpty()) {
            HashSet<Category> managedCategories = new HashSet<>();
            for (Category category : donation.getCategories()) {
                Long categoryId = Objects.requireNonNull(category.getId(), "Category ID cannot be null");
                Category managedCategory = categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new RuntimeException("Category not found: " + categoryId));
                managedCategories.add(managedCategory);
            }
            donation.setCategories(managedCategories);
        }
        
        Donation savedDonation = donationRepository.save(donation);
        return ResponseEntity.ok(ApiResponse.success("Donation created successfully", savedDonation));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @donationSecurity.isOwner(#id, authentication) or @donationSecurity.isNgoAdmin(#id, authentication)")
    public ResponseEntity<ApiResponse<Donation>> updateDonation(
            @PathVariable @NotNull Long id,
            @RequestBody Donation donation) {
        
        Long donationId = Objects.requireNonNull(id, "Donation ID cannot be null");
        Donation existingDonation = donationRepository.findById(donationId)
                .orElseThrow(() -> new RuntimeException("Donation not found"));
        
        existingDonation.setTitle(donation.getTitle());
        existingDonation.setDescription(donation.getDescription());
        existingDonation.setQuantity(donation.getQuantity());
        existingDonation.setUnit(donation.getUnit());
        existingDonation.setEstimatedValue(donation.getEstimatedValue());
        existingDonation.setCurrency(donation.getCurrency());
        existingDonation.setStatus(donation.getStatus());
        existingDonation.setType(donation.getType());
        existingDonation.setPriorityLevel(donation.getPriorityLevel());
        existingDonation.setDeadline(donation.getDeadline());
        existingDonation.setDeliveryMethod(donation.getDeliveryMethod());
        existingDonation.setImagesUrls(donation.getImagesUrls());
        
        // Update categories
        if (donation.getCategories() != null) {
            HashSet<Category> managedCategories = new HashSet<>();
            for (Category category : donation.getCategories()) {
                Long categoryId = Objects.requireNonNull(category.getId(), "Category ID cannot be null");
                Category managedCategory = categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new RuntimeException("Category not found: " + categoryId));
                managedCategories.add(managedCategory);
            }
            existingDonation.setCategories(managedCategories);
        }
        
        Donation updatedDonation = donationRepository.save(existingDonation);
        return ResponseEntity.ok(ApiResponse.success("Donation updated successfully", updatedDonation));
    }
    
    @PostMapping("/{id}/contribute")
    public ResponseEntity<ApiResponse<Contribution>> contributeToDonation(
            @AuthenticationPrincipal User currentUser,
            @PathVariable @NotNull Long id,
            @RequestBody Contribution contribution) {
        
        Long donationId = Objects.requireNonNull(id, "Donation ID cannot be null");
        Donation donation = donationRepository.findById(donationId)
                .orElseThrow(() -> new RuntimeException("Donation not found"));
        
        if (donation.getStatus() != DonationStatus.OPEN) {
            throw new RuntimeException("Donation is not open for contributions");
        }
        
        contribution.setContributor(currentUser);
        donation.addContribution(contribution);
        
        // Update donation status if fulfilled
        if (donation.isFulfilled()) {
            donation.setStatus(DonationStatus.FULFILLED);
            if (donation.getNgo() != null) {
                NGO ngo = Objects.requireNonNull(donation.getNgo(), "NGO cannot be null");
                ngo.incrementDonationsReceived();
                ngoRepository.save(ngo);
            }
        } else {
            int totalContributed = donation.getContributions().stream()
                    .map(Contribution::getQuantity)
                    .filter(q -> q != null)
                    .mapToInt(Integer::intValue)
                    .sum();
            if (totalContributed > 0) {
                donation.setStatus(DonationStatus.PARTIALLY_FULFILLED);
            }
        }
        
        donationRepository.save(donation);
        return ResponseEntity.ok(ApiResponse.success("Contribution added successfully", contribution));
    }
    
    @GetMapping("/my-donations")
    public ResponseEntity<ApiResponse<PageResponse<Donation>>> getMyDonations(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) DonationStatus status) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Donation> donations;
        
        if (status != null) {
            // Filter by status and created by user
            donations = donationRepository.findByCreatedByIdAndStatus(currentUser.getId(), status, pageable);
        } else {
            donations = donationRepository.findByCreatedById(currentUser.getId(), pageable);
        }
        
        PageResponse<Donation> response = new PageResponse<>(donations);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDonationStats() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalDonations", donationRepository.count());
        stats.put("openDonations", donationRepository.countByStatus(DonationStatus.OPEN));
        stats.put("fulfilledDonations", donationRepository.countByStatus(DonationStatus.FULFILLED));
        stats.put("donationsByProvince", donationRepository.countDonationsByProvince());
        stats.put("donationsByType", donationRepository.countDonationsByType());
        
        return ResponseEntity.ok(ApiResponse.success(stats));
    }
    
    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN') or hasRole('NGO')")
    @Transactional
    public ResponseEntity<ApiResponse<Donation>> approveDonation(
            @AuthenticationPrincipal User currentUser,
            @PathVariable @NotNull Long id,
            @RequestBody Map<String, Object> requestBody) {
        
        Long donationId = Objects.requireNonNull(id, "Donation ID cannot be null");
        
        // NGO ID is required for NGO users, optional for ADMIN (admin can approve without assigning to NGO)
        Long ngoId = null;
        if (requestBody.get("ngoId") != null) {
            ngoId = ((Number) requestBody.get("ngoId")).longValue();
        }
        
        // Use EntityGraph to eagerly load all associations (categories, location, etc.)
        Donation donation = donationRepository.findByIdWithAssociations(donationId)
                .orElseThrow(() -> new RuntimeException("Donation not found"));
        
        // Check if donation is open
        if (donation.getStatus() != DonationStatus.OPEN) {
            throw new RuntimeException("Donation is not in OPEN status and cannot be approved");
        }
        
        // Authorization: ADMIN can approve any donation, NGO users can approve any donation
        boolean isAdmin = currentUser.getRole().equals(com.imigishalink.users.Role.ADMIN);
        boolean isNgo = currentUser.getRole().equals(com.imigishalink.users.Role.NGO);
        
        if (!isAdmin && !isNgo) {
            throw new RuntimeException("User is not authorized to approve donations. You must be an administrator or NGO member.");
        }
        
        // Get the NGO if provided
        NGO ngo = null;
        if (ngoId != null) {
            ngo = ngoRepository.findById(ngoId)
                    .orElseThrow(() -> new RuntimeException("NGO not found"));
        }
        
        // For NGO users, NGO ID is required to assign the donation to their NGO
        if (isNgo && ngoId == null) {
            throw new RuntimeException("NGO ID is required for NGO users to approve donations. Please specify which NGO is approving this donation.");
        }
        
        // For NGO users, just verify the NGO exists - any NGO user can approve for any NGO
        // This allows NGOs to approve donations that meet their needs
        if (isNgo && ngo == null) {
            throw new RuntimeException("NGO not found. Please provide a valid NGO ID.");
        }
        
        // Approve the donation
        donation.setStatus(DonationStatus.IN_PROGRESS);
        if (ngo != null) {
            donation.setNgo(ngo);
        }
        
        Donation updatedDonation = donationRepository.save(donation);
        
        // Reload with associations to ensure all lazy collections are initialized for JSON serialization
        Donation finalDonation = donationRepository.findByIdWithAssociations(updatedDonation.getId())
                .orElse(updatedDonation);
        
        // Status updated - notification will be shown in donation requests UI
        String approverName = ngo != null ? ngo.getName() : "Administrator";
        return ResponseEntity.ok(ApiResponse.success("Donation approved successfully by " + approverName + ". Status updated to IN_PROGRESS.", finalDonation));
    }
    
    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN') or hasRole('NGO')")
    @Transactional
    public ResponseEntity<ApiResponse<Donation>> rejectDonation(
            @AuthenticationPrincipal User currentUser,
            @PathVariable @NotNull Long id,
            @RequestBody(required = false) Map<String, Object> requestBody) {
        
        Long donationId = Objects.requireNonNull(id, "Donation ID cannot be null");
        
        // Get rejection reason if provided
        String reason = null;
        if (requestBody != null && requestBody.get("reason") != null) {
            reason = (String) requestBody.get("reason");
        }
        
        Donation donation = donationRepository.findById(donationId)
                .orElseThrow(() -> new RuntimeException("Donation not found"));
        
        // Check if donation is open or in progress
        if (donation.getStatus() != DonationStatus.OPEN && donation.getStatus() != DonationStatus.IN_PROGRESS) {
            throw new RuntimeException("Donation is not in OPEN or IN_PROGRESS status and cannot be rejected");
        }
        
        // Authorization: ADMIN can reject any donation, NGO users can reject any donation
        boolean isAdmin = currentUser.getRole().equals(com.imigishalink.users.Role.ADMIN);
        boolean isNgo = currentUser.getRole().equals(com.imigishalink.users.Role.NGO);
        
        if (!isAdmin && !isNgo) {
            throw new RuntimeException("User is not authorized to reject donations. You must be an administrator or NGO member.");
        }
        
        // Get NGO info if user is NGO
        NGO ngo = null;
        if (isNgo && donation.getNgo() != null) {
            ngo = donation.getNgo();
        } else if (isNgo && requestBody != null && requestBody.get("ngoId") != null) {
            Long ngoId = ((Number) requestBody.get("ngoId")).longValue();
            ngo = ngoRepository.findById(ngoId).orElse(null);
        }
        
        // Reject the donation
        donation.setStatus(DonationStatus.CANCELLED);
        
        Donation updatedDonation = donationRepository.save(donation);
        
        // Reload with associations to ensure all lazy collections are initialized for JSON serialization
        Donation finalDonation = donationRepository.findByIdWithAssociations(updatedDonation.getId())
                .orElse(updatedDonation);
        
        // Status updated - notification will be shown in donation requests UI
        String rejectorName = isNgo && ngo != null ? ngo.getName() : "Administrator";
        String message = "Donation rejected successfully by " + rejectorName + ". Status updated to CANCELLED.";
        if (reason != null && !reason.trim().isEmpty()) {
            message += " Reason: " + reason;
        }
        
        return ResponseEntity.ok(ApiResponse.success(message, finalDonation));
    }
    
    @PostMapping("/{id}/schedule-pickup")
    @PreAuthorize("hasRole('ADMIN') or hasRole('NGO')")
    @Transactional
    public ResponseEntity<ApiResponse<Donation>> schedulePickup(
            @AuthenticationPrincipal User currentUser,
            @PathVariable @NotNull Long id,
            @RequestBody Map<String, Object> requestBody) {
        
        Long donationId = Objects.requireNonNull(id, "Donation ID cannot be null");
        String pickupDateStr = (String) requestBody.get("pickupDate");
        if (pickupDateStr == null) {
            throw new RuntimeException("Pickup date is required");
        }
        LocalDate pickupDate = LocalDate.parse(pickupDateStr);
        
        // Load donation with associations to avoid lazy loading issues
        Donation donation = donationRepository.findByIdWithAssociations(donationId)
                .orElseThrow(() -> new RuntimeException("Donation not found"));
        
        // Check if donation has an NGO assigned and user is admin of that NGO
        if (donation.getNgo() == null) {
            throw new RuntimeException("Donation must be approved by an NGO before scheduling pickup");
        }
        
        // Load NGO with admins to avoid lazy loading exception
        NGO ngo = ngoRepository.findByIdWithAdmins(donation.getNgo().getId())
                .orElseThrow(() -> new RuntimeException("NGO not found"));
        
        // Verify user authorization
        if (!currentUser.getRole().equals(com.imigishalink.users.Role.ADMIN) && 
            (ngo.getAdmins() == null || !ngo.getAdmins().contains(currentUser))) {
            throw new RuntimeException("User is not authorized to schedule pickup for this donation");
        }
        
        // Check if donation is in progress
        if (donation.getStatus() != DonationStatus.IN_PROGRESS) {
            throw new RuntimeException("Donation must be in IN_PROGRESS status to schedule pickup");
        }
        
        // Schedule pickup
        donation.setDeadline(pickupDate);
        // Status can remain IN_PROGRESS or be changed to SCHEDULED if you have that status
        
        Donation updatedDonation = donationRepository.save(donation);
        
        // Reload with associations for JSON serialization
        updatedDonation = donationRepository.findByIdWithAssociations(updatedDonation.getId())
                .orElse(updatedDonation);
        
        return ResponseEntity.ok(ApiResponse.success("Pickup scheduled successfully", updatedDonation));
    }
    
    @PostMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or @donationSecurity.isOwner(#id, authentication) or @donationSecurity.isNgoAdmin(#id, authentication)")
    public ResponseEntity<ApiResponse<Void>> updateDonationStatus(
            @PathVariable @NotNull Long id,
            @RequestParam DonationStatus status) {
        
        Long donationId = Objects.requireNonNull(id, "Donation ID cannot be null");
        Donation donation = donationRepository.findById(donationId)
                .orElseThrow(() -> new RuntimeException("Donation not found"));
        
        donation.setStatus(status);
        donationRepository.save(donation);
        
        return ResponseEntity.ok(ApiResponse.success("Donation status updated successfully", null));
    }
    
    // NGO members can request approval/rejection (requires admin confirmation)
    @PostMapping("/{id}/request-approval")
    @PreAuthorize("hasRole('NGO')")
    public ResponseEntity<ApiResponse<DonationApprovalRequest>> requestApproval(
            @AuthenticationPrincipal User currentUser,
            @PathVariable @NotNull Long id,
            @RequestBody Map<String, Object> requestBody) {
        
        Long donationId = Objects.requireNonNull(id, "Donation ID cannot be null");
        String requestTypeStr = (String) requestBody.get("requestType"); // "APPROVE" or "REJECT"
        Long ngoId = ((Number) requestBody.get("ngoId")).longValue();
        String reason = (String) requestBody.get("reason");
        
        if (requestTypeStr == null || (!requestTypeStr.equals("APPROVE") && !requestTypeStr.equals("REJECT"))) {
            throw new RuntimeException("requestType must be either 'APPROVE' or 'REJECT'");
        }
        
        DonationApprovalRequest.RequestType requestType = DonationApprovalRequest.RequestType.valueOf(requestTypeStr);
        
        Donation donation = donationRepository.findById(donationId)
                .orElseThrow(() -> new RuntimeException("Donation not found"));
        
        if (donation.getStatus() != DonationStatus.OPEN) {
            throw new RuntimeException("Donation is not in OPEN status");
        }
        
        // Use EntityGraph to eagerly fetch admins to avoid lazy loading issues
        NGO ngo = ngoRepository.findByIdWithAdmins(ngoId)
                .orElseThrow(() -> new RuntimeException("NGO not found"));
        
        // Check if user is member of the NGO (must be in admins list - for now, we require admin status)
        // In future, you might want to add a separate "members" relationship
        if (ngo.getAdmins() == null || !ngo.getAdmins().contains(currentUser)) {
            throw new RuntimeException("User is not a member of this NGO");
        }
        
        // Check if there's already a pending request for this donation
        List<DonationApprovalRequest> existingRequests = approvalRequestRepository.findPendingByDonationId(donationId);
        if (!existingRequests.isEmpty()) {
            throw new RuntimeException("There is already a pending approval request for this donation");
        }
        
        DonationApprovalRequest request = DonationApprovalRequest.builder()
                .donation(donation)
                .requestedBy(currentUser)
                .ngo(ngo)
                .requestType(requestType)
                .status(DonationApprovalRequest.RequestStatus.PENDING)
                .reason(reason)
                .build();
        
        DonationApprovalRequest savedRequest = approvalRequestRepository.save(request);
        return ResponseEntity.ok(ApiResponse.success("Approval request submitted successfully. Waiting for admin confirmation.", savedRequest));
    }
    
    // Admin or NGO confirms approval request
    @PostMapping("/approval-requests/{requestId}/confirm")
    @PreAuthorize("hasRole('ADMIN') or hasRole('NGO')")
    @Transactional
    public ResponseEntity<ApiResponse<Donation>> confirmApprovalRequest(
            @AuthenticationPrincipal User currentUser,
            @PathVariable @NotNull Long requestId,
            @RequestBody Map<String, Object> requestBody) {
        
        Boolean approved = (Boolean) requestBody.get("approved");
        String adminNotes = (String) requestBody.get("adminNotes");
        
        DonationApprovalRequest request = approvalRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Approval request not found"));
        
        if (request.getStatus() != DonationApprovalRequest.RequestStatus.PENDING) {
            throw new RuntimeException("This request has already been processed");
        }
        
        // If NGO user, verify they belong to the NGO that made the request
        if (currentUser.getRole() == com.imigishalink.users.Role.NGO) {
            NGO ngo = request.getNgo();
            if (ngo == null || ngo.getAdmins() == null || !ngo.getAdmins().contains(currentUser)) {
                throw new RuntimeException("You are not authorized to approve/reject this request. You must be a member of the NGO that submitted the request.");
            }
        }
        
        // Get donation with associations to avoid lazy loading
        Long donationId = request.getDonation().getId();
        Donation donation = donationRepository.findByIdWithAssociations(donationId)
                .orElseThrow(() -> new RuntimeException("Donation not found"));
        
        if (approved != null && approved) {
            // Admin approved the request
            if (request.getRequestType() == DonationApprovalRequest.RequestType.APPROVE) {
                // Approve the donation
                donation.setStatus(DonationStatus.IN_PROGRESS);
                donation.setNgo(request.getNgo());
                Donation savedDonation = donationRepository.save(donation);
                
                // Reload with associations for JSON serialization
                savedDonation = donationRepository.findByIdWithAssociations(savedDonation.getId())
                        .orElse(savedDonation);
                
                request.setStatus(DonationApprovalRequest.RequestStatus.APPROVED);
                request.setReviewedBy(currentUser);
                request.setAdminNotes(adminNotes);
                approvalRequestRepository.save(request);
                
                // Return the reloaded donation with all associations loaded
                // Status updated - notification will be shown in donation requests UI
                return ResponseEntity.ok(ApiResponse.success("Donation approved successfully. Status updated to IN_PROGRESS.", savedDonation));
            } else {
                // Reject the donation
                donation.setStatus(DonationStatus.CANCELLED);
                Donation savedDonation = donationRepository.save(donation);
                
                // Reload with associations for JSON serialization
                savedDonation = donationRepository.findByIdWithAssociations(savedDonation.getId())
                        .orElse(savedDonation);
                
                request.setStatus(DonationApprovalRequest.RequestStatus.APPROVED);
                request.setReviewedBy(currentUser);
                request.setAdminNotes(adminNotes);
                approvalRequestRepository.save(request);
                
                // Status updated - notification will be shown in donation requests UI
                return ResponseEntity.ok(ApiResponse.success("Donation rejected successfully. Status updated to CANCELLED.", savedDonation));
            }
        } else {
            // Admin/NGO rejected the request
            request.setStatus(DonationApprovalRequest.RequestStatus.REJECTED);
            request.setReviewedBy(currentUser);
            request.setAdminNotes(adminNotes);
            approvalRequestRepository.save(request);
            
            // Status updated - notification will be shown in donation requests UI
            String message = "Approval request rejected.";
            if (adminNotes != null && !adminNotes.isEmpty()) {
                message += " Reason: " + adminNotes;
            }
            return ResponseEntity.ok(ApiResponse.success(message, null));
        }
    }
    
    // Get all pending approval requests (for admin)
    @GetMapping("/approval-requests/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<DonationApprovalRequest>>> getPendingApprovalRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<DonationApprovalRequest> requests = approvalRequestRepository.findByStatus(
                DonationApprovalRequest.RequestStatus.PENDING, pageable);
        
        PageResponse<DonationApprovalRequest> response = new PageResponse<>(requests);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    // Get user's approval requests (for their NGO)
    @GetMapping("/approval-requests/my-requests")
    @PreAuthorize("hasRole('NGO')")
    public ResponseEntity<ApiResponse<PageResponse<DonationApprovalRequest>>> getMyApprovalRequests(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        
        // Get NGOs where user is an admin
        Page<NGO> userNGOsPage = ngoRepository.findByAdminId(currentUser.getId(), PageRequest.of(0, 100));
        List<NGO> userNGOs = userNGOsPage.getContent();
        
        if (userNGOs.isEmpty()) {
            PageResponse<DonationApprovalRequest> emptyResponse = new PageResponse<>(Page.empty(pageable));
            return ResponseEntity.ok(ApiResponse.success(emptyResponse));
        }
        
        // Get approval requests for all NGOs where user is an admin
        // For now, get requests from the first NGO (in future, could aggregate from all)
        Long ngoId = userNGOs.get(0).getId();
        Page<DonationApprovalRequest> requests = approvalRequestRepository.findByNgoId(ngoId, pageable);
        
        PageResponse<DonationApprovalRequest> response = new PageResponse<>(requests);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    // Get all pending donations (OPEN status) for NGOs to approve/reject
    @GetMapping("/pending")
    @PreAuthorize("hasRole('NGO') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<Donation>>> getPendingDonations(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        // Debug: Log user role for troubleshooting
        if (currentUser != null) {
            System.out.println("DEBUG: User accessing /pending - Email: " + currentUser.getEmail() + ", Role: " + currentUser.getRole());
            System.out.println("DEBUG: User authorities: " + currentUser.getAuthorities());
        } else {
            System.out.println("DEBUG: currentUser is null!");
        }
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("priorityLevel").descending()
                .and(Sort.by("createdAt").descending()));
        
        // Get all OPEN donations (pending requests from donors)
        Page<Donation> donations = donationRepository.findByStatus(DonationStatus.OPEN, pageable);
        
        PageResponse<Donation> response = new PageResponse<>(donations);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}