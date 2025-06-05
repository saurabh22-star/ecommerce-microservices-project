package com.ecommerce.ecommercehub.productmodule.repositories;

import com.ecommerce.ecommercehub.productmodule.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepo  extends JpaRepository<Product, Long> {


}
