/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */

package com.ecommerce.ecommercehub.productmodule.services;

import java.util.List;

import com.ecommerce.ecommercehub.productmodule.entities.Category;

/**
 *
 * @author saurabh
 */
public interface CategoryService {

    public List<Category> getAllCategories();

    public Category createCategory(Category category);

    public Category getCategoryById(Long categoryId);

    public Category getCategoryByTitle(String title);

    public void deleteCategoryById(Long categoryId);

    public Category updateCategory(Category category, Long categoryId);

}
