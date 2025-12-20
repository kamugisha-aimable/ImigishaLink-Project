package com.imigishalink.categories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    Optional<Category> findByName(String name);
    
    Page<Category> findByIsActiveTrue(Pageable pageable);
    
    List<Category> findByParentCategoryId(Long parentId);
    
    @Query("SELECT c FROM Category c WHERE " +
           "LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.description) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Category> searchCategories(@Param("search") String search, Pageable pageable);
    
    @Query("SELECT c FROM Category c WHERE c.parentCategory IS NULL")
    Page<Category> findRootCategories(Pageable pageable);
    
    @Query("SELECT COUNT(d) FROM Donation d JOIN d.categories c WHERE c.id = :categoryId")
    long countDonationsByCategory(@Param("categoryId") Long categoryId);
    
    @Query("SELECT COUNT(n) FROM NGO n JOIN n.categories c WHERE c.id = :categoryId")
    long countNgosByCategory(@Param("categoryId") Long categoryId);
}