package com.ecommerce.ecommercehub.productmodule.dtos;

import com.ecommerce.ecommercehub.productmodule.models.Category;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductRequestDto {
    private Long itemId;
    private String title;
    private String manufacturer;
    private BigDecimal cost;
    private int stockCount;
    private String details;
    private Category categoryRef;
}
