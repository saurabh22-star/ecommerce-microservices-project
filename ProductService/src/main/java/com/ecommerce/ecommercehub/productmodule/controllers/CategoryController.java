package com.ecommerce.ecommercehub.productmodule.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import com.ecommerce.ecommercehub.utility.dtos.CommonApiResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Saurabh
 */

import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.http.ResponseEntity.status;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.ecommercehub.productmodule.config.AppConstants;
import com.ecommerce.ecommercehub.productmodule.dtos.CategoryDTO;
import com.ecommerce.ecommercehub.productmodule.dtos.CategoryResponseDTO;
import com.ecommerce.ecommercehub.productmodule.entities.Category;
import com.ecommerce.ecommercehub.productmodule.exceptions.APIException;
import com.ecommerce.ecommercehub.productmodule.exceptions.DuplicateResourceException;
import com.ecommerce.ecommercehub.productmodule.services.CategoryService;
import com.ecommerce.ecommercehub.utility.exceptions.ResourceNotFoundException;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "E-Commerce Application")
public class CategoryController {

    private CategoryService categoryService;

    private static final Logger log = LoggerFactory.getLogger(CategoryController.class);


    @GetMapping("/public/categories")
	public ResponseEntity<CategoryResponseDTO> fetchCategories(
			@RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
			@RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
			@RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_CATEGORIES_BY, required = false) String sortBy,
			@RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder) {
		log.info("fetchCategories");
		CategoryResponseDTO categoryResponseDTO = categoryService.fetchCategories(pageNumber, pageSize, sortBy, sortOrder);
		log.info("categoryResponseDTO", categoryResponseDTO);
		return new ResponseEntity<CategoryResponseDTO>(categoryResponseDTO, HttpStatus.FOUND);
	}

    @PostMapping("/admin/category")
    public ResponseEntity<CategoryDTO> addCategory(@Valid @RequestBody Category newCategory) {
        log.info("addCategory called");
        CategoryDTO createdCategory = categoryService.addCategory(newCategory);
        log.info("createdCategory",createdCategory);
        return new ResponseEntity<>(createdCategory, HttpStatus.CREATED);
    }
/* 
    @GetMapping("/all")
    public ResponseEntity<CommonApiResponse> fetchAllCategories() {
        try {
            List<Category> categoryList = categoryService.getAllCategories();
            return ResponseEntity.ok(new CommonApiResponse("All Categories retrieved successfully.", categoryList));
        } catch (Exception ex) {
            return status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CommonApiResponse("Unable to retrieve categories.", ex.getMessage()));
        }
    }

    @PostMapping("/create")
    public ResponseEntity<CommonApiResponse> createCategory(@RequestBody Category request) {
        try {
            Category savedCategory = categoryService.createCategory(request);
            return ResponseEntity.ok(
                    new CommonApiResponse("Category created successfully", savedCategory)
            );
        } catch (DuplicateResourceException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new CommonApiResponse(ex.getMessage(), null));
        }
    }

    @PutMapping("/{categoryId}/update")
public ResponseEntity<CommonApiResponse> updateCategory(@PathVariable Long categoryId, @RequestBody Category category) {
    try {
        Category updatedCategory = categoryService.updateCategory(category, categoryId);
        return ResponseEntity.ok(new CommonApiResponse("Category updated successfully.", updatedCategory));
    } catch (ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new CommonApiResponse(ex.getMessage(), null));
    }
}

    @GetMapping("/{categoryId}")
public ResponseEntity<CommonApiResponse> fetchCategoryById(@PathVariable Long categoryId) {
    try {
        Category category = categoryService.getCategoryById(categoryId);
        return ResponseEntity.ok(new CommonApiResponse("Category retrieved successfully.", category));
    } catch (ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new CommonApiResponse(ex.getMessage(), null));
    }
}



    
    @GetMapping("/{title}")
    public ResponseEntity<CommonApiResponse> fetchCategoryByTitle(@PathVariable String title) {
      try {
          Category category = categoryService.getCategoryByTitle(title);
          return ResponseEntity.ok(new CommonApiResponse("Category retrieved successfully.", category));
      } catch (ResourceNotFoundException ex) {
          return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new CommonApiResponse(ex.getMessage(), null));
     }
}

    
   @DeleteMapping("/{categoryId}/delete")
   public ResponseEntity<CommonApiResponse> deleteCategory(@PathVariable Long categoryId) {
    try {
        categoryService.deleteCategoryById(categoryId);
        return ResponseEntity.ok(new CommonApiResponse("Category deleted successfully.", null));
    } catch (ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new CommonApiResponse(ex.getMessage(), null));
    }
}


     */
   

}
