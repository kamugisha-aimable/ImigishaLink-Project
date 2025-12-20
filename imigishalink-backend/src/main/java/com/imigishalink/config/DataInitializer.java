package com.imigishalink.config;

import com.imigishalink.categories.Category;
import com.imigishalink.categories.CategoryRepository;
import com.imigishalink.location.Location;
import com.imigishalink.location.LocationRepository;
import com.imigishalink.users.Role;
import com.imigishalink.users.User;
import com.imigishalink.users.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {
    
    private final PasswordEncoder passwordEncoder;
    
    @Bean
    public CommandLineRunner initData(
            UserRepository userRepository,
            CategoryRepository categoryRepository,
            LocationRepository locationRepository) {
        
        return args -> {
            log.info("Initializing sample data...");
            
            // Create Rwandan locations
            if (locationRepository.count() == 0) {
                List<Location> locations = Objects.requireNonNull(Arrays.asList(
                    createLocation("Kigali City", "Gasabo", "Kimihurura", "Gisozi", "Remera"),
                    createLocation("Kigali City", "Nyarugenge", "Nyamirambo", "Nyamirambo I", "Kiyovu"),
                    createLocation("Kigali City", "Kicukiro", "Gatenga", "Gatenga", "Nyakabanda"),
                    createLocation("Southern Province", "Muhanga", "Shyogwe", "Nyabinoni", "Kigeme"),
                    createLocation("Southern Province", "Huye", "Ngoma", "Ngoma", "Save"),
                    createLocation("Northern Province", "Musanze", "Muhoza", "Muhoza", "Kinigi"),
                    createLocation("Northern Province", "Burera", "Cyanika", "Cyanika", "Rugarama"),
                    createLocation("Western Province", "Rubavu", "Rubavu", "Rubavu", "Gisenyi"),
                    createLocation("Western Province", "Karongi", "Karongi", "Karongi", "Kibuye"),
                    createLocation("Eastern Province", "Rwamagana", "Rwamagana", "Rwamagana", "Kigabiro")
                ), "Locations list cannot be null");
                locationRepository.saveAll(locations);
                log.info("Created {} Rwandan locations", locations.size());
            }
            
            // Create categories
            if (categoryRepository.count() == 0) {
                List<Category> categories = Objects.requireNonNull(Arrays.asList(
                    createCategory("Education", "Books, school supplies, scholarships", "#4CAF50", "school"),
                    createCategory("Healthcare", "Medical equipment, medicines, first aid kits", "#2196F3", "medical-services"),
                    createCategory("Food & Nutrition", "Food items, cooking supplies, nutrition support", "#FF9800", "restaurant"),
                    createCategory("Clothing", "Clothes, shoes, textiles", "#9C27B0", "checkroom"),
                    createCategory("Shelter", "Housing materials, bedding, tents", "#795548", "home"),
                    createCategory("Agriculture", "Seeds, farming tools, livestock", "#8BC34A", "agriculture"),
                    createCategory("Technology", "Computers, phones, electronics", "#3F51B5", "computer"),
                    createCategory("Transport", "Bicycles, vehicles, fuel", "#607D8B", "directions-car"),
                    createCategory("Emergency", "Disaster relief, emergency kits", "#F44336", "emergency"),
                    createCategory("Sports", "Sports equipment, uniforms", "#00BCD4", "sports-soccer"),
                    createCategory("Arts & Culture", "Art supplies, musical instruments", "#FF5722", "palette")
                ), "Categories list cannot be null");
                categoryRepository.saveAll(categories);
                log.info("Created {} categories", categories.size());
            }
            
            // Create admin user
            if (userRepository.findByEmail("admin@imigishalink.rw").isEmpty()) {
                User admin = new User();
                admin.setFirstName("Admin");
                admin.setLastName("System");
                admin.setEmail("admin@imigishalink.rw");
                admin.setPassword(passwordEncoder.encode("Admin@123"));
                admin.setPhoneNumber("+250788123456");
                admin.setRole(Role.ADMIN);
                admin.setVerified(true);
                admin.setCreatedAt(LocalDateTime.now());
                admin.setActive(true);
                
                userRepository.save(admin);
                log.info("Created admin user: admin@imigishalink.rw / Admin@123");
            }
            
            // Create test NGO user
            if (userRepository.findByEmail("ngo@test.rw").isEmpty()) {
                User ngoUser = new User();
                ngoUser.setFirstName("NGO");
                ngoUser.setLastName("Test");
                ngoUser.setEmail("ngo@test.rw");
                ngoUser.setPassword(passwordEncoder.encode("Ngo@123"));
                ngoUser.setPhoneNumber("+250788654321");
                ngoUser.setRole(Role.NGO);
                ngoUser.setVerified(true);
                ngoUser.setCreatedAt(LocalDateTime.now());
                ngoUser.setActive(true);
                
                userRepository.save(ngoUser);
                log.info("Created NGO user: ngo@test.rw / Ngo@123");
            }
            
            // Create test donor user
            if (userRepository.findByEmail("donor@test.rw").isEmpty()) {
                User donorUser = new User();
                donorUser.setFirstName("Donor");
                donorUser.setLastName("Test");
                donorUser.setEmail("donor@test.rw");
                donorUser.setPassword(passwordEncoder.encode("Donor@123"));
                donorUser.setPhoneNumber("+250788987654");
                donorUser.setRole(Role.USER);
                donorUser.setVerified(true);
                donorUser.setCreatedAt(LocalDateTime.now());
                donorUser.setActive(true);
                
                userRepository.save(donorUser);
                log.info("Created donor user: donor@test.rw / Donor@123");
            }
            
            log.info("Data initialization completed!");
        };
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
    
    private Category createCategory(String name, String description, String colorCode, String icon) {
        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        category.setColorCode(colorCode);
        category.setIcon(icon);
        category.setCreatedAt(LocalDateTime.now());
        category.setActive(true);
        return category;
    }
}