package com.shivamsikarwar.novacart.service;

import com.shivamsikarwar.novacart.dto.PaymentResponseDto;
import com.shivamsikarwar.novacart.entity.Order;
import com.shivamsikarwar.novacart.entity.Payment;
import com.shivamsikarwar.novacart.entity.PaymentMethod;
import com.shivamsikarwar.novacart.entity.PaymentStatus;
import com.shivamsikarwar.novacart.exception.ResourceNotFoundException;
import com.shivamsikarwar.novacart.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminPaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private AdminPaymentService adminPaymentService;

    private Order order;
    private Payment payment;
    private Pageable pageable;

    @BeforeEach
    void setUp() {

        pageable = PageRequest.of(0, 10);

        order = new Order();
        order.setId(100L);

        payment = new Payment();
        payment.setId(200L);
        payment.setOrder(order);
        payment.setAmount(new BigDecimal("75000"));
        payment.setMethod(PaymentMethod.CARD);
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setTransactionId("TXN-ABC123456789");
        payment.setCreatedAt(
                LocalDateTime.of(2026, 8, 26, 10, 30)
        );
    }

    @Test
    void getPaymentsReturnsMappedPaymentsWithFilters() {

        Page<Payment> page =
                new PageImpl<>(
                        List.of(payment),
                        pageable,
                        1
                );

        when(paymentRepository.findAdminPayments(
                PaymentStatus.SUCCESS,
                PaymentMethod.CARD,
                pageable
        )).thenReturn(page);

        Page<PaymentResponseDto> response =
                adminPaymentService.getPayments(
                        PaymentStatus.SUCCESS,
                        PaymentMethod.CARD,
                        pageable
                );

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertEquals(1, response.getContent().size());

        PaymentResponseDto dto =
                response.getContent().get(0);

        assertEquals(200L, dto.getId());
        assertEquals(100L, dto.getOrderId());
        assertEquals(
                new BigDecimal("75000"),
                dto.getAmount()
        );
        assertEquals(
                PaymentMethod.CARD,
                dto.getMethod()
        );
        assertEquals(
                PaymentStatus.SUCCESS,
                dto.getStatus()
        );
        assertEquals(
                "TXN-ABC123456789",
                dto.getTransactionId()
        );
        assertEquals(
                payment.getCreatedAt(),
                dto.getCreatedAt()
        );

        verify(paymentRepository)
                .findAdminPayments(
                        PaymentStatus.SUCCESS,
                        PaymentMethod.CARD,
                        pageable
                );
    }

    @Test
    void getPaymentsWorksWithoutFilters() {

        Page<Payment> page =
                new PageImpl<>(
                        List.of(payment),
                        pageable,
                        1
                );

        when(paymentRepository.findAdminPayments(
                null,
                null,
                pageable
        )).thenReturn(page);

        Page<PaymentResponseDto> response =
                adminPaymentService.getPayments(
                        null,
                        null,
                        pageable
                );

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertEquals(
                200L,
                response.getContent().get(0).getId()
        );

        verify(paymentRepository)
                .findAdminPayments(
                        null,
                        null,
                        pageable
                );
    }

    @Test
    void getPaymentsReturnsEmptyPageWhenNoPaymentsExist() {

        Page<Payment> page =
                new PageImpl<>(
                        List.of(),
                        pageable,
                        0
                );

        when(paymentRepository.findAdminPayments(
                null,
                null,
                pageable
        )).thenReturn(page);

        Page<PaymentResponseDto> response =
                adminPaymentService.getPayments(
                        null,
                        null,
                        pageable
                );

        assertNotNull(response);
        assertTrue(response.isEmpty());
        assertEquals(0, response.getTotalElements());

        verify(paymentRepository)
                .findAdminPayments(
                        null,
                        null,
                        pageable
                );
    }

    @Test
    void getPaymentByIdReturnsCompletePaymentResponse() {

        when(paymentRepository.findById(200L))
                .thenReturn(Optional.of(payment));

        PaymentResponseDto response =
                adminPaymentService.getPaymentById(200L);

        assertNotNull(response);

        assertEquals(200L, response.getId());
        assertEquals(100L, response.getOrderId());
        assertEquals(
                new BigDecimal("75000"),
                response.getAmount()
        );
        assertEquals(
                PaymentMethod.CARD,
                response.getMethod()
        );
        assertEquals(
                PaymentStatus.SUCCESS,
                response.getStatus()
        );
        assertEquals(
                "TXN-ABC123456789",
                response.getTransactionId()
        );
        assertEquals(
                payment.getCreatedAt(),
                response.getCreatedAt()
        );

        verify(paymentRepository)
                .findById(200L);
    }

    @Test
    void getPaymentByIdThrowsWhenPaymentDoesNotExist() {

        when(paymentRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> adminPaymentService.getPaymentById(999L)
        );

        verify(paymentRepository)
                .findById(999L);
    }
}