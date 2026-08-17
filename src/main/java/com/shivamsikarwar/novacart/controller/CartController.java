package com.shivamsikarwar.novacart.controller;

import com.shivamsikarwar.novacart.dto.CartItemQuantityUpdateDto;
import com.shivamsikarwar.novacart.dto.CartItemRequestDto;
import com.shivamsikarwar.novacart.dto.CartResponseDto;
import com.shivamsikarwar.novacart.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    private final CartService cartService;

    public CartController (CartService cartService){
        this.cartService=cartService;
    }

    @GetMapping
    public CartResponseDto getCart(){
        return cartService.getCart();
    }

    @PostMapping("/items")
    public CartResponseDto addToCart(@Valid @RequestBody CartItemRequestDto request){
        return cartService.addToCart(request);
    }

    @PutMapping("/items/{itemId}")
    public CartResponseDto updateCartItem(
            @PathVariable Long itemId,
            @Valid @RequestBody CartItemQuantityUpdateDto request) {

        return cartService.updateCartItem(
                itemId,
                request.getQuantity()
        );
    }

    @DeleteMapping("/items/{itemId}")
    public CartResponseDto removeCartItem(
            @PathVariable Long itemId) {

        return cartService.removeCartItem(itemId);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clearCart() {
        cartService.clearCart();
    }
}

