package com.ecommerce.ecommercehub.productmodule.services;

public interface CartService {

    void updateProductInCarts(Long cartId, Long productId);

    String removeProductFromCart(Long cartId, Long productId);

	
    
}
