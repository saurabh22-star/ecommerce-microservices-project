package com.ecommerce.ecommercehub.productmodule.controllers;

import com.ecommerce.ecommercehub.productmodule.dtos.ProductResponseDTO;
import com.ecommerce.ecommercehub.productmodule.entities.Product;
import com.ecommerce.ecommercehub.productmodule.config.AppConstants;
import com.ecommerce.ecommercehub.productmodule.dtos.ProductDTO;
import com.ecommerce.ecommercehub.productmodule.services.ProductService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "E-Commerce Application")
public class ProductController {

    @Autowired
    private ProductService productService;

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);



    @GetMapping("/")
    public String home() {
        return "Welcome to the Product Service API!";
}


  /*   @GetMapping("product/{productId}/details")
    public  ResponseEntity<CommonApiResponse> getProductById(@PathVariable Long id) {
        try {
            Product product = productService.getProductById(id);
            ProductDto productDto = productService.convertToDto(product);
            return  ResponseEntity.ok(new CommonApiResponse("success", productDto));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new CommonApiResponse(e.getMessage(), null));
        }
    } */

    @GetMapping("/public/products")
    public ResponseEntity<ProductResponseDTO> fetchAllProducts(
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer page,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer size,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_PRODUCTS_BY, required = false) String sortField,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String order) {
        log.info("Fetching all products");
        ProductResponseDTO response = productService.getAllProducts(page, size, sortField, order);
        log.info("Response",response);
        return new ResponseEntity<>(response, HttpStatus.FOUND);
    }


    @GetMapping("/public/categories/{categoryId}/products")
    public ResponseEntity<ProductResponseDTO> fetchProductsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer page,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer size,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_PRODUCTS_BY, required = false) String sortField,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String order) {
        log.info("Fetching products for category {}", categoryId);
        ProductResponseDTO response = productService.searchProductByCategory(categoryId, page, size, sortField, order);
        log.info("Response", response);
        return new ResponseEntity<>(response, HttpStatus.FOUND);
    }



    @GetMapping("/public/products/keyword/{keyword}")
    public ResponseEntity<ProductResponseDTO> searchProductsByKeyword(
            @PathVariable String keyword,
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer page,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer size,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_PRODUCTS_BY, required = false) String sortField,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String order) {
        log.info("Searching products with keyword: {}", keyword);
        ProductResponseDTO response = productService.searchProductByKeyword(keyword, page, size, sortField, order);
        log.info("Response", response);
        return new ResponseEntity<>(response, HttpStatus.FOUND);
    }


    @PostMapping("/admin/categories/{categoryId}/product")
    public ResponseEntity<ProductDTO> createProduct(
            @Valid @RequestBody Product product,
            @PathVariable Long categoryId) {
        log.info("Creating new product in category {}", categoryId);
        ProductDTO created = productService.addProduct(categoryId, product);
        log.info("Created ", created);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDTO> modifyProduct(
            @RequestBody Product product,
            @PathVariable Long productId) {
        log.info("Modifying product with ID {}", productId);
        ProductDTO updated = productService.updateProduct(productId, product);
        log.info("updated", updated);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @PutMapping("/admin/products/{productId}/image")
    public ResponseEntity<ProductDTO> changeProductImage(
            @PathVariable Long productId,
            @RequestParam("image") MultipartFile image) throws IOException {
        log.info("Updating image for product {}", productId);
        ProductDTO updated = productService.updateProductImage(productId, image);
        log.info("updated",updated);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @DeleteMapping("/admin/products/{productId}")
    public ResponseEntity<String> removeProduct(@PathVariable Long productId) {
        log.info("Removing product with ID {}", productId);
        String result = productService.deleteProduct(productId);
        log.info(result);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /* @GetMapping("/products/by/manufacturer-and-title")
    public ResponseEntity<CommonApiResponse> fetchProductsByManufacturerAndTitle(@RequestParam String manufacturer, @RequestParam String title) {
        try {
            List<Product> productList = productService.getProductsByManufacturerAndTitle(manufacturer, title);
            if (productList == null || productList.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new CommonApiResponse("No products found for the given manufacturer and title.", null));
            }
            List<ProductDTO> productDtos = productService.getConvertedProducts(productList);
            return ResponseEntity.ok(new CommonApiResponse("Products retrieved successfully.", productDtos));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CommonApiResponse("An error occurred: " + ex.getMessage(), null));
        }
    } */


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


    /* @GetMapping("/products/{title}/manufacturer")
    public ResponseEntity<CommonApiResponse> fetchProductsByTitle(@PathVariable String title) {
        try {
            List<Product> productList = productService.getProductsByTitle(title);
            if (productList == null || productList.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new CommonApiResponse("No products found with the specified title.", null));
            }
            List<ProductDTO> productDtos = productService.getConvertedProducts(productList);
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
            List<ProductDTO> productDtoList = productService.getConvertedProducts(productList);
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
            List<ProductDTO> convertedProducts = productService.getConvertedProducts(products);
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
    } */


}
