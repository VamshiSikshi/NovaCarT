package com.shivamsikarwar.novacart.controller;

import com.shivamsikarwar.novacart.entity.Address;
import com.shivamsikarwar.novacart.entity.AddressType;
import com.shivamsikarwar.novacart.entity.Category;
import com.shivamsikarwar.novacart.entity.Order;
import com.shivamsikarwar.novacart.entity.OrderItem;
import com.shivamsikarwar.novacart.entity.OrderStatus;
import com.shivamsikarwar.novacart.entity.Product;
import com.shivamsikarwar.novacart.entity.Role;
import com.shivamsikarwar.novacart.entity.User;
import com.shivamsikarwar.novacart.repository.AddressRepository;
import com.shivamsikarwar.novacart.repository.CategoryRepository;
import com.shivamsikarwar.novacart.repository.OrderItemRepository;
import com.shivamsikarwar.novacart.repository.OrderRepository;
import com.shivamsikarwar.novacart.repository.ProductRepository;
import com.shivamsikarwar.novacart.repository.UserRepository;
import com.shivamsikarwar.novacart.support.DatabaseCleanup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class OrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    private User testUser;
    private User secondUser;
    private User adminUser;

    private Category category;
    private Product product;
    private Address address;

    @Autowired
    private DatabaseCleanup databaseCleanup;

    @BeforeEach
    void setUp() {
        databaseCleanup.clean();

        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("testuser@example.com");
        testUser.setPassword("password");
        testUser.setRole(Role.USER);
        testUser = userRepository.save(testUser);

        secondUser = new User();
        secondUser.setUsername("seconduser");
        secondUser.setEmail("seconduser@example.com");
        secondUser.setPassword("password");
        secondUser.setRole(Role.USER);
        secondUser = userRepository.save(secondUser);

        adminUser = new User();
        adminUser.setUsername("admin");
        adminUser.setEmail("admin@example.com");
        adminUser.setPassword("password");
        adminUser.setRole(Role.ADMIN);
        adminUser = userRepository.save(adminUser);

        category = new Category();
        category.setName("Electronics");
        category.setDescription("Electronic products");
        category = categoryRepository.save(category);

        product = new Product();
        product.setName("Laptop");
        product.setDescription("Test laptop");
        product.setPrice(new BigDecimal("50000.00"));
        product.setStock(10);
        product.setCategory(category);
        product = productRepository.save(product);

        address = createAddress(testUser);
    }

    private Address createAddress(User user) {

        Address address = new Address();

        address.setUser(user);
        address.setFullName("Test User");
        address.setPhone("9876543210");
        address.setAddressLine("123 Main Street");
        address.setCity("Gwalior");
        address.setState("Madhya Pradesh");
        address.setPostalCode("474001");
        address.setCountry("India");
        address.setType(AddressType.HOME);

        return addressRepository.save(address);
    }

    private Order createOrder(
            User user,
            Product product,
            int quantity,
            OrderStatus status) {

        Order order = new Order();

        order.setUser(user);
        order.setStatus(status);
        order.setTotalAmount(
                product.getPrice()
                        .multiply(BigDecimal.valueOf(quantity))
        );

        order.setShippingFullName("Test User");
        order.setShippingPhone("9876543210");
        order.setShippingAddressLine("123 Main Street");
        order.setShippingCity("Gwalior");
        order.setShippingState("Madhya Pradesh");
        order.setShippingPostalCode("474001");
        order.setShippingCountry("India");

        OrderItem item = new OrderItem();

        item.setOrder(order);
        item.setProduct(product);
        item.setQuantity(quantity);
        item.setPrice(product.getPrice());
        item.setSubtotal(
                product.getPrice()
                        .multiply(BigDecimal.valueOf(quantity))
        );

        order.setItems(new ArrayList<>());
        order.getItems().add(item);

        return orderRepository.save(order);
    }

    @Test
    void createOrderReturnsCreatedOrder() throws Exception {

        mockMvc.perform(
                        post("/api/orders")
                                .with(user("testuser").roles("USER"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "addressId": %d,
                                            "items": [
                                                {
                                                    "productId": %d,
                                                    "quantity": 2
                                                }
                                            ]
                                        }
                                        """.formatted(
                                        address.getId(),
                                        product.getId()
                                ))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.userId",
                        is(testUser.getId().intValue())))
                .andExpect(jsonPath("$.status",
                        is("PENDING")))
                .andExpect(jsonPath("$.totalAmount",
                        is(100000.0)))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].productId",
                        is(product.getId().intValue())))
                .andExpect(jsonPath("$.items[0].productName",
                        is("Laptop")))
                .andExpect(jsonPath("$.items[0].quantity",
                        is(2)))
                .andExpect(jsonPath("$.shippingFullName",
                        is("Test User")))
                .andExpect(jsonPath("$.shippingCity",
                        is("Gwalior")));

        Product updatedProduct =
                productRepository.findById(product.getId()).orElseThrow();

        assertEquals(8, updatedProduct.getStock());
    }

    @Test
    void createOrderWithUnknownAddressReturnsNotFound() throws Exception {

        mockMvc.perform(
                        post("/api/orders")
                                .with(user("testuser").roles("USER"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "addressId": 999999,
                                            "items": [
                                                {
                                                    "productId": %d,
                                                    "quantity": 1
                                                }
                                            ]
                                        }
                                        """.formatted(product.getId()))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void userCannotCreateOrderUsingAnotherUsersAddress() throws Exception {

        Address secondUserAddress = createAddress(secondUser);

        mockMvc.perform(
                        post("/api/orders")
                                .with(user("testuser").roles("USER"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "addressId": %d,
                                            "items": [
                                                {
                                                    "productId": %d,
                                                    "quantity": 1
                                                }
                                            ]
                                        }
                                        """.formatted(
                                        secondUserAddress.getId(),
                                        product.getId()
                                ))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void createOrderWithUnknownProductReturnsNotFound() throws Exception {

        mockMvc.perform(
                        post("/api/orders")
                                .with(user("testuser").roles("USER"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "addressId": %d,
                                            "items": [
                                                {
                                                    "productId": 999999,
                                                    "quantity": 1
                                                }
                                            ]
                                        }
                                        """.formatted(address.getId()))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void createOrderWithInsufficientStockReturnsConflict() throws Exception {

        mockMvc.perform(
                        post("/api/orders")
                                .with(user("testuser").roles("USER"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "addressId": %d,
                                            "items": [
                                                {
                                                    "productId": %d,
                                                    "quantity": 11
                                                }
                                            ]
                                        }
                                        """.formatted(
                                        address.getId(),
                                        product.getId()
                                ))
                )
                .andExpect(status().isConflict());
    }

    @Test
    void createOrderWithZeroQuantityReturnsNotFound() throws Exception {

        mockMvc.perform(
                        post("/api/orders")
                                .with(user("testuser").roles("USER"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                        "addressId": %d,
                                        "items": [
                                            {
                                                "productId": %d,
                                                "quantity": 0
                                            }
                                        ]
                                    }
                                    """.formatted(
                                        address.getId(),
                                        product.getId()
                                ))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void createOrderWithMissingAddressReturnsBadRequest() throws Exception {

        mockMvc.perform(
                        post("/api/orders")
                                .with(user("testuser").roles("USER"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "items": [
                                                {
                                                    "productId": %d,
                                                    "quantity": 1
                                                }
                                            ]
                                        }
                                        """.formatted(product.getId()))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void createOrderWithEmptyItemsReturnsBadRequest() throws Exception {

        mockMvc.perform(
                        post("/api/orders")
                                .with(user("testuser").roles("USER"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "addressId": %d,
                                            "items": []
                                        }
                                        """.formatted(address.getId()))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void getMyOrdersReturnsOnlyAuthenticatedUsersOrders()
            throws Exception {

        createOrder(
                testUser,
                product,
                2,
                OrderStatus.PENDING
        );

        createOrder(
                secondUser,
                product,
                1,
                OrderStatus.CONFIRMED
        );

        mockMvc.perform(
                        get("/api/orders")
                                .with(user("testuser").roles("USER"))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].userId",
                        is(testUser.getId().intValue())))
                .andExpect(jsonPath("$[0].status",
                        is("PENDING")))
                .andExpect(jsonPath("$[0].items", hasSize(1)));
    }

    @Test
    void getMyOrderReturnsOrder() throws Exception {

        Order order = createOrder(
                testUser,
                product,
                2,
                OrderStatus.PENDING
        );

        mockMvc.perform(
                        get("/api/orders/{orderId}", order.getId())
                                .with(user("testuser").roles("USER"))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id",
                        is(order.getId().intValue())))
                .andExpect(jsonPath("$.userId",
                        is(testUser.getId().intValue())))
                .andExpect(jsonPath("$.status",
                        is("PENDING")))
                .andExpect(jsonPath("$.totalAmount",
                        is(100000.0)));
    }

    @Test
    void getMyOrderWhenNotFoundReturnsNotFound() throws Exception {

        mockMvc.perform(
                        get("/api/orders/{orderId}", 999999L)
                                .with(user("testuser").roles("USER"))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void userCannotAccessAnotherUsersOrder() throws Exception {

        Order order = createOrder(
                secondUser,
                product,
                1,
                OrderStatus.PENDING
        );

        mockMvc.perform(
                        get("/api/orders/{orderId}", order.getId())
                                .with(user("testuser").roles("USER"))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void cancelPendingOrderReturnsCancelledOrder()
            throws Exception {

        Order order = createOrder(
                testUser,
                product,
                2,
                OrderStatus.PENDING
        );

        int stockBefore =
                productRepository.findById(product.getId())
                        .orElseThrow()
                        .getStock();

        mockMvc.perform(
                        patch("/api/orders/{orderId}/cancel",
                                order.getId())
                                .with(user("testuser").roles("USER"))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id",
                        is(order.getId().intValue())))
                .andExpect(jsonPath("$.status",
                        is("CANCELLED")));

        int stockAfter =
                productRepository.findById(product.getId())
                        .orElseThrow()
                        .getStock();

        assertEquals(stockBefore + 2, stockAfter);
    }

    @Test
    void cancelOrderWhenNotFoundReturnsNotFound()
            throws Exception {

        mockMvc.perform(
                        patch("/api/orders/{orderId}/cancel", 999999L)
                                .with(user("testuser").roles("USER"))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void userCannotCancelAnotherUsersOrder()
            throws Exception {

        Order order = createOrder(
                secondUser,
                product,
                1,
                OrderStatus.PENDING
        );

        mockMvc.perform(
                        patch("/api/orders/{orderId}/cancel",
                                order.getId())
                                .with(user("testuser").roles("USER"))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void cannotCancelConfirmedOrder() throws Exception {

        Order order = createOrder(
                testUser,
                product,
                1,
                OrderStatus.CONFIRMED
        );

        mockMvc.perform(
                        patch("/api/orders/{orderId}/cancel",
                                order.getId())
                                .with(user("testuser").roles("USER"))
                )
                .andExpect(status().isConflict());
    }

    @Test
    void getMyOrderStatusReturnsStatus() throws Exception {

        Order order = createOrder(
                testUser,
                product,
                1,
                OrderStatus.PENDING
        );

        mockMvc.perform(
                        get("/api/orders/{orderId}/status",
                                order.getId())
                                .with(user("testuser").roles("USER"))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId",
                        is(order.getId().intValue())))
                .andExpect(jsonPath("$.status",
                        is("PENDING")));
    }

    @Test
    void getMyOrderStatusWhenNotFoundReturnsNotFound()
            throws Exception {

        mockMvc.perform(
                        get("/api/orders/{orderId}/status", 999999L)
                                .with(user("testuser").roles("USER"))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void userCannotAccessAnotherUsersOrderStatus()
            throws Exception {

        Order order = createOrder(
                secondUser,
                product,
                1,
                OrderStatus.PENDING
        );

        mockMvc.perform(
                        get("/api/orders/{orderId}/status",
                                order.getId())
                                .with(user("testuser").roles("USER"))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void adminCanConfirmPendingOrder() throws Exception {

        Order order = createOrder(
                testUser,
                product,
                1,
                OrderStatus.PENDING
        );

        mockMvc.perform(
                        patch("/api/orders/{orderId}/status",
                                order.getId())
                                .with(user("admin").roles("ADMIN"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "status": "CONFIRMED"
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status",
                        is("CONFIRMED")));
    }

    @Test
    void normalUserCannotUpdateOrderStatus() throws Exception {

        Order order = createOrder(
                testUser,
                product,
                1,
                OrderStatus.PENDING
        );

        mockMvc.perform(
                        patch("/api/orders/{orderId}/status",
                                order.getId())
                                .with(user("testuser").roles("USER"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "status": "CONFIRMED"
                                        }
                                        """)
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void unauthenticatedUserCannotAccessOrders()
            throws Exception {

        mockMvc.perform(
                        get("/api/orders")
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void adminCannotMakeInvalidStatusTransition()
            throws Exception {

        Order order = createOrder(
                testUser,
                product,
                1,
                OrderStatus.PENDING
        );

        mockMvc.perform(
                        patch("/api/orders/{orderId}/status",
                                order.getId())
                                .with(user("admin").roles("ADMIN"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "status": "DELIVERED"
                                        }
                                        """)
                )
                .andExpect(status().isConflict());
    }
}
