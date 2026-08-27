package com.shivamsikarwar.novacart.repository;

import com.shivamsikarwar.novacart.entity.Category;
import com.shivamsikarwar.novacart.entity.Product;
import com.shivamsikarwar.novacart.support.DatabaseCleanup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class ProductCriteriaRepositoryIntegrationTest {

    @Autowired
    private ProductCriteriaRepository productCriteriaRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private DatabaseCleanup databaseCleanup;

    private Category testCategory;

    @BeforeEach
    void setUp() {

        databaseCleanup.clean();

        testCategory = createCategory(
                "Test Category"
        );
    }

    private Category createCategory(String name) {

        Category category = new Category();

        category.setName(name);
        category.setDescription(
                name + " products"
        );

        return categoryRepository.save(category);
    }

    private Product createProduct(
            String name,
            BigDecimal price,
            Integer stock) {

        Product product = new Product();

        product.setName(name);
        product.setPrice(price);
        product.setStock(stock);
        product.setCategory(testCategory);

        return productRepository.save(product);
    }

    @Test
    void findExpensiveProductsReturnsProductsAbove5000() {

        createProduct(
                "Cheap Product",
                new BigDecimal("2000"),
                10
        );

        createProduct(
                "Expensive Product",
                new BigDecimal("10000"),
                10
        );

        List<Product> products =
                productCriteriaRepository
                        .findExpensiveProducts();

        assertThat(products)
                .hasSize(1);

        assertThat(products.get(0).getName())
                .isEqualTo("Expensive Product");

        assertThat(products.get(0).getPrice())
                .isEqualByComparingTo("10000");
    }

    @Test
    void findExpensiveProductsExcludesProductsAtOrBelow5000() {

        createProduct(
                "Below Limit",
                new BigDecimal("4999.99"),
                10
        );

        createProduct(
                "Exactly Limit",
                new BigDecimal("5000"),
                10
        );

        createProduct(
                "Above Limit",
                new BigDecimal("5000.01"),
                10
        );

        List<Product> products =
                productCriteriaRepository
                        .findExpensiveProducts();

        assertThat(products)
                .hasSize(1);

        assertThat(products.get(0).getName())
                .isEqualTo("Above Limit");
    }

    @Test
    void findExpensiveProductsReturnsMultipleExpensiveProducts() {

        createProduct(
                "Laptop",
                new BigDecimal("80000"),
                10
        );

        createProduct(
                "Phone",
                new BigDecimal("40000"),
                20
        );

        createProduct(
                "Mouse",
                new BigDecimal("1000"),
                30
        );

        List<Product> products =
                productCriteriaRepository
                        .findExpensiveProducts();

        assertThat(products)
                .hasSize(2);

        assertThat(products)
                .extracting(Product::getName)
                .containsExactlyInAnyOrder(
                        "Laptop",
                        "Phone"
                );
    }

    @Test
    void findExpensiveProductsReturnsEmptyWhenNoProductIsAbove5000() {

        createProduct(
                "Mouse",
                new BigDecimal("1000"),
                10
        );

        createProduct(
                "Keyboard",
                new BigDecimal("3000"),
                10
        );

        createProduct(
                "Headphones",
                new BigDecimal("5000"),
                10
        );

        List<Product> products =
                productCriteriaRepository
                        .findExpensiveProducts();

        assertThat(products)
                .isEmpty();
    }

    @Test
    void findExpensiveProductsReturnsCorrectProductData() {

        Product savedProduct =
                createProduct(
                        "Gaming Laptop",
                        new BigDecimal("85000"),
                        5
                );

        List<Product> products =
                productCriteriaRepository
                        .findExpensiveProducts();

        assertThat(products)
                .hasSize(1);

        Product result =
                products.get(0);

        assertThat(result.getId())
                .isEqualTo(savedProduct.getId());

        assertThat(result.getName())
                .isEqualTo("Gaming Laptop");

        assertThat(result.getPrice())
                .isEqualByComparingTo("85000");

        assertThat(result.getStock())
                .isEqualTo(5);

        assertThat(result.getCategory().getId())
                .isEqualTo(testCategory.getId());
    }
}