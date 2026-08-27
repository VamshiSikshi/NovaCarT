package com.shivamsikarwar.novacart.repository;

import com.shivamsikarwar.novacart.entity.Category;
import com.shivamsikarwar.novacart.entity.Order;
import com.shivamsikarwar.novacart.entity.OrderStatus;
import com.shivamsikarwar.novacart.entity.Payment;
import com.shivamsikarwar.novacart.entity.PaymentMethod;
import com.shivamsikarwar.novacart.entity.PaymentStatus;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class PaymentRepositoryIntegrationTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DatabaseCleanup databaseCleanup;

    @BeforeEach
    void setUp() {
        databaseCleanup.clean();
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

    private Order createOrder(
            User user,
            OrderStatus status,
            BigDecimal totalAmount) {

        Order order = new Order();

        order.setUser(user);
        order.setStatus(status);
        order.setTotalAmount(totalAmount);

        order.setShippingFullName("Test User");
        order.setShippingPhone("9876543210");
        order.setShippingAddressLine("123 Main Street");
        order.setShippingCity("Gwalior");
        order.setShippingState("Madhya Pradesh");
        order.setShippingPostalCode("474001");
        order.setShippingCountry("India");

        return orderRepository.save(order);
    }

    private Payment createPayment(
            Order order,
            BigDecimal amount,
            PaymentMethod method,
            PaymentStatus status,
            String transactionId) {

        Payment payment = new Payment();

        payment.setOrder(order);
        payment.setAmount(amount);
        payment.setMethod(method);
        payment.setStatus(status);
        payment.setTransactionId(transactionId);

        return paymentRepository.save(payment);
    }

    @Test
    void findByOrderIdReturnsPayment() {

        User user =
                createUser(
                        "user1",
                        "user1@example.com"
                );

        Order order =
                createOrder(
                        user,
                        OrderStatus.CONFIRMED,
                        new BigDecimal("1500")
                );

        Payment payment =
                createPayment(
                        order,
                        new BigDecimal("1500"),
                        PaymentMethod.UPI,
                        PaymentStatus.SUCCESS,
                        "TXN-001"
                );

        Optional<Payment> result =
                paymentRepository.findByOrder_Id(
                        order.getId()
                );

        assertThat(result)
                .isPresent();

        assertThat(result.get().getId())
                .isEqualTo(payment.getId());

        assertThat(result.get().getOrder().getId())
                .isEqualTo(order.getId());

        assertThat(result.get().getAmount())
                .isEqualByComparingTo("1500");

        assertThat(result.get().getMethod())
                .isEqualTo(PaymentMethod.UPI);

        assertThat(result.get().getStatus())
                .isEqualTo(PaymentStatus.SUCCESS);
    }

    @Test
    void findByOrderIdReturnsEmptyWhenPaymentDoesNotExist() {

        User user =
                createUser(
                        "user1",
                        "user1@example.com"
                );

        Order order =
                createOrder(
                        user,
                        OrderStatus.PENDING,
                        new BigDecimal("1000")
                );

        Optional<Payment> result =
                paymentRepository.findByOrder_Id(
                        order.getId()
                );

        assertThat(result)
                .isEmpty();
    }

    @Test
    void existsByOrderIdReturnsTrueWhenPaymentExists() {

        User user =
                createUser(
                        "user1",
                        "user1@example.com"
                );

        Order order =
                createOrder(
                        user,
                        OrderStatus.CONFIRMED,
                        new BigDecimal("1000")
                );

        createPayment(
                order,
                new BigDecimal("1000"),
                PaymentMethod.CARD,
                PaymentStatus.PENDING,
                "TXN-001"
        );

        assertThat(
                paymentRepository.existsByOrder_Id(
                        order.getId()
                )
        ).isTrue();
    }

    @Test
    void existsByOrderIdReturnsFalseWhenPaymentDoesNotExist() {

        User user =
                createUser(
                        "user1",
                        "user1@example.com"
                );

        Order order =
                createOrder(
                        user,
                        OrderStatus.PENDING,
                        new BigDecimal("1000")
                );

        assertThat(
                paymentRepository.existsByOrder_Id(
                        order.getId()
                )
        ).isFalse();
    }

    @Test
    void findByTransactionIdReturnsPayment() {

        User user =
                createUser(
                        "user1",
                        "user1@example.com"
                );

        Order order =
                createOrder(
                        user,
                        OrderStatus.CONFIRMED,
                        new BigDecimal("2000")
                );

        Payment payment =
                createPayment(
                        order,
                        new BigDecimal("2000"),
                        PaymentMethod.CARD,
                        PaymentStatus.SUCCESS,
                        "TXN-ABC-123"
                );

        Optional<Payment> result =
                paymentRepository.findByTransactionId(
                        "TXN-ABC-123"
                );

        assertThat(result)
                .isPresent();

        assertThat(result.get().getId())
                .isEqualTo(payment.getId());

        assertThat(result.get().getTransactionId())
                .isEqualTo("TXN-ABC-123");
    }

    @Test
    void findByTransactionIdReturnsEmptyWhenTransactionDoesNotExist() {

        Optional<Payment> result =
                paymentRepository.findByTransactionId(
                        "UNKNOWN-TXN"
                );

        assertThat(result)
                .isEmpty();
    }

    @Test
    void countByStatusReturnsCorrectCount() {

        User user =
                createUser(
                        "user1",
                        "user1@example.com"
                );

        Order order1 =
                createOrder(
                        user,
                        OrderStatus.CONFIRMED,
                        new BigDecimal("1000")
                );

        Order order2 =
                createOrder(
                        user,
                        OrderStatus.CONFIRMED,
                        new BigDecimal("2000")
                );

        Order order3 =
                createOrder(
                        user,
                        OrderStatus.PENDING,
                        new BigDecimal("3000")
                );

        createPayment(
                order1,
                new BigDecimal("1000"),
                PaymentMethod.UPI,
                PaymentStatus.SUCCESS,
                "TXN-001"
        );

        createPayment(
                order2,
                new BigDecimal("2000"),
                PaymentMethod.CARD,
                PaymentStatus.SUCCESS,
                "TXN-002"
        );

        createPayment(
                order3,
                new BigDecimal("3000"),
                PaymentMethod.COD,
                PaymentStatus.PENDING,
                "TXN-003"
        );

        assertThat(
                paymentRepository.countByStatus(
                        PaymentStatus.SUCCESS
                )
        ).isEqualTo(2);

        assertThat(
                paymentRepository.countByStatus(
                        PaymentStatus.PENDING
                )
        ).isEqualTo(1);

        assertThat(
                paymentRepository.countByStatus(
                        PaymentStatus.FAILED
                )
        ).isZero();
    }

    @Test
    void getTotalAmountByStatusReturnsCorrectTotal() {

        User user =
                createUser(
                        "user1",
                        "user1@example.com"
                );

        Order order1 =
                createOrder(
                        user,
                        OrderStatus.CONFIRMED,
                        new BigDecimal("1000")
                );

        Order order2 =
                createOrder(
                        user,
                        OrderStatus.CONFIRMED,
                        new BigDecimal("2500")
                );

        Order order3 =
                createOrder(
                        user,
                        OrderStatus.CANCELLED,
                        new BigDecimal("500")
                );

        createPayment(
                order1,
                new BigDecimal("1000"),
                PaymentMethod.UPI,
                PaymentStatus.SUCCESS,
                "TXN-001"
        );

        createPayment(
                order2,
                new BigDecimal("2500"),
                PaymentMethod.CARD,
                PaymentStatus.SUCCESS,
                "TXN-002"
        );

        createPayment(
                order3,
                new BigDecimal("500"),
                PaymentMethod.COD,
                PaymentStatus.FAILED,
                "TXN-003"
        );

        assertThat(
                paymentRepository.getTotalAmountByStatus(
                        PaymentStatus.SUCCESS
                )
        ).isEqualByComparingTo("3500");

        assertThat(
                paymentRepository.getTotalAmountByStatus(
                        PaymentStatus.FAILED
                )
        ).isEqualByComparingTo("500");
    }

    @Test
    void getTotalAmountByStatusReturnsZeroWhenNoPaymentsMatch() {

        User user =
                createUser(
                        "user1",
                        "user1@example.com"
                );

        Order order =
                createOrder(
                        user,
                        OrderStatus.CONFIRMED,
                        new BigDecimal("1000")
                );

        createPayment(
                order,
                new BigDecimal("1000"),
                PaymentMethod.UPI,
                PaymentStatus.SUCCESS,
                "TXN-001"
        );

        assertThat(
                paymentRepository.getTotalAmountByStatus(
                        PaymentStatus.FAILED
                )
        ).isEqualByComparingTo("0");
    }

    @Test
    void findAdminPaymentsWithoutFiltersReturnsAllPayments() {

        User user =
                createUser(
                        "user1",
                        "user1@example.com"
                );

        Order order1 =
                createOrder(
                        user,
                        OrderStatus.CONFIRMED,
                        new BigDecimal("1000")
                );

        Order order2 =
                createOrder(
                        user,
                        OrderStatus.CONFIRMED,
                        new BigDecimal("2000")
                );

        createPayment(
                order1,
                new BigDecimal("1000"),
                PaymentMethod.UPI,
                PaymentStatus.SUCCESS,
                "TXN-001"
        );

        createPayment(
                order2,
                new BigDecimal("2000"),
                PaymentMethod.CARD,
                PaymentStatus.PENDING,
                "TXN-002"
        );

        Page<Payment> page =
                paymentRepository.findAdminPayments(
                        null,
                        null,
                        PageRequest.of(0, 10)
                );

        assertThat(page.getContent())
                .hasSize(2);

        assertThat(page.getTotalElements())
                .isEqualTo(2);
    }

    @Test
    void findAdminPaymentsFiltersByStatus() {

        User user =
                createUser(
                        "user1",
                        "user1@example.com"
                );

        Order successOrder =
                createOrder(
                        user,
                        OrderStatus.CONFIRMED,
                        new BigDecimal("1000")
                );

        Order failedOrder =
                createOrder(
                        user,
                        OrderStatus.CANCELLED,
                        new BigDecimal("2000")
                );

        createPayment(
                successOrder,
                new BigDecimal("1000"),
                PaymentMethod.UPI,
                PaymentStatus.SUCCESS,
                "TXN-001"
        );

        createPayment(
                failedOrder,
                new BigDecimal("2000"),
                PaymentMethod.CARD,
                PaymentStatus.FAILED,
                "TXN-002"
        );

        Page<Payment> page =
                paymentRepository.findAdminPayments(
                        PaymentStatus.SUCCESS,
                        null,
                        PageRequest.of(0, 10)
                );

        assertThat(page.getContent())
                .hasSize(1);

        assertThat(page.getContent().get(0).getStatus())
                .isEqualTo(PaymentStatus.SUCCESS);
    }

    @Test
    void findAdminPaymentsFiltersByMethod() {

        User user =
                createUser(
                        "user1",
                        "user1@example.com"
                );

        Order upiOrder =
                createOrder(
                        user,
                        OrderStatus.CONFIRMED,
                        new BigDecimal("1000")
                );

        Order cardOrder =
                createOrder(
                        user,
                        OrderStatus.CONFIRMED,
                        new BigDecimal("2000")
                );

        createPayment(
                upiOrder,
                new BigDecimal("1000"),
                PaymentMethod.UPI,
                PaymentStatus.SUCCESS,
                "TXN-001"
        );

        createPayment(
                cardOrder,
                new BigDecimal("2000"),
                PaymentMethod.CARD,
                PaymentStatus.SUCCESS,
                "TXN-002"
        );

        Page<Payment> page =
                paymentRepository.findAdminPayments(
                        null,
                        PaymentMethod.UPI,
                        PageRequest.of(0, 10)
                );

        assertThat(page.getContent())
                .hasSize(1);

        assertThat(page.getContent().get(0).getMethod())
                .isEqualTo(PaymentMethod.UPI);
    }

    @Test
    void findAdminPaymentsFiltersByStatusAndMethod() {

        User user =
                createUser(
                        "user1",
                        "user1@example.com"
                );

        Order matchingOrder =
                createOrder(
                        user,
                        OrderStatus.CONFIRMED,
                        new BigDecimal("1000")
                );

        Order wrongStatusOrder =
                createOrder(
                        user,
                        OrderStatus.CONFIRMED,
                        new BigDecimal("2000")
                );

        Order wrongMethodOrder =
                createOrder(
                        user,
                        OrderStatus.CONFIRMED,
                        new BigDecimal("3000")
                );

        createPayment(
                matchingOrder,
                new BigDecimal("1000"),
                PaymentMethod.UPI,
                PaymentStatus.SUCCESS,
                "TXN-001"
        );

        createPayment(
                wrongStatusOrder,
                new BigDecimal("2000"),
                PaymentMethod.UPI,
                PaymentStatus.FAILED,
                "TXN-002"
        );

        createPayment(
                wrongMethodOrder,
                new BigDecimal("3000"),
                PaymentMethod.CARD,
                PaymentStatus.SUCCESS,
                "TXN-003"
        );

        Page<Payment> page =
                paymentRepository.findAdminPayments(
                        PaymentStatus.SUCCESS,
                        PaymentMethod.UPI,
                        PageRequest.of(0, 10)
                );

        assertThat(page.getContent())
                .hasSize(1);

        Payment result =
                page.getContent().get(0);

        assertThat(result.getMethod())
                .isEqualTo(PaymentMethod.UPI);

        assertThat(result.getStatus())
                .isEqualTo(PaymentStatus.SUCCESS);

        assertThat(result.getAmount())
                .isEqualByComparingTo("1000");
    }

    @Test
    void findAdminPaymentsSupportsPagination() {

        User user =
                createUser(
                        "user1",
                        "user1@example.com"
                );

        Order order1 =
                createOrder(
                        user,
                        OrderStatus.CONFIRMED,
                        new BigDecimal("1000")
                );

        Order order2 =
                createOrder(
                        user,
                        OrderStatus.CONFIRMED,
                        new BigDecimal("2000")
                );

        Order order3 =
                createOrder(
                        user,
                        OrderStatus.CONFIRMED,
                        new BigDecimal("3000")
                );

        createPayment(
                order1,
                new BigDecimal("1000"),
                PaymentMethod.UPI,
                PaymentStatus.SUCCESS,
                "TXN-001"
        );

        createPayment(
                order2,
                new BigDecimal("2000"),
                PaymentMethod.CARD,
                PaymentStatus.SUCCESS,
                "TXN-002"
        );

        createPayment(
                order3,
                new BigDecimal("3000"),
                PaymentMethod.COD,
                PaymentStatus.PENDING,
                "TXN-003"
        );

        Page<Payment> page =
                paymentRepository.findAdminPayments(
                        null,
                        null,
                        PageRequest.of(0, 2)
                );

        assertThat(page.getContent())
                .hasSize(2);

        assertThat(page.getTotalElements())
                .isEqualTo(3);

        assertThat(page.getTotalPages())
                .isEqualTo(2);
    }
}