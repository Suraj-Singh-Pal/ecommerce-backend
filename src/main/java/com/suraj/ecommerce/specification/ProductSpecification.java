package com.suraj.ecommerce.specification;

import com.suraj.ecommerce.entity.Product;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class ProductSpecification {

    private ProductSpecification() {
    }

    public static Specification<Product> hasKeyword(String keyword) {

        return (root, query, criteriaBuilder) -> {

            if (keyword == null || keyword.isBlank()) {
                return null;
            }

            String searchKeyword = "%" + keyword.toLowerCase() + "%";

            return criteriaBuilder.or(
                    criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("name")),
                            searchKeyword
                    ),
                    criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("description")),
                            searchKeyword
                    )
            );
        };
    }

    public static Specification<Product> hasMinPrice(BigDecimal minPrice) {

        return (root, query, criteriaBuilder) -> {

            if (minPrice == null) {
                return null;
            }

            return criteriaBuilder.greaterThanOrEqualTo(
                    root.get("price"),
                    minPrice
            );
        };
    }

    public static Specification<Product> hasMaxPrice(BigDecimal maxPrice) {

        return (root, query, criteriaBuilder) -> {

            if (maxPrice == null) {
                return null;
            }

            return criteriaBuilder.lessThanOrEqualTo(
                    root.get("price"),
                    maxPrice
            );
        };
    }
}