package com.imigishalink.communities;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunityRepository extends JpaRepository<Community, Long> {
    
    Page<Community> findByIsPublicTrue(Pageable pageable);
    
    Page<Community> findByLocationProvince(String province, Pageable pageable);
    
    Page<Community> findByLocationDistrict(String district, Pageable pageable);
    
    @Query("SELECT c FROM Community c WHERE " +
           "LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.description) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Community> searchCommunities(@Param("search") String search, Pageable pageable);
    
    @Query("SELECT c FROM Community c WHERE :userId MEMBER OF c.members")
    Page<Community> findByMemberId(@Param("userId") Long userId, Pageable pageable);
    
    @Query("SELECT c FROM Community c WHERE :userId MEMBER OF c.admins")
    Page<Community> findByAdminId(@Param("userId") Long userId, Pageable pageable);
    
    @Query("SELECT COUNT(c) FROM Community c WHERE c.isPublic = true")
    long countPublicCommunities();
    
    @Query("SELECT c.location.province, COUNT(c) FROM Community c GROUP BY c.location.province")
    List<Object[]> countCommunitiesByProvince();
    
    @Query("SELECT c FROM Community c ORDER BY c.memberCount DESC")
    Page<Community> findPopularCommunities(Pageable pageable);
}