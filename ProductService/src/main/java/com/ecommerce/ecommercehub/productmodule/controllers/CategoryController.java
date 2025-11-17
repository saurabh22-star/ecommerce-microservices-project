package com.ecommerce.ecommercehub.productmodule.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Saurabh
 */

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
import com.ecommerce.ecommercehub.productmodule.services.CategoryService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

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

    
    @PutMapping("/admin/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> modifyCategory(@RequestBody Category updatedCategory,
                                                    @PathVariable Long categoryId) {
        log.info("modifyCategory called");
        CategoryDTO updatedDTO = categoryService.modifyCategory(updatedCategory, categoryId);
        log.info("updatedDTO", updatedDTO);
        return new ResponseEntity<>(updatedDTO, HttpStatus.OK);
    }

    @DeleteMapping("/admin/categories/{categoryId}")
    public ResponseEntity<String> removeCategory(@PathVariable Long id) {
        log.info("removeCategory called");
        String result = categoryService.removeCategory(id);
        log.info(result);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
   

}
