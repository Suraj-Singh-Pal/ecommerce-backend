package com.suraj.ecommerce.repository;

import com.suraj.ecommerce.entity.Cart;
import com.suraj.ecommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByUser(User user);

    Optional<Cart> findByUserId(Long userId);

    Optional<Cart> findByUserIdAndActiveTrue(Long userId);
}