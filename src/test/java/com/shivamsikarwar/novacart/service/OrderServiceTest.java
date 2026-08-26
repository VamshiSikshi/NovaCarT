package com.shivamsikarwar.novacart.service;

import com.shivamsikarwar.novacart.dto.OrderResponseDto;
import com.shivamsikarwar.novacart.entity.*;
import com.shivamsikarwar.novacart.exception.InvalidOrderStatusException;
import com.shivamsikarwar.novacart.exception.ResourceNotFoundException;
import com.shivamsikarwar.novacart.repository.AddressRepository;
import com.shivamsikarwar.novacart.repository.CartRepository;
import com.shivamsikarwar.novacart.repository.OrderRepository;
import com.shivamsikarwar.novacart.repository.ProductRepository;
import com.shivamsikarwar.novacart.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private CartRepository cartRepository;

    @InjectMocks
    private OrderService orderService;

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
    void userCannotAccessAnotherUsersOrder() {

        User user = new User();
        user.setId(1L);
        user.setUsername("user1");

        when(userRepository.findByUsername("user1"))
                .thenReturn(Optional.of(user));

        when(orderRepository.findByIdAndUserId(100L, 1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> orderService.getMyOrder(100L)
        );

        verify(orderRepository)
                .findByIdAndUserId(100L, 1L);
    }

    @Test
    void userCanAccessOwnOrder() {

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

        OrderResponseDto response =
                orderService.getMyOrder(100L);

        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals(1L, response.getUserId());

        verify(orderRepository)
                .findByIdAndUserId(100L, 1L);
    }

    @Test
    void userCannotCancelAnotherUsersOrder() {

        User user = new User();
        user.setId(1L);
        user.setUsername("user1");

        when(userRepository.findByUsername("user1"))
                .thenReturn(Optional.of(user));

        when(orderRepository.findByIdAndUserId(100L, 1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> orderService.cancelOrder(100L)
        );

        verify(orderRepository)
                .findByIdAndUserId(100L, 1L);

        verify(orderRepository, never())
                .save(any(Order.class));
    }

    @Test
    void userCanCancelOwnPendingOrder() {

        User user = new User();
        user.setId(1L);
        user.setUsername("user1");

        Product product = new Product();
        product.setId(10L);
        product.setStock(5);

        OrderItem item = new OrderItem();
        item.setProduct(product);
        item.setQuantity(2);

        Order order = new Order();
        order.setId(100L);
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.getItems().add(item);

        when(userRepository.findByUsername("user1"))
                .thenReturn(Optional.of(user));

        when(orderRepository.findByIdAndUserId(100L, 1L))
                .thenReturn(Optional.of(order));

        when(orderRepository.save(order))
                .thenReturn(order);

        OrderResponseDto response =
                orderService.cancelOrder(100L);

        assertNotNull(response);
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(7, product.getStock());

        verify(orderRepository)
                .save(order);
    }

    @Test
    void userCannotCancelNonPendingOrder() {

        User user = new User();
        user.setId(1L);
        user.setUsername("user1");

        Order order = new Order();
        order.setId(100L);
        order.setUser(user);
        order.setStatus(OrderStatus.CONFIRMED);

        when(userRepository.findByUsername("user1"))
                .thenReturn(Optional.of(user));

        when(orderRepository.findByIdAndUserId(100L, 1L))
                .thenReturn(Optional.of(order));

        assertThrows(
                InvalidOrderStatusException.class,
                () -> orderService.cancelOrder(100L)
        );

        verify(orderRepository, never())
                .save(any(Order.class));
    }

    @Test
    void pendingOrderCanBeConfirmed() {

        User user = new User();
        user.setId(1L);

        Order order = new Order();
        order.setId(100L);
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);

        when(orderRepository.findById(100L))
                .thenReturn(Optional.of(order));

        when(orderRepository.save(order))
                .thenReturn(order);

        OrderResponseDto response =
                orderService.updateOrderStatus(
                        100L,
                        OrderStatus.CONFIRMED
                );

        assertNotNull(response);
        assertEquals(
                OrderStatus.CONFIRMED,
                order.getStatus()
        );

        verify(orderRepository).save(order);
    }

    @Test
    void confirmedOrderCanBeShipped() {

        User user = new User();
        user.setId(1L);

        Order order = new Order();
        order.setId(100L);
        order.setUser(user);
        order.setStatus(OrderStatus.CONFIRMED);

        when(orderRepository.findById(100L))
                .thenReturn(Optional.of(order));

        when(orderRepository.save(order))
                .thenReturn(order);

        OrderResponseDto response =
                orderService.updateOrderStatus(
                        100L,
                        OrderStatus.SHIPPED
                );

        assertNotNull(response);
        assertEquals(
                OrderStatus.SHIPPED,
                order.getStatus()
        );

        verify(orderRepository).save(order);
    }

    @Test
    void shippedOrderCanBeDelivered() {

        User user = new User();
        user.setId(1L);

        Order order = new Order();
        order.setId(100L);
        order.setUser(user);
        order.setStatus(OrderStatus.SHIPPED);

        when(orderRepository.findById(100L))
                .thenReturn(Optional.of(order));

        when(orderRepository.save(order))
                .thenReturn(order);

        OrderResponseDto response =
                orderService.updateOrderStatus(
                        100L,
                        OrderStatus.DELIVERED
                );

        assertNotNull(response);
        assertEquals(
                OrderStatus.DELIVERED,
                order.getStatus()
        );

        verify(orderRepository).save(order);
    }

    @Test
    void invalidOrderStatusTransitionIsRejected() {

        User user = new User();
        user.setId(1L);

        Order order = new Order();
        order.setId(100L);
        order.setUser(user);
        order.setStatus(OrderStatus.CONFIRMED);

        when(orderRepository.findById(100L))
                .thenReturn(Optional.of(order));

        assertThrows(
                InvalidOrderStatusException.class,
                () -> orderService.updateOrderStatus(
                        100L,
                        OrderStatus.DELIVERED
                )
        );

        verify(orderRepository, never())
                .save(any(Order.class));
    }

    @Test
    void deliveredOrderCannotChangeStatus() {

        User user = new User();
        user.setId(1L);

        Order order = new Order();
        order.setId(100L);
        order.setUser(user);
        order.setStatus(OrderStatus.DELIVERED);

        when(orderRepository.findById(100L))
                .thenReturn(Optional.of(order));

        assertThrows(
                InvalidOrderStatusException.class,
                () -> orderService.updateOrderStatus(
                        100L,
                        OrderStatus.SHIPPED
                )
        );

        verify(orderRepository, never())
                .save(any(Order.class));
    }

    @Test
    void cancelledOrderCannotChangeStatus() {

        User user = new User();
        user.setId(1L);

        Order order = new Order();
        order.setId(100L);
        order.setUser(user);
        order.setStatus(OrderStatus.CANCELLED);

        when(orderRepository.findById(100L))
                .thenReturn(Optional.of(order));

        assertThrows(
                InvalidOrderStatusException.class,
                () -> orderService.updateOrderStatus(
                        100L,
                        OrderStatus.CONFIRMED
                )
        );

        verify(orderRepository, never())
                .save(any(Order.class));
    }

    @Test
    void updateOrderStatusThrowsWhenOrderDoesNotExist() {

        when(orderRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> orderService.updateOrderStatus(
                        999L,
                        OrderStatus.CONFIRMED
                )
        );

        verify(orderRepository, never())
                .save(any(Order.class));
    }
}
