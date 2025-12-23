package com.imigishalink.users;

import com.imigishalink.common.PageResponse;
import com.imigishalink.location.Location;
import com.imigishalink.location.LocationRepository;
import com.imigishalink.location.RwandaLocationData;
import com.imigishalink.ngos.NGO;
import com.imigishalink.ngos.NGORepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {
    
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final NGORepository ngoRepository;
    private final PasswordEncoder passwordEncoder;
    private final Random random = new Random();
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }
    
    @Transactional
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
        
        User savedUser = userRepository.save(user);
        
        // If user is registering as NGO role, link them to the NGO with matching email
        if (user.getRole() == Role.NGO) {
            Optional<NGO> ngoOptional = ngoRepository.findByEmail(user.getEmail());
            if (ngoOptional.isPresent()) {
                NGO ngo = ngoOptional.get();
                ngo.getAdmins().add(savedUser);
                savedUser.getManagedNgos().add(ngo);
                ngoRepository.save(ngo);
                userRepository.save(savedUser);
                log.info("Linked user {} to NGO: {}", user.getEmail(), ngo.getName());
            }
        }
        
        return savedUser;
    }
    
    /**
     * Check if there's an NGO with matching email
     */
    public boolean hasNGOWithMatchingEmail(String email) {
        return ngoRepository.findByEmail(email).isPresent();
    }
    
    /**
     * Find existing location or create a new one if it doesn't exist
     */
    @Transactional
    public Location findOrCreateLocation(String province, String district, String sector, String cell, String village) {
        // Try to find existing location
        Optional<Location> existing = locationRepository.findByProvinceAndDistrictAndSectorAndCellAndVillage(
                province, district, sector, cell, village);
        
        if (existing.isPresent()) {
            return existing.get();
        }
        
        // Create new location if not found
        Location location = new Location();
        location.setCountry("Rwanda");
        location.setProvince(province);
        location.setDistrict(district);
        location.setSector(sector);
        location.setCell(cell);
        location.setVillage(village);
        location.setCreatedAt(LocalDateTime.now());
        location.setActive(true);
        
        return locationRepository.save(location);
    }
    
    /**
     * Ensure NGO user is automatically linked to their NGO (by matching email)
     * This is called during login to ensure NGO users are always members of their NGO
     */
    @Transactional
    public void ensureNGOUserLinked(User user) {
        if (user.getRole() == Role.NGO) {
            // Refresh user to get latest state
            User refreshedUser = userRepository.findById(user.getId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Check if user is already linked to an NGO
            if (refreshedUser.getManagedNgos() == null || refreshedUser.getManagedNgos().isEmpty()) {
                // Try to find NGO with matching email
                Optional<NGO> ngoOptional = ngoRepository.findByEmail(refreshedUser.getEmail());
                if (ngoOptional.isPresent()) {
                    NGO ngo = ngoOptional.get();
                    // Link user to NGO (bidirectional relationship)
                    if (ngo.getAdmins() == null) {
                        ngo.setAdmins(new HashSet<>());
                    }
                    if (!ngo.getAdmins().contains(refreshedUser)) {
                        ngo.getAdmins().add(refreshedUser);
                    }
                    if (refreshedUser.getManagedNgos() == null) {
                        refreshedUser.setManagedNgos(new HashSet<>());
                    }
                    if (!refreshedUser.getManagedNgos().contains(ngo)) {
                        refreshedUser.getManagedNgos().add(ngo);
                    }
                    ngoRepository.save(ngo);
                    userRepository.save(refreshedUser);
                    log.info("Auto-linked NGO user {} to NGO: {} during login", refreshedUser.getEmail(), ngo.getName());
                } else {
                    log.warn("NGO user {} logged in but no NGO found with matching email", refreshedUser.getEmail());
                }
            } else {
                log.debug("NGO user {} is already linked to {} NGO(s)", refreshedUser.getEmail(), refreshedUser.getManagedNgos().size());
            }
        }
    }
    
    @Transactional
    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already registered");
        }
        
        if (user.getPhoneNumber() != null && userRepository.existsByPhoneNumber(user.getPhoneNumber())) {
            throw new RuntimeException("Phone number already registered");
        }
        
        // Encode password if provided
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            throw new RuntimeException("Password is required");
        }
        
        // Set default role if not provided
        if (user.getRole() == null) {
            user.setRole(Role.USER);
        }
        
        // Auto-verify admin-created users
        user.setVerified(true);
        user.setVerificationCode(null);
        user.setVerificationCodeExpiry(null);
        user.setActive(true);
        
        log.info("Admin creating user: {} with role: {}", user.getEmail(), user.getRole());
        
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
        
        // Update role if provided (admin can change roles)
        if (updatedUser.getRole() != null) {
            existingUser.setRole(updatedUser.getRole());
        }
        
        // Update password if provided
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }
        
        // Update verification status if provided (check for both isVerified and verified field names)
        if (updatedUser.isVerified() != existingUser.isVerified()) {
            existingUser.setVerified(updatedUser.isVerified());
        }
        
        // Update active status if provided (check for both isActive and active field names)
        if (updatedUser.isActive() != existingUser.isActive()) {
            existingUser.setActive(updatedUser.isActive());
        }
        
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
    
    /**
     * Assign locations to all existing users that don't have locations,
     * or reassign locations to all users to distribute them across Rwanda.
     * Uses the Rwandan administrative structure from RwandaLocationData to ensure
     * proper distribution across provinces, districts, and sectors.
     */
    @Transactional
    public int assignLocationsToUsers(boolean reassignAll) {
        log.info("Starting to assign locations to users using Rwandan structure (reassignAll: {})...", reassignAll);
        
        // Get all users
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            log.warn("No users found to assign locations");
            return 0;
        }
        
        // Filter users that need locations
        List<User> usersToUpdate = new ArrayList<>();
        if (reassignAll) {
            usersToUpdate = users;
            log.info("Reassigning locations to all {} users", users.size());
        } else {
            for (User user : users) {
                if (user.getLocation() == null) {
                    usersToUpdate.add(user);
                }
            }
            log.info("Assigning locations to {} users without locations (out of {})", 
                    usersToUpdate.size(), users.size());
        }
        
        if (usersToUpdate.isEmpty()) {
            log.info("All users already have locations assigned");
            return 0;
        }
        
        // Use Rwandan administrative structure to get locations
        List<String> provinces = RwandaLocationData.getProvinces();
        Map<String, List<String>> districtsByProvince = RwandaLocationData.getDistrictsByProvince();
        Map<String, List<String>> sectorsByDistrict = RwandaLocationData.getSectorsByDistrict();
        Map<String, List<String>> cellsBySector = RwandaLocationData.getCellsBySector();
        Map<String, List<String>> villagesByCell = RwandaLocationData.getVillagesByCell();
        
        // Build a list of all sector-level locations (sectors are the primary location identifier)
        List<LocationInfo> sectorLocations = new ArrayList<>();
        
        for (String province : provinces) {
            List<String> districts = districtsByProvince.getOrDefault(province, new ArrayList<>());
            for (String district : districts) {
                List<String> sectors = sectorsByDistrict.getOrDefault(district, new ArrayList<>());
                for (String sector : sectors) {
                    // Get first cell and village if available, otherwise use null
                    List<String> cells = cellsBySector.getOrDefault(sector, new ArrayList<>());
                    String cell = cells.isEmpty() ? null : cells.get(0);
                    
                    String village = null;
                    if (cell != null) {
                        List<String> villages = villagesByCell.getOrDefault(cell, new ArrayList<>());
                        village = villages.isEmpty() ? null : villages.get(0);
                    }
                    
                    sectorLocations.add(new LocationInfo(province, district, sector, cell, village));
                }
            }
        }
        
        if (sectorLocations.isEmpty()) {
            log.warn("No sector locations found in Rwandan structure");
            return 0;
        }
        
        log.info("Found {} sector locations from Rwandan structure to distribute among {} users", 
                sectorLocations.size(), usersToUpdate.size());
        
        // Find or create locations in database and assign to users
        int updatedCount = 0;
        int locationIndex = 0;
        
        for (User user : usersToUpdate) {
            // Cycle through sector locations to ensure even distribution
            LocationInfo locationInfo = sectorLocations.get(locationIndex % sectorLocations.size());
            
            // Find or create the location in database
            Location location = findOrCreateLocation(
                    locationInfo.province,
                    locationInfo.district,
                    locationInfo.sector,
                    locationInfo.cell,
                    locationInfo.village
            );
            
            user.setLocation(location);
            userRepository.save(user);
            locationIndex++;
            updatedCount++;
            
            if (updatedCount % 10 == 0) {
                log.info("Assigned locations to {}/{} users", updatedCount, usersToUpdate.size());
            }
        }
        
        log.info("Successfully assigned locations to {} users using Rwandan administrative structure", updatedCount);
        return updatedCount;
    }
    
    /**
     * Helper class to hold location information
     */
    private static class LocationInfo {
        final String province;
        final String district;
        final String sector;
        final String cell;
        final String village;
        
        LocationInfo(String province, String district, String sector, String cell, String village) {
            this.province = province;
            this.district = district;
            this.sector = sector;
            this.cell = cell;
            this.village = village;
        }
    }
}