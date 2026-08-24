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
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminDashboardService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    @Transactional(readOnly = true)
    public AdminDashboardResponseDto getDashboard() {

        AdminDashboardResponseDto response =
                new AdminDashboardResponseDto();

        // =========================
        // USERS
        // =========================

        response.setTotalUsers(
                userRepository.count()
        );

        response.setTotalAdmins(
                userRepository.countByRole(Role.ADMIN)
        );

        // =========================
        // PRODUCTS
        // =========================

        response.setTotalProducts(
                productRepository.countProducts()
        );

        response.setTotalStock(
                productRepository.getTotalStock()
        );

        response.setLowStockProducts(
                productRepository.countByStockLessThanEqual(
                        DashboardConfig.LOW_STOCK_THRESHOLD
                )
        );

        // =========================
        // ORDERS
        // =========================

        response.setTotalOrders(
                orderRepository.count()
        );

        response.setPendingOrders(
                orderRepository.countByStatus(
                        OrderStatus.PENDING
                )
        );

        response.setConfirmedOrders(
                orderRepository.countByStatus(
                        OrderStatus.CONFIRMED
                )
        );

        response.setShippedOrders(
                orderRepository.countByStatus(
                        OrderStatus.SHIPPED
                )
        );

        response.setDeliveredOrders(
                orderRepository.countByStatus(
                        OrderStatus.DELIVERED
                )
        );

        response.setCancelledOrders(
                orderRepository.countByStatus(
                        OrderStatus.CANCELLED
                )
        );

        // =========================
        // PAYMENTS
        // =========================

        response.setTotalPayments(
                paymentRepository.count()
        );

        response.setSuccessfulPayments(
                paymentRepository.countByStatus(
                        PaymentStatus.SUCCESS
                )
        );

        BigDecimal revenue =
                paymentRepository.getTotalAmountByStatus(
                        PaymentStatus.SUCCESS
                );

        response.setTotalRevenue(
                revenue != null
                        ? revenue
                        : BigDecimal.ZERO
        );

        return response;
    }
}