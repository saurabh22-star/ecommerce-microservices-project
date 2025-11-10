package com.userservice.services;

import java.util.List;
import com.userservice.dtos.AddressDTO;
import com.userservice.entities.Address;

public interface AddressService {

    List<AddressDTO> retrieveAllAddresses();

    AddressDTO retrieveAddressById(Long addressId);

    AddressDTO registerAddress(AddressDTO addressDTO);

    AddressDTO modifyAddress(Long addressId, Address address);
	
	String removeAddress(Long addressId);

}
