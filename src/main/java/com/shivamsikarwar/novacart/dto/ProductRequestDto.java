package com.shivamsikarwar.novacart.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class ProductRequestDto {

    @NotBlank(message="Product name is required")
    @Size(max=100, message="Product name cannot exceed 100 characters")
    private String name;

    @Size(max=500, message="Description cannot exceed 500 characters")
    private String description;

    @NotNull(message="Price is required")
    @DecimalMin(value="0.01", message="Price must be greater than 0")
    private BigDecimal price;

    @NotNull(message="Stock is required")
    @Min(value=0, message="Stocks cannot be negative")
    private Integer stock;

    private String imageUrl;

    @NotNull(message="Category is required")
    private Long categoryId;
}
