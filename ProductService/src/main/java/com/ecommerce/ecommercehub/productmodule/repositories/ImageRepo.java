package com.ecommerce.ecommercehub.productmodule.repositories;

import com.ecommerce.ecommercehub.productmodule.models.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepo extends JpaRepository<Image, Long> {

    List<Image> findByProductId(Long id);

}
