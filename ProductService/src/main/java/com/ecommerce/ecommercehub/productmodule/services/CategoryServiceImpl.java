package com.ecommerce.ecommercehub.productmodule.services;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;

/**
 *
 * @author Saurabh
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.ecommerce.ecommercehub.productmodule.dtos.CategoryDTO;
import com.ecommerce.ecommercehub.productmodule.dtos.CategoryResponseDTO;
import com.ecommerce.ecommercehub.productmodule.entities.Category;
import com.ecommerce.ecommercehub.productmodule.exceptions.APIException;
import com.ecommerce.ecommercehub.productmodule.exceptions.DuplicateResourceException;
import com.ecommerce.ecommercehub.productmodule.repositories.CategoryRepo;
import com.ecommerce.ecommercehub.utility.exceptions.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepo categoryRepo;

    @Autowired
	private ModelMapper modelMapper;


    @Override
    public CategoryResponseDTO fetchCategories(Integer pageIdx, Integer size, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
				: Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageIdx, size, sortByAndOrder);

        Page<Category> categoryPage = categoryRepo.findAll(pageable);
        List<Category> categoryList = categoryPage.getContent();

        if (categoryList.isEmpty()) {
            throw new APIException("No categories found in the system.");
        }

        List<CategoryDTO> dtoList = categoryList.stream()
                .map(cat -> modelMapper.map(cat, CategoryDTO.class))
                .collect(Collectors.toList());

        CategoryResponseDTO response = new CategoryResponseDTO();
        response.setContent(dtoList);
        response.setPageNumber(categoryPage.getNumber());
        response.setPageSize(categoryPage.getSize());
        response.setTotalElements(categoryPage.getTotalElements());
        response.setTotalPages(categoryPage.getTotalPages());
        response.setLastPage(categoryPage.isLast());

        return response;
    }

    @Override
    public CategoryDTO addCategory(Category newCategory) {
        Category existing = categoryRepo.findByCategoryName(newCategory.getCategoryName());

        if (existing != null) {
            throw new APIException("A category named '" + newCategory.getCategoryName() + "' already exists.");
        }

        Category persisted = categoryRepo.save(newCategory);

        return modelMapper.map(persisted, CategoryDTO.class);
    }

    @Override
    public CategoryDTO updateCategory(Category category, Long categoryId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateCategory'");
    }

    @Override
    public String deleteCategory(Long categoryId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteCategory'");
    }


/* 
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
    } */
    
    }
