package com.loginFeature.login.service;

import com.loginFeature.login.entity.User;
import com.loginFeature.login.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("=== Loading User Details ===");
        System.out.println("Email: " + email);
        
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        
        System.out.println("User found: " + user.getUsername());
        System.out.println("User role: " + user.getRole());
        System.out.println("User active: " + user.getIsActive());
        
        if (!user.getIsActive()) {
            throw new UsernameNotFoundException("User account is deactivated");
        }
        
        // Map the role to Spring Security format
        String role = "ROLE_" + user.getRole().name();
        System.out.println("Assigned role: " + role);
        
        // Ensure new users get USER role by default if not specified
        if (user.getRole() == null) {
            role = "ROLE_USER";
            System.out.println("No role specified, defaulting to: " + role);
        }
        
        org.springframework.security.core.userdetails.User userDetails = new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.getIsActive(),
                true, // accountNonExpired
                true, // credentialsNonExpired
                true, // accountNonLocked
                Collections.singletonList(new SimpleGrantedAuthority(role))
        );
        
        System.out.println("UserDetails authorities: " + userDetails.getAuthorities());
        System.out.println("=== End Loading User Details ===");
        
        return userDetails;
    }
}