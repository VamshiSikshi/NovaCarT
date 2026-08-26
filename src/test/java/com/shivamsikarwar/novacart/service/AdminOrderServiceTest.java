package com.shivamsikarwar.novacart.service;

import com.shivamsikarwar.novacart.dto.AdminOrderSummaryDto;
import com.shivamsikarwar.novacart.dto.OrderResponseDto;
import com.shivamsikarwar.novacart.entity.Order;
import com.shivamsikarwar.novacart.entity.OrderItem;
import com.shivamsikarwar.novacart.entity.OrderStatus;
import com.shivamsikarwar.novacart.entity.Product;
import com.shivamsikarwar.novacart.entity.User;
import com.shivamsikarwar.novacart.exception.ResourceNotFoundException;
import com.shivamsikarwar.novacart.repository.OrderRepository;
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
class AdminOrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private AdminOrderService adminOrderService;

    private User user;
    private Product product;
    private Order order;
    private OrderItem orderItem;
    private Pageable pageable;

    @BeforeEach
    void setUp() {

        pageable = PageRequest.of(0, 10);

        user = new User();
        user.setId(1L);
        user.setUsername("shivam");

        product = new Product();
        product.setId(10L);
        product.setName("Laptop");

        order = new Order();
        order.setId(100L);
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setTotalAmount(new BigDecimal("75000"));
        order.setCreatedAt(
                LocalDateTime.of(2026, 8, 26, 10, 30)
        );

        order.setShippingFullName("Shivam Sikarwar");
        order.setShippingPhone("9876543210");
        order.setShippingAddressLine("123 Main Street");
        order.setShippingCity("Agra");
        order.setShippingState("Uttar Pradesh");
        order.setShippingPostalCode("282001");
        order.setShippingCountry("India");

        orderItem = new OrderItem();
        orderItem.setId(200L);
        orderItem.setProduct(product);
        orderItem.setQuantity(2);
        orderItem.setPrice(new BigDecimal("37500"));
        orderItem.setSubtotal(new BigDecimal("75000"));

        order.setItems(List.of(orderItem));
    }

    @Test
    void getAllOrdersReturnsPagedSummaries() {

        AdminOrderSummaryDto summary =
                new AdminOrderSummaryDto(
                        100L,
                        1L,
                        "shivam",
                        OrderStatus.PENDING,
                        new BigDecimal("75000"),
                        order.getCreatedAt()
                );

        Page<AdminOrderSummaryDto> page =
                new PageImpl<>(
                        List.of(summary),
                        pageable,
                        1
                );

        when(orderRepository.findAllAdminOrderSummaries(pageable))
                .thenReturn(page);

        Page<AdminOrderSummaryDto> response =
                adminOrderService.getAllOrders(pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertEquals(1, response.getContent().size());

        assertEquals(
                100L,
                response.getContent().get(0).getOrderId()
        );

        verify(orderRepository)
                .findAllAdminOrderSummaries(pageable);
    }

    @Test
    void getOrdersByStatusReturnsFilteredSummaries() {

        AdminOrderSummaryDto summary =
                new AdminOrderSummaryDto(
                        100L,
                        1L,
                        "shivam",
                        OrderStatus.PENDING,
                        new BigDecimal("75000"),
                        order.getCreatedAt()
                );

        Page<AdminOrderSummaryDto> page =
                new PageImpl<>(
                        List.of(summary),
                        pageable,
                        1
                );

        when(orderRepository.findAdminOrderSummariesByStatus(
                OrderStatus.PENDING,
                pageable
        )).thenReturn(page);

        Page<AdminOrderSummaryDto> response =
                adminOrderService.getOrdersByStatus(
                        OrderStatus.PENDING,
                        pageable
                );

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());

        assertEquals(
                OrderStatus.PENDING,
                response.getContent()
                        .get(0)
                        .getStatus()
        );

        verify(orderRepository)
                .findAdminOrderSummariesByStatus(
                        OrderStatus.PENDING,
                        pageable
                );
    }

    @Test
    void getOrderByIdReturnsCompleteOrderResponse() {

        when(orderRepository.findById(100L))
                .thenReturn(Optional.of(order));

        OrderResponseDto response =
                adminOrderService.getOrderById(100L);

        assertNotNull(response);

        assertEquals(100L, response.getId());
        assertEquals(1L, response.getUserId());
        assertEquals(
                OrderStatus.PENDING,
                response.getStatus()
        );

        assertEquals(
                new BigDecimal("75000"),
                response.getTotalAmount()
        );

        assertEquals(
                "Shivam Sikarwar",
                response.getShippingFullName()
        );

        assertEquals(
                "9876543210",
                response.getShippingPhone()
        );

        assertEquals(
                "123 Main Street",
                response.getShippingAddressLine()
        );

        assertEquals("Agra", response.getShippingCity());
        assertEquals(
                "Uttar Pradesh",
                response.getShippingState()
        );
        assertEquals(
                "282001",
                response.getShippingPostalCode()
        );
        assertEquals(
                "India",
                response.getShippingCountry()
        );

        assertNotNull(response.getItems());
        assertEquals(1, response.getItems().size());

        assertEquals(
                200L,
                response.getItems().get(0).getId()
        );

        assertEquals(
                10L,
                response.getItems().get(0).getProductId()
        );

        assertEquals(
                "Laptop",
                response.getItems().get(0).getProductName()
        );

        assertEquals(
                2,
                response.getItems().get(0).getQuantity()
        );

        assertEquals(
                new BigDecimal("37500"),
                response.getItems().get(0).getPrice()
        );

        assertEquals(
                new BigDecimal("75000"),
                response.getItems().get(0).getSubtotal()
        );

        verify(orderRepository)
                .findById(100L);
    }

    @Test
    void getOrderByIdThrowsWhenOrderDoesNotExist() {

        when(orderRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> adminOrderService.getOrderById(999L)
        );

        verify(orderRepository)
                .findById(999L);
    }

    @Test
    void getAllOrdersReturnsEmptyPageWhenNoOrdersExist() {

        Page<AdminOrderSummaryDto> page =
                new PageImpl<>(
                        List.of(),
                        pageable,
                        0
                );

        when(orderRepository.findAllAdminOrderSummaries(pageable))
                .thenReturn(page);

        Page<AdminOrderSummaryDto> response =
                adminOrderService.getAllOrders(pageable);

        assertNotNull(response);
        assertTrue(response.isEmpty());
        assertEquals(0, response.getTotalElements());

        verify(orderRepository)
                .findAllAdminOrderSummaries(pageable);
    }
}