package com.suraj.ecommerce.controller;

import com.suraj.ecommerce.dto.ApiResponse;
import com.suraj.ecommerce.dto.CategoryRequest;
import com.suraj.ecommerce.entity.Category;
import com.suraj.ecommerce.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Category>> createCategory(
            @Valid @RequestBody CategoryRequest request) {

        Category category = categoryService.createCategory(request);

        ApiResponse<Category> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage("Category created successfully");
        response.setData(category);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Category>>> getAllCategories() {

        List<Category> categories = categoryService.getAllCategories();

        ApiResponse<List<Category>> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage("Categories fetched successfully");
        response.setData(categories);

        return ResponseEntity.ok(response);
    }

@GetMapping("/{id}")
public ResponseEntity<ApiResponse<Category>> getCategoryById(
        @PathVariable Long id) {

    Category category = categoryService.getCategoryById(id);

    ApiResponse<Category> response = new ApiResponse<>();
    response.setSuccess(true);
    response.setMessage("Category fetched successfully");
    response.setData(category);

    return ResponseEntity.ok(response);
}

@PutMapping("/{id}")
public ResponseEntity<ApiResponse<Category>> updateCategory(
        @PathVariable Long id,
        @Valid @RequestBody CategoryRequest request) {

    Category category = categoryService.updateCategory(id, request);

    ApiResponse<Category> response = new ApiResponse<>();
    response.setSuccess(true);
    response.setMessage("Category updated successfully");
    response.setData(category);

    return ResponseEntity.ok(response);
}

@DeleteMapping("/{id}")
public ResponseEntity<ApiResponse<Void>> deleteCategory(
        @PathVariable Long id) {

    categoryService.deleteCategory(id);

    ApiResponse<Void> response = new ApiResponse<>();
    response.setSuccess(true);
    response.setMessage("Category deleted successfully");
    response.setData(null);

    return ResponseEntity.ok(response);
}

}
