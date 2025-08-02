package com.ecommerce.ecommercehub.productmodule.services;

import java.util.List;

import com.ecommerce.ecommercehub.productmodule.models.Category;

/**
 *
 * @author Saurabh
 */

import org.springframework.beans.factory.annotation.Autowired;

import com.ecommerce.ecommercehub.productmodule.repositories.CategoryRepo;

public class CategoryServiceImpl implements CategoryService {

    private CategoryRepo categoryRepo;

    @Override
    public List<Category> getAllCategories() {
        return categoryRepo.findAll();
    }

}
