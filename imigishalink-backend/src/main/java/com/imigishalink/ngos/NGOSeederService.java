package com.imigishalink.ngos;

import com.imigishalink.categories.Category;
import com.imigishalink.categories.CategoryRepository;
import com.imigishalink.location.Location;
import com.imigishalink.location.LocationRepository;
import com.imigishalink.location.RwandaLocationData;
import com.imigishalink.users.Role;
import com.imigishalink.users.User;
import com.imigishalink.users.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class NGOSeederService {
    
    private final NGORepository ngoRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final Random random = new Random();
    
    @Transactional
    public int seedSampleNGOs(int count) {
        log.info("Starting to seed {} sample NGOs...", count);
        
        // Get or create test NGO users
        User ngoUser1 = userRepository.findByEmail("ngo@test.rw")
                .orElseGet(() -> createTestUser("NGO", "Admin", "ngo@test.rw", Role.NGO));
        User ngoUser2 = userRepository.findByEmail("ngo2@test.rw")
                .orElseGet(() -> createTestUser("NGO", "Two", "ngo2@test.rw", Role.NGO));
        User ngoUser3 = userRepository.findByEmail("ngo3@test.rw")
                .orElseGet(() -> createTestUser("NGO", "Three", "ngo3@test.rw", Role.NGO));
        
        // Get categories
        List<Category> categories = categoryRepository.findAll();
        if (categories.isEmpty()) {
            log.warn("No categories found. Please run data initialization first.");
            return 0;
        }
        
        // Get locations
        List<Location> locations = locationRepository.findAll();
        if (locations.isEmpty()) {
            log.warn("No locations found. Please run data initialization first.");
            return 0;
        }
        
        // Sample NGO data
        String[] names = {
            "Hope for Children Foundation",
            "Rwanda Health Initiative",
            "Education First Rwanda",
            "Community Development Network",
            "Green Future Organization",
            "Women Empowerment Alliance",
            "Youth Development Center",
            "Rural Support Foundation"
        };
        
        String[] descriptions = {
            "Dedicated to improving the lives of children in Rwanda through education, healthcare, and community support programs.",
            "Providing essential healthcare services and medical supplies to underserved communities across Rwanda.",
            "Promoting quality education and literacy programs for children and adults in rural and urban areas.",
            "Supporting sustainable community development projects focusing on infrastructure, agriculture, and economic empowerment.",
            "Environmental conservation and sustainable development initiatives for a greener Rwanda.",
            "Empowering women through skills training, microfinance, and leadership development programs.",
            "Creating opportunities for youth through mentorship, vocational training, and entrepreneurship support.",
            "Supporting rural communities with agricultural training, clean water access, and infrastructure development."
        };
        
        String[] websites = {
            "www.hopeforchildren.rw",
            "www.rwandahealth.rw",
            "www.educationfirst.rw",
            "www.communitydev.rw",
            "www.greenfuture.rw",
            "www.womenempower.rw",
            "www.youthdev.rw",
            "www.ruralsupport.rw"
        };
        
        User[] ngoUsers = {ngoUser1, ngoUser2, ngoUser3};
        
        List<NGO> ngos = new ArrayList<>();
        
        for (int i = 0; i < count; i++) {
            int nameIndex = i % names.length;
            
            NGO ngo = NGO.builder()
                    .name(names[nameIndex] + (i >= names.length ? " #" + (i + 1) : ""))
                    .description(descriptions[nameIndex])
                    .registrationNumber("RNGO-" + String.format("%06d", 1000 + i))
                    .email("ngo" + (i + 1) + "@organization.rw")
                    .phoneNumber("+250788" + String.format("%06d", random.nextInt(1000000)))
                    .website(websites[nameIndex])
                    .foundedYear(2010 + random.nextInt(14))
                    .isVerified(random.nextBoolean())
                    .totalDonationsReceived(random.nextInt(100))
                    .totalBeneficiaries(random.nextInt(5000) + 100)
                    .headOfficeLocation(locations.get(random.nextInt(locations.size())))
                    .build();
            
            // Set createdAt after building
            ngo.setCreatedAt(LocalDateTime.now().minusDays(random.nextInt(365)));
            
            // Add random admin user
            ngo.getAdmins().add(ngoUsers[random.nextInt(ngoUsers.length)]);
            
            // Add random categories (1-3 categories per NGO)
            int categoryCount = random.nextInt(3) + 1;
            for (int j = 0; j < categoryCount && j < categories.size(); j++) {
                Category cat = categories.get(random.nextInt(categories.size()));
                if (!ngo.getCategories().contains(cat)) {
                    ngo.addCategory(cat);
                }
            }
            
            ngos.add(ngo);
        }
        
        ngoRepository.saveAll(ngos);
        log.info("Successfully seeded {} sample NGOs", ngos.size());
        
        return ngos.size();
    }
    
    private User createTestUser(String firstName, String lastName, String email, Role role) {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword("$2a$10$dummy"); // Dummy password hash
        user.setPhoneNumber("+250788" + random.nextInt(1000000));
        user.setRole(role);
        user.setVerified(true);
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }
    
    /**
     * Assign locations to all existing NGOs that don't have locations,
     * or reassign locations to all NGOs to distribute them across Rwanda.
     * Uses the Rwandan administrative structure from RwandaLocationData to ensure
     * proper distribution across provinces, districts, and sectors.
     */
    @Transactional
    public int assignLocationsToNGOs(boolean reassignAll) {
        log.info("Starting to assign locations to NGOs using Rwandan structure (reassignAll: {})...", reassignAll);
        
        // Get all NGOs
        List<NGO> ngos = ngoRepository.findAll();
        if (ngos.isEmpty()) {
            log.warn("No NGOs found to assign locations");
            return 0;
        }
        
        // Filter NGOs that need locations
        List<NGO> ngosToUpdate = new ArrayList<>();
        if (reassignAll) {
            ngosToUpdate = ngos;
            log.info("Reassigning locations to all {} NGOs", ngos.size());
        } else {
            for (NGO ngo : ngos) {
                if (ngo.getHeadOfficeLocation() == null) {
                    ngosToUpdate.add(ngo);
                }
            }
            log.info("Assigning locations to {} NGOs without locations (out of {})", 
                    ngosToUpdate.size(), ngos.size());
        }
        
        if (ngosToUpdate.isEmpty()) {
            log.info("All NGOs already have locations assigned");
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
        
        log.info("Found {} sector locations from Rwandan structure to distribute among {} NGOs", 
                sectorLocations.size(), ngosToUpdate.size());
        
        // Find or create locations in database and assign to NGOs
        int updatedCount = 0;
        int locationIndex = 0;
        
        for (NGO ngo : ngosToUpdate) {
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
            
            ngo.setHeadOfficeLocation(location);
            ngoRepository.save(ngo);
            locationIndex++;
            updatedCount++;
            
            if (updatedCount % 10 == 0) {
                log.info("Assigned locations to {}/{} NGOs", updatedCount, ngosToUpdate.size());
            }
        }
        
        log.info("Successfully assigned locations to {} NGOs using Rwandan administrative structure", updatedCount);
        return updatedCount;
    }
    
    /**
     * Find existing location or create a new one if it doesn't exist
     */
    private Location findOrCreateLocation(String province, String district, String sector, String cell, String village) {
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

