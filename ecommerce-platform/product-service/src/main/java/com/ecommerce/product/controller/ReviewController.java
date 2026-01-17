package com.ecommerce.product.controller;

import com.ecommerce.common.dto.ApiResponse;
import com.ecommerce.product.dto.ReviewDto;
import com.ecommerce.product.service.ReviewService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Tag(name = "Reviews", description = "Product review APIs")
public class ReviewController {
    
    private final ReviewService reviewService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<ReviewDto.ReviewResponse>> createReview(
            Authentication authentication,
            @Valid @RequestBody ReviewDto.CreateReviewRequest request) {
        Long userId = Long.parseLong(authentication.getName());
        ReviewDto.ReviewResponse review = reviewService.createReview(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Review created successfully", review));
    }
    
    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<Page<ReviewDto.ReviewResponse>>> getProductReviews(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<ReviewDto.ReviewResponse> reviews = reviewService.getProductReviews(productId, PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success(reviews));
    }
    
    @PutMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<ReviewDto.ReviewResponse>> updateReview(
            @PathVariable Long reviewId,
            Authentication authentication,
            @Valid @RequestBody ReviewDto.UpdateReviewRequest request) {
        Long userId = Long.parseLong(authentication.getName());
        ReviewDto.ReviewResponse review = reviewService.updateReview(reviewId, userId, request);
        return ResponseEntity.ok(ApiResponse.success("Review updated successfully", review));
    }
    
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(
            @PathVariable Long reviewId,
            Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        reviewService.deleteReview(reviewId, userId);
        return ResponseEntity.ok(ApiResponse.success("Review deleted successfully", null));
    }
}
