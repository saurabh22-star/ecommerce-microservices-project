package com.userservice.dtos;

import java.util.HashSet;
import java.util.Set;

import com.userservice.entities.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
	
	private Long userId;
	private String firstName;
	private String lastName;
	private String mobileNumber;
	private String email;
	private String password;
	private Set<Role> roles = new HashSet<>();
	private AddressDTO address;

	public UserDTO(String firstname, String lastname, String mail, String password, String number, AddressDTO address) {
		this.firstName = firstname;
		this.lastName = lastname;
		this.mobileNumber = number;
		this.email = mail;
		this.password = password;
		this.address = address;
	}
}