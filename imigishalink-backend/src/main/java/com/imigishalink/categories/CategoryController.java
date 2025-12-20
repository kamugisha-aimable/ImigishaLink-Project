package com.imigishalink.categories;

import com.imigishalink.common.ApiResponse;
import com.imigishalink.common.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {
    
    private final CategoryRepository categoryRepository;
    
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<Category>>> getAllCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) String search) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<Category> categories;
        
        if (search != null && !search.trim().isEmpty()) {
            categories = categoryRepository.searchCategories(search, pageable);
        } else if (active != null && active) {
            categories = categoryRepository.findByIsActiveTrue(pageable);
        } else {
            categories = categoryRepository.findAll(pageable);
        }
        
        PageResponse<Category> response = new PageResponse<>(categories);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/root")
    public ResponseEntity<ApiResponse<PageResponse<Category>>> getRootCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<Category> categories = categoryRepository.findRootCategories(pageable);
        
        PageResponse<Category> response = new PageResponse<>(categories);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Category>> getCategoryById(@PathVariable @NotNull Long id) {
        Long categoryId = Objects.requireNonNull(id, "Category ID cannot be null");
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        return ResponseEntity.ok(ApiResponse.success(category));
    }
    
    @GetMapping("/{id}/subcategories")
    public ResponseEntity<ApiResponse<?>> getSubCategories(@PathVariable @NotNull Long id) {
        Long categoryId = Objects.requireNonNull(id, "Category ID cannot be null");
        var subCategories = categoryRepository.findByParentCategoryId(categoryId);
        return ResponseEntity.ok(ApiResponse.success(subCategories));
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Category>> createCategory(@RequestBody Category category) {
        if (categoryRepository.findByName(category.getName()).isPresent()) {
            throw new RuntimeException("Category with this name already exists");
        }
        
        Category savedCategory = categoryRepository.save(category);
        return ResponseEntity.ok(ApiResponse.success("Category created successfully", savedCategory));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Category>> updateCategory(
            @PathVariable @NotNull Long id,
            @RequestBody Category category) {
        
        Long categoryId = Objects.requireNonNull(id, "Category ID cannot be null");
        Category existingCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        
        // Check name uniqueness if changed
        if (!existingCategory.getName().equals(category.getName()) &&
            categoryRepository.findByName(category.getName()).isPresent()) {
            throw new RuntimeException("Category name already taken");
        }
        
        existingCategory.setName(category.getName());
        existingCategory.setDescription(category.getDescription());
        existingCategory.setColorCode(category.getColorCode());
        existingCategory.setIcon(category.getIcon());
        existingCategory.setActive(category.isActive());
        
        Category updatedCategory = categoryRepository.save(existingCategory);
        return ResponseEntity.ok(ApiResponse.success("Category updated successfully", updatedCategory));
    }
    
    @GetMapping("/{id}/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCategoryStats(@PathVariable @NotNull Long id) {
        Long categoryId = Objects.requireNonNull(id, "Category ID cannot be null");
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("category", category.getName());
        stats.put("totalDonations", categoryRepository.countDonationsByCategory(categoryId));
        stats.put("totalNgos", categoryRepository.countNgosByCategory(categoryId));
        stats.put("subCategoriesCount", category.getSubCategories().size());
        
        return ResponseEntity.ok(ApiResponse.success(stats));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable @NotNull Long id) {
        Long categoryId = Objects.requireNonNull(id, "Category ID cannot be null");
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        
        category.setActive(false);
        categoryRepository.save(category);
        
        return ResponseEntity.ok(ApiResponse.success("Category deactivated successfully", null));
    }
}