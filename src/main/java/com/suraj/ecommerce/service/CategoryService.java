package com.suraj.ecommerce.service;

import com.suraj.ecommerce.dto.CategoryRequest;
import com.suraj.ecommerce.entity.Category;
import com.suraj.ecommerce.exception.ResourceNotFoundException;
import com.suraj.ecommerce.repository.CategoryRepository;
import com.suraj.ecommerce.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public CategoryService(
        CategoryRepository categoryRepository,
        ProductRepository productRepository) {

    this.categoryRepository = categoryRepository;
    this.productRepository = productRepository;
}

    public Category createCategory(CategoryRequest request) {

        if (categoryRepository.existsByNameIgnoreCase(request.getName())) {
            throw new IllegalArgumentException(
                    "Category with this name already exists"
            );
        }

        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .active(true)
                .build();

        return categoryRepository.save(category);
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll()
        .stream()
        .filter(category -> Boolean.TRUE.equals(category.getActive()))
        .toList();
    }

    public Category getCategoryById(Long id) {

    return categoryRepository.findByIdAndActiveTrue(id)
            .orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Category not found with ID: " + id
                    ));
}

public Category updateCategory(Long id, CategoryRequest request) {

    Category category = categoryRepository.findByIdAndActiveTrue(id)
            .orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Category not found with ID: " + id
                    ));

    if (categoryRepository.existsByNameIgnoreCase(request.getName())
            && !category.getName().equalsIgnoreCase(request.getName())) {

        throw new IllegalArgumentException(
                "Category with this name already exists"
        );
    }

    category.setName(request.getName());
    category.setDescription(request.getDescription());

    return categoryRepository.save(category);
}

public void deleteCategory(Long id) {

    Category category = categoryRepository.findByIdAndActiveTrue(id)
            .orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Category not found with ID: " + id
                    ));

if (productRepository.existsByCategoryIdAndActiveTrue(id)) {
    throw new IllegalArgumentException(
            "Cannot delete category because active products are associated with it"
    );
}

    category.setActive(false);

    categoryRepository.save(category);
}

}