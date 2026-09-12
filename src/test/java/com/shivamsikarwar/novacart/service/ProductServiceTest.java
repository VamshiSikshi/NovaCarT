package com.shivamsikarwar.novacart.service;

import com.shivamsikarwar.novacart.dto.ProductCategoryDto;
import com.shivamsikarwar.novacart.dto.ProductRequestDto;
import com.shivamsikarwar.novacart.dto.ProductResponseDto;
import com.shivamsikarwar.novacart.dto.ProductSummaryDto;
import com.shivamsikarwar.novacart.entity.Category;
import com.shivamsikarwar.novacart.entity.Product;
import com.shivamsikarwar.novacart.exception.ResourceNotFoundException;
import com.shivamsikarwar.novacart.repository.CategoryRepository;
import com.shivamsikarwar.novacart.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductService productService;

    private Category category;
    private Product product;
    private ProductRequestDto request;

    @BeforeEach
    void setUp() {

        category = new Category();
        category.setId(1L);
        category.setName("Electronics");

        product = new Product();
        product.setId(10L);
        product.setName("Laptop");
        product.setDescription("Gaming laptop");
        product.setPrice(new BigDecimal("75000"));
        product.setStock(10);
        product.setImageUrl("laptop.jpg");
        product.setCategory(category);

        request = new ProductRequestDto();
        request.setName("Laptop");
        request.setDescription("Gaming laptop");
        request.setPrice(new BigDecimal("75000"));
        request.setStock(10);
        request.setImageUrl("laptop.jpg");
        request.setCategoryId(1L);
    }

    @Test
    void createProductCreatesProductWithCategory() {

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        when(productRepository.save(any(Product.class)))
                .thenReturn(product);

        ProductResponseDto response =
                productService.createProduct(request);

        assertNotNull(response);
        assertEquals(10L, response.getId());
        assertEquals("Laptop", response.getName());
        assertEquals(
                new BigDecimal("75000"),
                response.getPrice()
        );
        assertEquals(10, response.getStock());
        assertEquals(1L, response.getCategoryId());
        assertEquals(
                "Electronics",
                response.getCategoryName()
        );

        verify(categoryRepository)
                .findById(1L);

        verify(productRepository)
                .save(any(Product.class));
    }

    @Test
    void getProductByIdReturnsProduct() {

        when(productRepository.findById(10L))
                .thenReturn(Optional.of(product));

        ProductResponseDto response =
                productService.getProductById(10L);

        assertNotNull(response);
        assertEquals(10L, response.getId());
        assertEquals("Laptop", response.getName());
        assertEquals(
                new BigDecimal("75000"),
                response.getPrice()
        );
        assertEquals(10, response.getStock());
        assertEquals(1L, response.getCategoryId());
        assertEquals(
                "Electronics",
                response.getCategoryName()
        );

        verify(productRepository)
                .findById(10L);
    }

    @Test
    void getProductByIdThrowsWhenProductDoesNotExist() {

        when(productRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> productService.getProductById(999L)
        );

        verify(productRepository)
                .findById(999L);
    }

    @Test
    void updateProductUpdatesProductAndCategory() {

        when(productRepository.findById(10L))
                .thenReturn(Optional.of(product));

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        when(productRepository.save(product))
                .thenReturn(product);

        ProductResponseDto response =
                productService.updateProduct(10L, request);

        assertNotNull(response);

        assertEquals("Laptop", product.getName());
        assertEquals(
                new BigDecimal("75000"),
                product.getPrice()
        );
        assertEquals(10, product.getStock());
        assertEquals(category, product.getCategory());

        verify(productRepository)
                .findById(10L);

        verify(categoryRepository)
                .findById(1L);

        verify(productRepository)
                .save(product);
    }

    @Test
    void updateProductThrowsWhenProductDoesNotExist() {

        when(productRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> productService.updateProduct(999L, request)
        );

        verify(productRepository)
                .findById(999L);

        verify(productRepository, never())
                .save(any(Product.class));
    }

    @Test
    void deleteProductDeletesExistingProduct() {

        when(productRepository.findById(10L))
                .thenReturn(Optional.of(product));

        productService.deleteProduct(10L);

        verify(productRepository)
                .findById(10L);

        verify(productRepository)
                .delete(product);
    }

    @Test
    void deleteProductThrowsWhenProductDoesNotExist() {

        when(productRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> productService.deleteProduct(999L)
        );

        verify(productRepository)
                .findById(999L);

        verify(productRepository, never())
                .delete(any(Product.class));
    }

    @Test
    void updateStockUpdatesProductStock() {

        when(productRepository.findById(10L))
                .thenReturn(Optional.of(product));

        when(productRepository.save(product))
                .thenReturn(product);

        ProductResponseDto response =
                productService.updateStock(10L, 25);

        assertNotNull(response);
        assertEquals(25, product.getStock());

        verify(productRepository)
                .findById(10L);

        verify(productRepository)
                .save(product);
    }

    @Test
    void updateStockRejectsNegativeStock() {

        assertThrows(
                IllegalArgumentException.class,
                () -> productService.updateStock(10L, -1)
        );

        verify(productRepository, never())
                .findById(anyLong());

        verify(productRepository, never())
                .save(any(Product.class));
    }

    @Test
    void updateStockThrowsWhenProductDoesNotExist() {

        when(productRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> productService.updateStock(999L, 25)
        );

        verify(productRepository)
                .findById(999L);

        verify(productRepository, never())
                .save(any(Product.class));
    }

    @Test
    void updateStockRejectsNullStock() {

        assertThrows(
                IllegalArgumentException.class,
                () -> productService.updateStock(10L, null)
        );

        verify(productRepository, never())
                .findById(anyLong());

        verify(productRepository, never())
                .save(any(Product.class));
    }

    @Test
    void getLowStockProductsReturnsProductsAtOrBelowThreshold() {

        Product lowStock = new Product();
        lowStock.setId(20L);
        lowStock.setName("Mouse");
        lowStock.setPrice(new BigDecimal("1000"));
        lowStock.setStock(2);
        lowStock.setCategory(category);

        Product mediumStock = new Product();
        mediumStock.setId(21L);
        mediumStock.setName("Keyboard");
        mediumStock.setPrice(new BigDecimal("2000"));
        mediumStock.setStock(5);
        mediumStock.setCategory(category);

        when(productRepository
                .findByStockLessThanEqualOrderByStockAsc(5))
                .thenReturn(List.of(lowStock, mediumStock));

        List<ProductResponseDto> response =
                productService.getLowStockProducts(5);

        assertNotNull(response);
        assertEquals(2, response.size());

        assertEquals(20L, response.get(0).getId());
        assertEquals(2, response.get(0).getStock());

        assertEquals(21L, response.get(1).getId());
        assertEquals(5, response.get(1).getStock());

        verify(productRepository)
                .findByStockLessThanEqualOrderByStockAsc(5);
    }

    @Test
    void getLowStockProductsRejectsNegativeThreshold() {

        assertThrows(
                IllegalArgumentException.class,
                () -> productService.getLowStockProducts(-1)
        );

        verify(productRepository, never())
                .findByStockLessThanEqualOrderByStockAsc(anyInt());
    }

    @Test
    void getLowStockProductsRejectsNullThreshold() {

        assertThrows(
                IllegalArgumentException.class,
                () -> productService.getLowStockProducts(null)
        );

        verify(productRepository, never())
                .findByStockLessThanEqualOrderByStockAsc(anyInt());
    }

    @Test
    void searchProductsReturnsMatchingProducts() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Product> productPage =
                new PageImpl<>(List.of(product), pageable, 1);

        when(productRepository.findAll(
                any(Specification.class),
                eq(pageable)
        )).thenReturn(productPage);

        Page<ProductResponseDto> response =
                productService.searchProducts(
                        "Laptop",
                        null,
                        null,
                        null,
                        pageable
                );

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertEquals(1, response.getContent().size());

        assertEquals(
                10L,
                response.getContent().get(0).getId()
        );

        assertEquals(
                "Laptop",
                response.getContent().get(0).getName()
        );

        verify(productRepository)
                .findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void searchProductsRejectsNegativeMinPrice() {

        Pageable pageable = PageRequest.of(0, 10);

        assertThrows(
                IllegalArgumentException.class,
                () -> productService.searchProducts(
                        null,
                        null,
                        new BigDecimal("-1"),
                        null,
                        pageable
                )
        );

        verify(productRepository, never())
                .findAll(
                        any(Specification.class),
                        any(Pageable.class)
                );
    }

    @Test
    void searchProductsRejectsNegativeMaxPrice() {

        Pageable pageable = PageRequest.of(0, 10);

        assertThrows(
                IllegalArgumentException.class,
                () -> productService.searchProducts(
                        null,
                        null,
                        null,
                        new BigDecimal("-1"),
                        pageable
                )
        );

        verify(productRepository, never())
                .findAll(
                        any(Specification.class),
                        any(Pageable.class)
                );
    }

    @Test
    void searchProductsRejectsMinPriceGreaterThanMaxPrice() {

        Pageable pageable = PageRequest.of(0, 10);

        assertThrows(
                IllegalArgumentException.class,
                () -> productService.searchProducts(
                        null,
                        null,
                        new BigDecimal("1000"),
                        new BigDecimal("500"),
                        pageable
                )
        );

        verify(productRepository, never())
                .findAll(
                        any(Specification.class),
                        any(Pageable.class)
                );
    }

    @Test
    void getProductsByCategoryReturnsProducts() {

        Product product2 = new Product();
        product2.setId(11L);
        product2.setName("Keyboard");
        product2.setPrice(new BigDecimal("2000"));
        product2.setStock(5);
        product2.setCategory(category);

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        when(productRepository.findByCategoryId(1L))
                .thenReturn(List.of(product, product2));

        List<ProductResponseDto> response =
                productService.getProductsByCategory(1L);

        assertNotNull(response);
        assertEquals(2, response.size());

        assertEquals(10L, response.get(0).getId());
        assertEquals(11L, response.get(1).getId());

        verify(categoryRepository)
                .findById(1L);

        verify(productRepository)
                .findByCategoryId(1L);
    }

    @Test
    void getProductsByCategoryThrowsWhenCategoryDoesNotExist() {

        when(categoryRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> productService.getProductsByCategory(999L)
        );

        verify(categoryRepository)
                .findById(999L);

        verify(productRepository, never())
                .findByCategoryId(anyLong());
    }

    @Test
    void filterProductsByPriceReturnsProductsInRange() {

        when(productRepository.findByPriceBetween(
                new BigDecimal("50000"),
                new BigDecimal("100000")
        )).thenReturn(List.of(product));

        List<ProductResponseDto> response =
                productService.filterProductsByPrice(
                        new BigDecimal("50000"),
                        new BigDecimal("100000")
                );

        assertNotNull(response);
        assertEquals(1, response.size());

        assertEquals(10L, response.get(0).getId());
        assertEquals(
                new BigDecimal("75000"),
                response.get(0).getPrice()
        );

        verify(productRepository)
                .findByPriceBetween(
                        new BigDecimal("50000"),
                        new BigDecimal("100000")
                );
    }

    @Test
    void getExpensiveProductsReturnsProductsAbovePrice() {

        when(productRepository.findProductsWithPriceGreaterThan(
                new BigDecimal("50000")
        )).thenReturn(List.of(product));

        List<ProductResponseDto> response =
                productService.getExpensiveProducts(
                        new BigDecimal("50000")
                );

        assertNotNull(response);
        assertEquals(1, response.size());

        assertEquals(10L, response.get(0).getId());
        assertEquals(
                new BigDecimal("75000"),
                response.get(0).getPrice()
        );

        verify(productRepository)
                .findProductsWithPriceGreaterThan(
                        new BigDecimal("50000")
                );
    }

    @Test
    void countProductsReturnsProductCount() {

        when(productRepository.countProducts())
                .thenReturn(25L);

        Long result = productService.countProducts();

        assertEquals(25L, result);

        verify(productRepository)
                .countProducts();
    }

    @Test
    void getHighestPriceReturnsHighestPrice() {

        when(productRepository.getHighestPrice())
                .thenReturn(new BigDecimal("150000"));

        BigDecimal result =
                productService.getHighestPrice();

        assertEquals(
                new BigDecimal("150000"),
                result
        );

        verify(productRepository)
                .getHighestPrice();
    }

    @Test
    void getLowestPriceReturnsLowestPrice() {

        when(productRepository.getLowestPrice())
                .thenReturn(new BigDecimal("500"));

        BigDecimal result =
                productService.getLowestPrice();

        assertEquals(
                new BigDecimal("500"),
                result
        );

        verify(productRepository)
                .getLowestPrice();
    }

    @Test
    void getAveragePriceReturnsAveragePrice() {

        when(productRepository.getAveragePrice())
                .thenReturn(42500.50);

        Double result =
                productService.getAveragePrice();

        assertEquals(
                42500.50,
                result
        );

        verify(productRepository)
                .getAveragePrice();
    }

    @Test
    void getTotalStockReturnsTotalStock() {

        when(productRepository.getTotalStock())
                .thenReturn(750L);

        Long result =
                productService.getTotalStock();

        assertEquals(750L, result);

        verify(productRepository)
                .getTotalStock();
    }

    @Test
    void getProductSummariesReturnsSummaries() {

        ProductSummaryDto summary =
                new ProductSummaryDto(
                        10L,
                        "Laptop",
                        new BigDecimal("75000")
                );

        when(productRepository.getProductSummaries())
                .thenReturn(List.of(summary));

        List<ProductSummaryDto> response =
                productService.getProductSummaries();

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(10L, response.get(0).getId());
        assertEquals("Laptop", response.get(0).getName());
        assertEquals(
                new BigDecimal("75000"),
                response.get(0).getPrice()
        );

        verify(productRepository)
                .getProductSummaries();
    }

    @Test
    void getProductWithCategoryReturnsProducts() {

        ProductCategoryDto result =
                new ProductCategoryDto(
                        "Laptop",
                        new BigDecimal("75000"),
                        "Electronics"
                );

        when(productRepository.getProductWithCategory())
                .thenReturn(List.of(result));

        List<ProductCategoryDto> response =
                productService.getProductWithCategory();

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(
                "Laptop",
                response.get(0).getProductName()
        );
        assertEquals(
                new BigDecimal("75000"),
                response.get(0).getPrice()
        );
        assertEquals(
                "Electronics",
                response.get(0).getCategoryName()
        );

        verify(productRepository)
                .getProductWithCategory();
    }

    @Test
    void getAllProductWithCategoryReturnsProducts() {

        when(productRepository.findAllWithCategory())
                .thenReturn(List.of(product));

        List<ProductResponseDto> response =
                productService.getAllProductWithCategory();

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(10L, response.get(0).getId());
        assertEquals("Laptop", response.get(0).getName());
        assertEquals(
                "Electronics",
                response.get(0).getCategoryName()
        );

        verify(productRepository)
                .findAllWithCategory();
    }

    @Test
    void getAvailableProductsReturnsAvailableProducts() {

        when(productRepository.findAvailableProducts(5))
                .thenReturn(List.of(product));

        List<ProductResponseDto> response =
                productService.getAvailableProducts(5);

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(10L, response.get(0).getId());
        assertEquals(10, response.get(0).getStock());

        verify(productRepository)
                .findAvailableProducts(5);
    }

    @Test
    void getAllProductsWithEntityGraphReturnsProducts() {

        when(productRepository.findAllWithCategory())
                .thenReturn(List.of(product));

        List<ProductResponseDto> response =
                productService.getAllProductsWithEntityGraph();

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(10L, response.get(0).getId());
        assertEquals("Laptop", response.get(0).getName());
        assertEquals(
                "Electronics",
                response.get(0).getCategoryName()
        );

        verify(productRepository)
                .findAllWithCategory();
    }

    @Test
    void increaseAllPricesReturnsUpdatedRowCount() {

        when(productRepository.increaseAllPrices())
                .thenReturn(10);

        int result =
                productService.increaseAllPrices();

        assertEquals(10, result);

        verify(productRepository)
                .increaseAllPrices();
    }

    @Test
    void deleteOutOfStockProductsReturnsDeletedRowCount() {

        when(productRepository.deleteOutOfStockProducts())
                .thenReturn(4);

        int result =
                productService.deleteOutOfStockProducts();

        assertEquals(4, result);

        verify(productRepository)
                .deleteOutOfStockProducts();
    }

}