package com.ecommerce.ecommercehub.productmodule.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.ecommercehub.productmodule.dtos.CartDTO;
import com.ecommerce.ecommercehub.productmodule.dtos.UserDTO;
import com.ecommerce.ecommercehub.productmodule.services.CartService;
import com.ecommerce.ecommercehub.productmodule.services.UserService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "E-Commerce Application")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final Logger log = LoggerFactory.getLogger(CartController.class);

    @GetMapping("/admin/carts")
    public ResponseEntity<List<CartDTO>> fetchAllCarts() {
        log.info("Fetching all cart records");
        List<CartDTO> allCarts = cartService.fetchAllCarts();
        log.info("Cart list: {}", allCarts);
        return new ResponseEntity<>(allCarts, HttpStatus.FOUND);
    }

    @GetMapping("/public/users/{emailId}/carts/{cartId}")
    public ResponseEntity<CartDTO> fetchCartByUserEmailAndId(@PathVariable String emailId, @PathVariable Long cartId) {
        log.info("Fetching cart for user: {} with cart ID: {}", emailId, cartId);
        String cacheKey = "getCartById_" + emailId + "_" + cartId;
        Object cachedCart = redisTemplate.opsForValue().get(cacheKey);

        if (cachedCart != null) {
            log.info("Cart found in cache for key: {}", cacheKey);
            return new ResponseEntity<>((CartDTO) cachedCart, HttpStatus.FOUND);
        }

        UserDTO user = userService.getUserByEmail(emailId);
        CartDTO cart = cartService.fetchCartDetails(user.getUserId(), cartId);

        redisTemplate.opsForValue().set(cacheKey, cart);
        log.info("Cart retrieved and cached: {}", cart);
        return new ResponseEntity<>(cart, HttpStatus.FOUND);
    }

    @PostMapping("/public/carts/{cartId}/products/{productId}/quantity/{quantity}")
    public ResponseEntity<CartDTO> insertProductIntoCart(
            @PathVariable Long cartId,
            @PathVariable Long productId,
            @PathVariable Integer quantity) {
        log.info("Request to add product {} (qty: {}) to cart {}", productId, quantity, cartId);
        CartDTO updatedCart = cartService.insertProductIntoCart(cartId, productId, quantity);
        log.info("Updated cart details: {}", updatedCart);
        return new ResponseEntity<>(updatedCart, HttpStatus.CREATED);
    }

    @PostMapping("/public/users/{emailId}/carts")
    public ResponseEntity<CartDTO> createCartForUser(@PathVariable String emailId) {
        log.info("Initiating cart creation for user with email: {}", emailId);
        UserDTO user = userService.getUserByEmail(emailId);
        CartDTO newCart = cartService.addCartForUser(user.getUserId());
        log.info("New cart created: {}", newCart);
        return new ResponseEntity<>(newCart, HttpStatus.CREATED);
    }

    @PutMapping("/public/carts/{cartId}/products/{productId}/quantity/{quantity}")
    public ResponseEntity<CartDTO> modifyProductQuantityInCart(
            @PathVariable Long cartId,
            @PathVariable Long productId,
            @PathVariable Integer quantity) {
        log.info("Modifying quantity of product {} in cart {}", productId, cartId);
        CartDTO updatedCart = cartService.modifyProductQuantityInCart(cartId, productId, quantity);
        log.info("Cart after update: {}", updatedCart);
        return new ResponseEntity<>(updatedCart, HttpStatus.OK);
    }

    @DeleteMapping("/public/carts/{cartId}/product/{productId}")
    public ResponseEntity<String> removeProductFromCart(@PathVariable Long cartId, @PathVariable Long productId) {
        log.info("Removing product {} from cart {}", productId, cartId);
        String resultMessage = cartService.removeProductFromCart(cartId, productId);
        log.info("Delete status: {}", resultMessage);
        return new ResponseEntity<>(resultMessage, HttpStatus.OK);
    }

}
