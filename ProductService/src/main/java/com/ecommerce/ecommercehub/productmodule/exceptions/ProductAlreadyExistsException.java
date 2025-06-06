package com.ecommerce.ecommercehub.productmodule.exceptions;

public class ProductAlreadyExistsException  extends  RuntimeException{
    public ProductAlreadyExistsException(String message) {
        super(message);
    }
}
