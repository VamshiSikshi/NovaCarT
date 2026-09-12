package com.shivamsikarwar.novacart.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AdminDashboardResponseDto {

    // =========================
    // USERS
    // =========================

    private long totalUsers;

    private long totalAdmins;

    // =========================
    // PRODUCTS
    // =========================

    private long totalProducts;

    private long totalStock;

    private long lowStockProducts;

    // =========================
    // ORDERS
    // =========================

    private long totalOrders;

    private long pendingOrders;

    private long confirmedOrders;

    private long shippedOrders;

    private long deliveredOrders;

    private long cancelledOrders;

    // =========================
    // PAYMENTS / REVENUE
    // =========================

    private long totalPayments;

    private long successfulPayments;

    private BigDecimal totalRevenue;
}