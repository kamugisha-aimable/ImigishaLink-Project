package com.imigishalink.location;

import com.imigishalink.common.ApiResponse;
import com.imigishalink.common.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.constraints.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/locations")
@RequiredArgsConstructor
public class LocationController {
    
    private final LocationRepository locationRepository;
    
    @GetMapping("/provinces")
    public ResponseEntity<ApiResponse<List<String>>> getAllProvinces() {
        List<String> provinces = locationRepository.findAllProvinces();
        return ResponseEntity.ok(ApiResponse.success(provinces));
    }
    
    @GetMapping("/districts")
    public ResponseEntity<ApiResponse<List<String>>> getDistrictsByProvince(
            @RequestParam String province) {
        List<String> districts = locationRepository.findDistrictsByProvince(province);
        return ResponseEntity.ok(ApiResponse.success(districts));
    }
    
    @GetMapping("/sectors")
    public ResponseEntity<ApiResponse<List<String>>> getSectorsByDistrict(
            @RequestParam String district) {
        List<String> sectors = locationRepository.findSectorsByDistrict(district);
        return ResponseEntity.ok(ApiResponse.success(sectors));
    }
    
    @GetMapping("/cells")
    public ResponseEntity<ApiResponse<List<String>>> getCellsBySector(
            @RequestParam String sector) {
        List<String> cells = locationRepository.findCellsBySector(sector);
        return ResponseEntity.ok(ApiResponse.success(cells));
    }
    
    @GetMapping("/villages")
    public ResponseEntity<ApiResponse<List<String>>> getVillagesByCell(
            @RequestParam String cell) {
        List<String> villages = locationRepository.findVillagesByCell(cell);
        return ResponseEntity.ok(ApiResponse.success(villages));
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<Location>>> getAllLocations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String province,
            @RequestParam(required = false) String district,
            @RequestParam(required = false) String search) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("province", "district", "sector").ascending());
        Page<Location> locations;
        
        if (search != null && !search.trim().isEmpty()) {
            locations = locationRepository.searchLocations(search, pageable);
        } else if (province != null && district != null) {
            locations = locationRepository.findByDistrict(district, pageable);
        } else if (province != null) {
            locations = locationRepository.findByProvince(province, pageable);
        } else {
            locations = locationRepository.findAll(pageable);
        }
        
        PageResponse<Location> response = new PageResponse<>(locations);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Location>> getLocationById(@PathVariable @NotNull Long id) {
        Long locationId = Objects.requireNonNull(id, "Location ID cannot be null");
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new RuntimeException("Location not found"));
        return ResponseEntity.ok(ApiResponse.success(location));
    }
    
    @GetMapping("/{id}/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getLocationStats(@PathVariable @NotNull Long id) {
        Long locationId = Objects.requireNonNull(id, "Location ID cannot be null");
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new RuntimeException("Location not found"));
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("location", location.getFullAddress());
        stats.put("totalUsers", locationRepository.countUsersByLocation(locationId));
        stats.put("totalNgos", locationRepository.countNgosByLocation(locationId));
        stats.put("totalDonations", locationRepository.countDonationsByLocation(locationId));
        stats.put("subLocationsCount", location.getSubLocations().size());
        
        return ResponseEntity.ok(ApiResponse.success(stats));
    }
    
    @GetMapping("/hierarchy")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getLocationHierarchy() {
        List<String> provinces = locationRepository.findAllProvinces();
        
        Map<String, Object> hierarchy = new HashMap<>();
        for (String province : provinces) {
            List<String> districts = locationRepository.findDistrictsByProvince(province);
            Map<String, List<String>> districtMap = new HashMap<>();
            
            for (String district : districts) {
                List<String> sectors = locationRepository.findSectorsByDistrict(district);
                districtMap.put(district, sectors);
            }
            
            hierarchy.put(province, districtMap);
        }
        
        return ResponseEntity.ok(ApiResponse.success(hierarchy));
    }
}