package com.imigishalink.location;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.imigishalink.common.BaseEntity;
import com.imigishalink.communities.Community;
import com.imigishalink.donations.Donation;
import com.imigishalink.ngos.NGO;
import com.imigishalink.users.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "locations")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"users", "ngos", "donations", "communities"})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Location extends BaseEntity {
    
    @Column(name = "country", nullable = false)
    @Builder.Default
    private String country = "Rwanda";
    
    @Column(name = "province", nullable = false)
    private String province;
    
    @Column(name = "district", nullable = false)
    private String district;
    
    @Column(name = "sector", nullable = false)
    private String sector;
    
    @Column(name = "cell")
    private String cell;
    
    @Column(name = "village")
    private String village;
    
    @Column(name = "latitude")
    private Double latitude;
    
    @Column(name = "longitude")
    private Double longitude;
    
    // Self-referencing: Parent location for hierarchical structure
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_location_id")
    private Location parentLocation;
    
    @OneToMany(mappedBy = "parentLocation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Location> subLocations = new HashSet<>();
    
    // Users in this location
    @OneToMany(mappedBy = "location", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<User> users = new HashSet<>();
    
    // NGOs in this location
    @OneToMany(mappedBy = "headOfficeLocation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<NGO> ngos = new HashSet<>();
    
    // Donations in this location
    @OneToMany(mappedBy = "location", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Donation> donations = new HashSet<>();
    
    // Communities in this location
    @OneToMany(mappedBy = "location", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Community> communities = new HashSet<>();
    
    public String getFullAddress() {
        StringBuilder address = new StringBuilder();
        if (village != null) address.append(village).append(", ");
        if (cell != null) address.append(cell).append(", ");
        address.append(sector).append(", ");
        address.append(district).append(", ");
        address.append(province).append(", ");
        address.append(country);
        return address.toString();
    }
}