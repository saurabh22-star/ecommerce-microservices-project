package com.userservice.controllers;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.userservice.configs.KafkaNotificationProducerClient;
import com.userservice.dtos.LoginCredentials;
import com.userservice.dtos.SendNotificationMessageDTO;
import com.userservice.dtos.UserDTO;
import com.userservice.entities.User;
import com.userservice.security.JWTUtil;
import com.userservice.services.UserDetailService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

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

    @Autowired
	private PasswordEncoder passwordEncoder;

    @Autowired
	private JWTUtil jwtUtil;

	@Autowired
	private AuthenticationManager authenticationManager;

    @Autowired
	public KafkaNotificationProducerClient kafkaNotificationProducerClient;


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

@PostMapping("/register")
public ResponseEntity<Map<String, Object>> createUser(@Valid @RequestBody UserDTO newUser) {

    log.info("Attempting to register new user: {}", newUser);

    String hashedPassword = passwordEncoder.encode(newUser.getPassword());
    newUser.setPassword(hashedPassword);

    UserDTO registeredUser = userService.registerUser(newUser);

    SendNotificationMessageDTO notification = new SendNotificationMessageDTO();
    notification.setTo(registeredUser.getEmail());
    notification.setSubject("Welcome");
    notification.setBody("Account created for " + registeredUser.getFirstName() + " " + registeredUser.getLastName());
    kafkaNotificationProducerClient.publishNotiificationEvent(notification); 

    log.info("Registration successful for user: {}", registeredUser);
    return new ResponseEntity<>(Collections.singletonMap("user_details", registeredUser), HttpStatus.CREATED);
}

@PostMapping("/login")
public Map<String, Object> authenticateUser(@Valid @RequestBody LoginCredentials loginData) {
    log.info("Authenticating user with credentials: {}", loginData);
    UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(loginData.getEmail(), loginData.getPassword());

    authenticationManager.authenticate(authenticationToken);

    String jwt = jwtUtil.generateToken(loginData.getEmail());
    log.info("Generated JWT for user: {}", jwt);
    return Collections.singletonMap("jwt-token", jwt);
}

}
