package com.shivamsikarwar.novacart.service;

import com.shivamsikarwar.novacart.config.DashboardConfig;
import com.shivamsikarwar.novacart.dto.AdminDashboardResponseDto;
import com.shivamsikarwar.novacart.entity.OrderStatus;
import com.shivamsikarwar.novacart.entity.PaymentStatus;
import com.shivamsikarwar.novacart.entity.Role;
import com.shivamsikarwar.novacart.repository.OrderRepository;
import com.shivamsikarwar.novacart.repository.PaymentRepository;
import com.shivamsikarwar.novacart.repository.ProductRepository;
import com.shivamsikarwar.novacart.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminDashboardServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private AdminDashboardService adminDashboardService;

    @Test
    void getDashboardReturnsCompleteDashboardData() {

        when(userRepository.count())
                .thenReturn(100L);

        when(userRepository.countByRole(Role.ADMIN))
                .thenReturn(5L);

        when(productRepository.countProducts())
                .thenReturn(50L);

        when(productRepository.getTotalStock())
                .thenReturn(500L);

        when(productRepository.countByStockLessThanEqual(
                DashboardConfig.LOW_STOCK_THRESHOLD
        )).thenReturn(8L);

        when(orderRepository.count())
                .thenReturn(200L);

        when(orderRepository.countByStatus(OrderStatus.PENDING))
                .thenReturn(20L);

        when(orderRepository.countByStatus(OrderStatus.CONFIRMED))
                .thenReturn(40L);

        when(orderRepository.countByStatus(OrderStatus.SHIPPED))
                .thenReturn(50L);

        when(orderRepository.countByStatus(OrderStatus.DELIVERED))
                .thenReturn(80L);

        when(orderRepository.countByStatus(OrderStatus.CANCELLED))
                .thenReturn(10L);

        when(paymentRepository.count())
                .thenReturn(190L);

        when(paymentRepository.countByStatus(
                PaymentStatus.SUCCESS
        )).thenReturn(160L);

        when(paymentRepository.getTotalAmountByStatus(
                PaymentStatus.SUCCESS
        )).thenReturn(new BigDecimal("1250000.50"));

        AdminDashboardResponseDto response =
                adminDashboardService.getDashboard();

        assertNotNull(response);

        assertEquals(100L, response.getTotalUsers());
        assertEquals(5L, response.getTotalAdmins());

        assertEquals(50L, response.getTotalProducts());
        assertEquals(500L, response.getTotalStock());
        assertEquals(8L, response.getLowStockProducts());

        assertEquals(200L, response.getTotalOrders());
        assertEquals(20L, response.getPendingOrders());
        assertEquals(40L, response.getConfirmedOrders());
        assertEquals(50L, response.getShippedOrders());
        assertEquals(80L, response.getDeliveredOrders());
        assertEquals(10L, response.getCancelledOrders());

        assertEquals(190L, response.getTotalPayments());
        assertEquals(160L, response.getSuccessfulPayments());

        assertEquals(
                new BigDecimal("1250000.50"),
                response.getTotalRevenue()
        );

        verify(userRepository).count();
        verify(userRepository).countByRole(Role.ADMIN);

        verify(productRepository).countProducts();
        verify(productRepository).getTotalStock();
        verify(productRepository).countByStockLessThanEqual(
                DashboardConfig.LOW_STOCK_THRESHOLD
        );

        verify(orderRepository).count();
        verify(orderRepository).countByStatus(OrderStatus.PENDING);
        verify(orderRepository).countByStatus(OrderStatus.CONFIRMED);
        verify(orderRepository).countByStatus(OrderStatus.SHIPPED);
        verify(orderRepository).countByStatus(OrderStatus.DELIVERED);
        verify(orderRepository).countByStatus(OrderStatus.CANCELLED);

        verify(paymentRepository).count();
        verify(paymentRepository).countByStatus(
                PaymentStatus.SUCCESS
        );
        verify(paymentRepository).getTotalAmountByStatus(
                PaymentStatus.SUCCESS
        );
    }

    @Test
    void getDashboardUsesZeroWhenRevenueIsNull() {

        when(userRepository.count())
                .thenReturn(0L);

        when(userRepository.countByRole(Role.ADMIN))
                .thenReturn(0L);

        when(productRepository.countProducts())
                .thenReturn(0L);

        when(productRepository.getTotalStock())
                .thenReturn(0L);

        when(productRepository.countByStockLessThanEqual(
                DashboardConfig.LOW_STOCK_THRESHOLD
        )).thenReturn(0L);

        when(orderRepository.count())
                .thenReturn(0L);

        when(orderRepository.countByStatus(any(OrderStatus.class)))
                .thenReturn(0L);

        when(paymentRepository.count())
                .thenReturn(0L);

        when(paymentRepository.countByStatus(
                PaymentStatus.SUCCESS
        )).thenReturn(0L);

        when(paymentRepository.getTotalAmountByStatus(
                PaymentStatus.SUCCESS
        )).thenReturn(null);

        AdminDashboardResponseDto response =
                adminDashboardService.getDashboard();

        assertNotNull(response);

        assertEquals(0L, response.getTotalUsers());
        assertEquals(0L, response.getTotalAdmins());
        assertEquals(0L, response.getTotalProducts());
        assertEquals(0L, response.getTotalStock());
        assertEquals(0L, response.getLowStockProducts());

        assertEquals(0L, response.getTotalOrders());
        assertEquals(0L, response.getPendingOrders());
        assertEquals(0L, response.getConfirmedOrders());
        assertEquals(0L, response.getShippedOrders());
        assertEquals(0L, response.getDeliveredOrders());
        assertEquals(0L, response.getCancelledOrders());

        assertEquals(0L, response.getTotalPayments());
        assertEquals(0L, response.getSuccessfulPayments());

        assertEquals(
                BigDecimal.ZERO,
                response.getTotalRevenue()
        );

        verify(paymentRepository)
                .getTotalAmountByStatus(
                        PaymentStatus.SUCCESS
                );
    }
}