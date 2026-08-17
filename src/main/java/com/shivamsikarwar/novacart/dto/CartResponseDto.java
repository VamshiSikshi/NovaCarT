package com.shivamsikarwar.novacart.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class CartResponseDto {
    private Long id;
    private List<CartItemResponseDto> items;
    private BigDecimal total;
}
