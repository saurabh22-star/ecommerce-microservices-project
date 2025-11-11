package com.userservice.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import java.util.List;
import com.userservice.services.AddressService;
import com.userservice.dtos.AddressDTO;
import com.userservice.entities.Address;

@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "E-Commerce Security")
public class AddressController {

    @Autowired
	private AddressService addressService;

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

    private static final Logger log = LoggerFactory.getLogger(AddressController.class);
    private static final String ADDRESS_KEY_PREFIX = "getAddress_";
  //  private static final long CACHE_DURATION = 30;

   /*  @Autowired
    public AddressController(AddressService addressService, RedisTemplate<String, Object> redisTemplate) {
        this.addressService = addressService;
        this.redisTemplate = redisTemplate;
    } */

    @GetMapping("/admin/addresses")
    public ResponseEntity<List<AddressDTO>> retrieveAllAddresses() {
        log.debug("Initiating retrieval of all addresses");
        List<AddressDTO> addresses = addressService.retrieveAllAddresses();
        log.info("Successfully retrieved {} addresses", addresses.size());
        return new ResponseEntity<List<AddressDTO>>(addresses, HttpStatus.FOUND);
    }

    @GetMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDTO> retrieveAddressById(@PathVariable("addressId") Long addressId) {
        log.debug("Checking cache for address with ID: {}", addressId);
        String cacheKey = ADDRESS_KEY_PREFIX + addressId;
        
        Object cachedAddress = redisTemplate.opsForValue().get(cacheKey);
        if (cachedAddress != null) {
            log.debug("Cache hit for address ID: {}", addressId);
            return ResponseEntity.ok((AddressDTO) cachedAddress);
        }

        log.debug("Cache miss, fetching address details for ID: {}", addressId);
        AddressDTO address = addressService.retrieveAddressById(addressId);
        
        redisTemplate.opsForValue().set(cacheKey+addressId, address);
        log.info("Successfully retrieved and cached address for ID: {}", addressId);
        
        return new ResponseEntity<AddressDTO>(address, HttpStatus.FOUND);
    }

    @PostMapping("/address")
    public ResponseEntity<AddressDTO> registerNewAddress(@Valid @RequestBody AddressDTO addressDetails) {
        log.debug("Processing new address registration request");
        AddressDTO registeredAddress = addressService.registerAddress(addressDetails);
        log.info("Successfully registered new address with ID: {}", registeredAddress.getAddressId());
        return new ResponseEntity<AddressDTO>(registeredAddress, HttpStatus.CREATED);
    }

    @PutMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDTO> modifyAddress(
            @PathVariable("addressId") Long addressId,
            @Valid @RequestBody Address addressUpdates) {
        log.debug("Processing address update request for ID: {}", addressId);
        
        AddressDTO updatedAddress = addressService.modifyAddress(addressId, addressUpdates);
        
        // Invalidate cache after update
        String cacheKey = ADDRESS_KEY_PREFIX + addressId;
        redisTemplate.delete(cacheKey);
        
        log.info("Successfully updated address for ID: {}", addressId);
        return new ResponseEntity<AddressDTO>(updatedAddress, HttpStatus.OK);
    }

    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<String> removeAddress(@PathVariable("addressId") Long addressId) {
        log.debug("Processing address deletion request for ID: {}", addressId);
        
        String status = addressService.removeAddress(addressId);
        
        // Remove from cache
        String cacheKey = ADDRESS_KEY_PREFIX + addressId;
        redisTemplate.delete(cacheKey);
        
        log.info("Successfully removed address with ID: {}", addressId);
        return new ResponseEntity<String>(status, HttpStatus.OK);
    }
}
