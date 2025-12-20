package com.imigishalink.search;

import com.imigishalink.common.ApiResponse;
import com.imigishalink.common.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
public class SearchController {
    
    private final SearchService searchService;
    
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<SearchService.SearchResult>> searchAll(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        SearchService.SearchResult result = searchService.searchAll(query, pageable);
        
        return ResponseEntity.ok(ApiResponse.success(result));
    }
    
    @PostMapping("/advanced")
    public ResponseEntity<ApiResponse<SearchService.SearchResult>> advancedSearch(
            @RequestBody SearchService.SearchCriteria criteria,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        SearchService.SearchResult result = searchService.advancedSearch(criteria, pageable);
        
        return ResponseEntity.ok(ApiResponse.success("Advanced search results", result));
    }
    
    @GetMapping("/users/location")
    public ResponseEntity<ApiResponse<PageResponse<com.imigishalink.users.User>>> searchUsersByLocation(
            @RequestParam(required = false) String province,
            @RequestParam(required = false) String district,
            @RequestParam(required = false) String sector,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        SearchService.LocationSearchCriteria criteria = SearchService.LocationSearchCriteria.builder()
                .province(province)
                .district(district)
                .sector(sector)
                .build();
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("firstName").ascending());
        var users = searchService.searchUsersByLocation(criteria, pageable);
        
        PageResponse<com.imigishalink.users.User> response = new PageResponse<>(users);
        
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("province", province);
        metadata.put("district", district);
        metadata.put("sector", sector);
        
        return ResponseEntity.ok(ApiResponse.success("Users by location", response));
    }
    
    @GetMapping("/ngos")
    public ResponseEntity<ApiResponse<PageResponse<com.imigishalink.ngos.NGO>>> searchNGOs(
            @RequestParam(required = false) String province,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Boolean verified,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        var ngos = searchService.searchNGOsByLocationAndCategory(province, categoryId, pageable);
        
        PageResponse<com.imigishalink.ngos.NGO> response = new PageResponse<>(ngos);
        return ResponseEntity.ok(ApiResponse.success("NGOs search results", response));
    }
    
    @GetMapping("/donations")
    public ResponseEntity<ApiResponse<PageResponse<com.imigishalink.donations.Donation>>> searchDonations(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String province,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) com.imigishalink.donations.DonationStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        SearchService.DonationSearchCriteria criteria = SearchService.DonationSearchCriteria.builder()
                .searchTerm(search)
                .province(province)
                .categoryId(categoryId)
                .status(status)
                .build();
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        var donations = searchService.searchDonationsByCriteria(criteria, pageable);
        
        PageResponse<com.imigishalink.donations.Donation> response = new PageResponse<>(donations);
        return ResponseEntity.ok(ApiResponse.success("Donations search results", response));
    }
    
    @GetMapping("/donations/urgent")
    public ResponseEntity<ApiResponse<PageResponse<com.imigishalink.donations.Donation>>> findUrgentDonations(
            @RequestParam(required = false) String province,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("priorityLevel").descending());
        var donations = searchService.findUrgentDonations(province, pageable);
        
        PageResponse<com.imigishalink.donations.Donation> response = new PageResponse<>(donations);
        return ResponseEntity.ok(ApiResponse.success("Urgent donations", response));
    }
    
    @GetMapping("/suggestions")
    public ResponseEntity<ApiResponse<SearchService.SearchSuggestions>> getSearchSuggestions(
            @RequestParam String query) {
        
        SearchService.SearchSuggestions suggestions = searchService.getSearchSuggestions(query);
        return ResponseEntity.ok(ApiResponse.success("Search suggestions", suggestions));
    }
    
    @GetMapping("/analytics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSearchAnalytics() {
        Map<String, Object> analytics = searchService.getSearchAnalytics();
        return ResponseEntity.ok(ApiResponse.success("Search analytics", analytics));
    }
    
    @GetMapping("/nearby/users")
    public ResponseEntity<ApiResponse<PageResponse<com.imigishalink.users.User>>> findNearbyUsers(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "10.0") Double radiusKm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("firstName").ascending());
        var users = searchService.findNearbyUsers(userId, radiusKm, pageable);
        
        PageResponse<com.imigishalink.users.User> response = new PageResponse<>(users);
        
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("radiusKm", radiusKm);
        metadata.put("userId", userId);
        
        return ResponseEntity.ok(ApiResponse.success("Nearby users", response));
    }
    
    @GetMapping("/donors/category")
    public ResponseEntity<ApiResponse<PageResponse<com.imigishalink.users.User>>> findDonorsByCategory(
            @RequestParam Long categoryId,
            @RequestParam(required = false) String province,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("firstName").ascending());
        var donors = searchService.findDonorsByCategory(categoryId, province, pageable);
        
        PageResponse<com.imigishalink.users.User> response = new PageResponse<>(donors);
        
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("categoryId", categoryId);
        metadata.put("province", province);
        
        return ResponseEntity.ok(ApiResponse.success("Donors by category", response));
    }
}