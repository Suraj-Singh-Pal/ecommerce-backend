package com.suraj.ecommerce.service;

import com.suraj.ecommerce.dto.ProductRequest;
import com.suraj.ecommerce.dto.ProductResponse;
import com.suraj.ecommerce.entity.Product;
import com.suraj.ecommerce.exception.ResourceNotFoundException;
import com.suraj.ecommerce.repository.ProductRepository;
import com.suraj.ecommerce.specification.ProductSpecification;
import com.suraj.ecommerce.repository.CategoryRepository;
import com.suraj.ecommerce.entity.Category;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
private final CategoryRepository categoryRepository;

public ProductService(
        ProductRepository productRepository,
        CategoryRepository categoryRepository) {

    this.productRepository = productRepository;
    this.categoryRepository = categoryRepository;
}

    public List<Product> searchProducts(String keyword) {
    return productRepository
            .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                    keyword,
                    keyword
            );
}

public List<Product> filterByPrice(BigDecimal minPrice, BigDecimal maxPrice) {

    return productRepository.findByPriceBetween(minPrice, maxPrice);
}

    // CREATE PRODUCT
    public ProductResponse createProduct(ProductRequest request) {

        Product product = new Product();

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setImageUrl(request.getImageUrl());

Category category = categoryRepository
        .findByIdAndActiveTrue(request.getCategoryId())
        .orElseThrow(() ->
                new ResourceNotFoundException(
                        "Active category not found with ID: "
                                + request.getCategoryId()
                ));

product.setCategory(category);

Product savedProduct = productRepository.save(product);

        return mapToResponse(savedProduct);
    }

    // GET ALL PRODUCTS
    public List<ProductResponse> getAllProducts() {
    return productRepository.findByActiveTrue()
            .stream()
            .map(this::mapToResponse)
            .toList();
}

    // GET PRODUCT BY ID
    public ProductResponse getProductById(Long id) {
    Product product = productRepository.findById(id)
            .orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Product not found with ID: " + id
                    ));

    if (!Boolean.TRUE.equals(product.getActive())) {
        throw new ResourceNotFoundException(
                "Product not found with ID: " + id
        );
    }

    return mapToResponse(product);
}

    // UPDATE PRODUCT
    public ProductResponse updateProduct(Long id, ProductRequest request) {

    Product existingProduct = productRepository.findById(id)
            .orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Product not found with ID: " + id
                    ));

    if (!Boolean.TRUE.equals(existingProduct.getActive())) {
        throw new ResourceNotFoundException(
                "Product not found with ID: " + id
        );
    }

    existingProduct.setName(request.getName());
    existingProduct.setDescription(request.getDescription());
    existingProduct.setPrice(request.getPrice());
    existingProduct.setStockQuantity(request.getStockQuantity());
    existingProduct.setImageUrl(request.getImageUrl());

Category category = categoryRepository
        .findByIdAndActiveTrue(request.getCategoryId())
        .orElseThrow(() ->
                new ResourceNotFoundException(
                        "Active category not found with ID: "
                                + request.getCategoryId()
                ));

existingProduct.setCategory(category);

Product updatedProduct = productRepository.save(existingProduct);

    return mapToResponse(updatedProduct);
}

    // DELETE PRODUCT
    public void deleteProduct(Long id) {

    Product existingProduct = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                    "Product not found with ID: " + id
            ));

    existingProduct.setActive(false);

    productRepository.save(existingProduct);
}

    // ENTITY TO DTO CONVERSION
    private ProductResponse mapToResponse(Product product) {

        return new ProductResponse(
        product.getId(),
        product.getName(),
        product.getDescription(),
        product.getPrice(),
        product.getStockQuantity(),
        product.getImageUrl(),
        product.getActive(),
        product.getCreatedAt(),
        product.getUpdatedAt(),
        product.getCategory() != null
                ? product.getCategory().getId()
                : null,
        product.getCategory() != null
                ? product.getCategory().getName()
                : null
);
    }
    // GET PRODUCTS WITH PAGINATION AND SORTING
    public Page<ProductResponse> getProductsWithPagination(
        int page,
        int size,
        String sortBy,
        String direction) {

    if (page < 0) {
        throw new IllegalArgumentException("Page number cannot be negative");
    }

    if (size <= 0 || size > 100) {
        throw new IllegalArgumentException("Page size must be between 1 and 100");
    }

    Sort.Direction sortDirection;

    if (direction.equalsIgnoreCase("desc")) {
        sortDirection = Sort.Direction.DESC;
    } else if (direction.equalsIgnoreCase("asc")) {
        sortDirection = Sort.Direction.ASC;
    } else {
        throw new IllegalArgumentException("Direction must be either asc or desc");
    }

    String validatedSortBy = validateSortField(sortBy);

Pageable pageable = PageRequest.of(
        page,
        size,
        Sort.by(sortDirection, validatedSortBy)
);

    Page<Product> productPage = productRepository.findByActiveTrue(pageable);

    return productPage.map(this::mapToResponse);
}

public Page<ProductResponse> searchProducts(
        String name,
        int page,
        int size,
        String sortBy,
        String direction) {

    if (page < 0) {
        throw new IllegalArgumentException("Page number cannot be negative");
    }

    if (size <= 0 || size > 100) {
        throw new IllegalArgumentException("Page size must be between 1 and 100");
    }

    Sort.Direction sortDirection;

    if (direction.equalsIgnoreCase("desc")) {
        sortDirection = Sort.Direction.DESC;
    } else if (direction.equalsIgnoreCase("asc")) {
        sortDirection = Sort.Direction.ASC;
    } else {
        throw new IllegalArgumentException("Direction must be either asc or desc");
    }

    String validatedSortBy = validateSortField(sortBy);

Pageable pageable = PageRequest.of(
        page,
        size,
        Sort.by(sortDirection, validatedSortBy)
);

    Page<Product> productPage =
        productRepository.findByActiveTrueAndNameContainingIgnoreCase(name, pageable);

    return productPage.map(this::mapToResponse);
}

