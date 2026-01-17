package com.ecommerce.product.service;

import com.ecommerce.common.exceptions.BadRequestException;
import com.ecommerce.common.exceptions.ResourceNotFoundException;
import com.ecommerce.product.dto.ProductDto;
import com.ecommerce.product.model.Category;
import com.ecommerce.product.model.Product;
import com.ecommerce.product.model.ProductImage;
import com.ecommerce.product.repository.CategoryRepository;
import com.ecommerce.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    
    @Transactional
    public ProductDto.ProductResponse createProduct(ProductDto.CreateProductRequest request) {
        log.info("Creating new product: {}", request.getName());
        
        // Check if SKU already exists
        if (productRepository.findBySku(request.getSku()).isPresent()) {
            throw new BadRequestException("Product with SKU " + request.getSku() + " already exists");
        }
        
        // Verify category exists
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));
        
        // Create product
        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .category(category)
                .stockQuantity(request.getStockQuantity())
                .sku(request.getSku())
                .build();
        
        // Add images if provided
        if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            List<ProductImage> images = new ArrayList<>();
            for (int i = 0; i < request.getImageUrls().size(); i++) {
                ProductImage image = ProductImage.builder()
                        .product(product)
                        .imageUrl(request.getImageUrls().get(i))
                        .isPrimary(i == 0)
                        .displayOrder(i)
                        .build();
                images.add(image);
            }
            product.setImages(images);
        }
        
        product = productRepository.save(product);
        log.info("Product created successfully with ID: {}", product.getId());
        
        return mapToProductResponse(product);
    }
    
    public ProductDto.ProductResponse getProductById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        return mapToProductResponse(product);
    }
    
    public Page<ProductDto.ProductResponse> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(this::mapToProductResponse);
    }
    
    public Page<ProductDto.ProductResponse> searchProducts(String keyword, Pageable pageable) {
        return productRepository.searchProducts(keyword, pageable)
                .map(this::mapToProductResponse);
    }
    
    public Page<ProductDto.ProductResponse> getProductsByCategory(Long categoryId, Pageable pageable) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category", "id", categoryId);
        }
        return productRepository.findByCategoryId(categoryId, pageable)
                .map(this::mapToProductResponse);
    }
    
    @Transactional
    public ProductDto.ProductResponse updateProduct(Long productId, ProductDto.UpdateProductRequest request) {
        log.info("Updating product with ID: {}", productId);
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        
        if (request.getName() != null) {
            product.setName(request.getName());
        }
        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }
        if (request.getPrice() != null) {
            product.setPrice(request.getPrice());
        }
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));
            product.setCategory(category);
        }
        if (request.getStockQuantity() != null) {
            product.setStockQuantity(request.getStockQuantity());
        }
        
        product = productRepository.save(product);
        log.info("Product updated successfully: {}", productId);
        
        return mapToProductResponse(product);
    }
    
    @Transactional
    public void deleteProduct(Long productId) {
        log.info("Deleting product with ID: {}", productId);
        
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product", "id", productId);
        }
        
        productRepository.deleteById(productId);
        log.info("Product deleted successfully: {}", productId);
    }
    
    @Transactional
    public ProductDto.ProductResponse updateStock(Long productId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        
        product.setStockQuantity(quantity);
        product = productRepository.save(product);
        
        log.info("Updated stock for product {}: new quantity = {}", productId, quantity);
        return mapToProductResponse(product);
    }
    
    private ProductDto.ProductResponse mapToProductResponse(Product product) {
        List<ProductDto.ProductImageDto> imageDtos = new ArrayList<>();
        if (product.getImages() != null) {
            imageDtos = product.getImages().stream()
                    .map(img -> ProductDto.ProductImageDto.builder()
                            .id(img.getId())
                            .imageUrl(img.getImageUrl())
                            .isPrimary(img.getIsPrimary())
                            .displayOrder(img.getDisplayOrder())
                            .build())
                    .collect(Collectors.toList());
        }
        
        return ProductDto.ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .stockQuantity(product.getStockQuantity())
                .sku(product.getSku())
                .averageRating(product.getAverageRating())
                .totalReviews(product.getTotalReviews())
                .images(imageDtos)
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}
