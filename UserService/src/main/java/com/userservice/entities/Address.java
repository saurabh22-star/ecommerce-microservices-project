package com.userservice.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "addresses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    private Long addressId;

    @NotBlank(message = "Street is required")
    @Size(min = 5, message = "Street must be at least 5 characters")
    @Column(name = "street", length = 150, nullable = false)
    private String street;

    @NotBlank(message = "Building name is required")
    @Size(min = 2, message = "Building name must be at least 2 characters")
    @Column(name = "building_name", length = 100)
    private String buildingName;

    @NotBlank(message = "City is required")
    @Size(min = 2, message = "City must be at least 2 characters")
    @Column(name = "city", length = 80, nullable = false)
    private String city;

    @NotBlank(message = "State is required")
    @Size(min = 2, message = "State must be at least 2 characters")
    @Column(name = "state", length = 80, nullable = false)
    private String state;

    @NotBlank(message = "Country is required")
    @Size(min = 2, message = "Country must be at least 2 characters")
    @Column(name = "country", length = 80, nullable = false)
    private String country;

    @NotBlank(message = "Pincode is required")
    @Size(min = 5, max = 10, message = "Pincode must be between 5 and 10 characters")
    @Column(name = "pincode", length = 10, nullable = false)
    private String pincode;

    @JsonIgnore
    @ManyToMany(mappedBy = "addresses")
    private List<User> users = new ArrayList<>();

    public Address(String countryValue, String stateValue, String cityValue, String pincodeValue, String streetValue, String buildingValue) {
        this.country = countryValue;
        this.state = stateValue;
        this.city = cityValue;
        this.pincode = pincodeValue;
        this.street = streetValue;
        this.buildingName = buildingValue;
    }
    
    public Address(Long id, String stateValue, String cityValue, String pincodeValue, String streetValue) {
        this.addressId = id;
        this.state = stateValue;
        this.city = cityValue;
        this.pincode = pincodeValue;
        this.street = streetValue;
    }
}