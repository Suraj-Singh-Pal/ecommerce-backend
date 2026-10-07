package com.suraj.ecommerce.repository;

import com.suraj.ecommerce.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);

    Optional<Category> findByIdAndActiveTrue(Long id);
    
}