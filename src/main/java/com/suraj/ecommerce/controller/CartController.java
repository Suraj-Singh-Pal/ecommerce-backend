package com.suraj.ecommerce.controller;

import com.suraj.ecommerce.dto.CartResponse;
import com.suraj.ecommerce.entity.CartItem;
import com.suraj.ecommerce.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/items")
    public ResponseEntity<?> addToCart(
            Authentication authentication,
            @RequestParam Long productId,
            @RequestParam Integer quantity) {

        CartItem cartItem = cartService.addToCart(
                authentication.getName(),
                productId,
                quantity
        );

        return ResponseEntity.ok(cartItem);
    }

@GetMapping
public ResponseEntity<CartResponse> getCart(
        Authentication authentication) {

    CartResponse cartResponse =
            cartService.getCart(authentication.getName());

    return ResponseEntity.ok(cartResponse);
}

@PutMapping("/items/{cartItemId}")
public ResponseEntity<?> updateCartItemQuantity(
        Authentication authentication,
        @PathVariable Long cartItemId,
        @RequestParam Integer quantity) {

    CartItem cartItem = cartService.updateCartItemQuantity(
            authentication.getName(),
            cartItemId,
            quantity
    );

    return ResponseEntity.ok(cartItem);
}

@DeleteMapping("/items/{cartItemId}")
public ResponseEntity<?> removeCartItem(
        Authentication authentication,
        @PathVariable Long cartItemId) {

    cartService.removeCartItem(
            authentication.getName(),
            cartItemId
    );

    return ResponseEntity.ok(
            "Cart item removed successfully"
    );
}

@DeleteMapping
public ResponseEntity<?> clearCart(
        Authentication authentication) {

    cartService.clearCart(
            authentication.getName()
    );

    return ResponseEntity.ok(
            "Cart cleared successfully"
    );
}

}