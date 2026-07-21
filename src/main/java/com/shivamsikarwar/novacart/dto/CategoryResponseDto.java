package com.shivamsikarwar.novacart.dto;

import lombok.Setter;
import lombok.Getter;

@Getter
@Setter
public class CategoryResponseDto {
    private Long id;
    private String name;
    private String description;
}
