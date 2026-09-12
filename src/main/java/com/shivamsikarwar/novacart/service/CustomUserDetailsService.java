package com.shivamsikarwar.novacart.service;

import com.shivamsikarwar.novacart.entity.User;
import com.shivamsikarwar.novacart.repository.UserRepository;
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
    public UserDetails loadUserByUsername(String identifier)
            throws UsernameNotFoundException {

        String normalizedIdentifier = identifier.trim();

        User user;

        if (normalizedIdentifier.contains("@")) {

            user = userRepository.findByEmail(
                    normalizedIdentifier
            ).orElseThrow(() ->
                    new UsernameNotFoundException(
                            "User not found: " + identifier
                    )
            );

        } else {

            user = userRepository.findByUsername(normalizedIdentifier)
                    .orElseThrow(() ->
                            new UsernameNotFoundException(
                                    "User not found: " + identifier
                            )
                    );
        }

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build();
    }
}
