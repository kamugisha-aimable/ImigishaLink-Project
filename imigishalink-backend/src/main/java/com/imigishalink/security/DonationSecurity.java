package com.imigishalink.security;

import com.imigishalink.donations.DonationRepository;
import com.imigishalink.users.User;
import com.imigishalink.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("donationSecurity")
@RequiredArgsConstructor
public class DonationSecurity {
    
    private final DonationRepository donationRepository;
    private final UserRepository userRepository;
    
    public boolean isOwner(Long donationId, Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName()).orElse(null);
        if (user == null) return false;
        
        return donationRepository.findById(donationId)
                .map(donation -> donation.getCreatedBy().getId().equals(user.getId()))
                .orElse(false);
    }
    
    public boolean isNgoAdmin(Long donationId, Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName()).orElse(null);
        if (user == null) return false;
        
        return donationRepository.findById(donationId)
                .map(donation -> {
                    if (donation.getNgo() == null) return false;
                    return donation.getNgo().getAdmins().contains(user);
                })
                .orElse(false);
    }
}