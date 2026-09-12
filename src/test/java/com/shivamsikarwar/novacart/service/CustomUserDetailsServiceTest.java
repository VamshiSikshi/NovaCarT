package com.shivamsikarwar.novacart.service;

import com.shivamsikarwar.novacart.entity.Role;
import com.shivamsikarwar.novacart.entity.User;
import com.shivamsikarwar.novacart.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    private User user;

    @BeforeEach
    void setUp() {

        user = new User();

        user.setId(1L);
        user.setUsername("shivam");
        user.setPassword("encoded-password");
        user.setRole(Role.USER);
    }

    @Test
    void loadUserByUsernameReturnsUserDetails() {

        when(userRepository.findByUsername("shivam"))
                .thenReturn(Optional.of(user));

        UserDetails userDetails =
                userDetailsService.loadUserByUsername("shivam");

        assertNotNull(userDetails);

        assertEquals(
                "shivam",
                userDetails.getUsername()
        );

        assertEquals(
                "encoded-password",
                userDetails.getPassword()
        );

        assertTrue(
                userDetails.getAuthorities()
                        .stream()
                        .anyMatch(authority ->
                                authority.getAuthority()
                                        .equals("ROLE_USER")
                        )
        );

        verify(userRepository)
                .findByUsername("shivam");
    }

    @Test
    void loadUserByUsernameMapsAdminRole() {

        user.setUsername("admin");
        user.setRole(Role.ADMIN);

        when(userRepository.findByUsername("admin"))
                .thenReturn(Optional.of(user));

        UserDetails userDetails =
                userDetailsService.loadUserByUsername("admin");

        assertNotNull(userDetails);

        assertEquals(
                "admin",
                userDetails.getUsername()
        );

        assertEquals(
                "encoded-password",
                userDetails.getPassword()
        );

        assertTrue(
                userDetails.getAuthorities()
                        .stream()
                        .anyMatch(authority ->
                                authority.getAuthority()
                                        .equals("ROLE_ADMIN")
                        )
        );

        verify(userRepository)
                .findByUsername("admin");
    }

    @Test
    void loadUserByUsernameThrowsWhenUserDoesNotExist() {

        when(userRepository.findByUsername("unknown"))
                .thenReturn(Optional.empty());

        UsernameNotFoundException exception =
                assertThrows(
                        UsernameNotFoundException.class,
                        () -> userDetailsService
                                .loadUserByUsername("unknown")
                );

        assertEquals(
                "User not found: unknown",
                exception.getMessage()
        );

        verify(userRepository)
                .findByUsername("unknown");
    }
}