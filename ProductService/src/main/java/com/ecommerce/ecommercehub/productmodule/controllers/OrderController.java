package com.ecommerce.ecommercehub.productmodule.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.ecommercehub.productmodule.config.AppConstants;
import com.ecommerce.ecommercehub.productmodule.dtos.OrderDTO;
import com.ecommerce.ecommercehub.productmodule.dtos.OrderResponseDTO;
import com.ecommerce.ecommercehub.productmodule.dtos.UserDTO;
import com.ecommerce.ecommercehub.productmodule.services.OrderService;
import com.ecommerce.ecommercehub.productmodule.services.UserService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "E-Commerce Application")
public class OrderController {

    @Autowired
	public OrderService orderService;

    @Autowired
	public UserService userService;


    @Autowired
	private RedisTemplate<String, Object> redisTemplate;

    private static final String CACHE_KEY_PREFIX = "ordersByUser_";

    private static final Logger log = LoggerFactory.getLogger(CategoryController.class);

    @GetMapping("/admin/orders")
    public ResponseEntity<OrderResponseDTO> fetchAllOrders(
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer size,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_ORDERS_BY, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String orderBy) {
        log.info("fetchAllOrders called");
        OrderResponseDTO response = orderService.fetchAllOrders(pageNo, size, sortBy, orderBy);
        log.info("response", response);
        return new ResponseEntity<>(response, HttpStatus.FOUND);
    }

    @GetMapping("public/users/{emailId}/orders")
    public ResponseEntity<List<OrderDTO>> fetchOrdersForUser(@PathVariable String emailId) {
        log.info("fetchOrdersForUser called");
        Object cachedOrders = redisTemplate.opsForValue().get(CACHE_KEY_PREFIX + emailId);
        if (cachedOrders != null) {
            return new ResponseEntity<>((List<OrderDTO>) cachedOrders, HttpStatus.FOUND);
        }

        UserDTO user = userService.getUserByEmail(emailId);
        List<OrderDTO> userOrders = orderService.fetchOrdersForUser(user);

        redisTemplate.opsForValue().set(CACHE_KEY_PREFIX + emailId, userOrders);
        log.info("userOrders", userOrders);
        return new ResponseEntity<List<OrderDTO>>(userOrders, HttpStatus.FOUND);
    }

   @GetMapping("public/users/{emailId}/orders/{orderId}")
    public ResponseEntity<OrderDTO> fetchOrderForUserById(@PathVariable String emailId, @PathVariable Long orderId) {
        log.info("fetchOrderForUserById called");
        String cacheKey = CACHE_KEY_PREFIX + emailId + "_" + orderId;
        Object cachedOrder = redisTemplate.opsForValue().get(cacheKey);
        if (cachedOrder != null) {
            return new ResponseEntity<>((OrderDTO) cachedOrder, HttpStatus.FOUND);
        }

        UserDTO user = userService.getUserByEmail(emailId);
        OrderDTO orderDetails = orderService.fetchOrder(user.getUserId(), orderId);
        redisTemplate.opsForValue().set(cacheKey, orderDetails);
        log.info("orderDetails",orderDetails);
        return new ResponseEntity<>(orderDetails, HttpStatus.FOUND);
    }

    @GetMapping("public/orders/{orderId}")
    public ResponseEntity<OrderDTO> fetchOrderById(@PathVariable Long orderId) {
        log.info("fetchOrderById called");
        String cacheKey = "orderById_" + orderId;
        Object cachedOrder = redisTemplate.opsForValue().get(cacheKey);
        if (cachedOrder != null) {
            return new ResponseEntity<>((OrderDTO) cachedOrder, HttpStatus.FOUND);
        }

        OrderDTO orderDetails = orderService.fetchOrder(orderId);
        redisTemplate.opsForValue().set(cacheKey, orderDetails);
        log.info("orderDetails",orderDetails);
        return new ResponseEntity<>(orderDetails, HttpStatus.FOUND);
    }


}