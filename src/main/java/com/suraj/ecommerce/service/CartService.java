package com.suraj.ecommerce.service;

import com.suraj.ecommerce.entity.Cart;
import com.suraj.ecommerce.entity.User;
import com.suraj.ecommerce.exception.ResourceNotFoundException;
import com.suraj.ecommerce.repository.CartRepository;
import com.suraj.ecommerce.repository.UserRepository;
import org.springframework.stereotype.Service;

import com.suraj.ecommerce.entity.CartItem;
import com.suraj.ecommerce.entity.Product;
import com.suraj.ecommerce.repository.CartItemRepository;
import com.suraj.ecommerce.repository.ProductRepository;

import com.suraj.ecommerce.dto.CartItemResponse;
import com.suraj.ecommerce.dto.CartResponse;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public CartService(
        CartRepository cartRepository,
        UserRepository userRepository,
        CartItemRepository cartItemRepository,
        ProductRepository productRepository) {

    this.cartRepository = cartRepository;
    this.userRepository = userRepository;
    this.cartItemRepository = cartItemRepository;
    this.productRepository = productRepository;
}

    public Cart getOrCreateCart(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with username: " + username
                        ));

        return cartRepository.findByUserIdAndActiveTrue(user.getId())
                .orElseGet(() -> {

                    Cart cart = Cart.builder()
                            .user(user)
                            .active(true)
                            .build();

                    return cartRepository.save(cart);
                });
    }

public CartResponse getCart(String username) {

    Cart cart = getOrCreateCart(username);

    List<CartItem> cartItems =
            cartItemRepository.findByCartIdAndCartActiveTrue(
                    cart.getId()
            );

    List<CartItemResponse> itemResponses = cartItems.stream()
            .map(item -> {

                BigDecimal totalPrice =
                        item.getUnitPrice()
                                .multiply(
                                        BigDecimal.valueOf(
                                                item.getQuantity()
                                        )
                                );

                return new CartItemResponse(
                        item.getId(),
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        totalPrice
                );
            })
            .toList();

    BigDecimal totalAmount = itemResponses.stream()
            .map(CartItemResponse::getTotalPrice)
            .reduce(
                    BigDecimal.ZERO,
                    BigDecimal::add
            );

    return new CartResponse(
            cart.getId(),
            cart.getUser().getId(),
            itemResponses,
            totalAmount
    );
}

public CartItem addToCart(
        String username,
        Long productId,
        Integer quantity) {

    if (quantity == null || quantity <= 0) {
        throw new IllegalArgumentException(
                "Quantity must be greater than 0"
        );
    }

    Cart cart = getOrCreateCart(username);

    Product product = productRepository.findById(productId)
            .orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Product not found with ID: " + productId
                    ));

    if (!Boolean.TRUE.equals(product.getActive())) {
        throw new IllegalArgumentException(
                "Product is not active"
        );
    }

    if (product.getStockQuantity() < quantity) {
        throw new IllegalArgumentException(
                "Insufficient stock. Available stock: "
                        + product.getStockQuantity()
        );
    }

    CartItem cartItem = cartItemRepository
            .findByCartIdAndProductId(
                    cart.getId(),
                    productId
            )
            .orElse(null);

    if (cartItem != null) {

        int newQuantity = cartItem.getQuantity() + quantity;

        if (newQuantity > product.getStockQuantity()) {
            throw new IllegalArgumentException(
                    "Insufficient stock. Available stock: "
                            + product.getStockQuantity()
            );
        }

        cartItem.setQuantity(newQuantity);

    } else {

        cartItem = CartItem.builder()
                .cart(cart)
                .product(product)
                .quantity(quantity)
                .unitPrice(product.getPrice())
                .build();
    }

    return cartItemRepository.save(cartItem);
}

public CartItem updateCartItemQuantity(
        String username,
        Long cartItemId,
        Integer quantity) {

    if (quantity == null || quantity <= 0) {
        throw new IllegalArgumentException(
                "Quantity must be greater than 0"
        );
    }

    Cart cart = getOrCreateCart(username);

    CartItem cartItem = cartItemRepository
            .findById(cartItemId)
            .orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Cart item not found with ID: "
                                    + cartItemId
                    ));

    if (!cartItem.getCart().getId().equals(cart.getId())) {
        throw new IllegalArgumentException(
                "Cart item does not belong to your cart"
        );
    }

    Product product = cartItem.getProduct();

    if (!Boolean.TRUE.equals(product.getActive())) {
        throw new IllegalArgumentException(
                "Product is not active"
        );
    }

    if (quantity > product.getStockQuantity()) {
        throw new IllegalArgumentException(
                "Insufficient stock. Available stock: "
                        + product.getStockQuantity()
        );
    }

    cartItem.setQuantity(quantity);

    return cartItemRepository.save(cartItem);
}

public void removeCartItem(
        String username,
        Long cartItemId) {

    Cart cart = getOrCreateCart(username);

    CartItem cartItem = cartItemRepository
            .findById(cartItemId)
            .orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Cart item not found with ID: "
                                    + cartItemId
                    ));

    if (!cartItem.getCart().getId().equals(cart.getId())) {
        throw new IllegalArgumentException(
                "Cart item does not belong to your cart"
        );
    }

    cartItemRepository.delete(cartItem);
}

public void clearCart(String username) {

    Cart cart = getOrCreateCart(username);

    cartItemRepository.deleteByCartId(cart.getId());
}

}