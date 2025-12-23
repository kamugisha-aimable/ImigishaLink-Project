package com.imigishalink.ngos;

import com.imigishalink.categories.Category;
import com.imigishalink.categories.CategoryRepository;
import com.imigishalink.common.ApiResponse;
import com.imigishalink.common.PageResponse;
import com.imigishalink.location.Location;
import com.imigishalink.location.LocationRepository;
import com.imigishalink.users.User;
import com.imigishalink.users.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/ngos")
@RequiredArgsConstructor
public class NGOController {
    
    private final NGORepository ngoRepository;
    private final LocationRepository locationRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<NGO>>> getAllNGOs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Boolean verified,
            @RequestParam(required = false) String province,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String search) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<NGO> ngos;
        
        if (search != null && !search.trim().isEmpty()) {
            ngos = ngoRepository.searchNgos(search, pageable);
        } else if (verified != null) {
            ngos = verified ? ngoRepository.findByIsVerifiedTrue(pageable) : 
                             ngoRepository.findByIsVerifiedFalse(pageable);
        } else if (province != null) {
            ngos = ngoRepository.findByHeadOfficeLocationProvince(province, pageable);
        } else if (categoryId != null) {
            ngos = ngoRepository.findByCategoryId(categoryId, pageable);
        } else {
            ngos = ngoRepository.findAll(pageable);
        }
        
        PageResponse<NGO> response = new PageResponse<>(ngos);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/top")
    public ResponseEntity<ApiResponse<PageResponse<NGO>>> getTopNGOs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("totalDonationsReceived").descending());
        Page<NGO> ngos = ngoRepository.findTopNgos(pageable);
        
        PageResponse<NGO> response = new PageResponse<>(ngos);
        return ResponseEntity.ok(ApiResponse.success("Top NGOs by donations received", response));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<NGO>> getNGOById(@PathVariable @NotNull Long id) {
        Long ngoId = Objects.requireNonNull(id, "NGO ID cannot be null");
        NGO ngo = ngoRepository.findById(ngoId)
                .orElseThrow(() -> new RuntimeException("NGO not found"));
        return ResponseEntity.ok(ApiResponse.success(ngo));
    }
    
    // Public registration endpoint - allows anyone to register an NGO
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<NGO>> registerNGO(@Valid @RequestBody NGO ngo) {
        // Validate required fields
        if (ngo.getName() == null || ngo.getName().trim().isEmpty()) {
            throw new RuntimeException("NGO name is required");
        }
        if (ngo.getRegistrationNumber() == null || ngo.getRegistrationNumber().trim().isEmpty()) {
            throw new RuntimeException("Registration number is required");
        }
        if (ngo.getEmail() == null || ngo.getEmail().trim().isEmpty()) {
            throw new RuntimeException("Email is required");
        }
        
        // Check for duplicates
        if (ngoRepository.existsByRegistrationNumber(ngo.getRegistrationNumber())) {
            throw new RuntimeException("Registration number already exists");
        }
        
        if (ngoRepository.existsByEmail(ngo.getEmail())) {
            throw new RuntimeException("Email already registered");
        }
        
        // Initialize collections if null
        if (ngo.getAdmins() == null) {
            ngo.setAdmins(new HashSet<>());
        }
        if (ngo.getCategories() == null) {
            ngo.setCategories(new HashSet<>());
        }
        
        // Set location if provided
        if (ngo.getHeadOfficeLocation() != null && ngo.getHeadOfficeLocation().getId() != null) {
            Long locationId = Objects.requireNonNull(ngo.getHeadOfficeLocation().getId(), "Location ID cannot be null");
            Location location = locationRepository.findById(locationId)
                    .orElseThrow(() -> new RuntimeException("Location not found"));
            ngo.setHeadOfficeLocation(location);
        }
        
        // Set categories
        if (ngo.getCategories() != null && !ngo.getCategories().isEmpty()) {
            HashSet<Category> managedCategories = new HashSet<>();
            for (Category category : ngo.getCategories()) {
                Long categoryId = Objects.requireNonNull(category.getId(), "Category ID cannot be null");
                Category managedCategory = categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new RuntimeException("Category not found: " + categoryId));
                managedCategories.add(managedCategory);
            }
            ngo.setCategories(managedCategories);
        }
        
        // Ensure default values are set (boolean primitives don't need null checks)
        // isVerified defaults to false in the entity
        if (ngo.getTotalDonationsReceived() == null) {
            ngo.setTotalDonationsReceived(0);
        }
        if (ngo.getTotalBeneficiaries() == null) {
            ngo.setTotalBeneficiaries(0);
        }
        
        // For public registration, admins will be added later when the user account is created
        // The user with matching email will be added as admin during account creation
        
        // Save NGO to database
        NGO savedNGO = ngoRepository.save(ngo);
        
        // Verify the NGO was saved (check if it has an ID)
        if (savedNGO.getId() == null) {
            throw new RuntimeException("Failed to save NGO to database");
        }
        
        return ResponseEntity.ok(ApiResponse.success("NGO registered successfully", savedNGO));
    }
    
    // Admin/NGO endpoint - requires authentication
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('NGO')")
    public ResponseEntity<ApiResponse<NGO>> createNGO(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody NGO ngo) {
        
        if (ngoRepository.existsByRegistrationNumber(ngo.getRegistrationNumber())) {
            throw new RuntimeException("Registration number already exists");
        }
        
        if (ngoRepository.existsByEmail(ngo.getEmail())) {
            throw new RuntimeException("Email already registered");
        }
        
        // Set location if provided
        if (ngo.getHeadOfficeLocation() != null && ngo.getHeadOfficeLocation().getId() != null) {
            Long locationId = Objects.requireNonNull(ngo.getHeadOfficeLocation().getId(), "Location ID cannot be null");
            Location location = locationRepository.findById(locationId)
                    .orElseThrow(() -> new RuntimeException("Location not found"));
            ngo.setHeadOfficeLocation(location);
        }
        
        // Set categories
        if (ngo.getCategories() != null && !ngo.getCategories().isEmpty()) {
            HashSet<Category> managedCategories = new HashSet<>();
            for (Category category : ngo.getCategories()) {
                Long categoryId = Objects.requireNonNull(category.getId(), "Category ID cannot be null");
                Category managedCategory = categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new RuntimeException("Category not found: " + categoryId));
                managedCategories.add(managedCategory);
            }
            ngo.setCategories(managedCategories);
        }
        
        // Add current user as admin
        ngo.getAdmins().add(currentUser);
        
        NGO savedNGO = ngoRepository.save(ngo);
        return ResponseEntity.ok(ApiResponse.success("NGO created successfully", savedNGO));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @ngoSecurity.isAdmin(#id, authentication)")
    public ResponseEntity<ApiResponse<NGO>> updateNGO(
            @PathVariable @NotNull Long id,
            @Valid @RequestBody NGO ngo) {
        
        Long ngoId = Objects.requireNonNull(id, "NGO ID cannot be null");
        NGO existingNGO = ngoRepository.findById(ngoId)
                .orElseThrow(() -> new RuntimeException("NGO not found"));
        
        // Update basic info
        existingNGO.setName(ngo.getName());
        existingNGO.setDescription(ngo.getDescription());
        existingNGO.setEmail(ngo.getEmail());
        existingNGO.setPhoneNumber(ngo.getPhoneNumber());
        existingNGO.setWebsite(ngo.getWebsite());
        existingNGO.setLogoUrl(ngo.getLogoUrl());
        existingNGO.setBannerUrl(ngo.getBannerUrl());
        existingNGO.setFoundedYear(ngo.getFoundedYear());
        existingNGO.setVerified(ngo.isVerified());
        existingNGO.setVerificationDocumentsUrl(ngo.getVerificationDocumentsUrl());
        
        // Update location if changed
        if (ngo.getHeadOfficeLocation() != null && ngo.getHeadOfficeLocation().getId() != null) {
            Long locationId = Objects.requireNonNull(ngo.getHeadOfficeLocation().getId(), "Location ID cannot be null");
            Location location = locationRepository.findById(locationId)
                    .orElseThrow(() -> new RuntimeException("Location not found"));
            existingNGO.setHeadOfficeLocation(location);
        }
        
        // Update categories
        if (ngo.getCategories() != null) {
            HashSet<Category> managedCategories = new HashSet<>();
            for (Category category : ngo.getCategories()) {
                Long categoryId = Objects.requireNonNull(category.getId(), "Category ID cannot be null");
                Category managedCategory = categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new RuntimeException("Category not found: " + categoryId));
                managedCategories.add(managedCategory);
            }
            existingNGO.setCategories(managedCategories);
        }
        
        NGO updatedNGO = ngoRepository.save(existingNGO);
        return ResponseEntity.ok(ApiResponse.success("NGO updated successfully", updatedNGO));
    }
    
    @PostMapping("/{id}/admins/{userId}")
    @PreAuthorize("hasRole('ADMIN') or @ngoSecurity.isAdmin(#id, authentication)")
    public ResponseEntity<ApiResponse<Void>> addAdmin(
            @PathVariable @NotNull Long id,
            @PathVariable @NotNull Long userId) {
        
        Long ngoId = Objects.requireNonNull(id, "NGO ID cannot be null");
        Long userIdValue = Objects.requireNonNull(userId, "User ID cannot be null");
        NGO ngo = ngoRepository.findById(ngoId)
                .orElseThrow(() -> new RuntimeException("NGO not found"));
        
        User user = userRepository.findById(userIdValue)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        ngo.addAdmin(user);
        ngoRepository.save(ngo);
        
        return ResponseEntity.ok(ApiResponse.success("Admin added successfully", null));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteNGO(
            @PathVariable @NotNull Long id) {
        
        Long ngoId = Objects.requireNonNull(id, "NGO ID cannot be null");
        NGO ngo = ngoRepository.findById(ngoId)
                .orElseThrow(() -> new RuntimeException("NGO not found"));
        
        // Delete the NGO
        ngoRepository.delete(ngo);
        
        return ResponseEntity.ok(ApiResponse.success("NGO deleted successfully", null));
    }
    
    @DeleteMapping("/{id}/admins/{userId}")
    @PreAuthorize("hasRole('ADMIN') or @ngoSecurity.isAdmin(#id, authentication)")
    public ResponseEntity<ApiResponse<Void>> removeAdmin(
            @PathVariable @NotNull Long id,
            @PathVariable @NotNull Long userId) {
        
        Long ngoId = Objects.requireNonNull(id, "NGO ID cannot be null");
        Long userIdValue = Objects.requireNonNull(userId, "User ID cannot be null");
        NGO ngo = ngoRepository.findById(ngoId)
                .orElseThrow(() -> new RuntimeException("NGO not found"));
        
        User user = userRepository.findById(userIdValue)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        ngo.removeAdmin(user);
        ngoRepository.save(ngo);
        
        return ResponseEntity.ok(ApiResponse.success("Admin removed successfully", null));
    }
    
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getNGOStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalNgos", ngoRepository.count());
        stats.put("verifiedNgos", ngoRepository.countVerifiedNgos());
        stats.put("unverifiedNgos", ngoRepository.count() - ngoRepository.countVerifiedNgos());
        stats.put("ngosByProvince", ngoRepository.countNgosByProvince());
        
        return ResponseEntity.ok(ApiResponse.success(stats));
    }
    
    @GetMapping("/my-ngos")
    @PreAuthorize("hasRole('NGO')")
    public ResponseEntity<ApiResponse<PageResponse<NGO>>> getMyNGOs(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        // Debug: Log user role for troubleshooting
        if (currentUser != null) {
            System.out.println("DEBUG: User accessing /my-ngos - Email: " + currentUser.getEmail() + ", Role: " + currentUser.getRole());
            System.out.println("DEBUG: User authorities: " + currentUser.getAuthorities());
        } else {
            System.out.println("DEBUG: currentUser is null!");
            throw new RuntimeException("User not authenticated");
        }
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<NGO> ngos = ngoRepository.findByAdminId(currentUser.getId(), pageable);
        
        PageResponse<NGO> response = new PageResponse<>(ngos);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}