package com.shivamsikarwar.novacart.dto;

import com.shivamsikarwar.novacart.entity.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class AdminOrderSummaryDto {

    private Long orderId;
    private Long userId;
    private String username;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;

    public AdminOrderSummaryDto(
            Long orderId,
            Long userId,
            String username,
            OrderStatus status,
            BigDecimal totalAmount,
            LocalDateTime createdAt) {

        this.orderId = orderId;
        this.userId = userId;
        this.username = username;
        this.status = status;
        this.totalAmount = totalAmount;
        this.createdAt = createdAt;
    }
}