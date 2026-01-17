package com.ecommerce.product.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ProductDto {
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateProductRequest {
        @NotBlank(message = "Product name is required")
        private String name;
        
        private String description;
        
        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
        private BigDecimal price;
        
        @NotNull(message = "Category ID is required")
        private Long categoryId;
        
        @Min(value = 0, message = "Stock quantity cannot be negative")
        private Integer stockQuantity;
        
        @NotBlank(message = "SKU is required")
        private String sku;
        
        private List<String> imageUrls;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateProductRequest {
        private String name;
        private String description;
        
        @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
        private BigDecimal price;
        
        private Long categoryId;
        
        @Min(value = 0, message = "Stock quantity cannot be negative")
        private Integer stockQuantity;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductResponse {
        private Long id;
        private String name;
        private String description;
        private BigDecimal price;
        private String categoryName;
        private Long categoryId;
        private Integer stockQuantity;
        private String sku;
        private BigDecimal averageRating;
        private Integer totalReviews;
        private List<ProductImageDto> images;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductImageDto {
        private Long id;
        private String imageUrl;
        private Boolean isPrimary;
        private Integer displayOrder;
    }
}
