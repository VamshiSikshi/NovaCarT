package com.shivamsikarwar.novacart.dto;

import com.shivamsikarwar.novacart.entity.OrderStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderStatusResponseDto {

    private Long orderId;
    private OrderStatus status;
}
