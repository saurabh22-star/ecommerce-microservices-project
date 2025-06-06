package com.ecommerce.ecommercehub.productmodule.controllers;

import com.ecommerce.ecommercehub.productmodule.dtos.ProductRequestDto;
import com.ecommerce.ecommercehub.productmodule.dtos.ProductDto;
import com.ecommerce.ecommercehub.productmodule.exceptions.ProductAlreadyExistsException;
import com.ecommerce.ecommercehub.productmodule.models.Product;
import com.ecommerce.ecommercehub.productmodule.services.ProductService;
import com.ecommerce.ecommercehub.utility.dtos.CommonApiResponse;
import com.ecommerce.ecommercehub.utility.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

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
        } catch (ProductAlreadyExistsException ex) {
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

}
