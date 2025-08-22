package com.ecommerce.ecommercehub.productmodule.controllers;

import com.ecommerce.ecommercehub.productmodule.dtos.ProductRequestDto;
import com.ecommerce.ecommercehub.productmodule.dtos.ProductDto;
import com.ecommerce.ecommercehub.productmodule.models.Product;
import com.ecommerce.ecommercehub.productmodule.services.ProductService;
import com.ecommerce.ecommercehub.utility.dtos.CommonApiResponse;
import com.ecommerce.ecommercehub.utility.exceptions.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.ecommerce.ecommercehub.productmodule.exceptions.DuplicateResourceException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;


    @GetMapping("/")
    public String home() {
        return "Welcome to the Product Service API!";
}


    @GetMapping("product/{productId}/details")
    public  ResponseEntity<CommonApiResponse> getProductById(@PathVariable Long id) {
        try {
            Product product = productService.getProductById(id);
            ProductDto productDto = productService.convertToDto(product);
            return  ResponseEntity.ok(new CommonApiResponse("success", productDto));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new CommonApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<CommonApiResponse> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        List<ProductDto> convertedProducts = productService.getConvertedProducts(products);

        return  ResponseEntity.ok(new CommonApiResponse("success", convertedProducts));
    }


    //@PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/add")
    public ResponseEntity<CommonApiResponse> addProduct(@RequestBody ProductRequestDto request) {
        try {
            Product createdProduct = productService.addProduct(request);
            ProductDto productDto = productService.convertToDto(createdProduct);
            return ResponseEntity.ok(new CommonApiResponse("Product added successfully!", productDto));
        } catch (DuplicateResourceException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new CommonApiResponse(ex.getMessage(), null));
        }
    }

    //@PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/product/{productId}/update")
    public ResponseEntity<CommonApiResponse> updateProduct(@RequestBody ProductRequestDto updateRequest, @PathVariable Long productId) {
        try {
            Product updatedProduct = productService.updateProduct(updateRequest, productId);
            ProductDto updatedProductDto = productService.convertToDto(updatedProduct);
            return ResponseEntity.ok(new CommonApiResponse("Product updated successfully!", updatedProductDto));
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CommonApiResponse(ex.getMessage(), null));
        }
    }

    //@PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/product/{productId}/delete")
    public ResponseEntity<CommonApiResponse> deleteProduct(@PathVariable Long productId) {
        try {
            productService.deleteProductById(productId);
            return ResponseEntity.ok(new CommonApiResponse("Product deleted successfully!", productId));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new CommonApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/products/by/manufacturer-and-title")
    public ResponseEntity<CommonApiResponse> fetchProductsByManufacturerAndTitle(@RequestParam String manufacturer, @RequestParam String title) {
        try {
            List<Product> productList = productService.getProductsByManufacturerAndTitle(manufacturer, title);
            if (productList == null || productList.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new CommonApiResponse("No products found for the given manufacturer and title.", null));
            }
            List<ProductDto> productDtos = productService.getConvertedProducts(productList);
            return ResponseEntity.ok(new CommonApiResponse("Products retrieved successfully.", productDtos));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CommonApiResponse("An error occurred: " + ex.getMessage(), null));
        }
    }


    /*@GetMapping("/products/by/categoryRef-and-manufacturer")
    public ResponseEntity<CommonApiResponse> fetchProductsByCategoryRefAndManufacturer(@RequestParam String categoryRef, @RequestParam String manufacturer) {
        try {
            List<Product> productList = productService.getProductsByCategoryRefAndManufacturer(categoryRef, manufacturer);
            if (productList == null || productList.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new CommonApiResponse("No products found for the specified category and brand.", null));
            }
            List<ProductDto> productDtos = productService.getConvertedProducts(productList);
            return ResponseEntity.ok(new CommonApiResponse("Products fetched successfully.", productDtos));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CommonApiResponse("Failed to fetch products: " + ex.getMessage(), null));
        }
    }*/


    @GetMapping("/products/{title}/manufacturer")
    public ResponseEntity<CommonApiResponse> fetchProductsByTitle(@PathVariable String title) {
        try {
            List<Product> productList = productService.getProductsByTitle(title);
            if (productList == null || productList.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new CommonApiResponse("No products found with the specified title.", null));
            }
            List<ProductDto> productDtos = productService.getConvertedProducts(productList);
            return ResponseEntity.ok(new CommonApiResponse("Products retrieved successfully.", productDtos));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CommonApiResponse("An error occurred: " + ex.getMessage(), null));
        }
    }

    @GetMapping("/product/by-manufacturer")
    public ResponseEntity<CommonApiResponse> findProductsByManufacturer(@RequestParam String manufacturer) {
        try {
            List<Product> productList = productService.getProductsByManufacturer(manufacturer);
            if (productList == null || productList.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new CommonApiResponse("No products available for the specified manufacturer.", null));
            }
            List<ProductDto> productDtoList = productService.getConvertedProducts(productList);
            return ResponseEntity.ok(new CommonApiResponse("Products fetched successfully.", productDtoList));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CommonApiResponse("Error occurred: " + ex.getMessage(), null));
        }
    }

    @GetMapping("/product/{category}/all/products")
    public ResponseEntity<CommonApiResponse> findProductByCategoryRef(@PathVariable String categoryRef) {
        try {
            List<Product> products = productService.getProductsByCategoryRef(categoryRef);
            if (products.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new CommonApiResponse("No products available for the specified category ", null));
            }
            List<ProductDto> convertedProducts = productService.getConvertedProducts(products);
            return  ResponseEntity.ok(new CommonApiResponse("success", convertedProducts));
        } catch (Exception e) {
            return ResponseEntity.ok(new CommonApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/product/count/by-brand/and-name")
    public ResponseEntity<CommonApiResponse> countProductsByManufacturerAndTitle(@RequestParam String manufacturer, @RequestParam String title) {
        try {
            var productCount = productService.countProductsByManufacturerAndTitle(manufacturer, title);
            return ResponseEntity.ok(new CommonApiResponse("Product count!", productCount));
        } catch (Exception e) {
            return ResponseEntity.ok(new CommonApiResponse(e.getMessage(), null));
        }
    }


}
