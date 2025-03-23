package com.productservice.productservice.controller;

import com.productservice.productservice.services.FakeStoreProductService;
import com.productservice.productservice.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
public class ProductController {


    private ProductService productService;

    //Constructor Injection
    ProductController(@Qualifier("fakeStoreProductService") ProductService productService){
        this.productService = productService;
    }

    @GetMapping("/home")
    public String home() {
        return "Welcome to Product Service!";
    }

    @GetMapping("/{id}")
    public String getProductById(@PathVariable("id") Long id){
        return productService.getProductById(id);
    }


    @GetMapping
    public void getAllProducts(){

    }

    @DeleteMapping("/{id}")
    public void deleteProductById(){

    }


    public void createProduct(){

    }

    public void updateProductById(){

    }


}
