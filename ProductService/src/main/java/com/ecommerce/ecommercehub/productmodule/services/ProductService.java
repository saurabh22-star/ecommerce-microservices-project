package com.ecommerce.ecommercehub.productmodule.services;

import com.ecommerce.ecommercehub.productmodule.dtos.ProductRequestDto;
import com.ecommerce.ecommercehub.productmodule.dtos.ProductDto;
import com.ecommerce.ecommercehub.productmodule.models.Product;

import java.util.List;


public interface ProductService {

    Product getProductById(Long id);

    List<Product> getAllProducts();


    List<ProductDto> getConvertedProducts(List<Product> products);

    ProductDto convertToDto(Product product);

    Product addProduct(ProductRequestDto request);

    Product updateProduct(ProductRequestDto updateRequest, Long productId);

    void deleteProductById(Long productId);

    List<Product> getProductsByManufacturerAndTitle(String manufacturer, String title);

    List<Product> getProductsByCategoryRefAndManufacturer(String categoryRef, String manufacturer);

    List<Product> getProductsByTitle(String title);
}
