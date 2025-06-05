package com.ecommerce.ecommercehub.productmodule.services;

import com.ecommerce.ecommercehub.productmodule.dtos.ProductDto;
import com.ecommerce.ecommercehub.productmodule.models.Product;
import org.springframework.stereotype.Service;

import java.util.List;


public interface ProductService {

    Product getProductById(Long id);

    List<Product> getAllProducts();


    List<ProductDto> getConvertedProducts(List<Product> products);

    ProductDto convertToDto(Product product);

}
