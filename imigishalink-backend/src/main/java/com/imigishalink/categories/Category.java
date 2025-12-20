package com.imigishalink.categories;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.imigishalink.common.BaseEntity;
import com.imigishalink.donations.Donation;
import com.imigishalink.ngos.NGO;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "categories", uniqueConstraints = {
    @UniqueConstraint(columnNames = "name")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"donations", "ngos"})
public class Category extends BaseEntity {
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "description", length = 1000)
    private String description;
    
    @Column(name = "color_code")
    private String colorCode;
    
    @Column(name = "icon")
    private String icon;
    
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean isActive = true;
    
    // Many-to-many with Donations
    @ManyToMany(mappedBy = "categories")
    @Builder.Default
    @JsonIgnore
    private Set<Donation> donations = new HashSet<>();
    
    // Many-to-many with NGOs
    @ManyToMany(mappedBy = "categories")
    @Builder.Default
    @JsonIgnore
    private Set<NGO> ngos = new HashSet<>();
    
    // Self-referencing: Parent category for hierarchical structure
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_category_id")
    @JsonIgnore
    private Category parentCategory;
    
    @OneToMany(mappedBy = "parentCategory", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    @JsonIgnore
    private Set<Category> subCategories = new HashSet<>();
}