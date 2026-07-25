package com.shivamsikarwar.novacart.dto;

import java.math.BigDecimal;

public class ProductSummaryDto {
    private Long id;
    private String name;
    private BigDecimal price;

    public ProductSummaryDto(Long id, String name, BigDecimal price){
        this.id=id;
        this.name=name;
        this.price=price;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }
}
