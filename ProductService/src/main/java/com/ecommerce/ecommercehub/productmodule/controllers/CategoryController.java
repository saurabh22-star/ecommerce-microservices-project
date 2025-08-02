package com.ecommerce.ecommercehub.productmodule.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.springframework.http.ResponseEntity.status;
import org.springframework.web.bind.annotation.GetMapping;

import com.ecommerce.ecommercehub.productmodule.models.Category;
import com.ecommerce.ecommercehub.utility.dtos.CommonApiResponse;


/**
 *
 * @author Saurabh
 */

import org.springframework.beans.factory.annotation.Autowired;

import com.ecommerce.ecommercehub.productmodule.services.CategoryService;

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

}
