package com.imigishalink.donations;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.imigishalink.categories.Category;
import com.imigishalink.common.BaseEntity;
import com.imigishalink.location.Location;
import com.imigishalink.ngos.NGO;
import com.imigishalink.users.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "donations")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"categories", "contributions"})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "contributions", "totalContributedValue", "fulfilled"})
public class Donation extends BaseEntity {
    
    @Column(name = "title", nullable = false)
    private String title;
    
    @Column(name = "description", length = 2000)
    private String description;
    
    @Column(name = "quantity")
    private Integer quantity;
    
    @Column(name = "unit")
    private String unit;
    
    @Column(name = "estimated_value")
    private BigDecimal estimatedValue;
    
    @Column(name = "currency")
    @Builder.Default
    private String currency = "RWF";
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private DonationStatus status = DonationStatus.OPEN;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    @Builder.Default
    private DonationType type = DonationType.MATERIAL;
    
    @Column(name = "priority_level")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private PriorityLevel priorityLevel = PriorityLevel.MEDIUM;
    
    @Column(name = "deadline")
    private LocalDate deadline;
    
    @Column(name = "delivery_method")
    private String deliveryMethod;
    
    @Column(name = "images_urls", length = 1000)
    private String imagesUrls; // JSON array of image URLs
    
    // Many-to-one with User (Creator)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", nullable = false)
    private User createdBy;
    
    // Many-to-one with NGO (Optional)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ngo_id")
    private NGO ngo;
    
    // Many-to-one with Location
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Location location;
    
    // Many-to-many with Categories
    @ManyToMany
    @JoinTable(
        name = "donation_categories",
        joinColumns = @JoinColumn(name = "donation_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    @Builder.Default
    private Set<Category> categories = new HashSet<>();
    
    // One-to-many with Contributions (Self-referencing)
    // Explicitly excluded from serialization to prevent lazy loading issues
    @OneToMany(mappedBy = "donation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    @JsonIgnore
    @Getter(AccessLevel.NONE) // Don't generate getter for this field
    @Setter
    private Set<Contribution> contributions = new HashSet<>();
    
    // Manual getter with @JsonIgnore to prevent serialization
    @JsonIgnore
    public Set<Contribution> getContributions() {
        return contributions;
    }
    
    // Helper methods
    public void addCategory(Category category) {
        categories.add(category);
        category.getDonations().add(this);
    }
    
    public void removeCategory(Category category) {
        categories.remove(category);
        category.getDonations().remove(this);
    }
    
    public void addContribution(Contribution contribution) {
        contributions.add(contribution);
        contribution.setDonation(this);
    }
    
    @JsonIgnore
    public BigDecimal getTotalContributedValue() {
        if (contributions == null || contributions.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return contributions.stream()
                .map(Contribution::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    @JsonIgnore
    public boolean isFulfilled() {
        if (quantity == null) return false;
        if (contributions == null || contributions.isEmpty()) {
            return false;
        }
        int totalContributed = contributions.stream()
                .map(Contribution::getQuantity)
                .filter(q -> q != null)
                .mapToInt(Integer::intValue)
                .sum();
        return totalContributed >= quantity;
    }
}