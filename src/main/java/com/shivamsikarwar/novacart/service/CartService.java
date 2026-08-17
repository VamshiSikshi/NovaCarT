package com.shivamsikarwar.novacart.service;

import com.shivamsikarwar.novacart.dto.CartItemRequestDto;
import com.shivamsikarwar.novacart.dto.CartItemResponseDto;
import com.shivamsikarwar.novacart.dto.CartResponseDto;
import com.shivamsikarwar.novacart.entity.Cart;
import com.shivamsikarwar.novacart.entity.CartItem;
import com.shivamsikarwar.novacart.entity.Product;
import com.shivamsikarwar.novacart.entity.User;
import com.shivamsikarwar.novacart.exception.AuthenticatedUserNotFoundException;
import com.shivamsikarwar.novacart.exception.ResourceNotFoundException;
import com.shivamsikarwar.novacart.repository.CartItemRepository;
import com.shivamsikarwar.novacart.repository.CartRepository;
import com.shivamsikarwar.novacart.repository.ProductRepository;
import com.shivamsikarwar.novacart.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.shivamsikarwar.novacart.exception.InsufficientStockException;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CartService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;

    public CartService(UserRepository userRepository,
                       CartRepository cartRepository,
                       ProductRepository productRepository,
                       CartItemRepository cartItemRepository) {
        this.userRepository = userRepository;
        this.cartRepository=cartRepository;
        this.productRepository=productRepository;
        this.cartItemRepository=cartItemRepository;
    }

    private User getCurrentUser(){
        Authentication authentication=
                SecurityContextHolder.getContext().getAuthentication();

        return userRepository.findByUsername(
                authentication.getName()
                ).orElseThrow(()->
                new AuthenticatedUserNotFoundException(
                        "Authenticated user not found"
                )
        );
    }

    public Cart getOrCreateCart(){
        User user =getCurrentUser();

        return cartRepository.findByUserId(user.getId())
                .orElseGet(()-> {
                    Cart cart = new Cart();
                    cart.setUser(user);
                    return cartRepository.save((cart));
                });
    }

    public CartResponseDto getCart(){
        Cart cart = getOrCreateCart();
        CartResponseDto response = new CartResponseDto();
        response.setId(cart.getId());

        List<CartItemResponseDto> items=
                cart.getItems()
                .stream()
                .map(this::toCartItemResponse)
                .toList();

        response.setItems(items);
        BigDecimal total = items.stream()
                .map(CartItemResponseDto::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        response.setTotal(total);
        return response;
    }

    private CartItemResponseDto toCartItemResponse(CartItem item){
        CartItemResponseDto response = new CartItemResponseDto();

        response.setId(item.getId());
        response.setProductId(item.getProduct().getId());
        response.setProductName(item.getProduct().getName());
        response.setPrice(item.getProduct().getPrice());
        response.setQuantity(item.getQuantity());

        BigDecimal subtotal=
                item.getProduct()
                        .getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())
                        );

        response.setSubtotal(subtotal);
        return response;
    }

    @Transactional
    public CartResponseDto addToCart(CartItemRequestDto request) {

        Cart cart = getOrCreateCart();

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product not found with id: "
                                        + request.getProductId()
                        )
                );

        CartItem cartItem =
                cartItemRepository
                        .findByCartIdAndProductId(
                                cart.getId(),
                                product.getId()
                        )
                        .orElse(null);

        int existingQuantity = 0;

        if (cartItem != null) {
            existingQuantity = cartItem.getQuantity();
        }

        int requestedQuantity =
                existingQuantity + request.getQuantity();

        if (requestedQuantity > product.getStock()) {
            throw new InsufficientStockException(
                    "Insufficient stock. Available stock: "
                            + product.getStock()
            );
        }

        if (cartItem == null) {

            cartItem = new CartItem();

            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(request.getQuantity());

            cart.getItems().add(cartItem);

        } else {

            cartItem.setQuantity(requestedQuantity);
        }

        cartRepository.save(cart);

        return getCart();
    }

    @Transactional
    public CartResponseDto updateCartItem(
            Long itemId,
            Integer quantity) {

        Cart cart = getOrCreateCart();
        CartItem cartItem = cartItemRepository
                .findById(itemId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Cart item not found with id: " + itemId
                        )
                );

        // Security check: item must belong to current user's cart
        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new ResourceNotFoundException(
                    "Cart item not found with id: " + itemId
            );
        }

        Product product = cartItem.getProduct();

        if (quantity > product.getStock()) {
            throw new InsufficientStockException(
                    "Insufficient stock. Available stock: "
                            + product.getStock()
            );
        }

        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);
        return getCart();
    }

    @Transactional
    public CartResponseDto removeCartItem(Long itemId) {

        Cart cart = getOrCreateCart();

        CartItem cartItem = cartItemRepository
                .findById(itemId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Cart item not found with id: " + itemId
                        )
                );

        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new ResourceNotFoundException(
                    "Cart item not found with id: " + itemId
            );
        }

        cart.getItems().remove(cartItem);

        cartItemRepository.delete(cartItem);

        return getCart();
    }

    @Transactional
    public void clearCart() {

        Cart cart = getOrCreateCart();

        cart.getItems().clear();

        cartRepository.save(cart);
    }
}
