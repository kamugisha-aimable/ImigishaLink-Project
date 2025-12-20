package com.imigishalink.users;

import com.imigishalink.common.ApiResponse;
import com.imigishalink.common.PageResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    private final UserRepository userRepository;
    
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<User>> getCurrentUser(@AuthenticationPrincipal User user) {
        Long userId = Objects.requireNonNull(user.getId(), "User ID cannot be null");
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        currentUser.setPassword(null);
        return ResponseEntity.ok(ApiResponse.success(currentUser));
    }
    
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<User>> updateCurrentUser(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody User updatedUser) {
        User user = userService.updateUser(currentUser.getId(), updatedUser);
        user.setPassword(null);
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", user));
    }
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<User>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        
        Sort sort = direction.equalsIgnoreCase("asc") ? 
                   Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        PageResponse<User> users = new PageResponse<>(userRepository.findAll(pageable));
        return ResponseEntity.ok(ApiResponse.success(users));
    }
    
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<User>>> searchUsers(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("firstName").ascending());
        PageResponse<User> users = userService.searchUsers(query, pageable);
        return ResponseEntity.ok(ApiResponse.success(users));
    }
    
    @GetMapping("/location")
    public ResponseEntity<ApiResponse<PageResponse<User>>> getUsersByLocation(
            @RequestParam(required = false) String province,
            @RequestParam(required = false) String district,
            @RequestParam(required = false) String sector,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("firstName").ascending());
        PageResponse<User> users = userService.getUsersByLocation(province, district, sector, pageable);
        
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("province", province);
        metadata.put("district", district);
        metadata.put("sector", sector);
        
        return ResponseEntity.ok(ApiResponse.success("Users filtered by location", users));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> getUserById(@PathVariable @NotNull Long id) {
        Long userId = Objects.requireNonNull(id, "User ID cannot be null");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setPassword(null);
        return ResponseEntity.ok(ApiResponse.success(user));
    }
    
    @PutMapping("/{id}/location")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<ApiResponse<Void>> updateUserLocation(
            @PathVariable Long id,
            @RequestParam Long locationId) {
        
        userService.updateUserLocation(id, locationId);
        return ResponseEntity.ok(ApiResponse.success("Location updated successfully", null));
    }
    
    @PostMapping("/{id}/follow")
    public ResponseEntity<ApiResponse<Void>> followUser(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long id) {
        
        userService.followUser(currentUser.getId(), id);
        return ResponseEntity.ok(ApiResponse.success("User followed successfully", null));
    }
    
    @PostMapping("/{id}/unfollow")
    public ResponseEntity<ApiResponse<Void>> unfollowUser(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long id) {
        
        userService.unfollowUser(currentUser.getId(), id);
        return ResponseEntity.ok(ApiResponse.success("User unfollowed successfully", null));
    }
    
    @GetMapping("/{id}/followers")
    public ResponseEntity<ApiResponse<PageResponse<User>>> getFollowers(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("firstName").ascending());
        PageResponse<User> followers = userService.getFollowers(id, pageable);
        return ResponseEntity.ok(ApiResponse.success(followers));
    }
    
    @GetMapping("/{id}/following")
    public ResponseEntity<ApiResponse<PageResponse<User>>> getFollowing(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("firstName").ascending());
        PageResponse<User> following = userService.getFollowing(id, pageable);
        return ResponseEntity.ok(ApiResponse.success(following));
    }
    
    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userRepository.count());
        stats.put("totalAdmins", userService.countUsersByRole(Role.ADMIN));
        stats.put("totalNgos", userService.countUsersByRole(Role.NGO));
        stats.put("totalDonors", userService.countUsersByRole(Role.USER));
        stats.put("totalVerified", userRepository.count());
        
        return ResponseEntity.ok(ApiResponse.success(stats));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable @NotNull Long id) {
        Long userId = Objects.requireNonNull(id, "User ID cannot be null");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setActive(false);
        userRepository.save(user);
        
        return ResponseEntity.ok(ApiResponse.success("User deactivated successfully", null));
    }
}