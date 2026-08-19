package com.shivamsikarwar.novacart.dto;

import com.shivamsikarwar.novacart.entity.PaymentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentStatusUpdateRequestDto {

    @NotNull(message = "Payment status is required")
    private PaymentStatus status;
}