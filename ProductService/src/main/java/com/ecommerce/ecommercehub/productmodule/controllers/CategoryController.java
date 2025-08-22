package com.ecommerce.ecommercehub.productmodule.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import com.ecommerce.ecommercehub.productmodule.models.Category;
import com.ecommerce.ecommercehub.utility.dtos.CommonApiResponse;


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
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.ecommercehub.productmodule.exceptions.DuplicateResourceException;
import com.ecommerce.ecommercehub.productmodule.services.CategoryService;
import com.ecommerce.ecommercehub.utility.exceptions.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/category")
public class CategoryController {

    private CategoryService categoryService;

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


    
   

}
