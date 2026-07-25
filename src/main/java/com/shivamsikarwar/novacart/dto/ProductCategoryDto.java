package com.shivamsikarwar.novacart.dto;

import java.math.BigDecimal;

public class ProductCategoryDto {
    private String productName;
    private BigDecimal price;
    private String categoryName;

    public ProductCategoryDto(String productName, BigDecimal price, String categoryName) {
        this.productName = productName;
        this.price = price;
        this.categoryName = categoryName;
    }

    public String getProductName() {
        return productName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getCategoryName() {
        return categoryName;
    }
}
