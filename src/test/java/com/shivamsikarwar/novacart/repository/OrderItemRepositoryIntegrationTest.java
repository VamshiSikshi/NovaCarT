package com.shivamsikarwar.novacart.repository;

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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class OrderItemRepositoryIntegrationTest {

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DatabaseCleanup databaseCleanup;

    private User testUser;
    private Category testCategory;
    private Order testOrder;

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

        testOrder = createOrder(
                testUser,
                OrderStatus.CONFIRMED,
                new BigDecimal("80000")
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

    private Category createCategory(
            String name) {

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
    void saveOrderItemPersistsOrderItem() {

        Product product =
                createProduct(
                        "Laptop",
                        new BigDecimal("80000"),
                        10
                );

        OrderItem savedItem =
                createOrderItem(
                        testOrder,
                        product,
                        2,
                        new BigDecimal("80000")
                );

        assertThat(savedItem.getId())
                .isNotNull();

        assertThat(savedItem.getOrder().getId())
                .isEqualTo(testOrder.getId());

        assertThat(savedItem.getProduct().getId())
                .isEqualTo(product.getId());

        assertThat(savedItem.getQuantity())
                .isEqualTo(2);

        assertThat(savedItem.getPrice())
                .isEqualByComparingTo("80000");

        assertThat(savedItem.getSubtotal())
                .isEqualByComparingTo("160000");
    }

    @Test
    void findByIdReturnsSavedOrderItem() {

        Product product =
                createProduct(
                        "Laptop",
                        new BigDecimal("80000"),
                        10
                );

        OrderItem savedItem =
                createOrderItem(
                        testOrder,
                        product,
                        1,
                        new BigDecimal("80000")
                );

        Optional<OrderItem> result =
                orderItemRepository.findById(
                        savedItem.getId()
                );

        assertThat(result)
                .isPresent();

        assertThat(result.get().getId())
                .isEqualTo(savedItem.getId());

        assertThat(result.get().getQuantity())
                .isEqualTo(1);

        assertThat(result.get().getPrice())
                .isEqualByComparingTo("80000");

        assertThat(result.get().getSubtotal())
                .isEqualByComparingTo("80000");
    }

    @Test
    void findAllReturnsAllOrderItems() {

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

        createOrderItem(
                testOrder,
                laptop,
                1,
                new BigDecimal("80000")
        );

        createOrderItem(
                testOrder,
                phone,
                2,
                new BigDecimal("40000")
        );

        assertThat(
                orderItemRepository.findAll()
        ).hasSize(2);
    }

    @Test
    void deleteRemovesOrderItem() {

        Product product =
                createProduct(
                        "Laptop",
                        new BigDecimal("80000"),
                        10
                );

        OrderItem savedItem =
                createOrderItem(
                        testOrder,
                        product,
                        1,
                        new BigDecimal("80000")
                );

        orderItemRepository.delete(savedItem);

        assertThat(
                orderItemRepository.findById(
                        savedItem.getId()
                )
        ).isEmpty();
    }

    @Test
    void orderItemMaintainsOrderRelationship() {

        Product product =
                createProduct(
                        "Laptop",
                        new BigDecimal("80000"),
                        10
                );

        OrderItem savedItem =
                createOrderItem(
                        testOrder,
                        product,
                        1,
                        new BigDecimal("80000")
                );

        OrderItem result =
                orderItemRepository.findById(
                        savedItem.getId()
                ).orElseThrow();

        assertThat(result.getOrder())
                .isNotNull();

        assertThat(result.getOrder().getId())
                .isEqualTo(testOrder.getId());
    }

    @Test
    void orderItemMaintainsProductRelationship() {

        Product product =
                createProduct(
                        "Laptop",
                        new BigDecimal("80000"),
                        10
                );

        OrderItem savedItem =
                createOrderItem(
                        testOrder,
                        product,
                        1,
                        new BigDecimal("80000")
                );

        OrderItem result =
                orderItemRepository.findById(
                        savedItem.getId()
                ).orElseThrow();

        assertThat(result.getProduct())
                .isNotNull();

        assertThat(result.getProduct().getId())
                .isEqualTo(product.getId());

        assertThat(result.getProduct().getName())
                .isEqualTo("Laptop");
    }
}