package com.shivamsikarwar.novacart.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderRequestDto {

    private List<OrderItemRequestDto> items;
}