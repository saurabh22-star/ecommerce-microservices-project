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
import com.ecommerce.ecommercehub.productmodule.entities.Product;
import com.ecommerce.ecommercehub.productmodule.exceptions.APIException;
import com.ecommerce.ecommercehub.productmodule.exceptions.ResourceNotFoundException;
import com.ecommerce.ecommercehub.productmodule.repositories.CategoryRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepo categoryRepo;

    @Autowired
	private ModelMapper modelMapper;

    @Autowired
	private ProductService productService;


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
    public CategoryDTO modifyCategory(Category updatedCategory, Long id) {
        categoryRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Category not found", "id", id));

        updatedCategory.setCategoryId(id);

        Category persistedCategory = categoryRepo.save(updatedCategory);

        return modelMapper.map(persistedCategory, CategoryDTO.class);
    }

    @Override
    public String removeCategory(Long id) {
        Category foundCategory = categoryRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Category not found", "id", id));

        List<Product> relatedProducts = foundCategory.getProductList();

        relatedProducts.forEach(prod -> productService.deleteProduct(prod.getProductId()));

        categoryRepo.delete(foundCategory);

        return "Category with id: " + id + " has been removed successfully.";
    }

    
    }
