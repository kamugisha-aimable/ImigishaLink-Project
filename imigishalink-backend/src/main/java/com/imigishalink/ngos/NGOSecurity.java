package com.imigishalink.ngos;

import com.imigishalink.users.User;
import com.imigishalink.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("ngoSecurity")
@RequiredArgsConstructor
public class NGOSecurity {
    
    private final NGORepository ngoRepository;
    private final UserRepository userRepository;
    
    public boolean isAdmin(Long ngoId, Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName()).orElse(null);
        if (user == null || ngoId == null) return false;
        
        NGO ngo = ngoRepository.findById(ngoId).orElse(null);
        if (ngo == null || ngo.getAdmins() == null) return false;
        
        return ngo.getAdmins().contains(user);
    }
}