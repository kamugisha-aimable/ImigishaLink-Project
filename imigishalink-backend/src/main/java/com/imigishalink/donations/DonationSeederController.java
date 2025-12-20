package com.imigishalink.donations;

import com.imigishalink.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/donations/seeder")
@RequiredArgsConstructor
@Slf4j
public class DonationSeederController {
    
    private final DonationSeederService seederService;
    private final DonationRepository donationRepository;
    
    @PostMapping("/seed-small")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> seedSmallSample(
            @RequestParam(defaultValue = "6") int count) {
        
        try {
            int created = seederService.seedSampleDonations(count);
            return ResponseEntity.ok(ApiResponse.success(
                String.format("Successfully created %d sample donations", created)
            ));
        } catch (Exception e) {
            log.error("Error seeding donations", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(
                "Failed to seed donations: " + e.getMessage()
            ));
        }
    }
    
    @PostMapping("/seed")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> seedDonations(
            @RequestParam(defaultValue = "50") int count) {
        
        try {
            int created = seederService.seedSampleDonations(count);
            return ResponseEntity.ok(ApiResponse.success(
                String.format("Successfully created %d sample donations", created)
            ));
        } catch (Exception e) {
            log.error("Error seeding donations", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(
                "Failed to seed donations: " + e.getMessage()
            ));
        }
    }
    
    @DeleteMapping("/clear")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> clearAllDonations() {
        try {
            long count = donationRepository.count();
            donationRepository.deleteAll();
            return ResponseEntity.ok(ApiResponse.success(
                String.format("Successfully deleted %d donations", count)
            ));
        } catch (Exception e) {
            log.error("Error clearing donations", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(
                "Failed to clear donations: " + e.getMessage()
            ));
        }
    }
}

