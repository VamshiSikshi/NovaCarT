package com.shivamsikarwar.novacart.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderRequestDto {

    @NotNull(message = "Address ID is required")
    private Long addressId;

    @NotEmpty(message = "Order must contain at least one item")
    private List<OrderItemRequestDto> items;
}