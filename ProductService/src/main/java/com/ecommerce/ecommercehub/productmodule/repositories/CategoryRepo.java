package com.ecommerce.ecommercehub.productmodule.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.ecommercehub.productmodule.entities.Category;

public interface CategoryRepo extends JpaRepository<Category, Long> {
    Category findByTitle(String title);
    boolean existsByTitle(String title);
}
