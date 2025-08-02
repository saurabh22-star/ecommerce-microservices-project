package com.ecommerce.ecommercehub.productmodule.models;


import com.ecommerce.ecommercehub.utility.models.BaseModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Product extends BaseModel {

    private String title;
    private String manufacturer;
    private BigDecimal cost;
    private int stockCount;
    private String details;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "category_id")
    private Category categoryRef;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> imageList;

    public Product(String title, String manufacturer, BigDecimal cost, int stockCount, String details, Category categoryRef) {
        this.title = title;
        this.manufacturer = manufacturer;
        this.cost = cost;
        this.stockCount = stockCount;
        this.details = details;
        this.categoryRef = categoryRef;
    }
}
