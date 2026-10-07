package com.suraj.ecommerce.service;

import com.suraj.ecommerce.dto.WishlistResponse;
import com.suraj.ecommerce.entity.Product;
import com.suraj.ecommerce.entity.User;
import com.suraj.ecommerce.entity.Wishlist;
import com.suraj.ecommerce.exception.ResourceNotFoundException;
import com.suraj.ecommerce.repository.ProductRepository;
import com.suraj.ecommerce.repository.UserRepository;
import com.suraj.ecommerce.repository.WishlistRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Service
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public WishlistService(
            WishlistRepository wishlistRepository,
            UserRepository userRepository,
            ProductRepository productRepository
    ) {
        this.wishlistRepository = wishlistRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    // Add product to wishlist
    @Transactional
    public WishlistResponse addToWishlist(
            String username,
            Long productId
    ) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found")
                );

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product not found with id: " + productId
                        )
                );

        if (!Boolean.TRUE.equals(product.getActive())) {
            throw new IllegalArgumentException(
                    "Inactive products cannot be added to wishlist"
            );
        }

        if (wishlistRepository.existsByUserIdAndProductId(
                user.getId(), productId)) {
            throw new IllegalArgumentException(
                    "Product is already in your wishlist"
            );
        }

        Wishlist wishlist = Wishlist.builder()
                .user(user)
                .product(product)
                .build();

        Wishlist savedWishlist = wishlistRepository.save(wishlist);

        return mapToResponse(savedWishlist);
    }

    // Get current user's wishlist
    @Transactional(readOnly = true)
public Page<WishlistResponse> getWishlist(
        String username,
        int page,
        int size
) {

    if (page < 0) {
        throw new IllegalArgumentException(
                "Page number cannot be negative"
        );
    }

    if (size < 1 || size > 100) {
        throw new IllegalArgumentException(
                "Page size must be between 1 and 100"
        );
    }

    User user = userRepository.findByUsername(username)
            .orElseThrow(() ->
                    new ResourceNotFoundException("User not found")
            );

    Pageable pageable = PageRequest.of(
            page,
            size,
            Sort.by("createdAt").descending()
    );

    Page<Wishlist> wishlistPage =
            wishlistRepository.findByUserIdOrderByCreatedAtDesc(
                    user.getId(),
                    pageable
            );

    return wishlistPage.map(this::mapToResponse);
}

@Transactional(readOnly = true)
public long getWishlistCount(String username) {

    User user = userRepository.findByUsername(username)
            .orElseThrow(() ->
                    new ResourceNotFoundException("User not found")
            );

    return wishlistRepository.countByUserId(user.getId());
}

@Transactional(readOnly = true)
public boolean isProductInWishlist(
        String username,
        Long productId
) {
    User user = userRepository.findByUsername(username)
            .orElseThrow(() ->
                    new ResourceNotFoundException("User not found")
            );

    return wishlistRepository.existsByUserIdAndProductId(
            user.getId(),
            productId
    );
}

    // Remove a product from current user's wishlist
    @Transactional
    public void removeFromWishlist(
            String username,
            Long productId
    ) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found")
                );

        Wishlist wishlist = wishlistRepository
                .findByUserIdAndProductId(user.getId(), productId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product not found in your wishlist"
                        )
                );

        wishlistRepository.delete(wishlist);
    }

    // Clear current user's wishlist
    @Transactional
    public void clearWishlist(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found")
                );

        wishlistRepository.deleteByUserId(user.getId());
    }

    // Convert entity to DTO
    private WishlistResponse mapToResponse(Wishlist wishlist) {

        Product product = wishlist.getProduct();

        return new WishlistResponse(
                wishlist.getId(),
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getImageUrl(),
                wishlist.getCreatedAt()
        );
    }
}