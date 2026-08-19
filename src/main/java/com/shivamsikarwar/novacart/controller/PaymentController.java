package com.shivamsikarwar.novacart.controller;

import com.shivamsikarwar.novacart.dto.PaymentRequestDto;
import com.shivamsikarwar.novacart.dto.PaymentResponseDto;
import com.shivamsikarwar.novacart.dto.PaymentStatusUpdateRequestDto;
import com.shivamsikarwar.novacart.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders/{orderId}/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;


    // =========================
    // CREATE PAYMENT
    // =========================

    @PostMapping
    public ResponseEntity<PaymentResponseDto> createPayment(
            @PathVariable Long orderId,
            @Valid @RequestBody PaymentRequestDto request) {

        PaymentResponseDto response =
                paymentService.createPayment(orderId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    // =========================
    // GET PAYMENT
    // =========================

    @GetMapping
    public ResponseEntity<PaymentResponseDto> getPayment(
            @PathVariable Long orderId) {

        PaymentResponseDto response =
                paymentService.getPayment(orderId);

        return ResponseEntity.ok(response);
    }

    @PatchMapping
    public ResponseEntity<PaymentResponseDto> processPayment(
            @PathVariable Long orderId,
            @Valid @RequestBody PaymentStatusUpdateRequestDto request) {

        PaymentResponseDto response =
                paymentService.processPayment(
                        orderId,
                        request.getStatus()
                );

        return ResponseEntity.ok(response);
    }
}