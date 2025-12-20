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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.constraints.NotNull;

import java.util.HashMap;
import java.util.HashSet;
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
            donations = donationRepository.findOpenDonationsByPriority(pageable);
        }
        
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
}