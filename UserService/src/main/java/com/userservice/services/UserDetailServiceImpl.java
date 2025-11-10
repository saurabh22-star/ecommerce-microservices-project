package com.userservice.services;


import com.userservice.configs.AppConstants;
import com.userservice.dtos.AddressDTO;
import com.userservice.dtos.UserDTO;
import com.userservice.dtos.UserResponse;
import com.userservice.entities.Address;
import com.userservice.entities.Role;
import com.userservice.entities.User;
import com.userservice.exceptions.APIException;
import com.userservice.exceptions.ResourceNotFoundException;
import com.userservice.repositories.AddressRepository;
import com.userservice.repositories.RoleRepository;
import com.userservice.repositories.UserRepository;

import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
@Service
public class UserDetailServiceImpl implements UserDetailService {

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private RoleRepository roleRepo;

	@Autowired
	private AddressRepository addressRepo;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private ModelMapper modelMapper;

	@Override
	public UserDTO registerUser(UserDTO userDTO) {

		try {
			User user = modelMapper.map(userDTO, User.class);
			Role role = roleRepo.findById(AppConstants.USER_ID).get();
			user.getRoles().add(role);

			Address address = getAddress(userDTO);

			user.setAddresses(List.of(address));
			User registeredUser = userRepo.save(user);
			userDTO = modelMapper.map(registeredUser, UserDTO.class);
			userDTO.setAddress(modelMapper.map(user.getAddresses().stream().findFirst().get(), AddressDTO.class));
			return userDTO;

		} catch (DataIntegrityViolationException e) {
			throw new APIException("User already exists with emailId: " + userDTO.getEmail());
		}

	}

	@Override
	public UserResponse getAllUsers(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
		Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
				: Sort.by(sortBy).descending();

		Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
		
		Page<User> pageUsers = userRepo.findAll(pageDetails);
		
		List<User> users = pageUsers.getContent();

		if (users.isEmpty()) {
			throw new APIException("No User exists !!!");
		}

		List<UserDTO> userDTOs = users.stream().map(user -> {
			UserDTO dto = modelMapper.map(user, UserDTO.class);

			if (!user.getAddresses().isEmpty()) {
				dto.setAddress(modelMapper.map(user.getAddresses().stream().findFirst().get(), AddressDTO.class));
			}
			return dto;

		}).collect(Collectors.toList());

		UserResponse userResponse = new UserResponse();
		
		userResponse.setContent(userDTOs);
		userResponse.setPageNumber(pageUsers.getNumber());
		userResponse.setPageSize(pageUsers.getSize());
		userResponse.setTotalElements(pageUsers.getTotalElements());
		userResponse.setTotalPages(pageUsers.getTotalPages());
		userResponse.setLastPage(pageUsers.isLast());
		
		return userResponse;
	}

	@Override
	public UserDTO getUserById(Long userId) {
		User user = userRepo.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));

		UserDTO userDTO = modelMapper.map(user, UserDTO.class);
		userDTO.setAddress(modelMapper.map(user.getAddresses().stream().findFirst().get(), AddressDTO.class));
		return userDTO;
	}

	@Override
	public UserDTO updateUser(Long userId, UserDTO userDTO) {
		User user = userRepo.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));

		String encodedPass = passwordEncoder.encode(userDTO.getPassword());

		user.setFirstName(userDTO.getFirstName());
		user.setLastName(userDTO.getLastName());
		user.setMobileNumber(userDTO.getMobileNumber());
		user.setEmail(userDTO.getEmail());
		user.setPassword(encodedPass);

		if (userDTO.getAddress() != null) {
				Address address = getAddress(userDTO);
				user.setAddresses(List.of(address));
		}

		userDTO = modelMapper.map(user, UserDTO.class);
		userDTO.setAddress(modelMapper.map(user.getAddresses().stream().findFirst().get(), AddressDTO.class));
		return userDTO;
	}

	@Override
	public String deleteUser(Long userId) {
		User user = userRepo.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));
		userRepo.delete(user);
		return "User with userId " + userId + " deleted successfully!!!";
	}

	@Override
	public Optional<User> getUserByEmail(String email) {
		return userRepo.findByEmail(email);
	}

	private Address getAddress(UserDTO userDTO) {
		String country = userDTO.getAddress().getCountry();
		String state = userDTO.getAddress().getState();
		String city = userDTO.getAddress().getCity();
		String pincode = userDTO.getAddress().getPincode();
		String street = userDTO.getAddress().getStreet();
		String buildingName = userDTO.getAddress().getBuildingName();

        Address curAddress = addressRepo.findByCountryAndStateAndCityAndPincodeAndStreetAndBuildingName(country, state,
                city, pincode, street, buildingName);

		if (curAddress == null) {
			curAddress = new Address(country, state, city, pincode, street, buildingName);
			addressRepo.save(curAddress);
		}

		return curAddress;
	}

}