package com.shivamsikarwar.novacart.controller;

import com.shivamsikarwar.novacart.entity.Category;
import com.shivamsikarwar.novacart.entity.Product;
import com.shivamsikarwar.novacart.repository.CategoryRepository;
import com.shivamsikarwar.novacart.repository.ProductRepository;
import com.shivamsikarwar.novacart.support.DatabaseCleanup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Category category;

    @Autowired
    private DatabaseCleanup databaseCleanup;

    @BeforeEach
    void setUp() {
        databaseCleanup.clean();

        category = new Category();
        category.setName("Electronics");
        category.setDescription("Electronic products");

        category = categoryRepository.save(category);
    }

    private Product createProduct(
            String name,
            BigDecimal price,
            Integer stock) {

        Product product = new Product();

        product.setName(name);
        product.setDescription(name + " description");
        product.setPrice(price);
        product.setStock(stock);
        product.setImageUrl("image.jpg");
        product.setCategory(category);

        return productRepository.save(product);
    }

    @Test
    void getAllProductsReturnsProducts() throws Exception {

        createProduct(
                "Laptop",
                new BigDecimal("75000.00"),
                10
        );

        mockMvc.perform(
                        get("/api/products")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name", is("Laptop")))
                .andExpect(jsonPath("$.content[0].price", is(75000.0)))
                .andExpect(jsonPath("$.content[0].stock", is(10)))
                .andExpect(jsonPath("$.content[0].categoryName", is("Electronics")));
    }

    @Test
    void getProductByIdReturnsProduct() throws Exception {

        Product product = createProduct(
                "Phone",
                new BigDecimal("30000.00"),
                20
        );

        mockMvc.perform(
                        get("/api/products/{id}", product.getId())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(product.getId().intValue())))
                .andExpect(jsonPath("$.name", is("Phone")))
                .andExpect(jsonPath("$.price", is(30000.0)))
                .andExpect(jsonPath("$.stock", is(20)))
                .andExpect(jsonPath("$.categoryId", is(category.getId().intValue())))
                .andExpect(jsonPath("$.categoryName", is("Electronics")));
    }

    @Test
    void getProductByIdWhenNotFoundReturnsNotFound() throws Exception {

        mockMvc.perform(
                        get("/api/products/{id}", 999999L)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void createProductReturnsCreatedProduct() throws Exception {

        mockMvc.perform(
                        post("/api/products")
                                .with(user("admin").roles("ADMIN"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "name": "Monitor",
                                            "description": "4K monitor",
                                            "price": 25000.00,
                                            "stock": 15,
                                            "imageUrl": "monitor.jpg",
                                            "categoryId": %d
                                        }
                                        """.formatted(category.getId()))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name", is("Monitor")))
                .andExpect(jsonPath("$.price", is(25000.0)))
                .andExpect(jsonPath("$.stock", is(15)))
                .andExpect(jsonPath("$.categoryId", is(category.getId().intValue())))
                .andExpect(jsonPath("$.categoryName", is("Electronics")));
    }

    @Test
    void createProductWithUserRoleReturnsForbidden() throws Exception {

        mockMvc.perform(
                        post("/api/products")
                                .with(user("testuser").roles("USER"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "name": "Monitor",
                                            "description": "4K monitor",
                                            "price": 25000.00,
                                            "stock": 15,
                                            "imageUrl": "monitor.jpg",
                                            "categoryId": %d
                                        }
                                        """.formatted(category.getId()))
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void createProductWithInvalidPriceReturnsBadRequest() throws Exception {

        mockMvc.perform(
                        post("/api/products")
                                .with(user("admin").roles("ADMIN"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "name": "Monitor",
                                            "description": "4K monitor",
                                            "price": 0,
                                            "stock": 15,
                                            "categoryId": %d
                                        }
                                        """.formatted(category.getId()))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void createProductWithNegativeStockReturnsBadRequest() throws Exception {

        mockMvc.perform(
                        post("/api/products")
                                .with(user("admin").roles("ADMIN"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "name": "Monitor",
                                            "description": "4K monitor",
                                            "price": 25000.00,
                                            "stock": -1,
                                            "categoryId": %d
                                        }
                                        """.formatted(category.getId()))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void createProductWithMissingCategoryReturnsNotFound() throws Exception {

        mockMvc.perform(
                        post("/api/products")
                                .with(user("admin").roles("ADMIN"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "name": "Monitor",
                                            "description": "4K monitor",
                                            "price": 25000.00,
                                            "stock": 15,
                                            "categoryId": 999999
                                        }
                                        """)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void updateProductReturnsUpdatedProduct() throws Exception {

        Product product = createProduct(
                "Old Product",
                new BigDecimal("10000.00"),
                5
        );

        mockMvc.perform(
                        put("/api/products/{id}", product.getId())
                                .with(user("admin").roles("ADMIN"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "name": "Updated Product",
                                            "description": "Updated description",
                                            "price": 15000.00,
                                            "stock": 25,
                                            "imageUrl": "updated.jpg",
                                            "categoryId": %d
                                        }
                                        """.formatted(category.getId()))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(product.getId().intValue())))
                .andExpect(jsonPath("$.name", is("Updated Product")))
                .andExpect(jsonPath("$.price", is(15000.0)))
                .andExpect(jsonPath("$.stock", is(25)));
    }

    @Test
    void updateProductWithUserRoleReturnsForbidden() throws Exception {

        Product product = createProduct(
                "Old Product",
                new BigDecimal("10000.00"),
                5
        );

        mockMvc.perform(
                        put("/api/products/{id}", product.getId())
                                .with(user("testuser").roles("USER"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "name": "Updated Product",
                                            "description": "Updated description",
                                            "price": 15000.00,
                                            "stock": 25,
                                            "categoryId": %d
                                        }
                                        """.formatted(category.getId()))
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void updateProductWhenNotFoundReturnsNotFound() throws Exception {

        mockMvc.perform(
                        put("/api/products/{id}", 999999L)
                                .with(user("admin").roles("ADMIN"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "name": "Updated Product",
                                            "description": "Updated description",
                                            "price": 15000.00,
                                            "stock": 25,
                                            "categoryId": %d
                                        }
                                        """.formatted(category.getId()))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteProductReturnsNoContent() throws Exception {

        Product product = createProduct(
                "Delete Me",
                new BigDecimal("5000.00"),
                5
        );

        mockMvc.perform(
                        delete("/api/products/{id}", product.getId())
                                .with(user("admin").roles("ADMIN"))
                )
                .andExpect(status().isNoContent());

        assertFalse(
                productRepository
                        .findById(product.getId())
                        .isPresent()
        );
    }

    @Test
    void deleteProductWithUserRoleReturnsForbidden() throws Exception {

        Product product = createProduct(
                "Delete Me",
                new BigDecimal("5000.00"),
                5
        );

        mockMvc.perform(
                        delete("/api/products/{id}", product.getId())
                                .with(user("testuser").roles("USER"))
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteProductWhenNotFoundReturnsNotFound() throws Exception {

        mockMvc.perform(
                        delete("/api/products/{id}", 999999L)
                                .with(user("admin").roles("ADMIN"))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void searchProductsReturnsMatchingProducts() throws Exception {

        createProduct(
                "Gaming Laptop",
                new BigDecimal("80000.00"),
                10
        );

        createProduct(
                "Office Chair",
                new BigDecimal("10000.00"),
                20
        );

        mockMvc.perform(
                        get("/api/products/search")
                                .param("keyword", "Laptop")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name", is("Gaming Laptop")));
    }

    @Test
    void getProductsByCategoryReturnsProducts() throws Exception {

        createProduct(
                "Laptop",
                new BigDecimal("70000.00"),
                10
        );

        createProduct(
                "Phone",
                new BigDecimal("30000.00"),
                20
        );

        mockMvc.perform(
                        get("/api/products/category/{categoryId}", category.getId())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void filterProductsByPriceReturnsProductsInRange() throws Exception {

        createProduct(
                "Cheap Product",
                new BigDecimal("5000.00"),
                10
        );

        createProduct(
                "Expensive Product",
                new BigDecimal("50000.00"),
                10
        );

        mockMvc.perform(
                        get("/api/products/price-range")
                                .param("minPrice", "4000")
                                .param("maxPrice", "10000")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Cheap Product")));
    }

    @Test
    void getExpensiveProductsReturnsProductsAbovePrice() throws Exception {

        createProduct(
                "Cheap Product",
                new BigDecimal("5000.00"),
                10
        );

        createProduct(
                "Expensive Product",
                new BigDecimal("50000.00"),
                10
        );

        mockMvc.perform(
                        get("/api/products/expensive")
                                .param("price", "10000")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Expensive Product")));
    }

    @Test
    void getProductsByPriceDescReturnsProducts() throws Exception {

        createProduct(
                "Cheap Product",
                new BigDecimal("5000.00"),
                10
        );

        createProduct(
                "Expensive Product",
                new BigDecimal("50000.00"),
                10
        );

        mockMvc.perform(
                        get("/api/products/expensive-first")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Expensive Product")))
                .andExpect(jsonPath("$[1].name", is("Cheap Product")));
    }

    @Test
    void countProductsReturnsCount() throws Exception {

        createProduct(
                "Product One",
                new BigDecimal("1000.00"),
                5
        );

        createProduct(
                "Product Two",
                new BigDecimal("2000.00"),
                10
        );

        mockMvc.perform(
                        get("/api/products/count")
                )
                .andExpect(status().isOk())
                .andExpect(content().string("2"));
    }

    @Test
    void priceStatisticsReturnCorrectValues() throws Exception {

        createProduct(
                "Product One",
                new BigDecimal("1000.00"),
                5
        );

        createProduct(
                "Product Two",
                new BigDecimal("3000.00"),
                10
        );

        mockMvc.perform(
                        get("/api/products/highest-price")
                )
                .andExpect(status().isOk())
                .andExpect(content().string("3000.00"));

        mockMvc.perform(
                        get("/api/products/lowest-price")
                )
                .andExpect(status().isOk())
                .andExpect(content().string("1000.00"));

        mockMvc.perform(
                        get("/api/products/average-price")
                )
                .andExpect(status().isOk())
                .andExpect(content().string("2000.0"));
    }

    @Test
    void totalStockReturnsTotal() throws Exception {

        createProduct(
                "Product One",
                new BigDecimal("1000.00"),
                5
        );

        createProduct(
                "Product Two",
                new BigDecimal("2000.00"),
                10
        );

        mockMvc.perform(
                        get("/api/products/total-stock")
                )
                .andExpect(status().isOk())
                .andExpect(content().string("15"));
    }

    @Test
    void getAvailableProductsReturnsProductsAboveStock() throws Exception {

        createProduct(
                "Low Stock",
                new BigDecimal("1000.00"),
                3
        );

        createProduct(
                "Available Product",
                new BigDecimal("2000.00"),
                10
        );

        mockMvc.perform(
                        get("/api/products/available")
                                .param("stock", "5")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Available Product")));
    }

    @Test
    void getProductSummariesReturnsSummaries() throws Exception {

        createProduct(
                "Laptop",
                new BigDecimal("50000.00"),
                10
        );

        mockMvc.perform(
                        get("/api/products/summary")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Laptop")))
                .andExpect(jsonPath("$[0].price", is(50000.0)));
    }

    @Test
    void getProductsWithCategoryReturnsProducts() throws Exception {

        createProduct(
                "Laptop",
                new BigDecimal("50000.00"),
                10
        );

        mockMvc.perform(
                        get("/api/products/with-category")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].productName", is("Laptop")))
                .andExpect(jsonPath("$[0].price", is(50000.0)))
                .andExpect(jsonPath("$[0].categoryName", is("Electronics")));
    }

    @Test
    void getProductsWithCategoryFetchReturnsProducts() throws Exception {

        createProduct(
                "Laptop",
                new BigDecimal("50000.00"),
                10
        );

        mockMvc.perform(
                        get("/api/products/with-category-fetch")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Laptop")))
                .andExpect(jsonPath("$[0].categoryName", is("Electronics")));
    }

    @Test
    void getProductsWithEntityGraphReturnsProducts() throws Exception {

        createProduct(
                "Laptop",
                new BigDecimal("50000.00"),
                10
        );

        mockMvc.perform(
                        get("/api/products/with-category-graph")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Laptop")))
                .andExpect(jsonPath("$[0].categoryName", is("Electronics")));
    }

    @Test
    void increaseAllPricesWithAdminReturnsUpdatedCount() throws Exception {

        createProduct(
                "Laptop",
                new BigDecimal("10000.00"),
                10
        );

        mockMvc.perform(
                        put("/api/products/increase-price")
                                .with(user("admin").roles("ADMIN"))
                )
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("1 products updated")));
    }

    @Test
    void increaseAllPricesWithUserReturnsForbidden() throws Exception {

        mockMvc.perform(
                        put("/api/products/increase-price")
                                .with(user("testuser").roles("USER"))
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteOutOfStockProductsWithAdminReturnsDeletedCount() throws Exception {

        createProduct(
                "Out Of Stock",
                new BigDecimal("1000.00"),
                0
        );

        createProduct(
                "Available",
                new BigDecimal("2000.00"),
                10
        );

        mockMvc.perform(
                        delete("/api/products/out-of-stock")
                                .with(user("admin").roles("ADMIN"))
                )
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("1 products deleted")));
    }

    @Test
    void deleteOutOfStockProductsWithUserReturnsForbidden() throws Exception {

        mockMvc.perform(
                        delete("/api/products/out-of-stock")
                                .with(user("testuser").roles("USER"))
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void adminLowStockReturnsProducts() throws Exception {

        createProduct(
                "Low Stock Product",
                new BigDecimal("1000.00"),
                3
        );

        createProduct(
                "Normal Stock Product",
                new BigDecimal("2000.00"),
                10
        );

        mockMvc.perform(
                        get("/api/admin/products/low-stock")
                                .with(user("admin").roles("ADMIN"))
                                .param("threshold", "5")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Low Stock Product")));
    }

    @Test
    void adminLowStockWithUserReturnsForbidden() throws Exception {

        mockMvc.perform(
                        get("/api/admin/products/low-stock")
                                .with(user("testuser").roles("USER"))
                                .param("threshold", "5")
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void adminUpdateStockReturnsUpdatedProduct() throws Exception {

        Product product = createProduct(
                "Stock Product",
                new BigDecimal("10000.00"),
                5
        );

        mockMvc.perform(
                        patch("/api/admin/products/{id}/stock", product.getId())
                                .with(user("admin").roles("ADMIN"))
                                .param("stock", "25")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(product.getId().intValue())))
                .andExpect(jsonPath("$.stock", is(25)));
    }

    @Test
    void adminUpdateStockWithNegativeValueReturnsBadRequest() throws Exception {

        Product product = createProduct(
                "Stock Product",
                new BigDecimal("10000.00"),
                5
        );

        mockMvc.perform(
                        patch("/api/admin/products/{id}/stock", product.getId())
                                .with(user("admin").roles("ADMIN"))
                                .param("stock", "-1")
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void adminUpdateStockWithUserReturnsForbidden() throws Exception {

        Product product = createProduct(
                "Stock Product",
                new BigDecimal("10000.00"),
                5
        );

        mockMvc.perform(
                        patch("/api/admin/products/{id}/stock", product.getId())
                                .with(user("testuser").roles("USER"))
                                .param("stock", "25")
                )
                .andExpect(status().isForbidden());
    }
}
