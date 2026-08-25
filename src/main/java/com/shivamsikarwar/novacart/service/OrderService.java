package com.shivamsikarwar.novacart.service;

import com.shivamsikarwar.novacart.dto.*;
import com.shivamsikarwar.novacart.entity.*;
import com.shivamsikarwar.novacart.exception.AuthenticatedUserNotFoundException;
import com.shivamsikarwar.novacart.exception.InsufficientStockException;
import com.shivamsikarwar.novacart.exception.InvalidOrderStatusException;
import com.shivamsikarwar.novacart.exception.ResourceNotFoundException;
import com.shivamsikarwar.novacart.repository.AddressRepository;
import com.shivamsikarwar.novacart.repository.OrderRepository;
import com.shivamsikarwar.novacart.repository.ProductRepository;
import com.shivamsikarwar.novacart.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;


    // =========================
    // CREATE ORDER
    // =========================

    @Transactional
    public OrderResponseDto createOrder(OrderRequestDto request) {

        if (request == null ||
                request.getItems() == null ||
                request.getItems().isEmpty()) {

            throw new ResourceNotFoundException(
                    "Order must contain at least one item"
            );
        }

        User user = getAuthenticatedUser();

        Address address = addressRepository
                .findByIdAndUserId(
                        request.getAddressId(),
                        user.getId()
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Address not found with id: "
                                        + request.getAddressId()
                        )
                );

        Order order = new Order();

        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);

        order.setShippingFullName(address.getFullName());
        order.setShippingPhone(address.getPhone());
        order.setShippingAddressLine(address.getAddressLine());
        order.setShippingCity(address.getCity());
        order.setShippingState(address.getState());
        order.setShippingPostalCode(address.getPostalCode());
        order.setShippingCountry(address.getCountry());

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItemRequestDto itemRequest : request.getItems()) {

            if (itemRequest.getProductId() == null) {
                throw new ResourceNotFoundException(
                        "Product ID cannot be null"
                );
            }

            if (itemRequest.getQuantity() == null ||
                    itemRequest.getQuantity() <= 0) {

                throw new ResourceNotFoundException(
                        "Quantity must be greater than zero"
                );
            }

            Product product = productRepository
                    .findById(itemRequest.getProductId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Product not found with id: "
                                            + itemRequest.getProductId()
                            )
                    );

            if (product.getStock() < itemRequest.getQuantity()) {

                throw new InsufficientStockException(
                        "Insufficient stock for product: "
                                + product.getName()
                );
            }

            BigDecimal price = product.getPrice();

            BigDecimal subtotal = price.multiply(
                    BigDecimal.valueOf(itemRequest.getQuantity())
            );

            OrderItem orderItem = new OrderItem();

            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setPrice(price);
            orderItem.setSubtotal(subtotal);

            order.getItems().add(orderItem);

            totalAmount = totalAmount.add(subtotal);

            product.setStock(
                    product.getStock()
                            - itemRequest.getQuantity()
            );
        }

        order.setTotalAmount(totalAmount);

        Order savedOrder = orderRepository.save(order);

        return mapToResponse(savedOrder);
    }


    // =========================
    // GET MY ORDERS
    // =========================

    @Transactional(readOnly = true)
    public List<OrderResponseDto> getMyOrders() {

        User user = getAuthenticatedUser();

        return orderRepository
                .findMyOrdersWithItems(user.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    // =========================
    // GET MY SINGLE ORDER
    // =========================

    @Transactional(readOnly = true)
    public OrderResponseDto getMyOrder(Long orderId) {

        User user = getAuthenticatedUser();

        Order order = orderRepository
                .findByIdAndUserId(orderId, user.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Order not found with id: " + orderId
                        )
                );

        return mapToResponse(order);
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

    @Transactional
    public OrderResponseDto updateOrderStatus(
            Long orderId,
            OrderStatus newStatus) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Order not found with id: " + orderId
                        )
                );

        OrderStatus currentStatus = order.getStatus();

        if (!isValidTransition(currentStatus, newStatus)) {
            throw new InvalidOrderStatusException(
                    "Invalid order status transition: "
                            + currentStatus + " -> " + newStatus
            );
        }

        order.setStatus(newStatus);

        Order updatedOrder = orderRepository.save(order);

        return mapToResponse(updatedOrder);
    }

    private boolean isValidTransition(
            OrderStatus currentStatus,
            OrderStatus newStatus) {

        return switch (currentStatus) {

            case PENDING ->
                    newStatus == OrderStatus.CONFIRMED
                            || newStatus == OrderStatus.CANCELLED;

            case CONFIRMED ->
                    newStatus == OrderStatus.SHIPPED;

            case SHIPPED ->
                    newStatus == OrderStatus.DELIVERED;

            case DELIVERED, CANCELLED ->
                    false;
        };
    }

    @Transactional
    public OrderResponseDto cancelOrder(Long orderId) {

        User user = getAuthenticatedUser();

        Order order = orderRepository
                .findByIdAndUserId(orderId, user.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Order not found with id: " + orderId
                        )
                );

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new InvalidOrderStatusException(
                    "Order cannot be cancelled when status is "
                            + order.getStatus()
            );
        }

        // Restore product stock
        for (OrderItem item : order.getItems()) {

            Product product = item.getProduct();

            product.setStock(
                    product.getStock() + item.getQuantity()
            );
        }

        order.setStatus(OrderStatus.CANCELLED);

        Order cancelledOrder = orderRepository.save(order);

        return mapToResponse(cancelledOrder);
    }

    @Transactional(readOnly = true)
    public OrderStatusResponseDto getMyOrderStatus(Long orderId) {

        User user = getAuthenticatedUser();

        Order order = orderRepository
                .findByIdAndUserId(orderId, user.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Order not found with id: " + orderId
                        )
                );

        OrderStatusResponseDto response =
                new OrderStatusResponseDto();

        response.setOrderId(order.getId());
        response.setStatus(order.getStatus());

        return response;
    }

    // =========================
    // ORDER → RESPONSE DTO
    // =========================

    private OrderResponseDto mapToResponse(Order order) {

        OrderResponseDto response = new OrderResponseDto();

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

        List<OrderItemResponseDto> items =
                order.getItems()
                        .stream()
                        .map(this::mapItemToResponse)
                        .toList();

        response.setItems(items);

        return response;
    }


    // =========================
    // ORDER ITEM → RESPONSE DTO
    // =========================

    private OrderItemResponseDto mapItemToResponse(OrderItem item) {

        OrderItemResponseDto response =
                new OrderItemResponseDto();

        response.setId(item.getId());
        response.setProductId(item.getProduct().getId());
        response.setProductName(item.getProduct().getName());
        response.setQuantity(item.getQuantity());
        response.setPrice(item.getPrice());
        response.setSubtotal(item.getSubtotal());

        return response;
    }

    private AdminOrderResponseDto mapToAdminResponse(Order order) {

        AdminOrderResponseDto response =
                new AdminOrderResponseDto();

        response.setOrderId(order.getId());
        response.setUserId(order.getUser().getId());
        response.setUsername(order.getUser().getUsername());
        response.setStatus(order.getStatus());
        response.setTotalAmount(order.getTotalAmount());
        response.setCreatedAt(order.getCreatedAt());

        return response;
    }
}