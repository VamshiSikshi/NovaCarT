package com.shivamsikarwar.novacart.controller;

import com.shivamsikarwar.novacart.entity.Address;
import com.shivamsikarwar.novacart.entity.AddressType;
import com.shivamsikarwar.novacart.entity.Category;
import com.shivamsikarwar.novacart.entity.Order;
import com.shivamsikarwar.novacart.entity.OrderItem;
import com.shivamsikarwar.novacart.entity.OrderStatus;
import com.shivamsikarwar.novacart.entity.Payment;
import com.shivamsikarwar.novacart.entity.PaymentMethod;
import com.shivamsikarwar.novacart.entity.PaymentStatus;
import com.shivamsikarwar.novacart.entity.Product;
import com.shivamsikarwar.novacart.entity.Role;
import com.shivamsikarwar.novacart.entity.User;
import com.shivamsikarwar.novacart.repository.AddressRepository;
import com.shivamsikarwar.novacart.repository.CategoryRepository;
import com.shivamsikarwar.novacart.repository.OrderItemRepository;
import com.shivamsikarwar.novacart.repository.OrderRepository;
import com.shivamsikarwar.novacart.repository.PaymentRepository;
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
class PaymentControllerIntegrationTest {

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

    @Autowired
    private PaymentRepository paymentRepository;

