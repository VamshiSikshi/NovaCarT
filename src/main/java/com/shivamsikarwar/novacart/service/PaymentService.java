package com.shivamsikarwar.novacart.service;

import com.shivamsikarwar.novacart.dto.PaymentRequestDto;
import com.shivamsikarwar.novacart.dto.PaymentResponseDto;
import com.shivamsikarwar.novacart.entity.*;
import com.shivamsikarwar.novacart.exception.AuthenticatedUserNotFoundException;
import com.shivamsikarwar.novacart.exception.InvalidPaymentException;
import com.shivamsikarwar.novacart.exception.ResourceAlreadyExistsException;
import com.shivamsikarwar.novacart.exception.ResourceNotFoundException;
import com.shivamsikarwar.novacart.repository.OrderRepository;
import com.shivamsikarwar.novacart.repository.PaymentRepository;
import com.shivamsikarwar.novacart.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;




    // =========================
    // CREATE PAYMENT
    // =========================

    @Transactional
    public PaymentResponseDto createPayment(
            Long orderId,
            PaymentRequestDto request) {

        User user = getAuthenticatedUser();

        Order order = orderRepository
                .findByIdAndUserId(orderId, user.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Order not found with id: " + orderId
                        )
                );


        // Check whether payment already exists
        if (order.getStatus() != OrderStatus.PENDING) {

            throw new InvalidPaymentException(
                    "Payment cannot be created for order with status: "
                            + order.getStatus()
            );
        }
        if (paymentRepository.existsByOrder_Id(orderId)) {
            throw new ResourceAlreadyExistsException(
                    "Payment already exists for order: " + orderId
            );
        }


        // Create payment
        Payment payment = new Payment();

        payment.setOrder(order);
        payment.setAmount(order.getTotalAmount());
        payment.setMethod(request.getMethod());
        payment.setStatus(PaymentStatus.PENDING);


        Payment savedPayment =
                paymentRepository.save(payment);

        return mapToResponse(savedPayment);
    }


    // =========================
    // GET PAYMENT
    // =========================

    @Transactional(readOnly = true)
    public PaymentResponseDto getPayment(Long orderId) {

        User user = getAuthenticatedUser();

        // Verify order belongs to authenticated user
        orderRepository
                .findByIdAndUserId(orderId, user.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Order not found with id: " + orderId
                        )
                );


        Payment payment = paymentRepository
                .findByOrder_Id(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Payment not found for order: "
                                        + orderId
                        )
                );

        return mapToResponse(payment);
    }

    @Transactional
    public PaymentResponseDto processPayment(
            Long orderId,
            PaymentStatus newStatus) {

        User user = getAuthenticatedUser();

        Order order = orderRepository
                .findByIdAndUserId(orderId, user.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Order not found with id: " + orderId
                        )
                );

        Payment payment = paymentRepository
                .findByOrder_Id(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Payment not found for order: "
                                        + orderId
                        )
                );

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new InvalidPaymentException(
                    "Payment cannot be updated from status: "
                            + payment.getStatus()
            );
        }

        if (newStatus != PaymentStatus.SUCCESS &&
                newStatus != PaymentStatus.FAILED) {

            throw new InvalidPaymentException(
                    "Payment status can only be SUCCESS or FAILED"
            );
        }

        payment.setStatus(newStatus);

        if (newStatus == PaymentStatus.SUCCESS) {

            payment.setTransactionId(
                    generateTransactionId()
            );

            order.setStatus(OrderStatus.CONFIRMED);
            orderRepository.save(order);
        }

        Payment updatedPayment =
                paymentRepository.save(payment);

        return mapToResponse(updatedPayment);
    }

    private String generateTransactionId() {
        return "TXN-" + java.util.UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 12)
                .toUpperCase();
    }


    // =========================
    // GET AUTHENTICATED USER
    // =========================

    private User getAuthenticatedUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated()) {

            throw new AuthenticatedUserNotFoundException(
                    "Authenticated user not found"
            );
        }

        String username = authentication.getName();

        return userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new AuthenticatedUserNotFoundException(
                                "Authenticated user not found: "
                                        + username
                        )
                );
    }


    // =========================
    // ENTITY → RESPONSE DTO
    // =========================

    private PaymentResponseDto mapToResponse(
            Payment payment) {

        PaymentResponseDto response =
                new PaymentResponseDto();

        response.setId(payment.getId());
        response.setOrderId(
                payment.getOrder().getId()
        );
        response.setAmount(payment.getAmount());
        response.setMethod(payment.getMethod());
        response.setStatus(payment.getStatus());
        response.setTransactionId(
                payment.getTransactionId()
        );
        response.setCreatedAt(
                payment.getCreatedAt()
        );

        return response;
    }
}