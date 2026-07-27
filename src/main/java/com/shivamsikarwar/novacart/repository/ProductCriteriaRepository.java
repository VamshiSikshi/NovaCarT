package com.shivamsikarwar.novacart.repository;

import com.shivamsikarwar.novacart.entity.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductCriteriaRepository {

    private final EntityManager entityManager;

    public List<Product> findExpensiveProducts() {

        CriteriaBuilder cb =
                entityManager.getCriteriaBuilder();

        CriteriaQuery<Product> query =
                cb.createQuery(Product.class);

        Root<Product> product =
                query.from(Product.class);

        Predicate predicate =
                cb.greaterThan(
                        product.get("price"),
                        new BigDecimal("5000")
                );

        query.where(predicate);

        return entityManager
                .createQuery(query)
                .getResultList();
    }
}
