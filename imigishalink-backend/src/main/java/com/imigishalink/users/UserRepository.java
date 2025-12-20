package com.imigishalink.users;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    boolean existsByPhoneNumber(String phoneNumber);
    
    Optional<User> findByVerificationCode(String verificationCode);
    
    Page<User> findByRole(Role role, Pageable pageable);
    
    Page<User> findByLocationProvince(String province, Pageable pageable);
    
    Page<User> findByLocationDistrict(String district, Pageable pageable);
    
    Page<User> findByLocationSector(String sector, Pageable pageable);
    
    @Query("SELECT u FROM User u WHERE " +
           "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<User> searchUsers(@Param("search") String search, Pageable pageable);
    
    @Query("SELECT u FROM User u WHERE u.location.id = :locationId")
    Page<User> findByLocationId(@Param("locationId") Long locationId, Pageable pageable);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role")
    long countByRole(@Param("role") Role role);
    
    @Query("SELECT u FROM User u WHERE u.isVerified = true AND u.isActive = true")
    Page<User> findActiveVerifiedUsers(Pageable pageable);
    
    @Query("SELECT u FROM User u JOIN u.location l WHERE " +
           "l.province = :province AND l.district = :district")
    Page<User> findByProvinceAndDistrict(
            @Param("province") String province,
            @Param("district") String district,
            Pageable pageable);
    
    @Query("SELECT DISTINCT l.province FROM User u JOIN u.location l WHERE u.role = :role")
    List<String> findProvincesByRole(@Param("role") Role role);
    
    @Query("SELECT u FROM User u JOIN u.following f WHERE f.id = :userId")
    Page<User> findFollowers(@Param("userId") Long userId, Pageable pageable);
    
    @Query("SELECT u FROM User u WHERE u.id IN " +
           "(SELECT f.id FROM User u2 JOIN u2.following f WHERE u2.id = :userId)")
    Page<User> findFollowing(@Param("userId") Long userId, Pageable pageable);

    @Query(value = "SELECT u.* FROM users u " +
           "JOIN locations l ON u.location_id = l.id " +
           "WHERE (6371 * acos(cos(radians(:lat)) * cos(radians(l.latitude)) * " +
           "cos(radians(l.longitude) - radians(:lng)) + " +
           "sin(radians(:lat)) * sin(radians(l.latitude)))) <= :radiusKm " +
           "AND u.is_verified = true AND u.is_active = true AND u.id != :userId",
           nativeQuery = true)
    Page<User> findNearbyUsers(@Param("lat") Double lat, @Param("lng") Double lng,
                               @Param("radiusKm") Double radiusKm, @Param("userId") Long userId,
                               Pageable pageable);

    @Query("SELECT DISTINCT u FROM User u " +
           "JOIN u.donations d " +
           "JOIN d.categories c " +
           "WHERE c.id = :categoryId " +
           "AND u.role = 'USER' " +
           "AND u.isVerified = true " +
           "AND u.isActive = true " +
           "AND (:province IS NULL OR u.location.province = :province)")
    Page<User> findDonorsByCategory(@Param("categoryId") Long categoryId,
                                    @Param("province") String province,
                                    Pageable pageable);
}