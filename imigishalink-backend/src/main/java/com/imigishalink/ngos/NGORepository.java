package com.imigishalink.ngos;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NGORepository extends JpaRepository<NGO, Long> {
    
    Optional<NGO> findByRegistrationNumber(String registrationNumber);
    
    Optional<NGO> findByEmail(String email);
    
    boolean existsByRegistrationNumber(String registrationNumber);
    
    boolean existsByEmail(String email);
    
    Page<NGO> findByIsVerifiedTrue(Pageable pageable);
    
    Page<NGO> findByIsVerifiedFalse(Pageable pageable);
    
    Page<NGO> findByHeadOfficeLocationProvince(String province, Pageable pageable);
    
    Page<NGO> findByHeadOfficeLocationDistrict(String district, Pageable pageable);
    
    @Query("SELECT n FROM NGO n WHERE n.headOfficeLocation.province = :province AND :categoryId IN (SELECT c.id FROM n.categories c)")
    Page<NGO> findByHeadOfficeLocationProvinceAndCategoryId(@Param("province") String province, @Param("categoryId") Long categoryId, Pageable pageable);
    
    @Query("SELECT n FROM NGO n WHERE " +
           "LOWER(n.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(n.description) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<NGO> searchNgos(@Param("search") String search, Pageable pageable);
    
    @Query("SELECT n FROM NGO n JOIN n.categories c WHERE c.id = :categoryId")
    Page<NGO> findByCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);
    
    @Query("SELECT n FROM NGO n WHERE n.id IN " +
           "(SELECT ngo.id FROM User u JOIN u.managedNgos ngo WHERE u.id = :userId)")
    Page<NGO> findByAdminId(@Param("userId") Long userId, Pageable pageable);
    
    @Query("SELECT COUNT(n) FROM NGO n WHERE n.isVerified = true")
    long countVerifiedNgos();
    
    @Query("SELECT n.headOfficeLocation.province, COUNT(n) FROM NGO n GROUP BY n.headOfficeLocation.province")
    List<Object[]> countNgosByProvince();
    
    @Query("SELECT n FROM NGO n ORDER BY n.totalDonationsReceived DESC")
    Page<NGO> findTopNgos(Pageable pageable);
}