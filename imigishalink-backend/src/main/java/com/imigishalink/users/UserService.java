package com.imigishalink.users;

import com.imigishalink.common.PageResponse;
import com.imigishalink.location.Location;
import com.imigishalink.location.LocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {
    
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final PasswordEncoder passwordEncoder;
    private final Random random = new Random();
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }
    
    public User registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already registered");
        }
        
        if (user.getPhoneNumber() != null && userRepository.existsByPhoneNumber(user.getPhoneNumber())) {
            throw new RuntimeException("Phone number already registered");
        }
        
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // Role is already set from request, only set default if not provided
        if (user.getRole() == null) {
            user.setRole(Role.USER);
        }
        // Auto-verify users on registration so they can login immediately
        // Email verification can be implemented later if needed
        user.setVerified(true);
        user.setVerificationCode(null);
        user.setVerificationCodeExpiry(null);
        
        log.info("Registering user: {} with role: {}", user.getEmail(), user.getRole());
        
        return userRepository.save(user);
    }
    
    public User updateUser(Long userId, User updatedUser) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Check email uniqueness if changed
        if (!existingUser.getEmail().equals(updatedUser.getEmail()) &&
            userRepository.existsByEmail(updatedUser.getEmail())) {
            throw new RuntimeException("Email already taken");
        }
        
        // Check phone uniqueness if changed
        if (updatedUser.getPhoneNumber() != null &&
            !updatedUser.getPhoneNumber().equals(existingUser.getPhoneNumber()) &&
            userRepository.existsByPhoneNumber(updatedUser.getPhoneNumber())) {
            throw new RuntimeException("Phone number already taken");
        }
        
        existingUser.setFirstName(updatedUser.getFirstName());
        existingUser.setLastName(updatedUser.getLastName());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setPhoneNumber(updatedUser.getPhoneNumber());
        existingUser.setBio(updatedUser.getBio());
        existingUser.setDateOfBirth(updatedUser.getDateOfBirth());
        existingUser.setProfileImageUrl(updatedUser.getProfileImageUrl());
        
        return userRepository.save(existingUser);
    }
    
    public void updateUserLocation(Long userId, Long locationId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new RuntimeException("Location not found"));
        
        user.setLocation(location);
        userRepository.save(user);
    }
    
    public boolean verifyUser(String verificationCode) {
        Optional<User> userOptional = userRepository.findByVerificationCode(verificationCode);
        
        if (userOptional.isEmpty()) {
            return false;
        }
        
        User user = userOptional.get();
        
        if (user.getVerificationCodeExpiry().isBefore(LocalDate.now())) {
            throw new RuntimeException("Verification code expired");
        }
        
        user.setVerified(true);
        user.setVerificationCode(null);
        user.setVerificationCodeExpiry(null);
        userRepository.save(user);
        
        return true;
    }
    
    public void followUser(Long followerId, Long followingId) {
        if (followerId.equals(followingId)) {
            throw new RuntimeException("Cannot follow yourself");
        }
        
        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new RuntimeException("Follower not found"));
        
        User following = userRepository.findById(followingId)
                .orElseThrow(() -> new RuntimeException("User to follow not found"));
        
        follower.follow(following);
        userRepository.save(follower);
    }
    
    public void unfollowUser(Long followerId, Long followingId) {
        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new RuntimeException("Follower not found"));
        
        User following = userRepository.findById(followingId)
                .orElseThrow(() -> new RuntimeException("User to unfollow not found"));
        
        follower.unfollow(following);
        userRepository.save(follower);
    }
    
    public PageResponse<User> getUsersByLocation(String province, String district, String sector, Pageable pageable) {
        Page<User> users;
        
        if (province != null && district != null) {
            users = userRepository.findByProvinceAndDistrict(province, district, pageable);
        } else if (province != null) {
            users = userRepository.findByLocationProvince(province, pageable);
        } else if (district != null) {
            users = userRepository.findByLocationDistrict(district, pageable);
        } else if (sector != null) {
            users = userRepository.findByLocationSector(sector, pageable);
        } else {
            users = userRepository.findActiveVerifiedUsers(pageable);
        }
        
        return new PageResponse<>(users);
    }
    
    public PageResponse<User> searchUsers(String searchTerm, Pageable pageable) {
        Page<User> users = userRepository.searchUsers(searchTerm, pageable);
        return new PageResponse<>(users);
    }
    
    public List<String> getProvincesByRole(Role role) {
        return userRepository.findProvincesByRole(role);
    }
    
    public long countUsersByRole(Role role) {
        return userRepository.countByRole(role);
    }
    
    public PageResponse<User> getFollowers(Long userId, Pageable pageable) {
        Page<User> followers = userRepository.findFollowers(userId, pageable);
        return new PageResponse<>(followers);
    }
    
    public PageResponse<User> getFollowing(Long userId, Pageable pageable) {
        Page<User> following = userRepository.findFollowing(userId, pageable);
        return new PageResponse<>(following);
    }
    
    private String generateVerificationCode() {
        return String.format("%06d", random.nextInt(999999));
    }
}