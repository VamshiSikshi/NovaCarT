package com.shivamsikarwar.novacart.service;

import com.shivamsikarwar.novacart.dto.CartItemRequestDto;
import com.shivamsikarwar.novacart.dto.CartResponseDto;
import com.shivamsikarwar.novacart.entity.Cart;
import com.shivamsikarwar.novacart.entity.CartItem;
import com.shivamsikarwar.novacart.entity.Product;
import com.shivamsikarwar.novacart.entity.User;
import com.shivamsikarwar.novacart.exception.InsufficientStockException;
import com.shivamsikarwar.novacart.exception.ResourceNotFoundException;
import com.shivamsikarwar.novacart.repository.CartItemRepository;
import com.shivamsikarwar.novacart.repository.CartRepository;
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

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @InjectMocks
    private CartService cartService;

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
    void getOrCreateCartCreatesCartWhenUserHasNoCart() {

        User user = new User();
        user.setId(1L);
        user.setUsername("user1");

        when(userRepository.findByUsername("user1"))
                .thenReturn(Optional.of(user));

        when(cartRepository.findByUserId(1L))
                .thenReturn(Optional.empty());

        when(cartRepository.save(any(Cart.class)))
                .thenAnswer(invocation -> {
                    Cart cart = invocation.getArgument(0);
                    cart.setId(10L);
                    return cart;
                });

        Cart cart = cartService.getOrCreateCart();

        assertNotNull(cart);
        assertEquals(10L, cart.getId());
        assertEquals(user, cart.getUser());

        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    void getOrCreateCartReturnsExistingCart() {

        User user = new User();
        user.setId(1L);
        user.setUsername("user1");

        Cart cart = new Cart();
        cart.setId(10L);
        cart.setUser(user);

        when(userRepository.findByUsername("user1"))
                .thenReturn(Optional.of(user));

        when(cartRepository.findByUserId(1L))
                .thenReturn(Optional.of(cart));

        Cart result = cartService.getOrCreateCart();

        assertNotNull(result);
        assertEquals(10L, result.getId());

        verify(cartRepository, never())
                .save(any(Cart.class));
    }

    @Test
    void addToCartAddsProductToCart() {

        User user = new User();
        user.setId(1L);
        user.setUsername("user1");

        Cart cart = new Cart();
        cart.setId(10L);
        cart.setUser(user);

        Product product = new Product();
        product.setId(100L);
        product.setName("Laptop");
        product.setPrice(new BigDecimal("50000.00"));
        product.setStock(10);

        CartItemRequestDto request = new CartItemRequestDto();
        request.setProductId(100L);
        request.setQuantity(2);

        when(userRepository.findByUsername("user1"))
                .thenReturn(Optional.of(user));

        when(cartRepository.findByUserId(1L))
                .thenReturn(Optional.of(cart));

        when(productRepository.findById(100L))
                .thenReturn(Optional.of(product));

        when(cartItemRepository.findByCartIdAndProductId(10L, 100L))
                .thenReturn(Optional.empty());

        when(cartRepository.save(cart))
                .thenReturn(cart);

        CartResponseDto response =
                cartService.addToCart(request);

        assertNotNull(response);
        assertEquals(10L, response.getId());

        assertEquals(1, cart.getItems().size());
        assertEquals(
                2,
                cart.getItems()
                        .get(0)
                        .getQuantity()
        );

        verify(cartRepository).save(cart);
    }

    @Test
    void addToCartRejectsInsufficientStock() {

        User user = new User();
        user.setId(1L);
        user.setUsername("user1");

        Cart cart = new Cart();
        cart.setId(10L);
        cart.setUser(user);

        Product product = new Product();
        product.setId(100L);
        product.setName("Laptop");
        product.setPrice(new BigDecimal("50000.00"));
        product.setStock(2);

        CartItemRequestDto request = new CartItemRequestDto();
        request.setProductId(100L);
        request.setQuantity(5);

        when(userRepository.findByUsername("user1"))
                .thenReturn(Optional.of(user));

        when(cartRepository.findByUserId(1L))
                .thenReturn(Optional.of(cart));

        when(productRepository.findById(100L))
                .thenReturn(Optional.of(product));

        when(cartItemRepository.findByCartIdAndProductId(10L, 100L))
                .thenReturn(Optional.empty());

        assertThrows(
                InsufficientStockException.class,
                () -> cartService.addToCart(request)
        );

        verify(cartRepository, never())
                .save(any(Cart.class));
    }

    @Test
    void addToCartIncreasesQuantityForExistingProduct() {

        User user = new User();
        user.setId(1L);
        user.setUsername("user1");

        Cart cart = new Cart();
        cart.setId(10L);
        cart.setUser(user);

        Product product = new Product();
        product.setId(100L);
        product.setName("Laptop");
        product.setPrice(new BigDecimal("50000.00"));
        product.setStock(10);

        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setQuantity(2);

        CartItemRequestDto request = new CartItemRequestDto();
        request.setProductId(100L);
        request.setQuantity(3);

        when(userRepository.findByUsername("user1"))
                .thenReturn(Optional.of(user));

        when(cartRepository.findByUserId(1L))
                .thenReturn(Optional.of(cart));

        when(productRepository.findById(100L))
                .thenReturn(Optional.of(product));

        when(cartItemRepository.findByCartIdAndProductId(10L, 100L))
                .thenReturn(Optional.of(cartItem));

        when(cartRepository.save(cart))
                .thenReturn(cart);

        CartResponseDto response =
                cartService.addToCart(request);

        assertNotNull(response);

        assertEquals(
                5,
                cartItem.getQuantity()
        );

        verify(cartRepository).save(cart);
    }

    @Test
    void addToCartRejectsWhenCombinedQuantityExceedsStock() {

        User user = new User();
        user.setId(1L);
        user.setUsername("user1");

        Cart cart = new Cart();
        cart.setId(10L);
        cart.setUser(user);

        Product product = new Product();
        product.setId(100L);
        product.setName("Laptop");
        product.setPrice(new BigDecimal("50000.00"));
        product.setStock(5);

        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setQuantity(4);

        CartItemRequestDto request = new CartItemRequestDto();
        request.setProductId(100L);
        request.setQuantity(2);

        when(userRepository.findByUsername("user1"))
                .thenReturn(Optional.of(user));

        when(cartRepository.findByUserId(1L))
                .thenReturn(Optional.of(cart));

        when(productRepository.findById(100L))
                .thenReturn(Optional.of(product));

        when(cartItemRepository.findByCartIdAndProductId(10L, 100L))
                .thenReturn(Optional.of(cartItem));

        assertThrows(
                InsufficientStockException.class,
                () -> cartService.addToCart(request)
        );

        assertEquals(4, cartItem.getQuantity());

        verify(cartRepository, never())
                .save(any(Cart.class));
    }

    @Test
    void updateCartItemChangesQuantity() {

        User user = new User();
        user.setId(1L);
        user.setUsername("user1");

        Cart cart = new Cart();
        cart.setId(10L);
        cart.setUser(user);

        Product product = new Product();
        product.setId(100L);
        product.setName("Laptop");
        product.setStock(10);

        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setQuantity(2);

        when(userRepository.findByUsername("user1"))
                .thenReturn(Optional.of(user));

        when(cartRepository.findByUserId(1L))
                .thenReturn(Optional.of(cart));

        when(cartItemRepository.findById(1L))
                .thenReturn(Optional.of(cartItem));

        when(cartItemRepository.save(cartItem))
                .thenReturn(cartItem);

        CartResponseDto response =
                cartService.updateCartItem(1L, 5);

        assertNotNull(response);

        assertEquals(5, cartItem.getQuantity());

        verify(cartItemRepository)
                .save(cartItem);
    }

    @Test
    void updateCartItemRejectsQuantityAboveStock() {

        User user = new User();
        user.setId(1L);
        user.setUsername("user1");

        Cart cart = new Cart();
        cart.setId(10L);
        cart.setUser(user);

        Product product = new Product();
        product.setId(100L);
        product.setName("Laptop");
        product.setStock(5);

        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setQuantity(2);

        when(userRepository.findByUsername("user1"))
                .thenReturn(Optional.of(user));

        when(cartRepository.findByUserId(1L))
                .thenReturn(Optional.of(cart));

        when(cartItemRepository.findById(1L))
                .thenReturn(Optional.of(cartItem));

        assertThrows(
                InsufficientStockException.class,
                () -> cartService.updateCartItem(1L, 6)
        );

        assertEquals(2, cartItem.getQuantity());

        verify(cartItemRepository, never())
                .save(any(CartItem.class));
    }

    @Test
    void userCannotUpdateCartItemFromAnotherCart() {

        User user = new User();
        user.setId(1L);
        user.setUsername("user1");

        Cart userCart = new Cart();
        userCart.setId(10L);
        userCart.setUser(user);

        Cart anotherCart = new Cart();
        anotherCart.setId(20L);

        Product product = new Product();
        product.setId(100L);
        product.setStock(10);

        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setCart(anotherCart);
        cartItem.setProduct(product);
        cartItem.setQuantity(2);

        when(userRepository.findByUsername("user1"))
                .thenReturn(Optional.of(user));

        when(cartRepository.findByUserId(1L))
                .thenReturn(Optional.of(userCart));

        when(cartItemRepository.findById(1L))
                .thenReturn(Optional.of(cartItem));

        assertThrows(
                ResourceNotFoundException.class,
                () -> cartService.updateCartItem(1L, 5)
        );

        assertEquals(2, cartItem.getQuantity());

        verify(cartItemRepository, never())
                .save(any(CartItem.class));
    }

    @Test
    void removeCartItemRemovesItemFromOwnCart() {

        User user = new User();
        user.setId(1L);
        user.setUsername("user1");

        Cart cart = new Cart();
        cart.setId(10L);
        cart.setUser(user);

        Product product = new Product();
        product.setId(100L);

        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setQuantity(2);

        cart.getItems().add(cartItem);

        when(userRepository.findByUsername("user1"))
                .thenReturn(Optional.of(user));

        when(cartRepository.findByUserId(1L))
                .thenReturn(Optional.of(cart));

        when(cartItemRepository.findById(1L))
                .thenReturn(Optional.of(cartItem));

        CartResponseDto response =
                cartService.removeCartItem(1L);

        assertNotNull(response);

        assertFalse(
                cart.getItems().contains(cartItem)
        );

        verify(cartItemRepository)
                .delete(cartItem);
    }

    @Test
    void userCannotRemoveCartItemFromAnotherCart() {

        User user = new User();
        user.setId(1L);
        user.setUsername("user1");

        Cart userCart = new Cart();
        userCart.setId(10L);
        userCart.setUser(user);

        Cart anotherCart = new Cart();
        anotherCart.setId(20L);

        Product product = new Product();
        product.setId(100L);

        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setCart(anotherCart);
        cartItem.setProduct(product);
        cartItem.setQuantity(2);

        when(userRepository.findByUsername("user1"))
                .thenReturn(Optional.of(user));

        when(cartRepository.findByUserId(1L))
                .thenReturn(Optional.of(userCart));

        when(cartItemRepository.findById(1L))
                .thenReturn(Optional.of(cartItem));

        assertThrows(
                ResourceNotFoundException.class,
                () -> cartService.removeCartItem(1L)
        );

        verify(cartItemRepository, never())
                .delete(any(CartItem.class));
    }

    @Test
    void clearCartRemovesAllItems() {

        User user = new User();
        user.setId(1L);
        user.setUsername("user1");

        Cart cart = new Cart();
        cart.setId(10L);
        cart.setUser(user);

        CartItem item1 = new CartItem();
        item1.setId(1L);

        CartItem item2 = new CartItem();
        item2.setId(2L);

        cart.getItems().add(item1);
        cart.getItems().add(item2);

        when(userRepository.findByUsername("user1"))
                .thenReturn(Optional.of(user));

        when(cartRepository.findByUserId(1L))
                .thenReturn(Optional.of(cart));

        cartService.clearCart();

        assertTrue(cart.getItems().isEmpty());

        verify(cartRepository)
                .save(cart);
    }

    @Test
    void updateCartItemThrowsWhenItemDoesNotExist() {

        User user = new User();
        user.setId(1L);
        user.setUsername("user1");

        Cart cart = new Cart();
        cart.setId(10L);
        cart.setUser(user);

        when(userRepository.findByUsername("user1"))
                .thenReturn(Optional.of(user));

        when(cartRepository.findByUserId(1L))
                .thenReturn(Optional.of(cart));

        when(cartItemRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> cartService.updateCartItem(999L, 5)
        );

        verify(cartItemRepository, never())
                .save(any(CartItem.class));
    }

}