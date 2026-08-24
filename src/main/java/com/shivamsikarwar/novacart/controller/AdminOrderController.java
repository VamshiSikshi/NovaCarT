package com.shivamsikarwar.novacart.controller;

import com.shivamsikarwar.novacart.dto.AdminOrderSummaryDto;
import com.shivamsikarwar.novacart.dto.OrderResponseDto;
import com.shivamsikarwar.novacart.entity.OrderStatus;
import com.shivamsikarwar.novacart.service.AdminOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final AdminOrderService adminOrderService;

    // =========================
    // GET ALL ORDERS
    // =========================

    @GetMapping
    public ResponseEntity<Page<AdminOrderSummaryDto>> getAllOrders(
            @RequestParam(required = false)
            OrderStatus status,

            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            )
            Pageable pageable) {

        if (status != null) {
            return ResponseEntity.ok(
                    adminOrderService.getOrdersByStatus(
                            status,
                            pageable
                    )
            );
        }

        return ResponseEntity.ok(
                adminOrderService.getAllOrders(pageable)
        );
    }

    // =========================
    // GET ORDER BY ID
    // =========================

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDto> getOrderById(
            @PathVariable Long orderId) {

        return ResponseEntity.ok(
                adminOrderService.getOrderById(orderId)
        );
    }
}