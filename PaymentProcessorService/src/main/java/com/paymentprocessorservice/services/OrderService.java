package com.paymentprocessorservice.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.paymentprocessorservice.dto.OrderDTO;

@Service
public class OrderService {

   @Autowired
   private RestTemplate restTemplate;

    public OrderDTO getOrderById(Long orderId)
    {
        return restTemplate.getForObject("http://UserService/api/public/orders/{orderId}", OrderDTO.class, orderId);
    }
}
