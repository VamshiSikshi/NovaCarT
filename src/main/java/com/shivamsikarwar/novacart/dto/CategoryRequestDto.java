package com.shivamsikarwar.novacart.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryRequestDto {

    @NotBlank(message="Category name is required")
    @Size(max=50, message="Category name cannot exceed 50 characters")
    private String name;

    @Size(max=500, message="Description cannot exceed 500 characters")
    private String description;
}
