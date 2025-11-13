package com.ecommerce.ecommercehub.productmodule.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ecommerce.ecommercehub.productmodule.entities.Product;

@Repository
public interface ProductRepo extends JpaRepository<Product, Long> {


    Page<Product> searchByProductNameContaining(String keyword, Pageable pageDetails);
    
    //List<Product> findByCategoryRef(String category);

    boolean existsByTitleAndManufacturer(String title, String manufacturer);

    List<Product> findByManufacturerAndTitle(String manufacturer, String title);

   // List<Product> findByCategoryRefAndManufacturer(String categoryRef, String manufacturer);

    @Query("SELECT p FROM Product p WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    List<Product> findByTitle(@Param("title") String title);

    List<Product> findByManufacturer(String manufacturer);

    Long countProductsByManufacturerAndTitle(String manufacturer, String title);
}
