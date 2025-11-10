package com.userservice.services;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.userservice.dtos.AddressDTO;
import com.userservice.entities.Address;
import com.userservice.exceptions.ResourceNotFoundException;
import com.userservice.repositories.AddressRepository;
import com.userservice.repositories.UserRepository;
import com.userservice.exceptions.APIException;
import com.userservice.entities.User;	


import jakarta.transaction.Transactional;

@Transactional
@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
	private AddressRepository addressRepo;

    @Autowired
	private ModelMapper modelMapper;

	@Autowired
	private UserRepository userRepo;


    @Override
    public List<AddressDTO> retrieveAllAddresses() {
       
        List<Address> addresses = addressRepo.findAll();

        return addresses.stream().map(address -> modelMapper.map(address, AddressDTO.class))
                .collect(Collectors.toList());
    }


    @Override
    public AddressDTO retrieveAddressById(Long addressId) {
        Address address = addressRepo.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "addressId",addressId));
        return modelMapper.map(address, AddressDTO.class);
    }


    @Override
	public AddressDTO registerAddress(AddressDTO addressDTO) {

		String country = addressDTO.getCountry();
		String state = addressDTO.getState();
		String city = addressDTO.getCity();
		String pincode = addressDTO.getPincode();
		String street = addressDTO.getStreet();
		String buildingName = addressDTO.getBuildingName();

		Address addressFromDB = addressRepo.findByCountryAndStateAndCityAndPincodeAndStreetAndBuildingName(country,
				state, city, pincode, street, buildingName);

		if (addressFromDB != null) {
            throw new APIException("Address with these details is already registered under ID: " + addressFromDB.getAddressId());
		}

		Address address = modelMapper.map(addressDTO, Address.class);

		Address savedAddress = addressRepo.save(address);

		return modelMapper.map(savedAddress, AddressDTO.class);
	}

	@Override
	public AddressDTO modifyAddress(Long addressId, Address address) {
		Address matchedAddress = addressRepo.findByCountryAndStateAndCityAndPincodeAndStreetAndBuildingName(
				address.getCountry(), address.getState(), address.getCity(), address.getPincode(), address.getStreet(),
				address.getBuildingName());

		if (matchedAddress != null) {
			List<User> usersWithOldAddress = userRepo.findByAddress(addressId);
			final Address target = matchedAddress;

			for (User u : usersWithOldAddress) {
				if (!u.getAddresses().contains(target)) {
					u.getAddresses().add(target);
				}
			}

			removeAddress(addressId);

			return modelMapper.map(matchedAddress, AddressDTO.class);
		} else {
			Address existing = addressRepo.findById(addressId)
					.orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", addressId));

			existing.setCountry(address.getCountry());
			existing.setState(address.getState());
			existing.setCity(address.getCity());
			existing.setPincode(address.getPincode());
			existing.setStreet(address.getStreet());
			existing.setBuildingName(address.getBuildingName());

			Address saved = addressRepo.save(existing);
			return modelMapper.map(saved, AddressDTO.class);
		}
	}


	@Override
	public String removeAddress(Long addressId) {
		Address address = addressRepo.findById(addressId)
				.orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", addressId));

		List<User> users = userRepo.findByAddress(addressId);
		for (User user : users) {
			user.getAddresses().remove(address);
			userRepo.save(user);
		}

		addressRepo.deleteById(addressId);

		return "Successfully deleted address with ID: " + addressId;
	}

}
