package com.shivamsikarwar.novacart.dto;

import com.shivamsikarwar.novacart.entity.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentRequestDto {

    @NotNull(message = "Payment method is required")
    private PaymentMethod method;
}
