package com.ecommerce.ecommercehub.productmodule.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ecommerce.ecommercehub.productmodule.dtos.UserDTO;

@Service
public class UserService {

    @Autowired
    private RestTemplate restTemplate;

    public UserDTO getUserByEmail(String email){
        return restTemplate.getForObject("http://UserServiceApplication/api/user/email/{email}", UserDTO.class, email);
    }

}