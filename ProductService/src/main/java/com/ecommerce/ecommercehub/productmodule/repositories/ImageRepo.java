package com.ecommerce.ecommercehub.productmodule.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.ecommercehub.productmodule.entities.Image;

import java.util.List;

public interface ImageRepo extends JpaRepository<Image, Long> {

    List<Image> findByProductId(Long id);

}
