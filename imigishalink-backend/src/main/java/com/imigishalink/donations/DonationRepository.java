package com.imigishalink.donations;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DonationRepository extends JpaRepository<Donation, Long> {
    
    @EntityGraph(attributePaths = {"createdBy", "ngo", "location", "categories"})
    @Query("SELECT d FROM Donation d WHERE d.id = :id")
    java.util.Optional<Donation> findByIdWithAssociations(@Param("id") Long id);
    
    @EntityGraph(attributePaths = {"createdBy", "ngo", "location", "categories"})
    Page<Donation> findByStatus(DonationStatus status, Pageable pageable);
    
    @EntityGraph(attributePaths = {"createdBy", "ngo", "location", "categories"})
    Page<Donation> findByType(DonationType type, Pageable pageable);
    
    @EntityGraph(attributePaths = {"createdBy", "ngo", "location", "categories"})
    Page<Donation> findByCreatedById(Long userId, Pageable pageable);
    
    @EntityGraph(attributePaths = {"createdBy", "ngo", "location", "categories"})
    @Query("SELECT d FROM Donation d WHERE d.createdBy.id = :userId AND d.status = :status")
    Page<Donation> findByCreatedByIdAndStatus(@Param("userId") Long userId, @Param("status") DonationStatus status, Pageable pageable);
    
    Page<Donation> findByNgoId(Long ngoId, Pageable pageable);
    
    Page<Donation> findByLocationId(Long locationId, Pageable pageable);
    
    @EntityGraph(attributePaths = {"createdBy", "ngo", "location", "categories"})
    @Query("SELECT d FROM Donation d WHERE d.location.province = :province")
    Page<Donation> findByProvince(@Param("province") String province, Pageable pageable);
    
    @EntityGraph(attributePaths = {"createdBy", "ngo", "location", "categories"})
    @Query("SELECT d FROM Donation d WHERE d.location.district = :district")
    Page<Donation> findByDistrict(@Param("district") String district, Pageable pageable);
    
    @EntityGraph(attributePaths = {"createdBy", "ngo", "location", "categories"})
    @Query("SELECT d FROM Donation d WHERE LOWER(d.location.province) = LOWER(:province) AND LOWER(d.location.district) = LOWER(:district)")
    Page<Donation> findByProvinceAndDistrict(@Param("province") String province, @Param("district") String district, Pageable pageable);
    
    @EntityGraph(attributePaths = {"createdBy", "ngo", "location", "categories"})
    @Query("SELECT d FROM Donation d WHERE " +
           "LOWER(d.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(d.description) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Donation> searchDonations(@Param("search") String search, Pageable pageable);
    
    @EntityGraph(attributePaths = {"createdBy", "ngo", "location", "categories"})
    @Query("SELECT d FROM Donation d JOIN d.categories c WHERE c.id = :categoryId")
    Page<Donation> findByCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);
    
    @EntityGraph(attributePaths = {"createdBy", "ngo", "location", "categories"})
    @Query("SELECT d FROM Donation d WHERE d.deadline < :date AND d.status IN ('OPEN', 'IN_PROGRESS')")
    Page<Donation> findExpiredDonations(@Param("date") LocalDate date, Pageable pageable);
    
    @EntityGraph(attributePaths = {"createdBy", "ngo", "location", "categories"})
    @Query("SELECT d FROM Donation d WHERE d.status = 'OPEN' ORDER BY d.priorityLevel DESC, d.createdAt DESC")
    Page<Donation> findOpenDonationsByPriority(Pageable pageable);
    
    @Query("SELECT COUNT(d) FROM Donation d WHERE d.status = :status")
    long countByStatus(@Param("status") DonationStatus status);
    
    @Query("SELECT d.location.province, COUNT(d) FROM Donation d GROUP BY d.location.province")
    List<Object[]> countDonationsByProvince();
    
    @Query("SELECT d.type, COUNT(d) FROM Donation d GROUP BY d.type")
    List<Object[]> countDonationsByType();
    
    @Query("SELECT d FROM Donation d WHERE d.location.province = :province AND d.priorityLevel = 'URGENT'")
    Page<Donation> findByProvinceAndUrgent(@Param("province") String province, Pageable pageable);
    
    @Query("SELECT d FROM Donation d WHERE d.ngo.id = :ngoId AND d.status = 'FULFILLED'")
    Page<Donation> findFulfilledDonationsByNgo(@Param("ngoId") Long ngoId, Pageable pageable);
}