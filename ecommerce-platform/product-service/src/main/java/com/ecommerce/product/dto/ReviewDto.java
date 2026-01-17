package com.ecommerce.product.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class ReviewDto {
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateReviewRequest {
        @NotNull(message = "Product ID is required")
        private Long productId;
        
        @Min(value = 1, message = "Rating must be at least 1")
        @Max(value = 5, message = "Rating must be at most 5")
        private Integer rating;
        
        @NotBlank(message = "Title is required")
        @Size(max = 100, message = "Title must not exceed 100 characters")
        private String title;
        
        @Size(max = 1000, message = "Comment must not exceed 1000 characters")
        private String comment;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateReviewRequest {
        @Min(value = 1, message = "Rating must be at least 1")
        @Max(value = 5, message = "Rating must be at most 5")
        private Integer rating;
        
        @Size(max = 100, message = "Title must not exceed 100 characters")
        private String title;
        
        @Size(max = 1000, message = "Comment must not exceed 1000 characters")
        private String comment;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReviewResponse {
        private Long id;
        private Long productId;
        private String productName;
        private Long userId;
        private String userFirstName;
        private String userLastName;
        private Integer rating;
        private String title;
        private String comment;
        private Boolean verifiedPurchase;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
}
