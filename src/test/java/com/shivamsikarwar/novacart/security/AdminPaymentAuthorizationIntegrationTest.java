package com.shivamsikarwar.novacart.security;

import com.shivamsikarwar.novacart.service.AdminPaymentService;
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
class AdminPaymentAuthorizationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdminPaymentService adminPaymentService;

    @Test
    void unauthenticatedUserCannotAccessAdminPayments()
            throws Exception {

        mockMvc.perform(
                get("/api/admin/payments")
        ).andExpect(status().isUnauthorized());
    }

    @Test
    void normalUserCannotAccessAdminPayments()
            throws Exception {

        mockMvc.perform(
                get("/api/admin/payments")
                        .with(user("user").roles("USER"))
        ).andExpect(status().isForbidden());
    }

    @Test
    void adminCanAccessAdminPayments()
            throws Exception {

        mockMvc.perform(
                get("/api/admin/payments")
                        .with(user("admin").roles("ADMIN"))
        ).andExpect(status().isOk());
    }
}