package com.shivamsikarwar.novacart.repository;

import com.shivamsikarwar.novacart.dto.ProductCategoryDto;
import com.shivamsikarwar.novacart.dto.ProductSummaryDto;
import com.shivamsikarwar.novacart.entity.Product;
import com.shivamsikarwar.novacart.service.ProductService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product,Long> {


    List<Product> findByNameContainingIgnoreCase(String keyword);

    List<Product> findByCategoryId(Long categoryId);
    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    @Query("""
            SELECT p
            FROM Product p
            WHERE p.price > :price
            """)
    List<Product> findProductsWithPriceGreaterThan(
            @Param("price") BigDecimal price
    );

    @Query("""
    SELECT p
    FROM Product p
    WHERE LOWER(p.name)
    LIKE LOWER(CONCAT('%', :keyword, '%'))
    """)
    List<Product> searchProducts(
            @Param("keyword") String keyword);

    @Query("""
            SELECT p
            FROM Product p
            ORDER BY p.price DESC
            """)
    List<Product> findAllOrderByPriceDesc();

    @Query("""
            SELECT COUNT(p)
            FROM Product p
            """)
    Long countProducts();

    @Query("""
            SELECT MAX(p.price)
            FROM Product p
            """)
    BigDecimal getHighestPrice();

    @Query("""
            SELECT MIN(p.price)
            FROM Product p
            """)
    BigDecimal getLowestPrice();

    @Query("""
            SELECT AVG(p.price)
            FROM Product p
            """)
    Double getAveragePrice();

    @Query("""
            SELECT SUM(p.price)
            FROM Product p
            """)
    Long getTotalStock();

    @Query("""
            SELECT new com.shivamsikarwar.novacart.dto.ProductSummaryDto(
            p.id,
            p.name,
            p.price
            )
            FROM Product p
            """)
    List<ProductSummaryDto> getProductSummaries();

    @Query("""
            SELECT new com.shivamsikarwar.novacart.dto.ProductCategoryDto(
            p.name,
            p.price,
            c.name
            )
            FROM Product p
            JOIN p.category c
            """)
    List<ProductCategoryDto> getProductWithCategory();

    @Query("""
            SELECT p
            FROM Product p
            JOIN FETCH p.category
            """)
    List<Product> findAllWithCategory();


}
