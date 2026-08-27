package com.shivamsikarwar.novacart.repository;

import com.shivamsikarwar.novacart.entity.Cart;
import com.shivamsikarwar.novacart.entity.CartItem;
import com.shivamsikarwar.novacart.entity.Category;
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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CartItemRepositoryIntegrationTest {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DatabaseCleanup databaseCleanup;

    private User testUser;
    private Cart testCart;
    private Category testCategory;

    @BeforeEach
    void setUp() {

        databaseCleanup.clean();

        String uniqueId = String.valueOf(System.nanoTime());

        testUser = createUser(
                "testuser_" + uniqueId,
                "testuser_" + uniqueId + "@example.com"
        );

        testCart = createCart(testUser);

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

    private Cart createCart(User user) {

        Cart cart = new Cart();

        cart.setUser(user);

        return cartRepository.save(cart);
    }

    private Category createCategory(String name) {

        Category category = new Category();

        category.setName(name);
        category.setDescription(name + " products");

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

    private CartItem createCartItem(
            Cart cart,
            Product product,
            Integer quantity) {

        CartItem cartItem = new CartItem();

        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setQuantity(quantity);

        return cartItemRepository.save(cartItem);
    }

    @Test
    void saveCartItemPersistsCartItem() {

        Product product =
                createProduct(
                        "Laptop",
                        new BigDecimal("80000"),
                        10
                );

        CartItem savedItem =
                createCartItem(
                        testCart,
                        product,
                        2
                );

        assertThat(savedItem.getId())
                .isNotNull();

        assertThat(savedItem.getCart().getId())
                .isEqualTo(testCart.getId());

        assertThat(savedItem.getProduct().getId())
                .isEqualTo(product.getId());

        assertThat(savedItem.getQuantity())
                .isEqualTo(2);
    }

    @Test
    void findByCartIdAndProductIdReturnsMatchingCartItem() {

        Product product =
                createProduct(
                        "Laptop",
                        new BigDecimal("80000"),
                        10
                );

        CartItem savedItem =
                createCartItem(
                        testCart,
                        product,
                        3
                );

        Optional<CartItem> result =
                cartItemRepository.findByCartIdAndProductId(
                        testCart.getId(),
                        product.getId()
                );

        assertThat(result)
                .isPresent();

        assertThat(result.get().getId())
                .isEqualTo(savedItem.getId());

        assertThat(result.get().getQuantity())
                .isEqualTo(3);

        assertThat(result.get().getCart().getId())
                .isEqualTo(testCart.getId());

        assertThat(result.get().getProduct().getId())
                .isEqualTo(product.getId());
    }

    @Test
    void findByCartIdAndProductIdReturnsEmptyWhenItemDoesNotExist() {

        Product product =
                createProduct(
                        "Laptop",
                        new BigDecimal("80000"),
                        10
                );

        Optional<CartItem> result =
                cartItemRepository.findByCartIdAndProductId(
                        testCart.getId(),
                        product.getId()
                );

        assertThat(result)
                .isEmpty();
    }

    @Test
    void findByCartIdAndProductIdDoesNotReturnItemFromAnotherCart() {

        Product product =
                createProduct(
                        "Laptop",
                        new BigDecimal("80000"),
                        10
                );

        createCartItem(
                testCart,
                product,
                2
        );

        User anotherUser =
                createUser(
                        "anotheruser",
                        "another@example.com"
                );

        Cart anotherCart =
                createCart(anotherUser);

        Optional<CartItem> result =
                cartItemRepository.findByCartIdAndProductId(
                        anotherCart.getId(),
                        product.getId()
                );

        assertThat(result)
                .isEmpty();
    }

    @Test
    void findByCartIdAndProductIdDoesNotReturnItemForAnotherProduct() {

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

        createCartItem(
                testCart,
                laptop,
                2
        );

        Optional<CartItem> result =
                cartItemRepository.findByCartIdAndProductId(
                        testCart.getId(),
                        phone.getId()
                );

        assertThat(result)
                .isEmpty();
    }

    @Test
    @Transactional
    void deleteAllByCartIdDeletesAllItemsFromCart() {

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

        createCartItem(
                testCart,
                laptop,
                2
        );

        createCartItem(
                testCart,
                phone,
                3
        );

        assertThat(
                cartItemRepository.findAll()
        ).hasSize(2);

        cartItemRepository.deleteAllByCartId(
                testCart.getId()
        );

        assertThat(
                cartItemRepository.findAll()
        ).isEmpty();
    }

    @Test
    @Transactional
    void deleteAllByCartIdDoesNotDeleteItemsFromAnotherCart() {

        Product product =
                createProduct(
                        "Laptop",
                        new BigDecimal("80000"),
                        10
                );

        createCartItem(
                testCart,
                product,
                2
        );

        User anotherUser =
                createUser(
                        "anotheruser",
                        "another@example.com"
                );

        Cart anotherCart =
                createCart(anotherUser);

        Product anotherProduct =
                createProduct(
                        "Phone",
                        new BigDecimal("40000"),
                        20
                );

        createCartItem(
                anotherCart,
                anotherProduct,
                1
        );

        cartItemRepository.deleteAllByCartId(
                testCart.getId()
        );

        List<CartItem> remainingItems =
                cartItemRepository.findAll();

        assertThat(remainingItems)
                .hasSize(1);

        assertThat(
                remainingItems.get(0).getCart().getId()
        ).isEqualTo(anotherCart.getId());

        assertThat(
                remainingItems.get(0).getProduct().getId()
        ).isEqualTo(anotherProduct.getId());
    }

    @Test
    void deleteCartItemRemovesItem() {

        Product product =
                createProduct(
                        "Laptop",
                        new BigDecimal("80000"),
                        10
                );

        CartItem savedItem =
                createCartItem(
                        testCart,
                        product,
                        2
                );

        cartItemRepository.delete(savedItem);

        assertThat(
                cartItemRepository.findById(
                        savedItem.getId()
                )
        ).isEmpty();
    }
}