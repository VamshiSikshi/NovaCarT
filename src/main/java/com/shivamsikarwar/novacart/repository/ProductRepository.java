package com.shivamsikarwar.novacart.repository;

import com.shivamsikarwar.novacart.entity.Product;
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


}
