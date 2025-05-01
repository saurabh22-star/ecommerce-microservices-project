package com.productservice.productservice.models;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Product extends BaseModel{

    private String title;

    private String description;

    private int price;

    private  String image;

    private Category category;
}
