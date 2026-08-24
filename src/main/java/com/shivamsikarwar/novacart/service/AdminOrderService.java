package com.shivamsikarwar.novacart.service;

import com.shivamsikarwar.novacart.dto.AdminOrderSummaryDto;
import com.shivamsikarwar.novacart.dto.OrderResponseDto;
import com.shivamsikarwar.novacart.dto.OrderItemResponseDto;
import com.shivamsikarwar.novacart.entity.Order;
import com.shivamsikarwar.novacart.entity.OrderItem;
import com.shivamsikarwar.novacart.entity.OrderStatus;
import com.shivamsikarwar.novacart.exception.ResourceNotFoundException;
import com.shivamsikarwar.novacart.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminOrderService {

    private final OrderRepository orderRepository;

    // =========================
    // GET ALL ORDERS
    // =========================

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public Page<AdminOrderSummaryDto> getAllOrders(
            Pageable pageable) {

        return orderRepository
                .findAllAdminOrderSummaries(pageable);
    }

    // =========================
    // GET ORDERS BY STATUS
    // =========================

    @Transactional(readOnly = true)
    public Page<AdminOrderSummaryDto> getOrdersByStatus(
            OrderStatus status,
            Pageable pageable) {

        return orderRepository
                .findAdminOrderSummariesByStatus(
                        status,
                        pageable
                );
    }

    // =========================
    // GET ORDER BY ID
    // =========================

    @Transactional(readOnly = true)
    public OrderResponseDto getOrderById(Long orderId) {

        Order order = orderRepository
                .findById(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Order not found with id: "
                                        + orderId
                        )
                );

        return mapToResponse(order);
    }

    // =========================
    // ORDER → RESPONSE DTO
    // =========================

    private OrderResponseDto mapToResponse(Order order) {

        OrderResponseDto response =
                new OrderResponseDto();

        response.setId(order.getId());
        response.setUserId(order.getUser().getId());
        response.setStatus(order.getStatus());
        response.setTotalAmount(order.getTotalAmount());
        response.setCreatedAt(order.getCreatedAt());

        response.setShippingFullName(
                order.getShippingFullName()
        );

        response.setShippingPhone(
                order.getShippingPhone()
        );

        response.setShippingAddressLine(
                order.getShippingAddressLine()
        );

        response.setShippingCity(
                order.getShippingCity()
        );

        response.setShippingState(
                order.getShippingState()
        );

        response.setShippingPostalCode(
                order.getShippingPostalCode()
        );

        response.setShippingCountry(
                order.getShippingCountry()
        );

        response.setItems(
                order.getItems()
                        .stream()
                        .map(this::mapItemToResponse)
                        .toList()
        );

        return response;
    }

    // =========================
    // ORDER ITEM → RESPONSE DTO
    // =========================

    private OrderItemResponseDto mapItemToResponse(
            OrderItem item) {

        OrderItemResponseDto response =
                new OrderItemResponseDto();

        response.setId(item.getId());
        response.setProductId(
                item.getProduct().getId()
        );
        response.setProductName(
                item.getProduct().getName()
        );
        response.setQuantity(item.getQuantity());
        response.setPrice(item.getPrice());
        response.setSubtotal(item.getSubtotal());

        return response;
    }
}