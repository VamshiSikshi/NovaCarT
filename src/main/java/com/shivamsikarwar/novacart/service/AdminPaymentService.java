package com.shivamsikarwar.novacart.service;

import com.shivamsikarwar.novacart.dto.PaymentResponseDto;
import com.shivamsikarwar.novacart.entity.Payment;
import com.shivamsikarwar.novacart.entity.PaymentMethod;
import com.shivamsikarwar.novacart.entity.PaymentStatus;
import com.shivamsikarwar.novacart.exception.ResourceNotFoundException;
import com.shivamsikarwar.novacart.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminPaymentService {

    private final PaymentRepository paymentRepository;

    // =========================
    // GET PAYMENTS
    // =========================

    @Transactional(readOnly = true)
    public Page<PaymentResponseDto> getPayments(
            PaymentStatus status,
            PaymentMethod method,
            Pageable pageable) {

        return paymentRepository
                .findAdminPayments(
                        status,
                        method,
                        pageable
                )
                .map(this::toResponseDto);
    }

    // =========================
    // GET PAYMENT BY ID
    // =========================

    @Transactional(readOnly = true)
    public PaymentResponseDto getPaymentById(
            Long id) {

        Payment payment =
                paymentRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Payment not found with id: "
                                                + id
                                )
                        );

        return toResponseDto(payment);
    }

    // =========================
    // ENTITY → RESPONSE DTO
    // =========================

    private PaymentResponseDto toResponseDto(
            Payment payment) {

        PaymentResponseDto response =
                new PaymentResponseDto();

        response.setId(payment.getId());

        response.setOrderId(
                payment.getOrder().getId()
        );

        response.setAmount(
                payment.getAmount()
        );

        response.setMethod(
                payment.getMethod()
        );

        response.setStatus(
                payment.getStatus()
        );

        response.setTransactionId(
                payment.getTransactionId()
        );

        response.setCreatedAt(
                payment.getCreatedAt()
        );

        return response;
    }
}