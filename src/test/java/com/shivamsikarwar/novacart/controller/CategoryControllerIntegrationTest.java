package com.shivamsikarwar.novacart.controller;

import com.shivamsikarwar.novacart.entity.Category;
import com.shivamsikarwar.novacart.repository.CategoryRepository;
import com.shivamsikarwar.novacart.support.DatabaseCleanup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class CategoryControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private DatabaseCleanup databaseCleanup;

    @BeforeEach
    void setUp() {
        databaseCleanup.clean();
    }

    @Test
    void getAllCategoriesReturnsCategories() throws Exception {

        Category category = new Category();
        category.setName("Electronics");
        category.setDescription("Electronic products");

        categoryRepository.save(category);

        mockMvc.perform(
                        get("/api/categories")
                                .with(user("testuser").roles("USER"))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Electronics")))
                .andExpect(jsonPath("$[0].description", is("Electronic products")));
    }

    @Test
    void getCategoryByIdReturnsCategory() throws Exception {

        Category category = new Category();
        category.setName("Books");
        category.setDescription("Books and literature");

        Category savedCategory =
                categoryRepository.save(category);

        mockMvc.perform(
                        get("/api/categories/{id}", savedCategory.getId())
                                .with(user("testuser").roles("USER"))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(savedCategory.getId().intValue())))
                .andExpect(jsonPath("$.name", is("Books")))
                .andExpect(jsonPath("$.description", is("Books and literature")));
    }

    @Test
    void getCategoryByIdWhenNotFoundReturnsNotFound() throws Exception {

        mockMvc.perform(
                        get("/api/categories/{id}", 999999L)
                                .with(user("testuser").roles("USER"))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void createCategoryReturnsCreatedCategory() throws Exception {

        mockMvc.perform(
                        post("/api/categories")
                                .with(user("admin").roles("ADMIN"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "name": "Laptops",
                                            "description": "Laptop products"
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name", is("Laptops")))
                .andExpect(jsonPath("$.description", is("Laptop products")));
    }

    @Test
    void createCategoryWithBlankNameReturnsBadRequest() throws Exception {

        mockMvc.perform(
                        post("/api/categories")
                                .with(user("admin").roles("ADMIN"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "name": "",
                                            "description": "Invalid category"
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void createCategoryWithNameTooLongReturnsBadRequest() throws Exception {

        mockMvc.perform(
                        post("/api/categories")
                                .with(user("admin").roles("ADMIN"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "name": "This category name is definitely longer than fifty characters",
                                            "description": "Invalid category"
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateCategoryReturnsUpdatedCategory() throws Exception {

        Category category = new Category();
        category.setName("Old Name");
        category.setDescription("Old description");

        Category savedCategory =
                categoryRepository.save(category);

        mockMvc.perform(
                        put("/api/categories/{id}", savedCategory.getId())
                                .with(user("admin").roles("ADMIN"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "name": "New Name",
                                            "description": "New description"
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(savedCategory.getId().intValue())))
                .andExpect(jsonPath("$.name", is("New Name")))
                .andExpect(jsonPath("$.description", is("New description")));
    }

    @Test
    void updateCategoryWhenNotFoundReturnsNotFound() throws Exception {

        mockMvc.perform(
                        put("/api/categories/{id}", 999999L)
                                .with(user("admin").roles("ADMIN"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "name": "New Name",
                                            "description": "New description"
                                        }
                                        """)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteCategoryReturnsNoContent() throws Exception {

        Category category = new Category();
        category.setName("Delete Me");
        category.setDescription("Temporary category");

        Category savedCategory =
                categoryRepository.save(category);

        mockMvc.perform(
                        delete("/api/categories/{id}", savedCategory.getId())
                                .with(user("admin").roles("ADMIN"))
                )
                .andExpect(status().isNoContent());

        assertFalse(
                categoryRepository
                        .findById(savedCategory.getId())
                        .isPresent()
        );
    }

    @Test
    void deleteCategoryWhenNotFoundReturnsNotFound() throws Exception {

        mockMvc.perform(
                        delete("/api/categories/{id}", 999999L)
                                .with(user("admin").roles("ADMIN"))
                )
                .andExpect(status().isNotFound());
    }
}