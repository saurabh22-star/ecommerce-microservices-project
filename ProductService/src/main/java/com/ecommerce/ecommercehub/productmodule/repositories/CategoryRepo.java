package com.ecommerce.ecommercehub.productmodule.repositories;

import com.ecommerce.ecommercehub.productmodule.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepo extends JpaRepository<Category, Long> {
    Category findByTitle(String title);
    boolean existsByTitle(String title);
}
