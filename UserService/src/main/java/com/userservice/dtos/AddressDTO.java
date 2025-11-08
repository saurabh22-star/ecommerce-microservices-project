package com.userservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressDTO {

	private Long addressId;
	private String street;
	private String buildingName;
	private String city;
	private String state;
	private String country;
	private String pincode;

	public AddressDTO(long addressId, String street, String buildingName, String city, String state) {
		this.addressId = addressId;
		this.street = street;
		this.buildingName = buildingName;
		this.city = city;
		this.state = state;
	}
}
