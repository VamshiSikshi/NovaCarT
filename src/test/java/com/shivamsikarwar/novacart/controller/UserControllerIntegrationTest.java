package com.shivamsikarwar.novacart.controller;

import com.shivamsikarwar.novacart.entity.Role;
import com.shivamsikarwar.novacart.entity.User;
import com.shivamsikarwar.novacart.repository.UserRepository;
import com.shivamsikarwar.novacart.support.DatabaseCleanup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DatabaseCleanup databaseCleanup;

    @BeforeEach
    void setUp() {
        databaseCleanup.clean();
    }

    @Test
    void createUserReturnsCreatedUser() throws Exception {

        mockMvc.perform(
                        post("/api/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "username": "testuser",
                                            "email": "testuser@example.com",
                                            "password": "password123"
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.username", is("testuser")))
                .andExpect(jsonPath("$.email", is("testuser@example.com")))
                .andExpect(jsonPath("$.role", is("USER")))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(content().string(
                        not(containsString("password123"))
                ));
    }

    @Test
    void createUserPersistsUserWithUserRole() throws Exception {

        mockMvc.perform(
                        post("/api/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "username": "testuser",
                                            "email": "testuser@example.com",
                                            "password": "password123"
                                        }
                                        """)
                )
                .andExpect(status().isOk());

        User savedUser =
                userRepository.findByUsername("testuser")
                        .orElseThrow();

        assertEquals("testuser", savedUser.getUsername());
        assertEquals("testuser@example.com", savedUser.getEmail());
        assertEquals(Role.USER, savedUser.getRole());
        assertNotEquals("password123", savedUser.getPassword());
    }

    @Test
    void createUserEncodesPassword() throws Exception {

        mockMvc.perform(
                        post("/api/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "username": "testuser",
                                            "email": "testuser@example.com",
                                            "password": "password123"
                                        }
                                        """)
                )
                .andExpect(status().isOk());

        User savedUser =
                userRepository.findByUsername("testuser")
                        .orElseThrow();

        assertNotEquals("password123", savedUser.getPassword());
        assertTrue(
                savedUser.getPassword().startsWith("$2")
        );
    }

    @Test
    void createUserWithDuplicateUsernameReturnsConflict() throws Exception {

        User existingUser = new User();
        existingUser.setUsername("testuser");
        existingUser.setEmail("existing@example.com");
        existingUser.setPassword("encoded-password");
        existingUser.setRole(Role.USER);

        userRepository.save(existingUser);

        mockMvc.perform(
                        post("/api/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "username": "testuser",
                                            "email": "new@example.com",
                                            "password": "password123"
                                        }
                                        """)
                )
                .andExpect(status().isConflict());
    }

    @Test
    void createUserWithDuplicateEmailReturnsConflict() throws Exception {

        User existingUser = new User();
        existingUser.setUsername("existinguser");
        existingUser.setEmail("testuser@example.com");
        existingUser.setPassword("encoded-password");
        existingUser.setRole(Role.USER);

        userRepository.save(existingUser);

        mockMvc.perform(
                        post("/api/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "username": "newuser",
                                            "email": "testuser@example.com",
                                            "password": "password123"
                                        }
                                        """)
                )
                .andExpect(status().isConflict());
    }

    @Test
    void createUserWithBlankUsernameReturnsBadRequest() throws Exception {

        mockMvc.perform(
                        post("/api/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "username": "",
                                            "email": "testuser@example.com",
                                            "password": "password123"
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUserWithShortUsernameReturnsBadRequest() throws Exception {

        mockMvc.perform(
                        post("/api/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "username": "ab",
                                            "email": "testuser@example.com",
                                            "password": "password123"
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUserWithInvalidEmailReturnsBadRequest() throws Exception {

        mockMvc.perform(
                        post("/api/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "username": "testuser",
                                            "email": "invalid-email",
                                            "password": "password123"
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUserWithBlankEmailReturnsBadRequest() throws Exception {

        mockMvc.perform(
                        post("/api/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "username": "testuser",
                                            "email": "",
                                            "password": "password123"
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUserWithShortPasswordReturnsBadRequest() throws Exception {

        mockMvc.perform(
                        post("/api/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "username": "testuser",
                                            "email": "testuser@example.com",
                                            "password": "short"
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUserWithBlankPasswordReturnsBadRequest() throws Exception {

        mockMvc.perform(
                        post("/api/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "username": "testuser",
                                            "email": "testuser@example.com",
                                            "password": ""
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUserWithMissingUsernameReturnsBadRequest() throws Exception {

        mockMvc.perform(
                        post("/api/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "email": "testuser@example.com",
                                            "password": "password123"
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUserWithMissingEmailReturnsBadRequest() throws Exception {

        mockMvc.perform(
                        post("/api/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "username": "testuser",
                                            "password": "password123"
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUserWithMissingPasswordReturnsBadRequest() throws Exception {

        mockMvc.perform(
                        post("/api/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "username": "testuser",
                                            "email": "testuser@example.com"
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUserDoesNotRequireAuthentication() throws Exception {

        mockMvc.perform(
                        post("/api/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "username": "publicuser",
                                            "email": "public@example.com",
                                            "password": "password123"
                                        }
                                        """)
                )
                .andExpect(status().isOk());
    }
}