/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */

package com.ecommerce.ecommercehub.productmodule.services;

import java.util.List;

import com.ecommerce.ecommercehub.productmodule.dtos.CategoryDTO;
import com.ecommerce.ecommercehub.productmodule.dtos.CategoryResponseDTO;
import com.ecommerce.ecommercehub.productmodule.entities.Category;

/**
 *
 * @author saurabh
 */
public interface CategoryService {

    CategoryDTO addCategory(Category category);

	CategoryResponseDTO fetchCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

	CategoryDTO updateCategory(Category category, Long categoryId);

	String deleteCategory(Long categoryId);

}
