package com.imigishalink.ngos;

import com.imigishalink.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ngos/seeder")
@RequiredArgsConstructor
@Slf4j
public class NGOSeederController {
    
    private final NGOSeederService seederService;
    private final NGORepository ngoRepository;
    
    @PostMapping("/seed")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> seedNGOs(
            @RequestParam(defaultValue = "6") int count) {
        
        try {
            int created = seederService.seedSampleNGOs(count);
            return ResponseEntity.ok(ApiResponse.success(
                String.format("Successfully created %d sample NGOs", created)
            ));
        } catch (Exception e) {
            log.error("Error seeding NGOs", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(
                "Failed to seed NGOs: " + e.getMessage()
            ));
        }
    }
    
    @DeleteMapping("/clear")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> clearAllNGOs() {
        try {
            long count = ngoRepository.count();
            ngoRepository.deleteAll();
            return ResponseEntity.ok(ApiResponse.success(
                String.format("Successfully deleted %d NGOs", count)
            ));
        } catch (Exception e) {
            log.error("Error clearing NGOs", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(
                "Failed to clear NGOs: " + e.getMessage()
            ));
        }
    }
}

