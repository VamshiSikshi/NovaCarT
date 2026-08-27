package com.shivamsikarwar.novacart.support;

import com.shivamsikarwar.novacart.repository.AddressRepository;
import com.shivamsikarwar.novacart.repository.CartItemRepository;
import com.shivamsikarwar.novacart.repository.CartRepository;
import com.shivamsikarwar.novacart.repository.CategoryRepository;
import com.shivamsikarwar.novacart.repository.OrderItemRepository;
import com.shivamsikarwar.novacart.repository.OrderRepository;
import com.shivamsikarwar.novacart.repository.PaymentRepository;
import com.shivamsikarwar.novacart.repository.ProductRepository;
import com.shivamsikarwar.novacart.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DatabaseCleanup {

    private final PaymentRepository paymentRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final AddressRepository addressRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public DatabaseCleanup(
            PaymentRepository paymentRepository,
            OrderItemRepository orderItemRepository,
            OrderRepository orderRepository,
            CartItemRepository cartItemRepository,
            CartRepository cartRepository,
            AddressRepository addressRepository,
            ProductRepository productRepository,
            CategoryRepository categoryRepository,
            UserRepository userRepository) {

        this.paymentRepository = paymentRepository;
        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;
        this.cartItemRepository = cartItemRepository;
        this.cartRepository = cartRepository;
        this.addressRepository = addressRepository;
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void clean() {

        paymentRepository.deleteAll();
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();

        cartItemRepository.deleteAll();
        cartRepository.deleteAll();

        addressRepository.deleteAll();

        productRepository.deleteAll();
        categoryRepository.deleteAll();

        userRepository.deleteAll();
    }
}