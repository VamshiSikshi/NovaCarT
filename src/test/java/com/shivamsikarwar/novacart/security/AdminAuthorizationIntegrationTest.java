package com.shivamsikarwar.novacart.security;

import com.shivamsikarwar.novacart.service.AdminUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AdminAuthorizationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdminUserService adminUserService;

    @Test
    void unauthenticatedUserCannotAccessAdminUsers() throws Exception {

        mockMvc.perform(
                        get("/api/admin/users")
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void normalUserCannotAccessAdminUsers() throws Exception {

        mockMvc.perform(
                        get("/api/admin/users")
                                .with(user("user").roles("USER"))
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCanAccessAdminUsers() throws Exception {

        mockMvc.perform(
                        get("/api/admin/users")
                                .with(user("admin").roles("ADMIN"))
                )
                .andExpect(status().isOk());
    }
}