
package com.imigishalink.search;

import com.imigishalink.categories.Category;
import com.imigishalink.categories.CategoryRepository;
import com.imigishalink.communities.Community;
import com.imigishalink.communities.CommunityRepository;
import com.imigishalink.donations.Donation;
import com.imigishalink.donations.DonationRepository;
import com.imigishalink.donations.DonationStatus;
import com.imigishalink.donations.DonationType;
import com.imigishalink.location.Location;
import com.imigishalink.location.LocationRepository;
import com.imigishalink.ngos.NGO;
import com.imigishalink.ngos.NGORepository;
import com.imigishalink.users.Role;
import com.imigishalink.users.User;
import com.imigishalink.users.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchService {
    
    private final UserRepository userRepository;
    private final NGORepository ngoRepository;
    private final DonationRepository donationRepository;
    private final CommunityRepository communityRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    
    /**
     * Comprehensive search across all entities
     */
    public SearchResult searchAll(String query, Pageable pageable) {
        SearchResult result = new SearchResult();
        
        // Search users
        Page<User> users = userRepository.searchUsers(query, pageable);
        result.setUsers(users);
        
        // Search NGOs
        Page<NGO> ngos = ngoRepository.searchNgos(query, pageable);
        result.setNgos(ngos);
        
        // Search donations
        Page<Donation> donations = donationRepository.searchDonations(query, pageable);
        result.setDonations(donations);
        
        // Search communities
        Page<Community> communities = communityRepository.searchCommunities(query, pageable);
        result.setCommunities(communities);
        
        // Search categories
        Page<Category> categories = categoryRepository.searchCategories(query, pageable);
        result.setCategories(categories);
        
        // Search locations
        Page<Location> locations = locationRepository.searchLocations(query, pageable);
        result.setLocations(locations);
        
        // Set counts
        result.setTotalUsers(users.getTotalElements());
        result.setTotalNgos(ngos.getTotalElements());
        result.setTotalDonations(donations.getTotalElements());
        result.setTotalCommunities(communities.getTotalElements());
        result.setTotalCategories(categories.getTotalElements());
        result.setTotalLocations(locations.getTotalElements());
        
        return result;
    }
    
    /**
     * Advanced search with multiple criteria
     */
    public SearchResult advancedSearch(SearchCriteria criteria, Pageable pageable) {
        SearchResult result = new SearchResult();
        
        // Users search with criteria
        if (criteria.isSearchUsers()) {
            Page<User> users = searchUsersWithCriteria(criteria, pageable);
            result.setUsers(users);
            result.setTotalUsers(users.getTotalElements());
        }
        
        // NGOs search with criteria
        if (criteria.isSearchNgos()) {
            Page<NGO> ngos = searchNGOsWithCriteria(criteria, pageable);
            result.setNgos(ngos);
            result.setTotalNgos(ngos.getTotalElements());
        }
        
        // Donations search with criteria
        if (criteria.isSearchDonations()) {
            Page<Donation> donations = searchDonationsWithCriteria(criteria, pageable);
            result.setDonations(donations);
            result.setTotalDonations(donations.getTotalElements());
        }
        
        // Communities search with criteria
        if (criteria.isSearchCommunities()) {
            Page<Community> communities = searchCommunitiesWithCriteria(criteria, pageable);
            result.setCommunities(communities);
            result.setTotalCommunities(communities.getTotalElements());
        }
        
        return result;
    }
    
    /**
     * Search users by location with detailed filtering
     */
    public Page<User> searchUsersByLocation(LocationSearchCriteria criteria, Pageable pageable) {
        // Build custom query based on criteria
        if (criteria.getProvince() != null && criteria.getDistrict() != null && criteria.getSector() != null) {
            // Exact location search
            return userRepository.findByProvinceAndDistrict(
                    criteria.getProvince(), criteria.getDistrict(), pageable);
        } else if (criteria.getProvince() != null && criteria.getDistrict() != null) {
            return userRepository.findByProvinceAndDistrict(
                    criteria.getProvince(), criteria.getDistrict(), pageable);
        } else if (criteria.getProvince() != null) {
            return userRepository.findByLocationProvince(criteria.getProvince(), pageable);
        } else if (criteria.getDistrict() != null) {
            return userRepository.findByLocationDistrict(criteria.getDistrict(), pageable);
        } else if (criteria.getSector() != null) {
            return userRepository.findByLocationSector(criteria.getSector(), pageable);
        } else if (criteria.getLocationId() != null) {
            return userRepository.findByLocationId(criteria.getLocationId(), pageable);
        }
        
        // Default: return all active verified users
        return userRepository.findActiveVerifiedUsers(pageable);
    }
    
    /**
     * Search NGOs by location and category
     */
    public Page<NGO> searchNGOsByLocationAndCategory(String province, Long categoryId, Pageable pageable) {
        if (province != null && categoryId != null) {
            // Combine location and category filters
            return ngoRepository.findByHeadOfficeLocationProvinceAndCategoryId(province, categoryId, pageable);
        } else if (province != null) {
            return ngoRepository.findByHeadOfficeLocationProvince(province, pageable);
        } else if (categoryId != null) {
            return ngoRepository.findByCategoryId(categoryId, pageable);
        }

        return ngoRepository.findByIsVerifiedTrue(pageable);
    }
    
    /**
     * Search donations by multiple criteria
     */
    public Page<Donation> searchDonationsByCriteria(DonationSearchCriteria criteria, Pageable pageable) {
        // If search term provided, use text search
        if (criteria.getSearchTerm() != null && !criteria.getSearchTerm().trim().isEmpty()) {
            return donationRepository.searchDonations(criteria.getSearchTerm(), pageable);
        }
        
        // Build custom query based on multiple criteria
        if (criteria.getProvince() != null && criteria.getDistrict() != null) {
            return donationRepository.findByProvinceAndDistrict(
                    criteria.getProvince(), criteria.getDistrict(), pageable);
        } else if (criteria.getProvince() != null) {
            return donationRepository.findByProvince(criteria.getProvince(), pageable);
        } else if (criteria.getCategoryId() != null) {
            return donationRepository.findByCategoryId(criteria.getCategoryId(), pageable);
        } else if (criteria.getNgoId() != null) {
            return donationRepository.findByNgoId(criteria.getNgoId(), pageable);
        } else if (criteria.getStatus() != null) {
            // use type-safe enum-based status query
            return donationRepository.findByStatus(criteria.getStatus(), pageable);
        } else if (criteria.getType() != null) {
            return donationRepository.findByType(criteria.getType(), pageable);
        }
        
        // Default: show open donations by priority
        return donationRepository.findOpenDonationsByPriority(pageable);
    }
    
    /**
     * Find nearby users based on location
     */
    public Page<User> findNearbyUsers(Long userId, Double radiusKm, Pageable pageable) {
        // Get current user's location
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (currentUser.getLocation() == null ||
            currentUser.getLocation().getLatitude() == null ||
            currentUser.getLocation().getLongitude() == null) {
            // Fallback: return users from the same province
            if (currentUser.getLocation() != null) {
                return userRepository.findByLocationProvince(
                        currentUser.getLocation().getProvince(), pageable);
            }
            return Page.empty(pageable);
        }

        // Use spatial query to find users within radius
        return userRepository.findNearbyUsers(
                currentUser.getLocation().getLatitude(),
                currentUser.getLocation().getLongitude(),
                radiusKm != null ? radiusKm : 50.0, // Default 50km if not specified
                userId,
                pageable);
    }
    
    /**
     * Search for donors interested in specific categories
     */
    public Page<User> findDonorsByCategory(Long categoryId, String province, Pageable pageable) {
        if (categoryId == null) {
            // If no category specified, return all verified users
            if (province != null) {
                return userRepository.findByLocationProvince(province, pageable);
            }
            return userRepository.findActiveVerifiedUsers(pageable);
        }

        return userRepository.findDonorsByCategory(categoryId, province, pageable);
    }
    
    /**
     * Get search suggestions/autocomplete
     */
    public SearchSuggestions getSearchSuggestions(String query) {
        SearchSuggestions suggestions = new SearchSuggestions();
        
        if (query == null || query.trim().isEmpty()) {
            return suggestions;
        }
        
        String searchTerm = query.toLowerCase();
        
        // Get user suggestions
        Pageable userPageable = PageRequest.of(0, 5, Sort.by("firstName").ascending());
        Page<User> userSuggestions = userRepository.searchUsers(searchTerm, userPageable);
        suggestions.setUsers(userSuggestions.getContent());
        
        // Get NGO suggestions
        Pageable ngoPageable = PageRequest.of(0, 5, Sort.by("name").ascending());
        Page<NGO> ngoSuggestions = ngoRepository.searchNgos(searchTerm, ngoPageable);
        suggestions.setNgos(ngoSuggestions.getContent());
        
        // Get donation suggestions
        Pageable donationPageable = PageRequest.of(0, 5, Sort.by("title").ascending());
        Page<Donation> donationSuggestions = donationRepository.searchDonations(searchTerm, donationPageable);
        suggestions.setDonations(donationSuggestions.getContent());
        
        // Get community suggestions
        Pageable communityPageable = PageRequest.of(0, 5, Sort.by("name").ascending());
        Page<Community> communitySuggestions = communityRepository.searchCommunities(searchTerm, communityPageable);
        suggestions.setCommunities(communitySuggestions.getContent());
        
        // Get category suggestions
        Pageable categoryPageable = PageRequest.of(0, 5, Sort.by("name").ascending());
        Page<Category> categorySuggestions = categoryRepository.searchCategories(searchTerm, categoryPageable);
        suggestions.setCategories(categorySuggestions.getContent());
        
        return suggestions;
    }
    
    /**
     * Get search analytics and trends
     */
    public Map<String, Object> getSearchAnalytics() {
        Map<String, Object> analytics = new HashMap<>();
        
        // Most searched provinces
        List<Object[]> provinceStats = donationRepository.countDonationsByProvince();
        analytics.put("popularProvinces", provinceStats);
        
        // Most active categories
        List<Category> activeCategories = categoryRepository.findAll()
                .stream()
                .sorted((c1, c2) -> Long.compare(
                        categoryRepository.countDonationsByCategory(c2.getId()),
                        categoryRepository.countDonationsByCategory(c1.getId())))
                .limit(10)
                .toList();
        analytics.put("activeCategories", activeCategories);
        
        // Trending searches (simulated - in real app, track actual searches)
        List<String> trendingSearches = Arrays.asList(
                "Education", "Food", "Clothing", "Medical", "Emergency",
                "Kigali", "Musanze", "Huye", "Rubavu"
        );
        analytics.put("trendingSearches", trendingSearches);
        
        // Search volume by entity type
        Map<String, Long> searchVolume = new HashMap<>();
        searchVolume.put("users", userRepository.count());
        searchVolume.put("ngos", ngoRepository.count());
        searchVolume.put("donations", donationRepository.count());
        searchVolume.put("communities", communityRepository.count());
        analytics.put("searchVolume", searchVolume);
        
        return analytics;
    }
    
    /**
     * Search for urgent/priority donations
     */
    public Page<Donation> findUrgentDonations(String province, Pageable pageable) {
        if (province != null) {
            // Find urgent donations in specific province
            return donationRepository.findByProvinceAndUrgent(province, pageable);
        }
        
        // Find all urgent donations
        return donationRepository.findOpenDonationsByPriority(pageable);
    }
    
    // Private helper methods
    
    private Page<User> searchUsersWithCriteria(SearchCriteria criteria, Pageable pageable) {
        // Complex user search with multiple criteria
        // This is simplified - in production, use Specification or QueryDSL
        
        if (criteria.getSearchTerm() != null) {
            return userRepository.searchUsers(criteria.getSearchTerm(), pageable);
        }
        
        // Default: return all active users
        return userRepository.findActiveVerifiedUsers(pageable);
    }
    
    private Page<NGO> searchNGOsWithCriteria(SearchCriteria criteria, Pageable pageable) {
        if (criteria.getSearchTerm() != null) {
            return ngoRepository.searchNgos(criteria.getSearchTerm(), pageable);
        }

        // Filter by verification status
        if (criteria.isVerifiedOnly()) {
            return ngoRepository.findByIsVerifiedTrue(pageable);
        }

        // Default to verified NGOs only
        return ngoRepository.findByIsVerifiedTrue(pageable);
    }
    
    private Page<Donation> searchDonationsWithCriteria(SearchCriteria criteria, Pageable pageable) {
        DonationSearchCriteria donationCriteria = new DonationSearchCriteria();
        donationCriteria.setSearchTerm(criteria.getSearchTerm());
        donationCriteria.setProvince(criteria.getProvince());
        donationCriteria.setCategoryId(criteria.getCategoryId());
        
        return searchDonationsByCriteria(donationCriteria, pageable);
    }
    
    private Page<Community> searchCommunitiesWithCriteria(SearchCriteria criteria, Pageable pageable) {
        if (criteria.getSearchTerm() != null) {
            return communityRepository.searchCommunities(criteria.getSearchTerm(), pageable);
        }
        
        if (criteria.getProvince() != null) {
            return communityRepository.findByLocationProvince(criteria.getProvince(), pageable);
        }
        
        return communityRepository.findByIsPublicTrue(pageable);
    }
    
    // Custom repository method for NGO
    public interface CustomNGORepository {
        Page<NGO> findByHeadOfficeLocationProvinceAndCategoryId(String province, Long categoryId, Pageable pageable);
    }
    
    // Custom repository method for Donation
    public interface CustomDonationRepository {
        Page<Donation> findByProvinceAndUrgent(String province, Pageable pageable);
    }
    
    // Implementations of custom repositories (simplified)
    @Service
    public static class CustomNGORepositoryImpl implements CustomNGORepository {
        private final NGORepository ngoRepository;
        
        public CustomNGORepositoryImpl(NGORepository ngoRepository) {
            this.ngoRepository = ngoRepository;
        }
        
        @Override
        public Page<NGO> findByHeadOfficeLocationProvinceAndCategoryId(String province, Long categoryId, Pageable pageable) {
            // In real implementation, use EntityManager or QueryDSL
            // For now, we'll filter manually (not efficient for large datasets)
            Page<NGO> byProvince = ngoRepository.findByHeadOfficeLocationProvince(province, pageable);
            List<NGO> filtered = byProvince.getContent().stream()
                    .filter(ngo -> ngo.getCategories().stream()
                            .anyMatch(cat -> cat.getId().equals(categoryId)))
                    .toList();
            
            return new org.springframework.data.domain.PageImpl<>(filtered, pageable, filtered.size());
        }
    }
    
    @Service
    public static class CustomDonationRepositoryImpl implements CustomDonationRepository {
        private final DonationRepository donationRepository;
        
        public CustomDonationRepositoryImpl(DonationRepository donationRepository) {
            this.donationRepository = donationRepository;
        }
        
        @Override
        public Page<Donation> findByProvinceAndUrgent(String province, Pageable pageable) {
            // Find urgent donations in province
            Page<Donation> donations = donationRepository.findByProvince(province, pageable);
            List<Donation> urgent = donations.getContent().stream()
                    .filter(d -> d.getPriorityLevel() != null &&
                            (d.getPriorityLevel().name().equals("HIGH") || 
                             d.getPriorityLevel().name().equals("URGENT")))
                    .toList();
            
            return new org.springframework.data.domain.PageImpl<>(urgent, pageable, urgent.size());
        }
    }
    
    // DTO classes for search
    
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class SearchResult {
        private Page<User> users;
        private Page<NGO> ngos;
        private Page<Donation> donations;
        private Page<Community> communities;
        private Page<Category> categories;
        private Page<Location> locations;
        private long totalUsers;
        private long totalNgos;
        private long totalDonations;
        private long totalCommunities;
        private long totalCategories;
        private long totalLocations;
        public long getTotalResults() {
            return totalUsers + totalNgos + totalDonations + totalCommunities;
        }
    }
    
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class SearchCriteria {
        private String searchTerm;
        private String province;
        private String district;
        private String sector;
        private Long categoryId;
        private Long ngoId;
        private Long locationId;
        private DonationStatus donationStatus;
        private DonationType donationType;
        private boolean verifiedOnly;
        @lombok.Builder.Default
        private boolean searchUsers = true;
        @lombok.Builder.Default
        private boolean searchNgos = true;
        @lombok.Builder.Default
        private boolean searchDonations = true;
        @lombok.Builder.Default
        private boolean searchCommunities = true;
        private LocalDate startDate;
        private LocalDate endDate;
        private Double minValue;
        private Double maxValue;
        private Integer minQuantity;
        private Integer maxQuantity;
    }
    
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class LocationSearchCriteria {
        private String province;
        private String district;
        private String sector;
        private String cell;
        private String village;
        private Long locationId;
        private Double latitude;
        private Double longitude;
        private Double radiusKm;
        private Role userRole;
        private boolean verifiedOnly;
    }
    
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class DonationSearchCriteria {
        private String searchTerm;
        private String province;
        private String district;
        private Long categoryId;
        private Long ngoId;
        private Long userId;
        private DonationStatus status;
        private DonationType type;
        private PriorityLevel priorityLevel;
        private LocalDate deadlineBefore;
        private LocalDate deadlineAfter;
        private BigDecimal minValue;
        private BigDecimal maxValue;
        private String deliveryMethod;
        private boolean urgentOnly;
    }
    
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class SearchSuggestions {
        @lombok.Builder.Default
        private List<User> users = new ArrayList<>();
        @lombok.Builder.Default
        private List<NGO> ngos = new ArrayList<>();
        @lombok.Builder.Default
        private List<Donation> donations = new ArrayList<>();
        @lombok.Builder.Default
        private List<Community> communities = new ArrayList<>();
        @lombok.Builder.Default
        private List<Category> categories = new ArrayList<>();
        @lombok.Builder.Default
        private List<String> locations = new ArrayList<>();
    }
    
    // Enum for priority level
    public enum PriorityLevel {
        LOW, MEDIUM, HIGH, URGENT
    }
}