package com.imigishalink.location;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    
    Optional<Location> findByProvinceAndDistrictAndSectorAndCellAndVillage(
            String province, String district, String sector, String cell, String village);
    
    Page<Location> findByProvince(String province, Pageable pageable);
    
    Page<Location> findByDistrict(String district, Pageable pageable);
    
    Page<Location> findBySector(String sector, Pageable pageable);
    
    List<Location> findByParentLocationId(Long parentLocationId);
    
    @Query("SELECT DISTINCT l.province FROM Location l ORDER BY l.province")
    List<String> findAllProvinces();
    
    @Query("SELECT DISTINCT l.district FROM Location l WHERE l.province = :province ORDER BY l.district")
    List<String> findDistrictsByProvince(@Param("province") String province);
    
    // Find sectors by district, ensuring they belong to the specified province
    @Query("SELECT DISTINCT l.sector FROM Location l WHERE l.district = :district AND l.province = :province ORDER BY l.sector")
    List<String> findSectorsByDistrictAndProvince(@Param("district") String district, @Param("province") String province);
    
    // Find sectors by district (backward compatibility)
    @Query("SELECT DISTINCT l.sector FROM Location l WHERE l.district = :district ORDER BY l.sector")
    List<String> findSectorsByDistrict(@Param("district") String district);
    
    // Find cells by sector, ensuring they belong to the specified district and province
    @Query("SELECT DISTINCT l.cell FROM Location l WHERE l.sector = :sector AND l.district = :district AND l.province = :province ORDER BY l.cell")
    List<String> findCellsBySectorDistrictAndProvince(@Param("sector") String sector, @Param("district") String district, @Param("province") String province);
    
    // Find cells by sector (backward compatibility)
    @Query("SELECT DISTINCT l.cell FROM Location l WHERE l.sector = :sector ORDER BY l.cell")
    List<String> findCellsBySector(@Param("sector") String sector);
    
    // Find villages by cell, ensuring they belong to the specified sector, district, and province
    @Query("SELECT DISTINCT l.village FROM Location l WHERE l.cell = :cell AND l.sector = :sector AND l.district = :district AND l.province = :province ORDER BY l.village")
    List<String> findVillagesByCellSectorDistrictAndProvince(@Param("cell") String cell, @Param("sector") String sector, @Param("district") String district, @Param("province") String province);
    
    // Find villages by cell (backward compatibility)
    @Query("SELECT DISTINCT l.village FROM Location l WHERE l.cell = :cell ORDER BY l.village")
    List<String> findVillagesByCell(@Param("cell") String cell);
    
    @Query("SELECT l FROM Location l WHERE " +
           "LOWER(l.province) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(l.district) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(l.sector) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Location> searchLocations(@Param("search") String search, Pageable pageable);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.location.id = :locationId")
    long countUsersByLocation(@Param("locationId") Long locationId);
    
    @Query("SELECT COUNT(n) FROM NGO n WHERE n.headOfficeLocation.id = :locationId")
    long countNgosByLocation(@Param("locationId") Long locationId);
    
    @Query("SELECT COUNT(d) FROM Donation d WHERE d.location.id = :locationId")
    long countDonationsByLocation(@Param("locationId") Long locationId);
}