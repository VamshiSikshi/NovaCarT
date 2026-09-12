package com.shivamsikarwar.novacart.security;

import com.shivamsikarwar.novacart.service.AdminDashboardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AdminDashboardAuthorizationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdminDashboardService adminDashboardService;

    @Test
    void unauthenticatedUserCannotAccessAdminDashboard()
            throws Exception {

        mockMvc.perform(
                get("/api/admin/dashboard")
        ).andExpect(status().isUnauthorized());
    }

    @Test
    void normalUserCannotAccessAdminDashboard()
            throws Exception {

        mockMvc.perform(
                get("/api/admin/dashboard")
                        .with(user("user").roles("USER"))
        ).andExpect(status().isForbidden());
    }

    @Test
    void adminCanAccessAdminDashboard()
            throws Exception {

        mockMvc.perform(
                get("/api/admin/dashboard")
                        .with(user("admin").roles("ADMIN"))
        ).andExpect(status().isOk());
    }
}