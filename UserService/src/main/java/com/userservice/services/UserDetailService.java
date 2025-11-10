package com.userservice.services;

import java.util.Optional;

import com.userservice.dtos.UserDTO;
import com.userservice.dtos.UserResponse;
import com.userservice.entities.User;

public interface UserDetailService {

    UserDTO registerUser(UserDTO userDTO);
	
	UserResponse getAllUsers(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
	
	UserDTO getUserById(Long userId);
	
	UserDTO updateUser(Long userId, UserDTO userDTO);
	
	String deleteUser(Long userId);

	Optional<User> getUserByEmail(String email);
}
