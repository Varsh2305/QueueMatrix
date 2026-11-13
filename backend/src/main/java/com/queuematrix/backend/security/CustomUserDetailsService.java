package com.queuematrix.backend.security;

import com.queuematrix.backend.entity.User;
import com.queuematrix.backend.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(identifier.trim().toLowerCase())
                .or(() -> userRepository.findByPhone(identifier.trim()))
                .orElseThrow(() -> new UsernameNotFoundException("User not found with identifier: " + identifier));
        return new UserPrincipal(user);
    }
}
