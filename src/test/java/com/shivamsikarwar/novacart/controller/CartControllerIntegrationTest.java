package com.shivamsikarwar.novacart.controller;

import com.shivamsikarwar.novacart.entity.Cart;
import com.shivamsikarwar.novacart.entity.CartItem;
import com.shivamsikarwar.novacart.entity.Category;
import com.shivamsikarwar.novacart.entity.Product;
import com.shivamsikarwar.novacart.entity.Role;
import com.shivamsikarwar.novacart.entity.User;
import com.shivamsikarwar.novacart.repository.CartItemRepository;
import com.shivamsikarwar.novacart.repository.CartRepository;
import com.shivamsikarwar.novacart.repository.CategoryRepository;
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

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class CartControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    private User testUser;
    private User secondUser;
    private Category category;
    private Product product;

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
    }

    private Cart createCart(User user) {

        Cart cart = new Cart();
        cart.setUser(user);

        return cartRepository.save(cart);
    }

    private CartItem createCartItem(
            Cart cart,
            Product product,
            int quantity) {

        CartItem item = new CartItem();

        item.setCart(cart);
        item.setProduct(product);
        item.setQuantity(quantity);

        cart.getItems().add(item);

        cartRepository.save(cart);

        return cartItemRepository
                .findByCartIdAndProductId(
                        cart.getId(),
                        product.getId()
                )
                .orElseThrow();
    }

    @Test
    void getCartReturnsEmptyCartForAuthenticatedUser() throws Exception {

        mockMvc.perform(
                        get("/api/cart")
                                .with(user("testuser").roles("USER"))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.items", hasSize(0)))
                .andExpect(jsonPath("$.total", is(0)));
    }

    @Test
    void getCartReturnsCartWithItems() throws Exception {

        Cart cart = createCart(testUser);
        createCartItem(cart, product, 2);

        mockMvc.perform(
                        get("/api/cart")
                                .with(user("testuser").roles("USER"))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(cart.getId().intValue())))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].productId",
                        is(product.getId().intValue())))
                .andExpect(jsonPath("$.items[0].productName",
                        is("Laptop")))
                .andExpect(jsonPath("$.items[0].price",
                        is(50000.0)))
                .andExpect(jsonPath("$.items[0].quantity",
                        is(2)))
                .andExpect(jsonPath("$.items[0].subtotal",
                        is(100000.0)))
                .andExpect(jsonPath("$.total",
                        is(100000.0)));
    }

    @Test
    void addToCartReturnsUpdatedCart() throws Exception {

        mockMvc.perform(
                        post("/api/cart/items")
                                .with(user("testuser").roles("USER"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "productId": %d,
                                            "quantity": 2
                                        }
                                        """.formatted(product.getId()))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].productName",
                        is("Laptop")))
                .andExpect(jsonPath("$.items[0].quantity",
                        is(2)))
                .andExpect(jsonPath("$.total",
                        is(100000.0)));
    }

    @Test
    void addSameProductTwiceIncreasesQuantity() throws Exception {

        mockMvc.perform(
                        post("/api/cart/items")
                                .with(user("testuser").roles("USER"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "productId": %d,
                                            "quantity": 2
                                        }
                                        """.formatted(product.getId()))
                )
                .andExpect(status().isOk());

        mockMvc.perform(
                        post("/api/cart/items")
                                .with(user("testuser").roles("USER"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "productId": %d,
                                            "quantity": 3
                                        }
                                        """.formatted(product.getId()))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].quantity",
                        is(5)))
                .andExpect(jsonPath("$.total",
                        is(250000.0)));
    }

    @Test
    void addToCartWithUnknownProductReturnsNotFound() throws Exception {

        mockMvc.perform(
                        post("/api/cart/items")
                                .with(user("testuser").roles("USER"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "productId": 999999,
                                            "quantity": 1
                                        }
                                        """)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void addToCartBeyondStockReturnsConflict() throws Exception {

        mockMvc.perform(
                        post("/api/cart/items")
                                .with(user("testuser").roles("USER"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "productId": %d,
                                            "quantity": 11
                                        }
                                        """.formatted(product.getId()))
                )
                .andExpect(status().isConflict());
    }

    @Test
    void addToCartWithZeroQuantityReturnsBadRequest() throws Exception {

        mockMvc.perform(
                        post("/api/cart/items")
                                .with(user("testuser").roles("USER"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "productId": %d,
                                            "quantity": 0
                                        }
                                        """.formatted(product.getId()))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void addToCartWithMissingProductIdReturnsBadRequest() throws Exception {

        mockMvc.perform(
                        post("/api/cart/items")
                                .with(user("testuser").roles("USER"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "quantity": 1
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void addToCartWithMissingQuantityReturnsBadRequest() throws Exception {

        mockMvc.perform(
                        post("/api/cart/items")
                                .with(user("testuser").roles("USER"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "productId": %d
                                        }
                                        """.formatted(product.getId()))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateCartItemReturnsUpdatedCart() throws Exception {

        Cart cart = createCart(testUser);
        CartItem item = createCartItem(cart, product, 2);

        mockMvc.perform(
                        put("/api/cart/items/{itemId}", item.getId())
                                .with(user("testuser").roles("USER"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "quantity": 5
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].quantity",
                        is(5)))
                .andExpect(jsonPath("$.total",
                        is(250000.0)));
    }

    @Test
    void updateCartItemWhenNotFoundReturnsNotFound() throws Exception {

        mockMvc.perform(
                        put("/api/cart/items/{itemId}", 999999L)
                                .with(user("testuser").roles("USER"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "quantity": 2
                                        }
                                        """)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void updateCartItemBeyondStockReturnsConflict() throws Exception {

        Cart cart = createCart(testUser);
        CartItem item = createCartItem(cart, product, 2);

        mockMvc.perform(
                        put("/api/cart/items/{itemId}", item.getId())
                                .with(user("testuser").roles("USER"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "quantity": 11
                                        }
                                        """)
                )
                .andExpect(status().isConflict());
    }

    @Test
    void updateCartItemWithZeroQuantityReturnsBadRequest() throws Exception {

        Cart cart = createCart(testUser);
        CartItem item = createCartItem(cart, product, 2);

        mockMvc.perform(
                        put("/api/cart/items/{itemId}", item.getId())
                                .with(user("testuser").roles("USER"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "quantity": 0
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void userCannotUpdateAnotherUsersCartItem() throws Exception {

        Cart secondUserCart = createCart(secondUser);
        CartItem item = createCartItem(secondUserCart, product, 2);

        mockMvc.perform(
                        put("/api/cart/items/{itemId}", item.getId())
                                .with(user("testuser").roles("USER"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "quantity": 5
                                        }
                                        """)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void removeCartItemReturnsUpdatedCart() throws Exception {

        Cart cart = createCart(testUser);
        CartItem item = createCartItem(cart, product, 2);

        mockMvc.perform(
                        delete("/api/cart/items/{itemId}", item.getId())
                                .with(user("testuser").roles("USER"))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(0)))
                .andExpect(jsonPath("$.total", is(0)));
    }

    @Test
    void removeCartItemWhenNotFoundReturnsNotFound() throws Exception {

        mockMvc.perform(
                        delete("/api/cart/items/{itemId}", 999999L)
                                .with(user("testuser").roles("USER"))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void userCannotRemoveAnotherUsersCartItem() throws Exception {

        Cart secondUserCart = createCart(secondUser);
        CartItem item = createCartItem(secondUserCart, product, 2);

        mockMvc.perform(
                        delete("/api/cart/items/{itemId}", item.getId())
                                .with(user("testuser").roles("USER"))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void clearCartReturnsNoContent() throws Exception {

        Cart cart = createCart(testUser);
        createCartItem(cart, product, 2);

        mockMvc.perform(
                        delete("/api/cart")
                                .with(user("testuser").roles("USER"))
                )
                .andExpect(status().isNoContent());

        mockMvc.perform(
                        get("/api/cart")
                                .with(user("testuser").roles("USER"))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(0)))
                .andExpect(jsonPath("$.total", is(0)));
    }

    @Test
    void unauthenticatedUserCannotAccessCart() throws Exception {

        mockMvc.perform(
                        get("/api/cart")
                )
                .andExpect(status().isUnauthorized());
    }
}