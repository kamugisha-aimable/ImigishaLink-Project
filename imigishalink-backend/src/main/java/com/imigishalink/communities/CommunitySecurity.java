package com.imigishalink.communities;

import com.imigishalink.users.User;
import com.imigishalink.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component("communitySecurity")
@RequiredArgsConstructor
public class CommunitySecurity {
    
    private final CommunityRepository communityRepository;
    private final UserRepository userRepository;
    
    public boolean isAdmin(Long communityId, Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName()).orElse(null);
        if (user == null) return false;
        
        Long id = Objects.requireNonNull(communityId, "Community ID cannot be null");
        return communityRepository.findById(id)
                .map(community -> community.getAdmins().contains(user))
                .orElse(false);
    }
}