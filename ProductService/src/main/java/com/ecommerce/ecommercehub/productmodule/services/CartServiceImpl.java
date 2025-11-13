package com.ecommerce.ecommercehub.productmodule.services;

import org.springframework.beans.factory.annotation.Autowired;

import com.ecommerce.ecommercehub.productmodule.entities.Cart;
import com.ecommerce.ecommercehub.productmodule.entities.CartItem;
import com.ecommerce.ecommercehub.productmodule.entities.Product;
import com.ecommerce.ecommercehub.productmodule.exceptions.APIException;
import com.ecommerce.ecommercehub.productmodule.exceptions.ResourceNotFoundException;
import com.ecommerce.ecommercehub.productmodule.repositories.CartItemRepo;
import com.ecommerce.ecommercehub.productmodule.repositories.CartRepo;
import com.ecommerce.ecommercehub.productmodule.repositories.ProductRepo;

public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepo cartRepo;

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private CartItemRepo cartItemRepo;

    @Override
    public void updateProductInCarts(Long cartId, Long prodId) {
        Cart selectedCart = cartRepo.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId", cartId));

        Product updatedProduct = productRepo.findById(prodId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "prodId", prodId));

        CartItem itemInCart = cartItemRepo.findCartItemByProductIdAndCartId(cartId, prodId);

        if (itemInCart == null) {
            throw new APIException("The product " + updatedProduct.getProductName() + " is missing from the cart.");
        }

        double recalculatedTotal = selectedCart.getTotalPrice()
                - (itemInCart.getProductPrice() * itemInCart.getQuantity());

        itemInCart.setProductPrice(updatedProduct.getSpecialPrice());

        selectedCart.setTotalPrice(recalculatedTotal + (itemInCart.getProductPrice() * itemInCart.getQuantity()));

        cartItemRepo.save(itemInCart);
    }

    @Override
    public String removeProductFromCart(Long cartId, Long prodId) {
        Cart selectedCart = cartRepo.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId", cartId));

        CartItem itemInCart = cartItemRepo.findCartItemByProductIdAndCartId(cartId, prodId);

        if (itemInCart == null) {
            throw new ResourceNotFoundException("Product", "prodId", prodId);
        }

        selectedCart.setTotalPrice(
                selectedCart.getTotalPrice() - (itemInCart.getProductPrice() * itemInCart.getQuantity()));

        Product relatedProduct = itemInCart.getProduct();
        relatedProduct.setQuantity(relatedProduct.getQuantity() + itemInCart.getQuantity());

        cartItemRepo.deleteCartItemByProductIdAndCartId(cartId, prodId);

        return "The product " + itemInCart.getProduct().getProductName()
                + " has been successfully removed from the cart.";
    }
}
