package com.ecommerce.ecommercehub.productmodule.services;

import java.util.List;

import com.ecommerce.ecommercehub.productmodule.dtos.CartDTO;

public interface CartService {


    void updateProductInCarts(Long cartId, Long productId);

    String removeProductFromCart(Long cartId, Long productId);

    List<CartDTO> fetchAllCarts();

    CartDTO fetchCartDetails(Long userId, Long cartId);

    CartDTO insertProductIntoCart(Long cartId, Long productId, Integer quantity);

    CartDTO addCartForUser(Long userId);

    CartDTO modifyProductQuantityInCart(Long cartId, Long productId, Integer quantity);

	
    
}
