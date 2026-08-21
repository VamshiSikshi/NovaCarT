package com.shivamsikarwar.novacart.specification;

import com.shivamsikarwar.novacart.entity.Product;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

<<<<<<< HEAD
public class ProductSpecification {
    public static Specification<Product> hasName(String keyword){
        return (root, query, cb) ->
                cb.like(
                        cb.lower(root.get("name")),
                        "%" + keyword.toLowerCase()+ "%"
                );
    }

    public static Specification<Product> hasCategory(Long categoryId){
        return (root, query,cb)->
                cb.equal(
=======
public final class ProductSpecification {

    private ProductSpecification() {
    }

    public static Specification<Product> hasName(String keyword) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")),
                        "%" + keyword.toLowerCase() + "%"
                );
    }

    public static Specification<Product> hasCategory(Long categoryId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
>>>>>>> 76ca847 (feat: add product search filtering and pagination)
                        root.get("category").get("id"),
                        categoryId
                );
    }

<<<<<<< HEAD
    public static Specification<Product> hasMinPrice(BigDecimal minPrice){
        return (root, query, cb) ->
                        cb.greaterThanOrEqualTo(
                                root.get("price"),
                                minPrice
                        );
    }

    public static Specification<Product> hasMaxPrice(BigDecimal maxPrice){
        return(root, query,cb)->
                cb.lessThanOrEqualTo(
=======
    public static Specification<Product> hasMinPrice(BigDecimal minPrice) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(
                        root.get("price"),
                        minPrice
                );
    }

    public static Specification<Product> hasMaxPrice(BigDecimal maxPrice) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(
>>>>>>> 76ca847 (feat: add product search filtering and pagination)
                        root.get("price"),
                        maxPrice
                );
    }

<<<<<<< HEAD
    public static Specification<Product> inStock(){
        return (root,query,cb)->
                cb.greaterThan(
=======
    public static Specification<Product> inStock() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThan(
>>>>>>> 76ca847 (feat: add product search filtering and pagination)
                        root.get("stock"),
                        0
                );
    }
<<<<<<< HEAD
}
=======

    public static Specification<Product> outOfStock() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("stock"),
                        0
                );
    }
}
>>>>>>> 76ca847 (feat: add product search filtering and pagination)
