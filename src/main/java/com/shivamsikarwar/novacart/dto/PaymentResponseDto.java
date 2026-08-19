package com.shivamsikarwar.novacart.dto;

import com.shivamsikarwar.novacart.entity.PaymentMethod;
import com.shivamsikarwar.novacart.entity.PaymentStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class PaymentResponseDto {

    private Long id;
    private Long orderId;
    private BigDecimal amount;
    private PaymentMethod method;
    private PaymentStatus status;
    private String transactionId;
    private LocalDateTime createdAt;
}
