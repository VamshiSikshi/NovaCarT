package com.shivamsikarwar.novacart.dto;

import com.shivamsikarwar.novacart.entity.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class OrderResponseDto {

    private Long id;
    private Long userId;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
    private List<OrderItemResponseDto> items;

    private String shippingFullName;
    private String shippingPhone;
    private String shippingAddressLine;
    private String shippingCity;
    private String shippingState;
    private String shippingPostalCode;
    private String shippingCountry;
}