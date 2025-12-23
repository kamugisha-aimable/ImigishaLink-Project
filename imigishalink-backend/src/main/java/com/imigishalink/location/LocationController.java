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
        // If no provinces in DB, return Rwandan provinces from static data
        if (provinces.isEmpty()) {
            provinces = RwandaLocationData.getProvinces();
        } else {
            // Filter out City of Kigali and ensure we have the 4 main provinces
            provinces.removeIf(p -> p.contains("Kigali") || p.equalsIgnoreCase("City of Kigali"));
            // Ensure all 4 provinces are present
            List<String> rwandaProvinces = RwandaLocationData.getProvinces();
            for (String province : rwandaProvinces) {
                if (!provinces.contains(province)) {
                    provinces.add(province);
                }
            }
            provinces.sort((a, b) -> {
                int aIndex = rwandaProvinces.indexOf(a);
                int bIndex = rwandaProvinces.indexOf(b);
                if (aIndex != -1 && bIndex != -1) return aIndex - bIndex;
                if (aIndex != -1) return -1;
                if (bIndex != -1) return 1;
                return a.compareTo(b);
            });
        }
        return ResponseEntity.ok(ApiResponse.success(provinces));
    }
    
    @GetMapping("/districts")
    public ResponseEntity<ApiResponse<List<String>>> getDistrictsByProvince(
            @RequestParam String province) {
        List<String> districts = locationRepository.findDistrictsByProvince(province);
        // If no districts in DB, return from static data
        if (districts.isEmpty()) {
            districts = RwandaLocationData.getDistricts(province);
        }
        return ResponseEntity.ok(ApiResponse.success(districts));
    }
    
    @GetMapping("/sectors")
    public ResponseEntity<ApiResponse<List<String>>> getSectorsByDistrict(
            @RequestParam String district,
            @RequestParam(required = false) String province) {
        List<String> sectors;
        if (province != null && !province.trim().isEmpty()) {
            // Use hierarchical query to ensure sectors belong to both district and province
            sectors = locationRepository.findSectorsByDistrictAndProvince(district, province);
        } else {
            sectors = locationRepository.findSectorsByDistrict(district);
        }
        // If no sectors in DB, return from static data
        if (sectors.isEmpty()) {
            sectors = RwandaLocationData.getSectors(district);
        }
        return ResponseEntity.ok(ApiResponse.success(sectors));
    }
    
    @GetMapping("/cells")
    public ResponseEntity<ApiResponse<List<String>>> getCellsBySector(
            @RequestParam String sector,
            @RequestParam(required = false) String district,
            @RequestParam(required = false) String province) {
        List<String> cells;
        if (province != null && district != null && !province.trim().isEmpty() && !district.trim().isEmpty()) {
            // Use hierarchical query to ensure cells belong to sector, district, and province
            cells = locationRepository.findCellsBySectorDistrictAndProvince(sector, district, province);
        } else {
            cells = locationRepository.findCellsBySector(sector);
        }
        // If no cells in DB, return from static data
        if (cells.isEmpty()) {
            cells = RwandaLocationData.getCells(sector);
        }
        return ResponseEntity.ok(ApiResponse.success(cells));
    }
    
    @GetMapping("/villages")
    public ResponseEntity<ApiResponse<List<String>>> getVillagesByCell(
            @RequestParam String cell,
            @RequestParam(required = false) String sector,
            @RequestParam(required = false) String district,
            @RequestParam(required = false) String province) {
        List<String> villages;
        if (province != null && district != null && sector != null 
            && !province.trim().isEmpty() && !district.trim().isEmpty() && !sector.trim().isEmpty()) {
            // Use hierarchical query to ensure villages belong to cell, sector, district, and province
            villages = locationRepository.findVillagesByCellSectorDistrictAndProvince(cell, sector, district, province);
        } else {
            villages = locationRepository.findVillagesByCell(cell);
        }
        // If no villages in DB, return from static data
        if (villages.isEmpty()) {
            villages = RwandaLocationData.getVillages(cell);
        }
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