    private User testUser;
    private User secondUser;

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
            OrderStatus status) {

        Order order = new Order();

        order.setUser(user);
        order.setStatus(status);
        order.setTotalAmount(new BigDecimal("100000.00"));

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
        item.setQuantity(2);
        item.setPrice(product.getPrice());
        item.setSubtotal(new BigDecimal("100000.00"));

        order.setItems(new ArrayList<>());
        order.getItems().add(item);

        return orderRepository.save(order);
    }

    private Payment createPayment(
            Order order,
            PaymentStatus status) {

        Payment payment = new Payment();

        payment.setOrder(order);
        payment.setAmount(order.getTotalAmount());
        payment.setMethod(PaymentMethod.CARD);
        payment.setStatus(status);

        if (status == PaymentStatus.SUCCESS) {
            payment.setTransactionId("TXN-TEST123456");
        }

        return paymentRepository.save(payment);
    }

    @Test
    void createPaymentReturnsCreatedPayment() throws Exception {

        Order order = createOrder(
                testUser,
                OrderStatus.PENDING
        );

        mockMvc.perform(
                        post(
                                "/api/orders/{orderId}/payment",
                                order.getId()
                        )
                                .with(user("testuser").roles("USER"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "method": "CARD"
                                        }
                                        """)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.orderId",
                        is(order.getId().intValue())))
                .andExpect(jsonPath("$.amount",
                        is(100000.0)))
                .andExpect(jsonPath("$.method",
                        is("CARD")))
                .andExpect(jsonPath("$.status",
                        is("PENDING")));

        assertEquals(
                1,
                paymentRepository.count()
        );
    }

    @Test
    void createPaymentWithMissingMethodReturnsBadRequest()
            throws Exception {

        Order order = createOrder(
                testUser,
                OrderStatus.PENDING
        );

        mockMvc.perform(
                        post(
                                "/api/orders/{orderId}/payment",
                                order.getId()
                        )
                                .with(user("testuser").roles("USER"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {}
                                        """)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void createPaymentWhenOrderNotFoundReturnsNotFound()
            throws Exception {

        mockMvc.perform(
                        post(
                                "/api/orders/{orderId}/payment",
                                999999L
                        )
                                .with(user("testuser").roles("USER"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "method": "CARD"
                                        }
                                        """)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void userCannotCreatePaymentForAnotherUsersOrder()
            throws Exception {

        Order order = createOrder(
                secondUser,
                OrderStatus.PENDING
        );

        mockMvc.perform(
                        post(
                                "/api/orders/{orderId}/payment",
                                order.getId()
                        )
                                .with(user("testuser").roles("USER"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "method": "CARD"
                                        }
                                        """)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void cannotCreatePaymentForConfirmedOrder()
            throws Exception {

        Order order = createOrder(
                testUser,
                OrderStatus.CONFIRMED
        );

        mockMvc.perform(
                        post(
                                "/api/orders/{orderId}/payment",
                                order.getId()
                        )
                                .with(user("testuser").roles("USER"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "method": "CARD"
                                        }
                                        """)
                )
                .andExpect(status().isConflict());
    }

    @Test
    void cannotCreateDuplicatePayment() throws Exception {

        Order order = createOrder(
                testUser,
                OrderStatus.PENDING
        );

        createPayment(
                order,
                PaymentStatus.PENDING
        );

        mockMvc.perform(
                        post(
                                "/api/orders/{orderId}/payment",
                                order.getId()
                        )
                                .with(user("testuser").roles("USER"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "method": "UPI"
                                        }
                                        """)
                )
                .andExpect(status().isConflict());
    }

    @Test
    void getPaymentReturnsPayment() throws Exception {

        Order order = createOrder(
                testUser,
                OrderStatus.PENDING
        );

        Payment payment = createPayment(
                order,
                PaymentStatus.PENDING
        );

        mockMvc.perform(
                        get(
                                "/api/orders/{orderId}/payment",
                                order.getId()
                        )
                                .with(user("testuser").roles("USER"))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id",
                        is(payment.getId().intValue())))
                .andExpect(jsonPath("$.orderId",
                        is(order.getId().intValue())))
                .andExpect(jsonPath("$.amount",
                        is(100000.0)))
                .andExpect(jsonPath("$.method",
                        is("CARD")))
                .andExpect(jsonPath("$.status",
                        is("PENDING")));
    }

    @Test
    void getPaymentWhenPaymentDoesNotExistReturnsNotFound()
            throws Exception {

        Order order = createOrder(
                testUser,
                OrderStatus.PENDING
        );

        mockMvc.perform(
                        get(
                                "/api/orders/{orderId}/payment",
                                order.getId()
                        )
                                .with(user("testuser").roles("USER"))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void getPaymentWhenOrderDoesNotExistReturnsNotFound()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/api/orders/{orderId}/payment",
                                999999L
                        )
                                .with(user("testuser").roles("USER"))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void userCannotGetAnotherUsersPayment()
            throws Exception {

        Order order = createOrder(
                secondUser,
                OrderStatus.PENDING
        );

        createPayment(
                order,
                PaymentStatus.PENDING
        );

        mockMvc.perform(
                        get(
                                "/api/orders/{orderId}/payment",
                                order.getId()
                        )
                                .with(user("testuser").roles("USER"))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void processPaymentWithSuccessReturnsSuccess()
            throws Exception {

        Order order = createOrder(
                testUser,
                OrderStatus.PENDING
        );

        Payment payment = createPayment(
                order,
                PaymentStatus.PENDING
        );

        mockMvc.perform(
                        patch(
                                "/api/orders/{orderId}/payment",
                                order.getId()
                        )
                                .with(user("testuser").roles("USER"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "status": "SUCCESS"
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id",
                        is(payment.getId().intValue())))
                .andExpect(jsonPath("$.status",
                        is("SUCCESS")))
                .andExpect(jsonPath("$.transactionId")
                        .isNotEmpty());

        Order updatedOrder =
                orderRepository
                        .findById(order.getId())
                        .orElseThrow();

        assertEquals(
                OrderStatus.CONFIRMED,
                updatedOrder.getStatus()
        );
    }

    @Test
    void processPaymentWithFailedStatusReturnsFailedPayment()
            throws Exception {

        Order order = createOrder(
                testUser,
                OrderStatus.PENDING
        );

        Payment payment = createPayment(
                order,
                PaymentStatus.PENDING
        );

        mockMvc.perform(
                        patch(
                                "/api/orders/{orderId}/payment",
                                order.getId()
                        )
                                .with(user("testuser").roles("USER"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "status": "FAILED"
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id",
                        is(payment.getId().intValue())))
                .andExpect(jsonPath("$.status",
                        is("FAILED")))
                .andExpect(jsonPath("$.transactionId")
                        .doesNotExist());

        Order updatedOrder =
                orderRepository
                        .findById(order.getId())
                        .orElseThrow();

        assertEquals(
                OrderStatus.PENDING,
                updatedOrder.getStatus()
        );
    }

    @Test
    void processPaymentWithMissingStatusReturnsBadRequest()
            throws Exception {

        Order order = createOrder(
                testUser,
                OrderStatus.PENDING
        );

        createPayment(
                order,
                PaymentStatus.PENDING
        );

        mockMvc.perform(
                        patch(
                                "/api/orders/{orderId}/payment",
                                order.getId()
                        )
                                .with(user("testuser").roles("USER"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {}
                                        """)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void processPaymentWhenPaymentDoesNotExistReturnsNotFound()
            throws Exception {

        Order order = createOrder(
                testUser,
                OrderStatus.PENDING
        );

        mockMvc.perform(
                        patch(
                                "/api/orders/{orderId}/payment",
                                order.getId()
                        )
                                .with(user("testuser").roles("USER"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "status": "SUCCESS"
                                        }
                                        """)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void processPaymentWhenOrderDoesNotExistReturnsNotFound()
            throws Exception {

        mockMvc.perform(
                        patch(
                                "/api/orders/{orderId}/payment",
                                999999L
                        )
                                .with(user("testuser").roles("USER"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "status": "SUCCESS"
                                        }
                                        """)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void userCannotProcessAnotherUsersPayment()
            throws Exception {

        Order order = createOrder(
                secondUser,
                OrderStatus.PENDING
        );

        createPayment(
                order,
                PaymentStatus.PENDING
        );

        mockMvc.perform(
                        patch(
                                "/api/orders/{orderId}/payment",
                                order.getId()
                        )
                                .with(user("testuser").roles("USER"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "status": "SUCCESS"
                                        }
                                        """)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void cannotProcessSuccessfulPaymentAgain()
            throws Exception {

        Order order = createOrder(
                testUser,
                OrderStatus.CONFIRMED
        );

        createPayment(
                order,
                PaymentStatus.SUCCESS
        );

        mockMvc.perform(
                        patch(
                                "/api/orders/{orderId}/payment",
                                order.getId()
                        )
                                .with(user("testuser").roles("USER"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "status": "FAILED"
                                        }
                                        """)
                )
                .andExpect(status().isConflict());
    }

    @Test
    void cannotProcessFailedPaymentAgain()
            throws Exception {

        Order order = createOrder(
                testUser,
                OrderStatus.PENDING
        );

        createPayment(
                order,
                PaymentStatus.FAILED
        );

        mockMvc.perform(
                        patch(
                                "/api/orders/{orderId}/payment",
                                order.getId()
                        )
                                .with(user("testuser").roles("USER"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "status": "SUCCESS"
                                        }
                                        """)
                )
                .andExpect(status().isConflict());
    }

    @Test
    void unauthenticatedUserCannotAccessPayment()
            throws Exception {

        Order order = createOrder(
                testUser,
                OrderStatus.PENDING
        );

        mockMvc.perform(
                        get(
                                "/api/orders/{orderId}/payment",
                                order.getId()
                        )
                )
                .andExpect(status().isUnauthorized());
    }
}
