package com.shivamsikarwar.novacart.repository;

import com.shivamsikarwar.novacart.entity.Cart;
import com.shivamsikarwar.novacart.entity.Role;
import com.shivamsikarwar.novacart.entity.User;
import com.shivamsikarwar.novacart.support.DatabaseCleanup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class CartRepositoryIntegrationTest {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DatabaseCleanup databaseCleanup;

    private User testUser;

    @BeforeEach
    void setUp() {
        databaseCleanup.clean();

        testUser = createUser(
                "testuser",
                "testuser@example.com"
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

    @Test
    void saveCartPersistsCart() {

        Cart savedCart =
                createCart(testUser);

        assertThat(savedCart.getId())
                .isNotNull();

        assertThat(savedCart.getUser())
                .isNotNull();

        assertThat(savedCart.getUser().getId())
                .isEqualTo(testUser.getId());
    }

    @Test
    void findByUserReturnsCart() {

        Cart savedCart =
                createCart(testUser);

        Optional<Cart> result =
                cartRepository.findByUser(testUser);

        assertThat(result)
                .isPresent();

        assertThat(result.get().getId())
                .isEqualTo(savedCart.getId());

        assertThat(result.get().getUser().getId())
                .isEqualTo(testUser.getId());
    }

    @Test
    void findByUserReturnsEmptyWhenUserHasNoCart() {

        Optional<Cart> result =
                cartRepository.findByUser(testUser);

        assertThat(result)
                .isEmpty();
    }

    @Test
    void findByUserIdReturnsCart() {

        Cart savedCart =
                createCart(testUser);

        Optional<Cart> result =
                cartRepository.findByUserId(
                        testUser.getId()
                );

        assertThat(result)
                .isPresent();

        assertThat(result.get().getId())
                .isEqualTo(savedCart.getId());

        assertThat(result.get().getUser().getId())
                .isEqualTo(testUser.getId());
    }

    @Test
    void findByUserIdReturnsEmptyWhenUserHasNoCart() {

        Optional<Cart> result =
                cartRepository.findByUserId(
                        testUser.getId()
                );

        assertThat(result)
                .isEmpty();
    }

    @Test
    void findByUserDoesNotReturnAnotherUsersCart() {

        User anotherUser =
                createUser(
                        "anotheruser",
                        "another@example.com"
                );

        createCart(anotherUser);

        Optional<Cart> result =
                cartRepository.findByUser(testUser);

        assertThat(result)
                .isEmpty();
    }

    @Test
    void findByUserIdDoesNotReturnAnotherUsersCart() {

        User anotherUser =
                createUser(
                        "anotheruser",
                        "another@example.com"
                );

        createCart(anotherUser);

        Optional<Cart> result =
                cartRepository.findByUserId(
                        testUser.getId()
                );

        assertThat(result)
                .isEmpty();
    }

    @Test
    void deleteCartRemovesCart() {

        Cart savedCart =
                createCart(testUser);

        cartRepository.delete(savedCart);

        assertThat(
                cartRepository.findById(
                        savedCart.getId()
                )
        ).isEmpty();
    }
}