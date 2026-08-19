package com.shivamsikarwar.novacart.controller;

import com.shivamsikarwar.novacart.dto.OrderRequestDto;
import com.shivamsikarwar.novacart.dto.OrderResponseDto;
import com.shivamsikarwar.novacart.dto.OrderStatusUpdateRequestDto;
import com.shivamsikarwar.novacart.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;


    // =========================
    // CREATE ORDER
    // =========================

    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(
            @RequestBody OrderRequestDto request) {

        OrderResponseDto response =
                orderService.createOrder(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    // =========================
    // GET MY ORDERS
    // =========================

    @GetMapping
    public ResponseEntity<List<OrderResponseDto>> getMyOrders() {

        List<OrderResponseDto> orders =
                orderService.getMyOrders();

        return ResponseEntity.ok(orders);
    }


    // =========================
    // GET MY SINGLE ORDER
    // =========================

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDto> getMyOrder(
            @PathVariable Long orderId) {

        OrderResponseDto response =
                orderService.getMyOrder(orderId);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<OrderResponseDto> cancelOrder(
            @PathVariable Long orderId) {

        OrderResponseDto response =
                orderService.cancelOrder(orderId);

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<OrderResponseDto> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestBody OrderStatusUpdateRequestDto request) {

        OrderResponseDto response =
                orderService.updateOrderStatus(
                        orderId,
                        request.getStatus()
                );

        return ResponseEntity.ok(response);
    }
}