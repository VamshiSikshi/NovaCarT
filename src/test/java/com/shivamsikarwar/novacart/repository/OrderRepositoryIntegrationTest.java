package com.shivamsikarwar.novacart.repository;

import com.shivamsikarwar.novacart.dto.AdminOrderSummaryDto;
import com.shivamsikarwar.novacart.entity.Category;
import com.shivamsikarwar.novacart.entity.Order;
import com.shivamsikarwar.novacart.entity.OrderItem;
import com.shivamsikarwar.novacart.entity.OrderStatus;
import com.shivamsikarwar.novacart.entity.Product;
import com.shivamsikarwar.novacart.entity.Role;
import com.shivamsikarwar.novacart.entity.User;
import com.shivamsikarwar.novacart.support.DatabaseCleanup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class OrderRepositoryIntegrationTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private DatabaseCleanup databaseCleanup;

    private User testUser;
    private Category testCategory;

    @BeforeEach
    void setUp() {

        databaseCleanup.clean();

        testUser = createUser(
                "testuser",
                "testuser@example.com"
        );

        testCategory = createCategory(
                "Test Category"
        );
    }

    private User createUser(
            String username,
            String email) {

        User user = new User();

        user.setUsername(username);
        user.setEmail(email);
        user.setPassword("password123");
        user.setRole(Role.USER);

        return userRepository.save(user);
    }

    private Category createCategory(String name) {

        Category category = new Category();

        category.setName(name);
        category.setDescription(
                name + " products"
        );

        return categoryRepository.save(category);
    }

    private Product createProduct(
            String name,
            BigDecimal price,
            Integer stock) {

        Product product = new Product();

        product.setName(name);
        product.setPrice(price);
        product.setStock(stock);
        product.setCategory(testCategory);

        return productRepository.save(product);
    }

    private Order createOrder(
            User user,
            OrderStatus status,
            BigDecimal totalAmount) {

        Order order = new Order();

        order.setUser(user);
        order.setStatus(status);
        order.setTotalAmount(totalAmount);

        order.setShippingFullName(
                "Test User"
        );
        order.setShippingPhone(
                "9876543210"
        );
        order.setShippingAddressLine(
                "123 Main Street"
        );
        order.setShippingCity(
                "Gwalior"
        );
        order.setShippingState(
                "Madhya Pradesh"
        );
        order.setShippingPostalCode(
                "474001"
        );
        order.setShippingCountry(
                "India"
        );

        return orderRepository.save(order);
    }

    private OrderItem createOrderItem(
            Order order,
            Product product,
            Integer quantity,
            BigDecimal price) {

        OrderItem item = new OrderItem();

        item.setOrder(order);
        item.setProduct(product);
        item.setQuantity(quantity);
        item.setPrice(price);
        item.setSubtotal(
                price.multiply(
                        BigDecimal.valueOf(quantity)
                )
        );

        OrderItem savedItem =
                orderItemRepository.save(item);

        order.getItems().add(savedItem);

        return savedItem;
    }

    @Test
    void findByUserIdOrderByCreatedAtDescReturnsUsersOrders() {

        Order firstOrder =
                createOrder(
                        testUser,
                        OrderStatus.PENDING,
                        new BigDecimal("1000")
                );

        Order secondOrder =
                createOrder(
                        testUser,
                        OrderStatus.CONFIRMED,
                        new BigDecimal("2000")
                );

        User anotherUser =
                createUser(
                        "anotheruser",
                        "another@example.com"
                );

        createOrder(
                anotherUser,
                OrderStatus.SHIPPED,
                new BigDecimal("3000")
        );

        List<Order> orders =
                orderRepository
                        .findByUserIdOrderByCreatedAtDesc(
                                testUser.getId()
                        );

        assertThat(orders)
                .hasSize(2);

        assertThat(orders)
                .extracting(Order::getId)
                .containsExactly(
                        secondOrder.getId(),
                        firstOrder.getId()
                );
    }

    @Test
    void findByUserIdOrderByCreatedAtDescReturnsEmptyForUserWithoutOrders() {

        List<Order> orders =
                orderRepository
                        .findByUserIdOrderByCreatedAtDesc(
                                testUser.getId()
                        );

        assertThat(orders)
                .isEmpty();
    }

    @Test
    void findByIdAndUserIdReturnsOrderForCorrectUser() {

        Order order =
                createOrder(
                        testUser,
                        OrderStatus.PENDING,
                        new BigDecimal("1500")
                );

        Optional<Order> result =
                orderRepository.findByIdAndUserId(
                        order.getId(),
                        testUser.getId()
                );

        assertThat(result)
                .isPresent();

        assertThat(result.get().getId())
                .isEqualTo(order.getId());

        assertThat(result.get().getUser().getId())
                .isEqualTo(testUser.getId());

        assertThat(result.get().getStatus())
                .isEqualTo(OrderStatus.PENDING);

        assertThat(result.get().getTotalAmount())
                .isEqualByComparingTo("1500");
    }

    @Test
    void findByIdAndUserIdReturnsEmptyForAnotherUser() {

        User anotherUser =
                createUser(
                        "anotheruser",
                        "another@example.com"
                );

        Order order =
                createOrder(
                        anotherUser,
                        OrderStatus.PENDING,
                        new BigDecimal("1500")
                );

        Optional<Order> result =
                orderRepository.findByIdAndUserId(
                        order.getId(),
                        testUser.getId()
                );

        assertThat(result)
                .isEmpty();
    }

    @Test
    void findByIdAndUserIdReturnsEmptyForMissingOrder() {

        Optional<Order> result =
                orderRepository.findByIdAndUserId(
                        999999L,
                        testUser.getId()
                );

        assertThat(result)
                .isEmpty();
    }

    @Test
    void findAllByOrderByCreatedAtDescReturnsOrdersInDescendingOrder() {

        Order firstOrder =
                createOrder(
                        testUser,
                        OrderStatus.PENDING,
                        new BigDecimal("1000")
                );

        Order secondOrder =
                createOrder(
                        testUser,
                        OrderStatus.CONFIRMED,
                        new BigDecimal("2000")
                );

        Order thirdOrder =
                createOrder(
                        testUser,
                        OrderStatus.SHIPPED,
                        new BigDecimal("3000")
                );

        Page<Order> page =
                orderRepository
                        .findAllByOrderByCreatedAtDesc(
                                PageRequest.of(0, 10)
                        );

        assertThat(page.getContent())
                .hasSize(3);

        assertThat(page.getContent())
                .extracting(Order::getId)
                .containsExactly(
                        thirdOrder.getId(),
                        secondOrder.getId(),
                        firstOrder.getId()
                );
    }

    @Test
    void findAllByOrderByCreatedAtDescSupportsPagination() {

        createOrder(
                testUser,
                OrderStatus.PENDING,
                new BigDecimal("1000")
        );

        createOrder(
                testUser,
                OrderStatus.CONFIRMED,
                new BigDecimal("2000")
        );

        createOrder(
                testUser,
                OrderStatus.SHIPPED,
                new BigDecimal("3000")
        );

        Page<Order> page =
                orderRepository
                        .findAllByOrderByCreatedAtDesc(
                                PageRequest.of(0, 2)
                        );

        assertThat(page.getContent())
                .hasSize(2);

        assertThat(page.getTotalElements())
                .isEqualTo(3);

        assertThat(page.getTotalPages())
                .isEqualTo(2);
    }

    @Test
    void findByStatusOrderByCreatedAtDescReturnsMatchingOrders() {

        Order pending1 =
                createOrder(
                        testUser,
                        OrderStatus.PENDING,
                        new BigDecimal("1000")
                );

        Order confirmed =
                createOrder(
                        testUser,
                        OrderStatus.CONFIRMED,
                        new BigDecimal("2000")
                );

        Order pending2 =
                createOrder(
                        testUser,
                        OrderStatus.PENDING,
                        new BigDecimal("3000")
                );

        Page<Order> page =
                orderRepository
                        .findByStatusOrderByCreatedAtDesc(
                                OrderStatus.PENDING,
                                PageRequest.of(0, 10)
                        );

        assertThat(page.getContent())
                .hasSize(2);

        assertThat(page.getContent())
                .extracting(Order::getId)
                .containsExactly(
                        pending2.getId(),
                        pending1.getId()
                );

        assertThat(page.getContent())
                .allSatisfy(order ->
                        assertThat(order.getStatus())
                                .isEqualTo(
                                        OrderStatus.PENDING
                                )
                );

        assertThat(page.getContent())
                .noneMatch(order ->
                        order.getId()
                                .equals(confirmed.getId())
                );
    }

    @Test
    void findByStatusOrderByCreatedAtDescReturnsEmptyForUnusedStatus() {

        createOrder(
                testUser,
                OrderStatus.PENDING,
                new BigDecimal("1000")
        );

        Page<Order> page =
                orderRepository
                        .findByStatusOrderByCreatedAtDesc(
                                OrderStatus.DELIVERED,
                                PageRequest.of(0, 10)
                        );

        assertThat(page.getContent())
                .isEmpty();

        assertThat(page.getTotalElements())
                .isZero();
    }

    @Test
    void countByStatusReturnsCorrectCount() {

        createOrder(
                testUser,
                OrderStatus.PENDING,
                new BigDecimal("1000")
        );

        createOrder(
                testUser,
                OrderStatus.PENDING,
                new BigDecimal("2000")
        );

        createOrder(
                testUser,
                OrderStatus.SHIPPED,
                new BigDecimal("3000")
        );

        assertThat(
                orderRepository.countByStatus(
                        OrderStatus.PENDING
                )
        ).isEqualTo(2);

        assertThat(
                orderRepository.countByStatus(
                        OrderStatus.SHIPPED
                )
        ).isEqualTo(1);

        assertThat(
                orderRepository.countByStatus(
                        OrderStatus.DELIVERED
                )
        ).isZero();
    }

    @Test
    void findMyOrdersWithItemsReturnsUsersOrdersWithItems() {

        Product laptop =
                createProduct(
                        "Laptop",
                        new BigDecimal("80000"),
                        10
                );

        Product phone =
                createProduct(
                        "Phone",
                        new BigDecimal("40000"),
                        20
                );

        Order order =
                createOrder(
                        testUser,
                        OrderStatus.CONFIRMED,
                        new BigDecimal("120000")
                );

        createOrderItem(
                order,
                laptop,
                1,
                new BigDecimal("80000")
        );

        createOrderItem(
                order,
                phone,
                1,
                new BigDecimal("40000")
        );

        List<Order> orders =
                orderRepository.findMyOrdersWithItems(
                        testUser.getId()
                );

        assertThat(orders)
                .hasSize(1);

        Order result =
                orders.get(0);

        assertThat(result.getId())
                .isEqualTo(order.getId());

        assertThat(result.getItems())
                .hasSize(2);

        assertThat(result.getItems())
                .extracting(item ->
                        item.getProduct().getName()
                )
                .containsExactlyInAnyOrder(
                        "Laptop",
                        "Phone"
                );
    }

    @Test
    void findMyOrdersWithItemsDoesNotReturnAnotherUsersOrders() {

        User anotherUser =
                createUser(
                        "anotheruser",
                        "another@example.com"
                );

        createOrder(
                anotherUser,
                OrderStatus.PENDING,
                new BigDecimal("1000")
        );

        List<Order> orders =
                orderRepository.findMyOrdersWithItems(
                        testUser.getId()
                );

        assertThat(orders)
                .isEmpty();
    }

    @Test
    void findAllAdminOrderSummariesReturnsCorrectProjection() {

        Order order =
                createOrder(
                        testUser,
                        OrderStatus.CONFIRMED,
                        new BigDecimal("2500")
                );

        Page<AdminOrderSummaryDto> page =
                orderRepository.findAllAdminOrderSummaries(
                        PageRequest.of(0, 10)
                );

        assertThat(page.getContent())
                .hasSize(1);

        AdminOrderSummaryDto summary =
                page.getContent().get(0);

        assertThat(summary.getOrderId())
                .isEqualTo(order.getId());

        assertThat(summary.getUserId())
                .isEqualTo(testUser.getId());

        assertThat(summary.getUsername())
                .isEqualTo(testUser.getUsername());

        assertThat(summary.getStatus())
                .isEqualTo(OrderStatus.CONFIRMED);

        assertThat(summary.getTotalAmount())
                .isEqualByComparingTo("2500");

        assertThat(summary.getCreatedAt())
                .isNotNull();
    }

    @Test
    void findAllAdminOrderSummariesSupportsPagination() {

        createOrder(
                testUser,
                OrderStatus.PENDING,
                new BigDecimal("1000")
        );

        createOrder(
                testUser,
                OrderStatus.CONFIRMED,
                new BigDecimal("2000")
        );

        createOrder(
                testUser,
                OrderStatus.SHIPPED,
                new BigDecimal("3000")
        );

        Page<AdminOrderSummaryDto> page =
                orderRepository.findAllAdminOrderSummaries(
                        PageRequest.of(0, 2)
                );

        assertThat(page.getContent())
                .hasSize(2);

        assertThat(page.getTotalElements())
                .isEqualTo(3);

        assertThat(page.getTotalPages())
                .isEqualTo(2);
    }

    @Test
    void findAdminOrderSummariesByStatusReturnsMatchingProjection() {

        createOrder(
                testUser,
                OrderStatus.PENDING,
                new BigDecimal("1000")
        );

        Order confirmedOrder =
                createOrder(
                        testUser,
                        OrderStatus.CONFIRMED,
                        new BigDecimal("2000")
                );

        createOrder(
                testUser,
                OrderStatus.SHIPPED,
                new BigDecimal("3000")
        );

        Page<AdminOrderSummaryDto> page =
                orderRepository
                        .findAdminOrderSummariesByStatus(
                                OrderStatus.CONFIRMED,
                                PageRequest.of(0, 10)
                        );

        assertThat(page.getContent())
                .hasSize(1);

        AdminOrderSummaryDto summary =
                page.getContent().get(0);

        assertThat(summary.getOrderId())
                .isEqualTo(confirmedOrder.getId());

        assertThat(summary.getUserId())
                .isEqualTo(testUser.getId());

        assertThat(summary.getUsername())
                .isEqualTo(testUser.getUsername());

        assertThat(summary.getStatus())
                .isEqualTo(OrderStatus.CONFIRMED);

        assertThat(summary.getTotalAmount())
                .isEqualByComparingTo("2000");
    }

    @Test
    void findAdminOrderSummariesByStatusReturnsEmptyForUnusedStatus() {

        createOrder(
                testUser,
                OrderStatus.PENDING,
                new BigDecimal("1000")
        );

        Page<AdminOrderSummaryDto> page =
                orderRepository
                        .findAdminOrderSummariesByStatus(
                                OrderStatus.DELIVERED,
                                PageRequest.of(0, 10)
                        );

        assertThat(page.getContent())
                .isEmpty();

        assertThat(page.getTotalElements())
                .isZero();
    }
}
