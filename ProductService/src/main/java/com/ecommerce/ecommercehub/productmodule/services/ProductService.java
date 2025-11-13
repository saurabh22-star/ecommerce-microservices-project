package com.ecommerce.ecommercehub.productmodule.services;

import com.ecommerce.ecommercehub.productmodule.dtos.ProductDTO;
import com.ecommerce.ecommercehub.productmodule.dtos.ProductRequestDto;
import com.ecommerce.ecommercehub.productmodule.dtos.ProductResponseDTO;
import com.ecommerce.ecommercehub.productmodule.entities.Product;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;


public interface ProductService {

    ProductResponseDTO getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    ProductResponseDTO searchProductByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy,
                                        String sortOrder);

    ProductResponseDTO searchProductByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy,
                                              String sortOrder);
    
    ProductDTO addProduct(Long categoryId, Product product);

    ProductDTO updateProduct(Long productId, Product product);

    ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException;

    String deleteProduct(Long productId);
                                                
/* 
                                    

    List<ProductDTO> getConvertedProducts(List<Product> products);

    ProductDTO convertToDto(Product product);

    

   

    void deleteProductById(Long productId);

    List<Product> getProductsByManufacturerAndTitle(String manufacturer, String title);

    List<Product> getProductsByCategoryRefAndManufacturer(String categoryRef, String manufacturer);

    List<Product> getProductsByTitle(String title);

    List<Product> getProductsByManufacturer(String manufacturer);

    public List<Product> getProductsByCategoryRef(String categoryRef);

    Long countProductsByManufacturerAndTitle(String manufacturer, String title); */
}