public Page<ProductResponse> filterProductsByPrice(
        BigDecimal minPrice,
        BigDecimal maxPrice,
        int page,
        int size,
        String sortBy,
        String direction) {

    validatePagination(page, size);

    if (minPrice.compareTo(maxPrice) > 0) {
        throw new IllegalArgumentException(
                "Minimum price cannot be greater than maximum price"
        );
    }

    Sort.Direction sortDirection = getSortDirection(direction);

    String validatedSortBy = validateSortField(sortBy);

Pageable pageable = PageRequest.of(
        page,
        size,
        Sort.by(sortDirection, validatedSortBy)
);

    Page<Product> productPage =
        productRepository.findByActiveTrueAndPriceBetween(
                minPrice,
                maxPrice,
                pageable
        );

    return productPage.map(this::mapToResponse);
}

public Page<ProductResponse> getInStockProducts(
        int page,
        int size,
        String sortBy,
        String direction) {

    validatePagination(page, size);

    Sort.Direction sortDirection = getSortDirection(direction);

    String validatedSortBy = validateSortField(sortBy);

Pageable pageable = PageRequest.of(
        page,
        size,
        Sort.by(sortDirection, validatedSortBy)
);

    Page<Product> productPage =
        productRepository.findByActiveTrueAndStockQuantityGreaterThan(
                0,
                pageable
        );

    return productPage.map(this::mapToResponse);
}

private void validatePagination(int page, int size) {

    if (page < 0) {
        throw new IllegalArgumentException(
                "Page number cannot be negative"
        );
    }

    if (size <= 0 || size > 100) {
        throw new IllegalArgumentException(
                "Page size must be between 1 and 100"
        );
    }
}

private Sort.Direction getSortDirection(String direction) {

    if (direction.equalsIgnoreCase("asc")) {
        return Sort.Direction.ASC;
    }

    if (direction.equalsIgnoreCase("desc")) {
        return Sort.Direction.DESC;
    }

    throw new IllegalArgumentException(
            "Direction must be either asc or desc"
    );
}

public Page<ProductResponse> getActiveProducts(
        int page,
        int size,
        String sortBy,
        String direction) {

    validatePagination(page, size);

    Sort.Direction sortDirection = getSortDirection(direction);

    String validatedSortBy = validateSortField(sortBy);

Pageable pageable = PageRequest.of(
        page,
        size,
        Sort.by(sortDirection, validatedSortBy)
);

    Page<Product> productPage =
            productRepository.findByActiveTrue(pageable);

    return productPage.map(this::mapToResponse);
}

public ProductResponse updateProductStatus(Long id, Boolean active) {

    Product existingProduct = productRepository.findById(id)
            .orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Product not found with ID: " + id
                    ));

    existingProduct.setActive(active);

    Product updatedProduct = productRepository.save(existingProduct);

    return mapToResponse(updatedProduct);
}

private String validateSortField(String sortBy) {

    List<String> allowedFields = List.of(
            "id",
            "name",
            "price",
            "stockQuantity",
            "createdAt",
            "updatedAt"
    );

    if (!allowedFields.contains(sortBy)) {
        throw new IllegalArgumentException(
                "Invalid sort field. Allowed fields: " + allowedFields
        );
    }

    return sortBy;
}

public List<Product> sortProducts(String sortBy, String direction) {

    Sort.Direction sortDirection =
            direction.equalsIgnoreCase("desc")
                    ? Sort.Direction.DESC
                    : Sort.Direction.ASC;

    return productRepository.findAll(
            Sort.by(sortDirection, sortBy)
    );
}

public Page<Product> getProductsPage(int page, int size) {

    Pageable pageable = PageRequest.of(page, size);

    return productRepository.findAll(pageable);
}

public Page<Product> filterProducts(
        String keyword,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        int page,
        int size,
        String sortBy,
        String direction) {

    Sort.Direction sortDirection =
            direction.equalsIgnoreCase("desc")
                    ? Sort.Direction.DESC
                    : Sort.Direction.ASC;

    Pageable pageable =
            PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

    Specification<Product> specification =
            Specification.where(ProductSpecification.hasKeyword(keyword))
                    .and(ProductSpecification.hasMinPrice(minPrice))
                    .and(ProductSpecification.hasMaxPrice(maxPrice));

    return productRepository.findAll(specification, pageable);
}

public List<ProductResponse> getProductsByCategory(Long categoryId) {

    return productRepository
            .findByCategoryIdAndActiveTrue(categoryId)
            .stream()
            .map(this::mapToResponse)
            .toList();
}

public Page<ProductResponse> getProductsByCategory(
        Long categoryId,
        int page,
        int size,
        String sortBy,
        String direction) {

    validatePagination(page, size);

    Sort.Direction sortDirection = getSortDirection(direction);

    String validatedSortBy = validateSortField(sortBy);

    Pageable pageable = PageRequest.of(
            page,
            size,
            Sort.by(sortDirection, validatedSortBy)
    );

    Page<Product> productPage =
            productRepository.findByCategoryIdAndActiveTrue(
                    categoryId,
                    pageable
            );

    return productPage.map(this::mapToResponse);
}

}