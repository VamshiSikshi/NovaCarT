package com.shivamsikarwar.novacart.controller;

import com.shivamsikarwar.novacart.dto.PaymentResponseDto;
import com.shivamsikarwar.novacart.entity.PaymentMethod;
import com.shivamsikarwar.novacart.entity.PaymentStatus;
import com.shivamsikarwar.novacart.service.AdminPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/payments")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminPaymentController {

    private final AdminPaymentService adminPaymentService;

    // =========================
    // GET PAYMENTS
    // =========================

    @GetMapping
    public ResponseEntity<Page<PaymentResponseDto>> getPayments(

            @RequestParam(required = false)
            PaymentStatus status,

            @RequestParam(required = false)
            PaymentMethod method,

            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            )
            Pageable pageable) {

        return ResponseEntity.ok(
                adminPaymentService.getPayments(
                        status,
                        method,
                        pageable
                )
        );
    }

    // =========================
    // GET PAYMENT BY ID
    // =========================

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponseDto> getPaymentById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                adminPaymentService.getPaymentById(id)
        );
    }
}