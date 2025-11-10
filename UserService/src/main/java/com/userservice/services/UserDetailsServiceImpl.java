package com.userservice.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.userservice.configs.UsersInfoConfig;
import com.userservice.entities.User;
import com.userservice.exceptions.ResourceNotFoundException;
import com.userservice.repositories.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

   @Autowired
   private UserRepository userRepo;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<User> user = userRepo.findByEmail(username);
		
		return user.map(UsersInfoConfig::new).orElseThrow(() -> new ResourceNotFoundException("User", "email", username));
	}
}
