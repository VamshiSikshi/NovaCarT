package com.shivamsikarwar.novacart.service;

import com.shivamsikarwar.novacart.dto.PaymentRequestDto;
import com.shivamsikarwar.novacart.dto.PaymentResponseDto;
import com.shivamsikarwar.novacart.entity.Order;
import com.shivamsikarwar.novacart.entity.OrderStatus;
import com.shivamsikarwar.novacart.entity.Payment;
import com.shivamsikarwar.novacart.entity.PaymentMethod;
import com.shivamsikarwar.novacart.entity.PaymentStatus;
import com.shivamsikarwar.novacart.entity.User;
import com.shivamsikarwar.novacart.exception.InvalidPaymentException;
import com.shivamsikarwar.novacart.exception.ResourceAlreadyExistsException;
import com.shivamsikarwar.novacart.exception.ResourceNotFoundException;
import com.shivamsikarwar.novacart.repository.OrderRepository;
import com.shivamsikarwar.novacart.repository.PaymentRepository;
import com.shivamsikarwar.novacart.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PaymentService paymentService;

    @BeforeEach
    void setUpSecurityContext() {

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "user1",
                        null,
                        java.util.List.of()
                )
        );
    }

    @Test
    void userCanCreatePaymentForOwnPendingOrder() {

        User user = new User();
        user.setId(1L);
        user.setUsername("user1");

        Order order = new Order();
        order.setId(100L);
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setTotalAmount(new BigDecimal("500.00"));

        PaymentRequestDto request = new PaymentRequestDto();
        request.setMethod(PaymentMethod.CARD);

        when(userRepository.findByUsername("user1"))
                .thenReturn(Optional.of(user));

        when(orderRepository.findByIdAndUserId(100L, 1L))
                .thenReturn(Optional.of(order));

        when(paymentRepository.existsByOrder_Id(100L))
                .thenReturn(false);

        when(paymentRepository.save(any(Payment.class)))
                .thenAnswer(invocation -> {
                    Payment payment = invocation.getArgument(0);
                    payment.setId(1L);
                    return payment;
                });

        PaymentResponseDto response =
                paymentService.createPayment(100L, request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(100L, response.getOrderId());
        assertEquals(
                new BigDecimal("500.00"),
                response.getAmount()
        );
        assertEquals(
                PaymentMethod.CARD,
                response.getMethod()
        );
        assertEquals(
                PaymentStatus.PENDING,
                response.getStatus()
        );

        verify(paymentRepository)
                .save(any(Payment.class));
    }

    @Test
    void userCannotCreatePaymentForAnotherUsersOrder() {

        User user = new User();
        user.setId(1L);
        user.setUsername("user1");

        when(userRepository.findByUsername("user1"))
                .thenReturn(Optional.of(user));

        when(orderRepository.findByIdAndUserId(100L, 1L))
                .thenReturn(Optional.empty());

        PaymentRequestDto request = new PaymentRequestDto();
        request.setMethod(PaymentMethod.CARD);

        assertThrows(
                ResourceNotFoundException.class,
                () -> paymentService.createPayment(
                        100L,
                        request
                )
        );

        verify(paymentRepository, never())
                .save(any(Payment.class));
    }

    @Test
    void cannotCreateDuplicatePayment() {

        User user = new User();
        user.setId(1L);
        user.setUsername("user1");

        Order order = new Order();
        order.setId(100L);
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setTotalAmount(new BigDecimal("500.00"));

        PaymentRequestDto request = new PaymentRequestDto();
        request.setMethod(PaymentMethod.CARD);

        when(userRepository.findByUsername("user1"))
                .thenReturn(Optional.of(user));

        when(orderRepository.findByIdAndUserId(100L, 1L))
                .thenReturn(Optional.of(order));

        when(paymentRepository.existsByOrder_Id(100L))
                .thenReturn(true);

        assertThrows(
                ResourceAlreadyExistsException.class,
                () -> paymentService.createPayment(
                        100L,
                        request
                )
        );

        verify(paymentRepository, never())
                .save(any(Payment.class));
    }

    @Test
    void cannotCreatePaymentForNonPendingOrder() {

        User user = new User();
        user.setId(1L);
        user.setUsername("user1");

        Order order = new Order();
        order.setId(100L);
        order.setUser(user);
        order.setStatus(OrderStatus.CONFIRMED);
        order.setTotalAmount(new BigDecimal("500.00"));

        PaymentRequestDto request = new PaymentRequestDto();
        request.setMethod(PaymentMethod.CARD);

        when(userRepository.findByUsername("user1"))
                .thenReturn(Optional.of(user));

        when(orderRepository.findByIdAndUserId(100L, 1L))
                .thenReturn(Optional.of(order));

        assertThrows(
                InvalidPaymentException.class,
                () -> paymentService.createPayment(
                        100L,
                        request
                )
        );

        verify(paymentRepository, never())
                .existsByOrder_Id(100L);

        verify(paymentRepository, never())
                .save(any(Payment.class));
    }

    @Test
    void successfulPaymentConfirmsOrderAndGeneratesTransactionId() {

        User user = new User();
        user.setId(1L);
        user.setUsername("user1");

        Order order = new Order();
        order.setId(100L);
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);

        Payment payment = new Payment();
        payment.setId(1L);
        payment.setOrder(order);
        payment.setAmount(new BigDecimal("500.00"));
        payment.setMethod(PaymentMethod.CARD);
        payment.setStatus(PaymentStatus.PENDING);

        when(userRepository.findByUsername("user1"))
                .thenReturn(Optional.of(user));

        when(orderRepository.findByIdAndUserId(100L, 1L))
                .thenReturn(Optional.of(order));

        when(paymentRepository.findByOrder_Id(100L))
                .thenReturn(Optional.of(payment));

        when(paymentRepository.save(payment))
                .thenReturn(payment);

        when(orderRepository.save(order))
                .thenReturn(order);

        PaymentResponseDto response =
                paymentService.processPayment(
                        100L,
                        PaymentStatus.SUCCESS
                );

        assertNotNull(response);

        assertEquals(
                PaymentStatus.SUCCESS,
                payment.getStatus()
        );

        assertEquals(
                OrderStatus.CONFIRMED,
                order.getStatus()
        );

        assertNotNull(payment.getTransactionId());

        assertTrue(
                payment.getTransactionId()
                        .startsWith("TXN-")
        );

        verify(orderRepository).save(order);
        verify(paymentRepository).save(payment);
    }

    @Test
    void failedPaymentDoesNotConfirmOrder() {

        User user = new User();
        user.setId(1L);
        user.setUsername("user1");

        Order order = new Order();
        order.setId(100L);
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);

        Payment payment = new Payment();
        payment.setId(1L);
        payment.setOrder(order);
        payment.setAmount(new BigDecimal("500.00"));
        payment.setMethod(PaymentMethod.CARD);
        payment.setStatus(PaymentStatus.PENDING);

        when(userRepository.findByUsername("user1"))
                .thenReturn(Optional.of(user));

        when(orderRepository.findByIdAndUserId(100L, 1L))
                .thenReturn(Optional.of(order));

        when(paymentRepository.findByOrder_Id(100L))
                .thenReturn(Optional.of(payment));

        when(paymentRepository.save(payment))
                .thenReturn(payment);

        PaymentResponseDto response =
                paymentService.processPayment(
                        100L,
                        PaymentStatus.FAILED
                );

        assertNotNull(response);

        assertEquals(
                PaymentStatus.FAILED,
                payment.getStatus()
        );

        assertEquals(
                OrderStatus.PENDING,
                order.getStatus()
        );

        assertNull(payment.getTransactionId());

        verify(paymentRepository).save(payment);

        verify(orderRepository, never())
                .save(any(Order.class));
    }

    @Test
    void successfulPaymentCannotBeProcessedAgain() {

        User user = new User();
        user.setId(1L);
        user.setUsername("user1");

        Order order = new Order();
        order.setId(100L);
        order.setUser(user);
        order.setStatus(OrderStatus.CONFIRMED);

        Payment payment = new Payment();
        payment.setId(1L);
        payment.setOrder(order);
        payment.setStatus(PaymentStatus.SUCCESS);

        when(userRepository.findByUsername("user1"))
                .thenReturn(Optional.of(user));

        when(orderRepository.findByIdAndUserId(100L, 1L))
                .thenReturn(Optional.of(order));

        when(paymentRepository.findByOrder_Id(100L))
                .thenReturn(Optional.of(payment));

        assertThrows(
                InvalidPaymentException.class,
                () -> paymentService.processPayment(
                        100L,
                        PaymentStatus.FAILED
                )
        );

        verify(paymentRepository, never())
                .save(any(Payment.class));
    }

    @Test
    void failedPaymentCannotBeProcessedAgain() {

        User user = new User();
        user.setId(1L);
        user.setUsername("user1");

        Order order = new Order();
        order.setId(100L);
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);

        Payment payment = new Payment();
        payment.setId(1L);
        payment.setOrder(order);
        payment.setStatus(PaymentStatus.FAILED);

        when(userRepository.findByUsername("user1"))
                .thenReturn(Optional.of(user));

        when(orderRepository.findByIdAndUserId(100L, 1L))
                .thenReturn(Optional.of(order));

        when(paymentRepository.findByOrder_Id(100L))
                .thenReturn(Optional.of(payment));

        assertThrows(
                InvalidPaymentException.class,
                () -> paymentService.processPayment(
                        100L,
                        PaymentStatus.SUCCESS
                )
        );

        verify(paymentRepository, never())
                .save(any(Payment.class));
    }

    @Test
    void invalidPaymentStatusIsRejected() {

        User user = new User();
        user.setId(1L);
        user.setUsername("user1");

        Order order = new Order();
        order.setId(100L);
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);

        Payment payment = new Payment();
        payment.setId(1L);
        payment.setOrder(order);
        payment.setStatus(PaymentStatus.PENDING);

        when(userRepository.findByUsername("user1"))
                .thenReturn(Optional.of(user));

        when(orderRepository.findByIdAndUserId(100L, 1L))
                .thenReturn(Optional.of(order));

        when(paymentRepository.findByOrder_Id(100L))
                .thenReturn(Optional.of(payment));

        assertThrows(
                InvalidPaymentException.class,
                () -> paymentService.processPayment(
                        100L,
                        PaymentStatus.PENDING
                )
        );

        verify(paymentRepository, never())
                .save(any(Payment.class));

        verify(orderRepository, never())
                .save(any(Order.class));
    }

    @Test
    void paymentNotFoundForOrder() {

        User user = new User();
        user.setId(1L);
        user.setUsername("user1");

        Order order = new Order();
        order.setId(100L);
        order.setUser(user);

        when(userRepository.findByUsername("user1"))
                .thenReturn(Optional.of(user));

        when(orderRepository.findByIdAndUserId(100L, 1L))
                .thenReturn(Optional.of(order));

        when(paymentRepository.findByOrder_Id(100L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> paymentService.processPayment(
                        100L,
                        PaymentStatus.SUCCESS
                )
        );

        verify(paymentRepository, never())
                .save(any(Payment.class));

        verify(orderRepository, never())
                .save(any(Order.class));
    }
}