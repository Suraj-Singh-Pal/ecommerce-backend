package com.suraj.ecommerce.controller;

import com.suraj.ecommerce.dto.ApiResponse;
import com.suraj.ecommerce.dto.ProductRequest;
import com.suraj.ecommerce.dto.ProductResponse;
import com.suraj.ecommerce.service.ProductService;
import com.suraj.ecommerce.entity.Product;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.math.BigDecimal;


@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @Valid @RequestBody ProductRequest request) {

        ProductResponse response = productService.createProduct(request);

        return new ResponseEntity<>(
                ApiResponse.success("Product created successfully", response),
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getAllProducts() {

        List<ProductResponse> products = productService.getAllProducts();

        return ResponseEntity.ok(
                ApiResponse.success("Products fetched successfully", products)
        );
    }

    @GetMapping("/paged")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getProductsWithPagination(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        Page<ProductResponse> products = productService.getProductsWithPagination(
                page, size, sortBy, direction
        );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Products fetched successfully with pagination",
                        products
                )
        );
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> searchProducts(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        Page<ProductResponse> products = productService.searchProducts(
                name, page, size, sortBy, direction
        );

        return ResponseEntity.ok(
                ApiResponse.success("Products searched successfully", products)
        );
    }

    @GetMapping("/filter/price")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> filterProductsByPrice(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "price") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        Page<ProductResponse> products = productService.filterProductsByPrice(
                minPrice, maxPrice, page, size, sortBy, direction
        );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Products filtered by price successfully",
                        products
                )
        );
    }

    @GetMapping("/filter/in-stock")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getInStockProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "stockQuantity") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        Page<ProductResponse> products = productService.getInStockProducts(
                page, size, sortBy, direction
        );

        return ResponseEntity.ok(
                ApiResponse.success("In-stock products fetched successfully", products)
        );
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getActiveProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        Page<ProductResponse> products = productService.getActiveProducts(
                page, size, sortBy, direction
        );

        return ResponseEntity.ok(
                ApiResponse.success("Active products fetched successfully", products)
        );
    }

    @GetMapping("/sort")
public ResponseEntity<ApiResponse<List<Product>>> sortProducts(
        @RequestParam(defaultValue = "id") String sortBy,
        @RequestParam(defaultValue = "asc") String direction) {

    List<Product> products =
            productService.sortProducts(sortBy, direction);

    ApiResponse<List<Product>> response = new ApiResponse<>();
    response.setSuccess(true);
    response.setMessage("Products sorted successfully");
    response.setData(products);

    return ResponseEntity.ok(response);
}

@GetMapping("/page")
public ResponseEntity<ApiResponse<Page<Product>>> getProductsPage(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int size) {

    Page<Product> products =
            productService.getProductsPage(page, size);

    ApiResponse<Page<Product>> response = new ApiResponse<>();
    response.setSuccess(true);
    response.setMessage("Products fetched successfully");
    response.setData(products);

    return ResponseEntity.ok(response);
}

@GetMapping("/filter")
public ResponseEntity<ApiResponse<Page<Product>>> filterProducts(
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) BigDecimal minPrice,
        @RequestParam(required = false) BigDecimal maxPrice,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int size,
        @RequestParam(defaultValue = "id") String sortBy,
        @RequestParam(defaultValue = "asc") String direction) {

    Page<Product> products = productService.filterProducts(
            keyword,
            minPrice,
            maxPrice,
            page,
            size,
            sortBy,
            direction
    );

    ApiResponse<Page<Product>> response = new ApiResponse<>();
    response.setSuccess(true);
    response.setMessage("Products filtered successfully");
    response.setData(products);

    return ResponseEntity.ok(response);
}

@GetMapping("/category/{categoryId}")
public ResponseEntity<ApiResponse<Page<ProductResponse>>> getProductsByCategory(
        @PathVariable Long categoryId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int size,
        @RequestParam(defaultValue = "id") String sortBy,
        @RequestParam(defaultValue = "asc") String direction) {

    Page<ProductResponse> products =
            productService.getProductsByCategory(
                    categoryId,
                    page,
                    size,
                    sortBy,
                    direction
            );

    ApiResponse<Page<ProductResponse>> response = new ApiResponse<>();
    response.setSuccess(true);
    response.setMessage("Products fetched by category successfully");
    response.setData(products);

    return ResponseEntity.ok(response);
}

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(
            @PathVariable Long id) {

        ProductResponse response = productService.getProductById(id);

        return ResponseEntity.ok(
                ApiResponse.success("Product fetched successfully", response)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {

        ProductResponse response = productService.updateProduct(id, request);

        return ResponseEntity.ok(
                ApiResponse.success("Product updated successfully", response)
        );
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProductStatus(
            @PathVariable Long id,
            @RequestParam Boolean active) {

        ProductResponse response = productService.updateProductStatus(id, active);

        return ResponseEntity.ok(
                ApiResponse.success("Product status updated successfully", response)
        );
    }

    @DeleteMapping("/{id}")
public ResponseEntity<ApiResponse<Void>> deleteProduct(
        @PathVariable Long id) {

    productService.deleteProduct(id);

    return ResponseEntity.ok(
            ApiResponse.success("Product deleted successfully", null)
    );
}

}