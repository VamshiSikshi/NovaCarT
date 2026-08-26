package com.shivamsikarwar.novacart.controller;

import com.shivamsikarwar.novacart.entity.Role;
import com.shivamsikarwar.novacart.entity.User;
import com.shivamsikarwar.novacart.repository.AddressRepository;
import com.shivamsikarwar.novacart.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.not;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AddressRepository addressRepository;

    @BeforeEach
    void setUp() {
        addressRepository.deleteAll();
        userRepository.deleteAll();

        User user = new User();
        user.setUsername("testuser");
        user.setEmail("testuser@example.com");
        user.setPassword(passwordEncoder.encode("password123"));
        user.setRole(Role.USER);

        userRepository.save(user);
    }

    @Test
    void loginWithValidCredentialsReturnsJwt() throws Exception {

        mockMvc.perform(
                post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "username": "testuser",
                                    "password": "password123"
                                }
                                """)
        )
        .andExpect(status().isOk())
                .andExpect(content().string(not(emptyString())));
    }

    @Test
    void loginWithWrongPasswordReturnsUnauthorized() throws Exception {

        mockMvc.perform(
                post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "username": "testuser",
                                    "password": "wrongpassword"
                                }
                                """)
        )
        .andExpect(status().isUnauthorized());
    }

    @Test
    void loginWithUnknownUsernameReturnsUnauthorized() throws Exception {

        mockMvc.perform(
                post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "username": "unknownuser",
                                    "password": "password123"
                                }
                                """)
        )
        .andExpect(status().isUnauthorized());
    }

    @Test
    void loginWithMissingUsernameReturnsBadRequest() throws Exception {

        mockMvc.perform(
                post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "password": "password123"
                                }
                                """)
        )
        .andExpect(status().isBadRequest());
    }

    @Test
    void loginWithMissingPasswordReturnsBadRequest() throws Exception {

        mockMvc.perform(
                post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "username": "testuser"
                                }
                                """)
        )
        .andExpect(status().isBadRequest());
    }
}
