package com.ecommerce.product.service;

import com.ecommerce.common.exceptions.BadRequestException;
import com.ecommerce.common.exceptions.ResourceNotFoundException;
import com.ecommerce.product.dto.ReviewDto;
import com.ecommerce.product.model.Product;
import com.ecommerce.product.model.Review;
import com.ecommerce.product.repository.ProductRepository;
import com.ecommerce.product.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {
    
    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    
    @Transactional
    public ReviewDto.ReviewResponse createReview(Long userId, ReviewDto.CreateReviewRequest request) {
        log.info("Creating review for product {} by user {}", request.getProductId(), userId);
        
        // Check if product exists
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", request.getProductId()));
        
        // Check if user already reviewed this product
        if (reviewRepository.findByProductIdAndUserId(request.getProductId(), userId).isPresent()) {
            throw new BadRequestException("You have already reviewed this product");
        }
        
        Review review = Review.builder()
                .product(product)
                .userId(userId)
                .rating(request.getRating())
                .title(request.getTitle())
                .comment(request.getComment())
                .verifiedPurchase(false) // TODO: Set based on order history
                .build();
        
        review = reviewRepository.save(review);
        
        // Update product rating
        product.updateRating();
        productRepository.save(product);
        
        log.info("Review created successfully with ID: {}", review.getId());
        return mapToReviewResponse(review);
    }
    
    public Page<ReviewDto.ReviewResponse> getProductReviews(Long productId, Pageable pageable) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product", "id", productId);
        }
        return reviewRepository.findByProductId(productId, pageable)
                .map(this::mapToReviewResponse);
    }
    
    @Transactional
    public ReviewDto.ReviewResponse updateReview(Long reviewId, Long userId, ReviewDto.UpdateReviewRequest request) {
        log.info("Updating review {} by user {}", reviewId, userId);
        
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", reviewId));
        
        if (!review.getUserId().equals(userId)) {
            throw new BadRequestException("You can only update your own reviews");
        }
        
        if (request.getRating() != null) {
            review.setRating(request.getRating());
        }
        if (request.getTitle() != null) {
            review.setTitle(request.getTitle());
        }
        if (request.getComment() != null) {
            review.setComment(request.getComment());
        }
        
        review = reviewRepository.save(review);
        
        // Update product rating
        Product product = review.getProduct();
        product.updateRating();
        productRepository.save(product);
        
        log.info("Review updated successfully: {}", reviewId);
        return mapToReviewResponse(review);
    }
    
    @Transactional
    public void deleteReview(Long reviewId, Long userId) {
        log.info("Deleting review {} by user {}", reviewId, userId);
        
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", reviewId));
        
        if (!review.getUserId().equals(userId)) {
            throw new BadRequestException("You can only delete your own reviews");
        }
        
        Product product = review.getProduct();
        reviewRepository.deleteById(reviewId);
        
        // Update product rating after deletion
        product.updateRating();
        productRepository.save(product);
        
        log.info("Review deleted successfully: {}", reviewId);
    }
    
    private ReviewDto.ReviewResponse mapToReviewResponse(Review review) {
        return ReviewDto.ReviewResponse.builder()
                .id(review.getId())
                .productId(review.getProduct().getId())
                .productName(review.getProduct().getName())
                .userId(review.getUserId())
                .rating(review.getRating())
                .title(review.getTitle())
                .comment(review.getComment())
                .verifiedPurchase(review.getVerifiedPurchase())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }
}
