package com.shivamsikarwar.novacart.specification;

import com.shivamsikarwar.novacart.entity.Product;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

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
                        root.get("category").get("id"),
                        categoryId
                );
    }

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
                        root.get("price"),
                        maxPrice
                );
    }

    public static Specification<Product> inStock(){
        return (root,query,cb)->
                cb.greaterThan(
                        root.get("stock"),
                        0
                );
    }
}
