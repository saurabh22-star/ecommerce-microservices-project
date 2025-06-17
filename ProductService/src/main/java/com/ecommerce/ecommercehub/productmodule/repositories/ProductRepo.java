package com.ecommerce.ecommercehub.productmodule.repositories;

import com.ecommerce.ecommercehub.productmodule.models.Product;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepo extends JpaRepository<Product, Long> {


    boolean existsByTitleAndManufacturer(String title, String manufacturer);

    List<Product> findByManufacturerAndTitle(String manufacturer, String title);

    List<Product> findByCategoryRefAndManufacturer(String categoryRef, String manufacturer);

    @Query("SELECT p FROM Product p WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    List<Product> findByTitle(@Param("title") String title);
}
