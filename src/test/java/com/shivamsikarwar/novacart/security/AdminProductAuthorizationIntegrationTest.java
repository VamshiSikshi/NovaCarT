package com.shivamsikarwar.novacart.security;

import com.shivamsikarwar.novacart.service.ProductService;
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
class AdminProductAuthorizationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @Test
    void unauthenticatedUserCannotAccessAdminProducts()
            throws Exception {

        mockMvc.perform(
                get("/api/admin/products")
        ).andExpect(status().isUnauthorized());
    }

    @Test
    void normalUserCannotAccessAdminProducts()
            throws Exception {

        mockMvc.perform(
                get("/api/admin/products")
                        .with(user("user").roles("USER"))
        ).andExpect(status().isForbidden());
    }

    @Test
    void adminCanAccessAdminProducts()
            throws Exception {

        mockMvc.perform(
                get("/api/admin/products")
                        .with(user("admin").roles("ADMIN"))
        ).andExpect(status().isOk());
    }
}
