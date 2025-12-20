package com.imigishalink.communities;

import com.imigishalink.common.BaseEntity;
import com.imigishalink.location.Location;
import com.imigishalink.messages.Message;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "communities", uniqueConstraints = {
    @UniqueConstraint(columnNames = "name")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Community extends BaseEntity {
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "description", length = 1000)
    private String description;
    
    @Column(name = "profile_image_url")
    private String profileImageUrl;
    
    @Column(name = "banner_image_url")
    private String bannerImageUrl;
    
    @Column(name = "is_public", nullable = false)
    private boolean isPublic = true;
    
    @Column(name = "member_count")
    private Integer memberCount = 0;
    
    @Column(name = "rules", length = 2000)
    private String rules;
    
    // Many-to-one with Location
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Location location;
    
    // Many-to-one with User (Creator)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", nullable = false)
    private com.imigishalink.users.User createdBy;
    
    // Many-to-many with Users (Members)
    @ManyToMany
    @JoinTable(
        name = "community_members",
        joinColumns = @JoinColumn(name = "community_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<com.imigishalink.users.User> members = new HashSet<>();
    
    // Many-to-many with Users (Admins)
    @ManyToMany
    @JoinTable(
        name = "community_admins",
        joinColumns = @JoinColumn(name = "community_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<com.imigishalink.users.User> admins = new HashSet<>();
    
    // One-to-many with Messages
    @OneToMany(mappedBy = "community", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Message> messages = new HashSet<>();
    
    // Helper methods
    public void addMember(com.imigishalink.users.User user) {
        members.add(user);
        memberCount = members.size();
        user.getCommunities().add(this);
    }
    
    public void removeMember(com.imigishalink.users.User user) {
        members.remove(user);
        admins.remove(user);
        memberCount = members.size();
        user.getCommunities().remove(this);
    }
    
    public void addAdmin(com.imigishalink.users.User user) {
        admins.add(user);
        if (!members.contains(user)) {
            addMember(user);
        }
    }
    
    public boolean isMember(com.imigishalink.users.User user) {
        return members.contains(user);
    }
    
    public boolean isAdmin(com.imigishalink.users.User user) {
        return admins.contains(user);
    }
}