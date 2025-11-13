package com.ecommerce.ecommercehub.productmodule.services;

import com.ecommerce.ecommercehub.productmodule.dtos.ProductDTO;
import com.ecommerce.ecommercehub.productmodule.dtos.ProductRequestDto;
import com.ecommerce.ecommercehub.productmodule.dtos.ProductResponseDTO;
import com.ecommerce.ecommercehub.productmodule.entities.Product;


import java.util.List;


public interface ProductService {

    //Product getProductById(Long id);

    ProductResponseDTO getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    ProductResponseDTO searchProductByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy,
                                        String sortOrder);

    ProductResponseDTO searchProductByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy,
                                              String sortOrder);
                                    

    List<ProductDTO> getConvertedProducts(List<Product> products);

    ProductDTO convertToDto(Product product);

    Product addProduct(ProductRequestDto request);

    Product updateProduct(ProductRequestDto updateRequest, Long productId);

    void deleteProductById(Long productId);

    List<Product> getProductsByManufacturerAndTitle(String manufacturer, String title);

    List<Product> getProductsByCategoryRefAndManufacturer(String categoryRef, String manufacturer);

    List<Product> getProductsByTitle(String title);

    List<Product> getProductsByManufacturer(String manufacturer);

    public List<Product> getProductsByCategoryRef(String categoryRef);

    Long countProductsByManufacturerAndTitle(String manufacturer, String title);
}
