package com.shivamsikarwar.novacart.security;

import com.shivamsikarwar.novacart.service.CategoryService;
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
class AdminCategoryAuthorizationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoryService categoryService;

    @Test
    void unauthenticatedUserCannotAccessAdminCategories()
            throws Exception {

        mockMvc.perform(
                get("/api/admin/categories")
        ).andExpect(status().isUnauthorized());
    }

    @Test
    void normalUserCannotAccessAdminCategories()
            throws Exception {

        mockMvc.perform(
                get("/api/admin/categories")
                        .with(user("user").roles("USER"))
        ).andExpect(status().isForbidden());
    }

    @Test
    void adminCanAccessAdminCategories()
            throws Exception {

        mockMvc.perform(
                get("/api/admin/categories")
                        .with(user("admin").roles("ADMIN"))
        ).andExpect(status().isOk());
    }
}