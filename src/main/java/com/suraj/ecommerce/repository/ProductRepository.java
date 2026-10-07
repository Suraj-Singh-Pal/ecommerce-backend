package com.suraj.ecommerce.repository;

import com.suraj.ecommerce.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.math.BigDecimal;
import java.util.List;

public interface ProductRepository
        extends JpaRepository<Product, Long>,
                JpaSpecificationExecutor<Product> {

        List<Product> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
        String name,
        String description
);

List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    List<Product> findByActiveTrue();

    Page<Product> findByActiveTrue(Pageable pageable);

    Page<Product> findByActiveTrueAndNameContainingIgnoreCase(
            String name,
            Pageable pageable
    );

    Page<Product> findByActiveTrueAndPriceBetween(
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Pageable pageable
    );

    Page<Product> findByActiveTrueAndStockQuantityGreaterThan(
            Integer stockQuantity,
            Pageable pageable
    );

List<Product> findByCategoryIdAndActiveTrue(Long categoryId);

Page<Product> findByCategoryIdAndActiveTrue(
        Long categoryId,
        Pageable pageable
);

boolean existsByCategoryIdAndActiveTrue(Long categoryId);


@Modifying
@Query("UPDATE Product p SET p.stockQuantity = p.stockQuantity - :quantity WHERE p.id = :productId AND p.stockQuantity >= :quantity")
int decreaseStock(@Param("productId") Long productId,
                  @Param("quantity") Integer quantity);

@Modifying
@Query("UPDATE Product p SET p.stockQuantity = p.stockQuantity + :quantity WHERE p.id = :productId")
int increaseStock(@Param("productId") Long productId,
                  @Param("quantity") Integer quantity);

}