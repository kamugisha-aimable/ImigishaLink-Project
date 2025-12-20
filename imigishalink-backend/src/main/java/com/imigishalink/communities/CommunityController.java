package com.imigishalink.communities;

import com.imigishalink.common.ApiResponse;
import com.imigishalink.common.PageResponse;
import com.imigishalink.location.Location;
import com.imigishalink.location.LocationRepository;
import com.imigishalink.users.User;
import com.imigishalink.users.UserRepository;
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
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/communities")
@RequiredArgsConstructor
public class CommunityController {
    
    private final CommunityRepository communityRepository;
    private final LocationRepository locationRepository;
    private final UserRepository userRepository;
    
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<Community>>> getAllCommunities(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String province,
            @RequestParam(required = false) String district,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "false") boolean popular) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Community> communities;
        
        if (search != null && !search.trim().isEmpty()) {
            communities = communityRepository.searchCommunities(search, pageable);
        } else if (province != null && district != null) {
            communities = communityRepository.findByLocationDistrict(district, pageable);
        } else if (province != null) {
            communities = communityRepository.findByLocationProvince(province, pageable);
        } else if (popular) {
            communities = communityRepository.findPopularCommunities(pageable);
        } else {
            communities = communityRepository.findByIsPublicTrue(pageable);
        }
        
        PageResponse<Community> response = new PageResponse<>(communities);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Community>> getCommunityById(@PathVariable @NotNull Long id) {
        Long communityId = Objects.requireNonNull(id, "Community ID cannot be null");
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new RuntimeException("Community not found"));
        
        if (!community.isPublic()) {
            throw new RuntimeException("Community is private");
        }
        
        return ResponseEntity.ok(ApiResponse.success(community));
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<Community>> createCommunity(
            @AuthenticationPrincipal User currentUser,
            @RequestBody Community community) {
        
        community.setCreatedBy(currentUser);
        community.addAdmin(currentUser);
        community.addMember(currentUser);
        
        // Set location if provided
        if (community.getLocation() != null && community.getLocation().getId() != null) {
            Long locationId = Objects.requireNonNull(community.getLocation().getId(), "Location ID cannot be null");
            Location location = locationRepository.findById(locationId)
                    .orElseThrow(() -> new RuntimeException("Location not found"));
            community.setLocation(location);
        }
        
        Community savedCommunity = communityRepository.save(community);
        return ResponseEntity.ok(ApiResponse.success("Community created successfully", savedCommunity));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("@communitySecurity.isAdmin(#id, authentication)")
    public ResponseEntity<ApiResponse<Community>> updateCommunity(
            @PathVariable @NotNull Long id,
            @RequestBody Community community) {
        
        Long communityId = Objects.requireNonNull(id, "Community ID cannot be null");
        Community existingCommunity = communityRepository.findById(communityId)
                .orElseThrow(() -> new RuntimeException("Community not found"));
        
        existingCommunity.setName(community.getName());
        existingCommunity.setDescription(community.getDescription());
        existingCommunity.setProfileImageUrl(community.getProfileImageUrl());
        existingCommunity.setBannerImageUrl(community.getBannerImageUrl());
        existingCommunity.setPublic(community.isPublic());
        existingCommunity.setRules(community.getRules());
        
        Community updatedCommunity = communityRepository.save(existingCommunity);
        return ResponseEntity.ok(ApiResponse.success("Community updated successfully", updatedCommunity));
    }
    
    @PostMapping("/{id}/join")
    public ResponseEntity<ApiResponse<Void>> joinCommunity(
            @AuthenticationPrincipal User currentUser,
            @PathVariable @NotNull Long id) {
        
        Long communityId = Objects.requireNonNull(id, "Community ID cannot be null");
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new RuntimeException("Community not found"));
        
        if (!community.isPublic()) {
            throw new RuntimeException("Community is private");
        }
        
        if (community.isMember(currentUser)) {
            throw new RuntimeException("Already a member");
        }
        
        community.addMember(currentUser);
        communityRepository.save(community);
        
        return ResponseEntity.ok(ApiResponse.success("Joined community successfully", null));
    }
    
    @PostMapping("/{id}/leave")
    public ResponseEntity<ApiResponse<Void>> leaveCommunity(
            @AuthenticationPrincipal User currentUser,
            @PathVariable @NotNull Long id) {
        
        Long communityId = Objects.requireNonNull(id, "Community ID cannot be null");
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new RuntimeException("Community not found"));
        
        if (!community.isMember(currentUser)) {
            throw new RuntimeException("Not a member");
        }
        
        community.removeMember(currentUser);
        communityRepository.save(community);
        
        return ResponseEntity.ok(ApiResponse.success("Left community successfully", null));
    }
    
    @PostMapping("/{id}/admins/{userId}")
    @PreAuthorize("@communitySecurity.isAdmin(#id, authentication)")
    public ResponseEntity<ApiResponse<Void>> addAdmin(
            @PathVariable @NotNull Long id,
            @PathVariable @NotNull Long userId) {
        
        Long communityId = Objects.requireNonNull(id, "Community ID cannot be null");
        Long userIdValue = Objects.requireNonNull(userId, "User ID cannot be null");
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new RuntimeException("Community not found"));
        
        User user = userRepository.findById(userIdValue)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        community.addAdmin(user);
        communityRepository.save(community);
        
        return ResponseEntity.ok(ApiResponse.success("Admin added successfully", null));
    }
    
    @GetMapping("/my-communities")
    public ResponseEntity<ApiResponse<PageResponse<Community>>> getMyCommunities(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<Community> communities = communityRepository.findByMemberId(currentUser.getId(), pageable);
        
        PageResponse<Community> response = new PageResponse<>(communities);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCommunityStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalCommunities", communityRepository.count());
        stats.put("publicCommunities", communityRepository.countPublicCommunities());
        stats.put("communitiesByProvince", communityRepository.countCommunitiesByProvince());
        
        return ResponseEntity.ok(ApiResponse.success(stats));
    }
}