package com.imigishalink.ngos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.imigishalink.categories.Category;
import com.imigishalink.common.BaseEntity;
import com.imigishalink.donations.Donation;
import com.imigishalink.location.Location;
import com.imigishalink.users.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "ngos", uniqueConstraints = {
    @UniqueConstraint(columnNames = "registration_number"),
    @UniqueConstraint(columnNames = "email"),
    @UniqueConstraint(columnNames = "phone_number")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"admins", "categories", "donations"})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class NGO extends BaseEntity {
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "description", length = 2000)
    private String description;
    
    @Column(name = "registration_number", nullable = false, unique = true)
    private String registrationNumber;
    
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    
    @Column(name = "phone_number", unique = true)
    private String phoneNumber;
    
    @Column(name = "website")
    private String website;
    
    @Column(name = "logo_url")
    private String logoUrl;
    
    @Column(name = "banner_url")
    private String bannerUrl;
    
    @Column(name = "founded_year")
    private Integer foundedYear;
    
    @Column(name = "is_verified", nullable = false)
    @Builder.Default
    private boolean isVerified = false;
    
    @Column(name = "verification_documents_url")
    private String verificationDocumentsUrl;
    
    @Column(name = "total_donations_received")
    @Builder.Default
    private Integer totalDonationsReceived = 0;
    
    @Column(name = "total_beneficiaries")
    @Builder.Default
    private Integer totalBeneficiaries = 0;
    
    // Many-to-one with Location
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Location headOfficeLocation;
    
    // Many-to-many with Users (Admins)
    @ManyToMany
    @JoinTable(
        name = "ngo_admins",
        joinColumns = @JoinColumn(name = "ngo_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @Builder.Default
    @JsonIgnore
    private Set<User> admins = new HashSet<>();
    
    // Many-to-many with Categories
    @ManyToMany
    @JoinTable(
        name = "ngo_categories",
        joinColumns = @JoinColumn(name = "ngo_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    @Builder.Default
    @JsonIgnore
    private Set<Category> categories = new HashSet<>();
    
    // One-to-many with Donations
    @OneToMany(mappedBy = "ngo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    @JsonIgnore
    private Set<Donation> donations = new HashSet<>();
    
    // Helper methods
    public void addAdmin(User admin) {
        admins.add(admin);
        admin.getManagedNgos().add(this);
    }
    
    public void removeAdmin(User admin) {
        admins.remove(admin);
        admin.getManagedNgos().remove(this);
    }
    
    public void addCategory(Category category) {
        categories.add(category);
        category.getNgos().add(this);
    }
    
    public void removeCategory(Category category) {
        categories.remove(category);
        category.getNgos().remove(this);
    }
    
    public void incrementDonationsReceived() {
        totalDonationsReceived = totalDonationsReceived == null ? 1 : totalDonationsReceived + 1;
    }
}