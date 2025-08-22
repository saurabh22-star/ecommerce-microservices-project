package com.ecommerce.ecommercehub.productmodule.services;

import java.util.List;

import com.ecommerce.ecommercehub.productmodule.models.Category;

/**
 *
 * @author Saurabh
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.ecommercehub.productmodule.exceptions.DuplicateResourceException;
import com.ecommerce.ecommercehub.productmodule.repositories.CategoryRepo;
import com.ecommerce.ecommercehub.utility.exceptions.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepo categoryRepo;

    @Override
    public List<Category> getAllCategories() {
        return categoryRepo.findAll();
    }

    @Override
    public Category createCategory(Category category){
        if (categoryRepo.existsByTitle(category.getTitle())) {
            throw new DuplicateResourceException("Category with title '" + category.getTitle() + "' already exists.");
        }
        return categoryRepo.save(category);
    }

    @Override
    public Category getCategoryById(Long categoryId) {
        return categoryRepo.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));
    }

    @Override
    public Category getCategoryByTitle(String title) {
        Category category = categoryRepo.findByTitle(title);
        if (category == null) {
            throw new ResourceNotFoundException("Category not found with title: " + title);
        }
        return category;
    }

    @Override
    public void deleteCategoryById(Long categoryId) {
        if (!categoryRepo.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category not found with id: " + categoryId);
        }
        categoryRepo.deleteById(categoryId);
    }

    @Override
    public Category updateCategory(Category category, Long categoryId) {
        if (!categoryRepo.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category not found with id: " + categoryId);
        }
        category.setId(categoryId);
        return categoryRepo.save(category);
    }
    
    }
