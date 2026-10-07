package com.suraj.ecommerce.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class WishlistResponse {

    private Long id;
    private Long productId;
    private String productName;
    private BigDecimal price;
    private String imageUrl;
    private LocalDateTime addedAt;

    public WishlistResponse() {
    }

    public WishlistResponse(
            Long id,
            Long productId,
            String productName,
            BigDecimal price,
            String imageUrl,
            LocalDateTime addedAt
    ) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.imageUrl = imageUrl;
        this.addedAt = addedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public LocalDateTime getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(LocalDateTime addedAt) {
        this.addedAt = addedAt;
    }
}