package com.userservice.controllers;

import org.springframework.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.userservice.dtos.ApiResponse;
import com.userservice.dtos.UserDTO;
import com.userservice.dtos.UserResponse;
import com.userservice.services.UserDetailsService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

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

    
    @GetMapping("/public/users/{userId}")
    public ResponseEntity<UserDTO> getUserDetailsById(@PathVariable Long userId) {
        logger.info("Retrieving user details for ID: {}", userId);
        
        String cacheKey = CACHE_KEY_PREFIX + userId;
        Object cachedUser = redisTemplate.opsForValue().get(cacheKey);
        
        if (cachedUser != null) {
            logger.debug("Cache hit for user ID: {}", userId);
            return ResponseEntity.status(HttpStatus.OK)
                               .body((UserDTO) cachedUser);
        }

        UserDTO userDTO = userService.getUserById(userId);
        redisTemplate.opsForValue().set(cacheKey, userDTO);
        
        logger.debug("User details retrieved and cached for ID: {}", userId);
        return ResponseEntity.status(HttpStatus.OK)
                           .body(userDTO);
    }



    
    @PutMapping("/public/users/{userId}")
    public ResponseEntity<UserDTO> modifyUserDetails(
            @PathVariable Long userId,
            @Valid @RequestBody UserDTO userDTO) {
        logger.info("Modifying user details for ID: {}", userId);
        
        UserDTO modifiedUser = userService.updateUser(userId, userDTO);
        
        // Invalidate cache
        redisTemplate.delete(CACHE_KEY_PREFIX + userId);
        
        logger.debug("User details modified successfully for ID: {}", userId);
        return ResponseEntity.status(HttpStatus.OK)
                           .body(modifiedUser);
    }

    @DeleteMapping("/admin/users/{userId}")
    public ResponseEntity<ApiResponse> removeUser(@PathVariable Long userId) {
        logger.info("Initiating user removal for ID: {}", userId);
        
        String result = userService.deleteUser(userId);
        
        redisTemplate.delete(CACHE_KEY_PREFIX + userId);
        
        ApiResponse response = new ApiResponse(true, result);
        
        logger.debug("User removal completed for ID: {}", userId);
        return ResponseEntity.status(HttpStatus.OK)
                           .body(response);
    }


    



    

}
