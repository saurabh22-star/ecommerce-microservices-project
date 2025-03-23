package com.productservice.productservice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
public class ProductController {

    //Controller for the project
    @GetMapping
    public String home() {
        return "Welcome to Product Service!";
    }


    public void getProductById(){

    }

    public void getAllProducts(){

    }

    public void deleteProductById(){

    }

    public void createProduct(){

    }

    public void updateProductById(){

    }


}
