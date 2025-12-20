package com.imigishalink.donations;

import com.imigishalink.categories.Category;
import com.imigishalink.categories.CategoryRepository;
import com.imigishalink.location.Location;
import com.imigishalink.location.LocationRepository;
import com.imigishalink.users.Role;
import com.imigishalink.users.User;
import com.imigishalink.users.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class DonationSeederService {
    
    private final DonationRepository donationRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final Random random = new Random();
    
    @Transactional
    public int seedSampleDonations(int count) {
        log.info("Starting to seed {} sample donations...", count);
        
        // Get or create test users
        User donor1 = userRepository.findByEmail("donor@test.rw")
                .orElseGet(() -> createTestUser("Donor", "One", "donor@test.rw", Role.USER));
        User donor2 = userRepository.findByEmail("donor2@test.rw")
                .orElseGet(() -> createTestUser("Donor", "Two", "donor2@test.rw", Role.USER));
        User donor3 = userRepository.findByEmail("donor3@test.rw")
                .orElseGet(() -> createTestUser("Donor", "Three", "donor3@test.rw", Role.USER));
        
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
        
        // Sample donation data
        String[] titles = {
            "School Supplies for Rural Children",
            "Medical Equipment for Health Center",
            "Food Packages for Families in Need",
            "Clothing for Orphanage",
            "Books for Community Library",
            "Computers for School Lab",
            "Agricultural Tools for Farmers",
            "Emergency Relief Supplies",
            "Sports Equipment for Youth Center",
            "Blankets and Bedding",
            "Cooking Utensils for Community Kitchen",
            "Bicycles for Students",
            "Water Filters for Clean Water",
            "Solar Panels for Rural School",
            "Musical Instruments for Arts Program",
            "Building Materials for Shelter",
            "First Aid Kits",
            "Educational Toys",
            "Furniture for Community Center",
            "Seeds for Community Garden"
        };
        
        String[] descriptions = {
            "We are collecting school supplies including notebooks, pens, pencils, and backpacks for children in rural areas who cannot afford basic educational materials.",
            "Our health center urgently needs medical equipment including stethoscopes, blood pressure monitors, and basic surgical instruments.",
            "Providing food packages containing rice, beans, cooking oil, and other essentials for families struggling with food insecurity.",
            "Collecting gently used or new clothing items for children at the local orphanage. All sizes needed.",
            "Building a community library and need books in Kinyarwanda, English, and French for all age groups.",
            "Setting up a computer lab for a rural school. Need desktop computers, monitors, keyboards, and mice.",
            "Supporting local farmers with agricultural tools including hoes, shovels, watering cans, and seeds.",
            "Emergency relief supplies including tents, blankets, food, and water for disaster-affected communities.",
            "Equipping a youth center with sports equipment including footballs, basketballs, volleyballs, and nets.",
            "Providing warm blankets and bedding for families during the cold season.",
            "Setting up a community kitchen and need cooking utensils, pots, pans, and serving dishes.",
            "Helping students travel to school by providing bicycles for those who live far from educational institutions.",
            "Installing water filters in rural communities to provide access to clean drinking water.",
            "Powering a rural school with solar panels to enable evening classes and computer use.",
            "Starting an arts program and need musical instruments including drums, guitars, and keyboards.",
            "Building temporary shelters for displaced families using building materials like wood, metal sheets, and nails.",
            "Distributing first aid kits to community health workers for emergency medical care.",
            "Providing educational toys and learning materials for early childhood development centers.",
            "Furnishing a new community center with tables, chairs, and storage cabinets.",
            "Starting a community garden project and need various seeds for vegetables and fruits."
        };
        
        String[] units = {"items", "pieces", "boxes", "kgs", "liters", "sets", "pairs"};
        DonationStatus[] statuses = DonationStatus.values();
        DonationType[] types = DonationType.values();
        PriorityLevel[] priorities = PriorityLevel.values();
        User[] donors = {donor1, donor2, donor3};
        
        List<Donation> donations = new ArrayList<>();
        
        for (int i = 0; i < count; i++) {
            int titleIndex = i % titles.length;
            int descIndex = i % descriptions.length;
            
            Donation donation = Donation.builder()
                    .title(titles[titleIndex] + (i >= titles.length ? " #" + (i + 1) : ""))
                    .description(descriptions[descIndex])
                    .quantity(random.nextInt(50) + 10)
                    .unit(units[random.nextInt(units.length)])
                    .estimatedValue(BigDecimal.valueOf(random.nextInt(500000) + 50000))
                    .currency("RWF")
                    .status(statuses[random.nextInt(statuses.length)])
                    .type(types[random.nextInt(types.length)])
                    .priorityLevel(priorities[random.nextInt(priorities.length)])
                    .createdBy(donors[random.nextInt(donors.length)])
                    .location(locations.get(random.nextInt(locations.size())))
                    .build();
            
            // Set createdAt after building (inherited from BaseEntity)
            donation.setCreatedAt(LocalDateTime.now().minusDays(random.nextInt(30)));
            
            // Add random categories (1-3 categories per donation)
            int categoryCount = random.nextInt(3) + 1;
            for (int j = 0; j < categoryCount && j < categories.size(); j++) {
                Category cat = categories.get(random.nextInt(categories.size()));
                if (!donation.getCategories().contains(cat)) {
                    donation.addCategory(cat);
                }
            }
            
            // Set deadline for some donations
            if (random.nextBoolean()) {
                donation.setDeadline(LocalDate.now().plusDays(random.nextInt(60) + 7));
            }
            
            donations.add(donation);
        }
        
        donationRepository.saveAll(donations);
        log.info("Successfully seeded {} sample donations", donations.size());
        
        return donations.size();
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
}

