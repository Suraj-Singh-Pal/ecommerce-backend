package com.suraj.ecommerce.repository;

import com.suraj.ecommerce.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    Page<Wishlist> findByUserIdOrderByCreatedAtDesc(
        Long userId,
        Pageable pageable
);

    Optional<Wishlist> findByUserIdAndProductId(
            Long userId,
            Long productId
    );

    boolean existsByUserIdAndProductId(
            Long userId,
            Long productId
    );

    void deleteByUserIdAndProductId(
            Long userId,
            Long productId
    );

    void deleteByUserId(Long userId);

    long countByUserId(Long userId);
}