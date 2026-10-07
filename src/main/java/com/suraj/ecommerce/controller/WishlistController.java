
package com.suraj.ecommerce.controller;

import com.suraj.ecommerce.dto.WishlistResponse;
import com.suraj.ecommerce.service.WishlistService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;

@Validated
@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {

    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    // Add product to wishlist
    @PostMapping("/{productId}")
    public ResponseEntity<WishlistResponse> addToWishlist(
            Authentication authentication,
            @PathVariable @jakarta.validation.constraints.Positive Long productId) {

        WishlistResponse response = wishlistService.addToWishlist(
                authentication.getName(),
                productId
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Get current user's wishlist
    @GetMapping
public ResponseEntity<Page<WishlistResponse>> getWishlist(
        Authentication authentication,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int size
) {
    return ResponseEntity.ok(
            wishlistService.getWishlist(
                    authentication.getName(),
                    page,
                    size
            )
    );
}

@GetMapping("/count")
public ResponseEntity<Long> getWishlistCount(
        Authentication authentication
) {
    return ResponseEntity.ok(
            wishlistService.getWishlistCount(
                    authentication.getName()
            )
    );
}

@GetMapping("/check/{productId}")
public ResponseEntity<Boolean> isProductInWishlist(
        Authentication authentication,
        @PathVariable Long productId
) {
    return ResponseEntity.ok(
            wishlistService.isProductInWishlist(
                    authentication.getName(),
                    productId
            )
    );
}

    // Remove product from wishlist
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> removeFromWishlist(
            Authentication authentication,
            @PathVariable Long productId) {

        wishlistService.removeFromWishlist(
                authentication.getName(),
                productId
        );

        return ResponseEntity.noContent().build();
    }

    // Clear entire wishlist
    @DeleteMapping
    public ResponseEntity<Void> clearWishlist(
            Authentication authentication) {

        wishlistService.clearWishlist(authentication.getName());

        return ResponseEntity.noContent().build();
    }
}