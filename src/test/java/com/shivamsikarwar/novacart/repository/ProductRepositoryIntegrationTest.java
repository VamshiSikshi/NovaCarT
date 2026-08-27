package com.shivamsikarwar.novacart.repository;

import com.shivamsikarwar.novacart.dto.ProductCategoryDto;
import com.shivamsikarwar.novacart.dto.ProductSummaryDto;
import com.shivamsikarwar.novacart.entity.Category;
import com.shivamsikarwar.novacart.entity.Product;
import com.shivamsikarwar.novacart.support.DatabaseCleanup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class ProductRepositoryIntegrationTest {

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

        testCategory = createCategory("Test Category");
    }

    private Product createProduct(
            String name,
            BigDecimal price,
            Integer stock,
            Category category) {

        if (category == null) {
            category = testCategory;
        }

        Product product = new Product();

        product.setName(name);
        product.setPrice(price);
        product.setStock(stock);
        product.setCategory(category);

        return productRepository.save(product);
    }

    private Category createCategory(String name) {

        Category category = new Category();

        category.setName(name);
        category.setDescription(name + " products");

        return categoryRepository.save(category);
    }

    @Test
    void findByNameContainingIgnoreCaseReturnsMatchingProducts() {

        createProduct(
                "Gaming Laptop",
                new BigDecimal("80000"),
                10,
                null
        );

        createProduct(
                "Laptop Stand",
                new BigDecimal("2000"),
                20,
                null
        );

        createProduct(
                "Wireless Mouse",
                new BigDecimal("1000"),
                30,
                null
        );

        List<Product> products =
                productRepository.findByNameContainingIgnoreCase("laptop");

        assertThat(products)
                .hasSize(2)
                .extracting(Product::getName)
                .containsExactlyInAnyOrder(
                        "Gaming Laptop",
                        "Laptop Stand"
                );
    }

    @Test
    void findByCategoryIdReturnsProductsFromCategory() {

        Category electronics =
                createCategory("Electronics");

        Category books =
                createCategory("Books");

        createProduct(
                "Laptop",
                new BigDecimal("80000"),
                10,
                electronics
        );

        createProduct(
                "Phone",
                new BigDecimal("40000"),
                15,
                electronics
        );

        createProduct(
                "Java Book",
                new BigDecimal("1000"),
                25,
                books
        );

        List<Product> products =
                productRepository.findByCategoryId(
                        electronics.getId()
                );

        assertThat(products)
                .hasSize(2)
                .extracting(Product::getName)
                .containsExactlyInAnyOrder(
                        "Laptop",
                        "Phone"
                );
    }

    @Test
    void findByPriceBetweenReturnsProductsInRange() {

        createProduct(
                "Cheap Product",
                new BigDecimal("100"),
                10,
                null
        );

        createProduct(
                "Mid Product",
                new BigDecimal("500"),
                10,
                null
        );

        createProduct(
                "Expensive Product",
                new BigDecimal("1000"),
                10,
                null
        );

        List<Product> products =
                productRepository.findByPriceBetween(
                        new BigDecimal("400"),
                        new BigDecimal("800")
                );

        assertThat(products)
                .hasSize(1)
                .first()
                .extracting(Product::getName)
                .isEqualTo("Mid Product");
    }

    @Test
    void findByStockLessThanEqualOrderByStockAscReturnsSortedProducts() {

        createProduct(
                "Product A",
                new BigDecimal("100"),
                5,
                null
        );

        createProduct(
                "Product B",
                new BigDecimal("200"),
                2,
                null
        );

        createProduct(
                "Product C",
                new BigDecimal("300"),
                10,
                null
        );

        List<Product> products =
                productRepository
                        .findByStockLessThanEqualOrderByStockAsc(5);

        assertThat(products)
                .hasSize(2)
                .extracting(Product::getStock)
                .containsExactly(2, 5);
    }

    @Test
    void findProductsWithPriceGreaterThanReturnsProductsAbovePrice() {

        createProduct(
                "Cheap",
                new BigDecimal("100"),
                10,
                null
        );

        createProduct(
                "Expensive",
                new BigDecimal("1000"),
                10,
                null
        );

        List<Product> products =
                productRepository.findProductsWithPriceGreaterThan(
                        new BigDecimal("500")
                );

        assertThat(products)
                .hasSize(1)
                .first()
                .extracting(Product::getName)
                .isEqualTo("Expensive");
    }

    @Test
    void searchProductsReturnsCaseInsensitiveMatches() {

        createProduct(
                "Gaming Laptop",
                new BigDecimal("80000"),
                10,
                null
        );

        createProduct(
                "Wireless Mouse",
                new BigDecimal("1000"),
                20,
                null
        );

        List<Product> products =
                productRepository.searchProducts("LAPTOP");

        assertThat(products)
                .hasSize(1)
                .first()
                .extracting(Product::getName)
                .isEqualTo("Gaming Laptop");
    }

    @Test
    void findAllOrderByPriceDescReturnsProductsByHighestPriceFirst() {

        createProduct(
                "Cheap",
                new BigDecimal("100"),
                10,
                null
        );

        createProduct(
                "Expensive",
                new BigDecimal("1000"),
                10,
                null
        );

        createProduct(
                "Medium",
                new BigDecimal("500"),
                10,
                null
        );

        List<Product> products =
                productRepository.findAllOrderByPriceDesc();

        assertThat(products)
                .extracting(Product::getName)
                .containsExactly(
                        "Expensive",
                        "Medium",
                        "Cheap"
                );
    }

    @Test
    void aggregateQueriesReturnCorrectValues() {

        createProduct(
                "Product A",
                new BigDecimal("100"),
                10,
                null
        );

        createProduct(
                "Product B",
                new BigDecimal("500"),
                20,
                null
        );

        createProduct(
                "Product C",
                new BigDecimal("900"),
                30,
                null
        );

        assertThat(productRepository.countProducts())
                .isEqualTo(3L);

        assertThat(productRepository.getHighestPrice())
                .isEqualByComparingTo("900");

        assertThat(productRepository.getLowestPrice())
                .isEqualByComparingTo("100");

        assertThat(productRepository.getAveragePrice())
                .isEqualTo(500.0);

        assertThat(productRepository.getTotalStock())
                .isEqualTo(60L);
    }

    @Test
    void getProductSummariesReturnsDtoProjection() {

        createProduct(
                "Laptop",
                new BigDecimal("80000"),
                10,
                null
        );

        List<ProductSummaryDto> summaries =
                productRepository.getProductSummaries();

        assertThat(summaries)
                .hasSize(1);

        ProductSummaryDto summary =
                summaries.get(0);

        assertThat(summary.getName())
                .isEqualTo("Laptop");

        assertThat(summary.getPrice())
                .isEqualByComparingTo("80000");
    }

    @Test
    void getProductWithCategoryReturnsDtoProjection() {

        Category category =
                createCategory("Electronics");

        createProduct(
                "Laptop",
                new BigDecimal("80000"),
                10,
                category
        );

        List<ProductCategoryDto> products =
                productRepository.getProductWithCategory();

        assertThat(products)
                .hasSize(1);

        ProductCategoryDto result =
                products.get(0);

        assertThat(result.getProductName())
                .isEqualTo("Laptop");

        assertThat(result.getPrice())
                .isEqualByComparingTo("80000");

        assertThat(result.getCategoryName())
                .isEqualTo("Electronics");
    }

    @Test
    void increaseAllPricesUpdatesEveryProduct() {

        createProduct(
                "Product A",
                new BigDecimal("100"),
                10,
                null
        );

        createProduct(
                "Product B",
                new BigDecimal("200"),
                10,
                null
        );

        int updatedRows =
                productRepository.increaseAllPrices();

        assertThat(updatedRows)
                .isEqualTo(2);

        List<Product> products =
                productRepository.findAll();

        assertThat(products)
                .extracting(Product::getPrice)
                .containsExactlyInAnyOrder(
                        new BigDecimal("110.00"),
                        new BigDecimal("220.00")
                );
    }

    @Test
    void deleteOutOfStockProductsDeletesOnlyZeroStockProducts() {

        createProduct(
                "Out Of Stock",
                new BigDecimal("100"),
                0,
                null
        );

        createProduct(
                "Available",
                new BigDecimal("200"),
                5,
                null
        );

        int deletedRows =
                productRepository.deleteOutOfStockProducts();

        assertThat(deletedRows)
                .isEqualTo(1);

        List<Product> products =
                productRepository.findAll();

        assertThat(products)
                .hasSize(1)
                .first()
                .extracting(Product::getName)
                .isEqualTo("Available");
    }

    @Test
    void findAvailableProductsReturnsProductsAboveStock() {

        createProduct(
                "Low Stock",
                new BigDecimal("100"),
                3,
                null
        );

        createProduct(
                "Available",
                new BigDecimal("200"),
                10,
                null
        );

        List<Product> products =
                productRepository.findAvailableProducts(5);

        assertThat(products)
                .hasSize(1)
                .first()
                .extracting(Product::getName)
                .isEqualTo("Available");
    }

    @Test
    void findAllWithCategoryReturnsProductsWithCategory() {

        Category category =
                createCategory("Electronics");

        createProduct(
                "Laptop",
                new BigDecimal("80000"),
                10,
                category
        );

        List<Product> products =
                productRepository.findAllWithCategory();

        assertThat(products)
                .hasSize(1);

        assertThat(products.get(0).getCategory().getName())
                .isEqualTo("Electronics");
    }

    @Test
    void findAllWithCategoryPageReturnsPagedProducts() {

        Category category =
                createCategory("Electronics");

        createProduct(
                "Laptop",
                new BigDecimal("80000"),
                10,
                category
        );

        createProduct(
                "Phone",
                new BigDecimal("40000"),
                20,
                category
        );

        createProduct(
                "Tablet",
                new BigDecimal("30000"),
                15,
                category
        );

        Page<Product> page =
                productRepository.findAllWithCategory(
                        PageRequest.of(
                                0,
                                2,
                                Sort.by("id").ascending()
                        )
                );

        assertThat(page.getContent())
                .hasSize(2);

        assertThat(page.getTotalElements())
                .isEqualTo(3);

        assertThat(page.getTotalPages())
                .isEqualTo(2);

        assertThat(page.getContent())
                .allSatisfy(product ->
                        assertThat(product.getCategory())
                                .isNotNull()
                );
    }

    @Test
    void countByStockLessThanEqualReturnsCorrectCount() {

        createProduct(
                "Low Stock 1",
                new BigDecimal("100"),
                2,
                null
        );

        createProduct(
                "Low Stock 2",
                new BigDecimal("200"),
                5,
                null
        );

        createProduct(
                "Available",
                new BigDecimal("300"),
                10,
                null
        );

        assertThat(
                productRepository.countByStockLessThanEqual(5)
        ).isEqualTo(2);
    }
}