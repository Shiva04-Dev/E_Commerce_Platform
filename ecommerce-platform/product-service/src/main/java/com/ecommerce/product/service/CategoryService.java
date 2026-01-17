package com.ecommerce.product.service;

import com.ecommerce.common.exceptions.BadRequestException;
import com.ecommerce.common.exceptions.ResourceNotFoundException;
import com.ecommerce.product.dto.CategoryDto;
import com.ecommerce.product.model.Category;
import com.ecommerce.product.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {
    
    private final CategoryRepository categoryRepository;
    
    @Transactional
    public CategoryDto.CategoryResponse createCategory(CategoryDto.CreateCategoryRequest request) {
        log.info("Creating new category: {}", request.getName());
        
        if (categoryRepository.findByName(request.getName()).isPresent()) {
            throw new BadRequestException("Category with name '" + request.getName() + "' already exists");
        }
        
        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
        
        if (request.getParentCategoryId() != null) {
            Category parentCategory = categoryRepository.findById(request.getParentCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent Category", "id", request.getParentCategoryId()));
            category.setParentCategory(parentCategory);
        }
        
        category = categoryRepository.save(category);
        log.info("Category created successfully with ID: {}", category.getId());
        
        return mapToCategoryResponse(category);
    }
    
    public CategoryDto.CategoryResponse getCategoryById(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));
        return mapToCategoryResponse(category);
    }
    
    public List<CategoryDto.CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::mapToCategoryResponse)
                .collect(Collectors.toList());
    }
    
    public List<CategoryDto.CategoryResponse> getRootCategories() {
        return categoryRepository.findByParentCategoryIsNull().stream()
                .map(this::mapToCategoryResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public void deleteCategory(Long categoryId) {
        log.info("Deleting category with ID: {}", categoryId);
        
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));
        
        if (category.getProducts() != null && !category.getProducts().isEmpty()) {
            throw new BadRequestException("Cannot delete category with existing products");
        }
        
        categoryRepository.deleteById(categoryId);
        log.info("Category deleted successfully: {}", categoryId);
    }
    
    private CategoryDto.CategoryResponse mapToCategoryResponse(Category category) {
        return CategoryDto.CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .parentCategoryId(category.getParentCategory() != null ? category.getParentCategory().getId() : null)
                .parentCategoryName(category.getParentCategory() != null ? category.getParentCategory().getName() : null)
                .productCount(category.getProducts() != null ? category.getProducts().size() : 0)
                .createdAt(category.getCreatedAt())
                .build();
    }
}
