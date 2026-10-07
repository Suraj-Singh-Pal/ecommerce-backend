package com.suraj.ecommerce.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class ProductRequest {

@NotNull(message = "Category is required")
private Long categoryId;

    @NotBlank(message = "Product name is required")
    @Size(
            min = 3,
            max = 150,
            message = "Product name must be between 3 and 150 characters"
    )
    private String name;

    @Size(
            max = 1000,
            message = "Description cannot exceed 1000 characters"
    )
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(
            value = "0.01",
            message = "Price must be greater than 0"
    )
    private BigDecimal price;

    @NotNull(message = "Stock quantity is required")
    @Min(
            value = 0,
            message = "Stock quantity cannot be negative"
    )
    private Integer stockQuantity;

    @Pattern(
            regexp = "^$|^(https?://).+",
            message = "Image URL must start with http:// or https://"
    )
    private String imageUrl;

    public ProductRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Long getCategoryId() {
    return categoryId;
}

public void setCategoryId(Long categoryId) {
    this.categoryId = categoryId;
}
}