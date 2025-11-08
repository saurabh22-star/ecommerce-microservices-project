package com.userservice.controllers;

import org.springframework.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.userservice.dtos.UserResponse;
import com.userservice.services.UserDetailsService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "E-Commerce Application")
public class UserDetailsController {

    private static final Logger logger = LoggerFactory.getLogger(UserDetailsController.class);
    private static final String CACHE_KEY_PREFIX = "getUserById_";

    private final UserDetailsService userService;
    private final RedisTemplate<String, Object> redisTemplate;

    public UserDetailsController(UserDetailsService userService, RedisTemplate<String, Object> redisTemplate) {
        this.userService = userService;
        this.redisTemplate = redisTemplate;
    }


    @GetMapping("/admin/users")
    public ResponseEntity<UserResponse> listUsers(
            @RequestParam(name = "pageNumber", defaultValue = "0", required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = "10", required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = "userId", required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = "asc", required = false) String sortOrder) {

        logger.info("Request received to list users (page={}, size={}, sortBy={}, order={})",
                pageNumber, pageSize, sortBy, sortOrder);

        UserResponse response = userService.getAllUsers(pageNumber, pageSize, sortBy, sortOrder);

        logger.debug("UserResponse prepared with {} users", response.getContent().size());
        return new ResponseEntity<UserResponse>(response, HttpStatus.FOUND);
    }

    

}
