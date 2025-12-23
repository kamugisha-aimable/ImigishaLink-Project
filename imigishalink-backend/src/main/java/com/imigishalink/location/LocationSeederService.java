package com.imigishalink.location;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocationSeederService {
    
    private final LocationRepository locationRepository;
    
    @Transactional
    public void seedAllRwandanLocations() {
        log.info("Starting to seed Rwandan administrative locations...");
        
        if (locationRepository.count() > 0) {
            log.info("Locations already exist. Skipping seed.");
            return;
        }
        
        List<Location> locations = new ArrayList<>();
        
        // Get all provinces (excluding City of Kigali)
        List<String> provinces = RwandaLocationData.getProvinces();
        Map<String, List<String>> districtsByProvince = RwandaLocationData.getDistrictsByProvince();
        Map<String, List<String>> sectorsByDistrict = RwandaLocationData.getSectorsByDistrict();
        Map<String, List<String>> cellsBySector = RwandaLocationData.getCellsBySector();
        Map<String, List<String>> villagesByCell = RwandaLocationData.getVillagesByCell();
        
        // Create locations for each province -> district -> sector -> cell -> village
        for (String province : provinces) {
            List<String> districts = districtsByProvince.getOrDefault(province, new ArrayList<>());
            
            for (String district : districts) {
                List<String> sectors = sectorsByDistrict.getOrDefault(district, new ArrayList<>());
                
                for (String sector : sectors) {
                    List<String> cells = cellsBySector.getOrDefault(sector, new ArrayList<>());
                    
                    // If no cells defined for this sector, create at least one location entry
                    if (cells.isEmpty()) {
                        Location location = createLocation(province, district, sector, null, null);
                        locations.add(location);
                    } else {
                        for (String cell : cells) {
                            List<String> villages = villagesByCell.getOrDefault(cell, new ArrayList<>());
                            
                            // If no villages defined for this cell, create at least one location entry
                            if (villages.isEmpty()) {
                                Location location = createLocation(province, district, sector, cell, null);
                                locations.add(location);
                            } else {
                                for (String village : villages) {
                                    Location location = createLocation(province, district, sector, cell, village);
                                    locations.add(location);
                                }
                            }
                        }
                    }
                }
            }
        }
        
        // Save all locations
        locationRepository.saveAll(locations);
        log.info("Successfully seeded {} Rwandan administrative locations", locations.size());
    }
    
    private Location createLocation(String province, String district, String sector, String cell, String village) {
        Location location = new Location();
        location.setCountry("Rwanda");
        location.setProvince(province);
        location.setDistrict(district);
        location.setSector(sector);
        location.setCell(cell);
        location.setVillage(village);
        location.setCreatedAt(LocalDateTime.now());
        location.setActive(true);
        return location;
    }
}

