package com.ecommerce.ecommercehub.productmodule.services;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import com.ecommerce.ecommercehub.productmodule.dtos.CartDTO;
import com.ecommerce.ecommercehub.productmodule.dtos.ProductDTO;
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

        @Autowired
        private ModelMapper modelMapper;

        @Override
        public void updateProductInCarts(Long cartId, Long prodId) {
                Cart selectedCart = cartRepo.findById(cartId)
                                .orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId", cartId));

                Product updatedProduct = productRepo.findById(prodId)
                                .orElseThrow(() -> new ResourceNotFoundException("Product", "prodId", prodId));

                CartItem itemInCart = cartItemRepo.findCartItemByProductIdAndCartId(cartId, prodId);

                if (itemInCart == null) {
                        throw new APIException("The product " + updatedProduct.getProductName()
                                        + " is missing from the cart.");
                }

                double recalculatedTotal = selectedCart.getTotalPrice()
                                - (itemInCart.getProductPrice() * itemInCart.getQuantity());

                itemInCart.setProductPrice(updatedProduct.getSpecialPrice());

                selectedCart.setTotalPrice(
                                recalculatedTotal + (itemInCart.getProductPrice() * itemInCart.getQuantity()));

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
                                selectedCart.getTotalPrice()
                                                - (itemInCart.getProductPrice() * itemInCart.getQuantity()));

                Product relatedProduct = itemInCart.getProduct();
                relatedProduct.setQuantity(relatedProduct.getQuantity() + itemInCart.getQuantity());

                cartItemRepo.deleteCartItemByProductIdAndCartId(cartId, prodId);

                return "The product " + itemInCart.getProduct().getProductName()
                                + " has been successfully removed from the cart.";
        }

        @Override
        public List<CartDTO> fetchAllCarts() {
                List<Cart> cartList = cartRepo.findAll();

                if (cartList.isEmpty()) {
                        throw new APIException("No carts found in the system");
                }

                return cartList.stream().map(singleCart -> {
                        CartDTO cartDetails = modelMapper.map(singleCart, CartDTO.class);

                        List<ProductDTO> productList = singleCart.getCartItems().stream()
                                        .map(item -> modelMapper.map(item.getProduct(), ProductDTO.class))
                                        .collect(Collectors.toList());

                        cartDetails.setProducts(productList);

                        return cartDetails;
                }).collect(Collectors.toList());
        }

        @Override
        public CartDTO fetchCartDetails(Long userId, Long cartId) {
                Cart foundCart = cartRepo.findCartByEmailAndCartId(userId, cartId);

                if (foundCart == null) {
                        throw new ResourceNotFoundException("Cart", "cartId", cartId);
                }

                CartDTO cartDetails = modelMapper.map(foundCart, CartDTO.class);

                List<ProductDTO> productList = foundCart.getCartItems().stream()
                                .map(item -> modelMapper.map(item.getProduct(), ProductDTO.class))
                                .collect(Collectors.toList());

                cartDetails.setProducts(productList);

                return cartDetails;
        }

        @Override
        public CartDTO insertProductIntoCart(Long cartId, Long productId, Integer quantity) {
                Cart cart = cartRepo.findById(cartId)
                                .orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId", cartId));

                Product product = productRepo.findById(productId)
                                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

                CartItem existingItem = cartItemRepo.findCartItemByProductIdAndCartId(cartId, productId);

                if (existingItem != null) {
                        throw new APIException(
                                        "The product " + product.getProductName() + " is already present in the cart.");
                }

                if (product.getQuantity() == 0) {
                        throw new APIException(product.getProductName() + " is currently out of stock.");
                }

                if (product.getQuantity() < quantity) {
                        throw new APIException("Order quantity for " + product.getProductName() +
                                        " must not exceed available stock: " + product.getQuantity());
                }

                CartItem cartItem = new CartItem();
                cartItem.setProduct(product);
                cartItem.setCart(cart);
                cartItem.setQuantity(quantity);
                cartItem.setDiscount(product.getDiscount());
                cartItem.setProductPrice(product.getSpecialPrice());

                cartItemRepo.save(cartItem);

                product.setQuantity(product.getQuantity() - quantity);
                cart.setTotalPrice(cart.getTotalPrice() + (product.getSpecialPrice() * quantity));

                CartDTO resultCartDTO = modelMapper.map(cart, CartDTO.class);

                List<ProductDTO> productDTOList = cart.getCartItems().stream()
                                .map(item -> modelMapper.map(item.getProduct(), ProductDTO.class))
                                .collect(Collectors.toList());

                resultCartDTO.setProducts(productDTOList);

                return resultCartDTO;
        }

        @Override
        public CartDTO addCartForUser(Long userId) {
                Cart newCart = new Cart();
                newCart.setUserId(userId);
                Cart savedCart = cartRepo.save(newCart);
                return modelMapper.map(savedCart, CartDTO.class);
        }

        @Override
        public CartDTO modifyProductQuantityInCart(Long cartId, Long productId, Integer newQuantity) {
                Cart currentCart = cartRepo.findById(cartId)
                                .orElseThrow(() -> new ResourceNotFoundException("Cart not found", "cartId", cartId));

                Product selectedProduct = productRepo.findById(productId)
                                .orElseThrow(() -> new ResourceNotFoundException("Product not found", "productId",
                                                productId));

                if (selectedProduct.getQuantity() == 0) {
                        throw new APIException(selectedProduct.getProductName() + " is out of stock.");
                }

                if (selectedProduct.getQuantity() < newQuantity) {
                        throw new APIException("Requested quantity for " + selectedProduct.getProductName() +
                                        " exceeds available stock (" + selectedProduct.getQuantity() + ").");
                }

                CartItem itemInCart = cartItemRepo.findCartItemByProductIdAndCartId(cartId, productId);

                if (itemInCart == null) {
                        throw new APIException(
                                        "The product " + selectedProduct.getProductName() + " is not in the cart.");
                }

                double updatedCartPrice = currentCart.getTotalPrice()
                                - (itemInCart.getProductPrice() * itemInCart.getQuantity());

                selectedProduct.setQuantity(selectedProduct.getQuantity() + itemInCart.getQuantity() - newQuantity);

                itemInCart.setProductPrice(selectedProduct.getSpecialPrice());
                itemInCart.setQuantity(newQuantity);
                itemInCart.setDiscount(selectedProduct.getDiscount());

                currentCart.setTotalPrice(updatedCartPrice + (itemInCart.getProductPrice() * newQuantity));

                cartItemRepo.save(itemInCart);

                CartDTO updatedCartDTO = modelMapper.map(currentCart, CartDTO.class);

                List<ProductDTO> productDTOList = currentCart.getCartItems().stream()
                                .map(ci -> modelMapper.map(ci.getProduct(), ProductDTO.class))
                                .collect(Collectors.toList());

                updatedCartDTO.setProducts(productDTOList);

                return updatedCartDTO;
        }

}
