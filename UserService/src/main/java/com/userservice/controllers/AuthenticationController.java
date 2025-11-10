package com.userservice.controllers;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.userservice.dtos.UserDTO;
import com.userservice.entities.User;
import com.userservice.services.UserDetailService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "E-Commerce Application")
public class AuthenticationController {

    @Autowired
	private UserDetailService userService;

    @Autowired
	private ModelMapper modelMapper;

    @Autowired
	private RedisTemplate<String, Object> redisTemplate;

    private static final Logger log = LoggerFactory.getLogger(AuthenticationController.class);

    @GetMapping("/user/email/{email}")
    public ResponseEntity<UserDTO> fetchUserByEmail(@PathVariable String email) {
    
    log.info("Fetching user details for email: {}", email);
    String cacheKey = "getUser_" + email;
    Object cachedUser = redisTemplate.opsForValue().get(cacheKey);

    if(cachedUser != null){
        	return new ResponseEntity<UserDTO>((UserDTO) cachedUser, HttpStatus.FOUND);
    }
		


    Optional<User> optionalUser = userService.getUserByEmail(email);

    if (optionalUser.isEmpty()) {
        return new ResponseEntity<UserDTO>(HttpStatus.NOT_FOUND);
    }

    UserDTO dto = modelMapper.map(optionalUser.get(), UserDTO.class);
    redisTemplate.opsForValue().set(cacheKey, dto);
    log.info("User details retrieved: {}", dto);
    return new ResponseEntity<UserDTO>(dto, HttpStatus.FOUND);
}

}
