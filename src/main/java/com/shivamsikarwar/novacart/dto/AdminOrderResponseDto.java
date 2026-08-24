package com.shivamsikarwar.novacart.dto;

import com.shivamsikarwar.novacart.entity.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class AdminOrderResponseDto {

    private Long orderId;
    private Long userId;
    private String username;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
}