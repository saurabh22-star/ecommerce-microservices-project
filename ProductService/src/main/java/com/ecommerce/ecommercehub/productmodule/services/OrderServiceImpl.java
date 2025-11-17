package com.ecommerce.ecommercehub.productmodule.services;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.ecommerce.ecommercehub.productmodule.dtos.OrderDTO;
import com.ecommerce.ecommercehub.productmodule.dtos.OrderResponseDTO;
import com.ecommerce.ecommercehub.productmodule.dtos.UserDTO;
import com.ecommerce.ecommercehub.productmodule.entities.Order;
import com.ecommerce.ecommercehub.productmodule.exceptions.APIException;
import com.ecommerce.ecommercehub.productmodule.exceptions.ResourceNotFoundException;
import com.ecommerce.ecommercehub.productmodule.repositories.OrderRepo;

public class OrderServiceImpl implements OrderService {

    @Autowired
	public OrderRepo orderRepo;


    @Autowired
	public ModelMapper modelMapper;

    @Override
    public OrderResponseDTO fetchAllOrders(Integer pageNo, Integer size, String sortBy, String sortOrder) {

        Sort sort = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, size, sort);

        Page<Order> orderPage = orderRepo.findAll(pageable);

        List<OrderDTO> dtos = orderPage.getContent().stream()
            .map(order -> modelMapper.map(order, OrderDTO.class))
            .collect(Collectors.toList());

        if (dtos.isEmpty()) {
            throw new APIException("No orders found for any user.");
        }

        OrderResponseDTO response = new OrderResponseDTO();
        response.setContent(dtos);
        response.setPageNumber(orderPage.getNumber());
        response.setPageSize(orderPage.getSize());
        response.setTotalElements(orderPage.getTotalElements());
        response.setTotalPages(orderPage.getTotalPages());
        response.setLastPage(orderPage.isLast());

        return response;
    }

    @Override
    public List<OrderDTO> fetchOrdersForUser(UserDTO user) {
        List<Order> userOrders = orderRepo.findAllByUserId(user.getUserId());

        List<OrderDTO> dtos = userOrders.stream()
            .map(order -> modelMapper.map(order, OrderDTO.class))
            .collect(Collectors.toList());

        if (dtos.isEmpty()) {
            throw new APIException("No orders found for user: " + user.getEmail());
        }

        return dtos;
    }

    @Override
    public OrderDTO fetchOrder(Long userIdentifier, Long orderIdentifier) {
        Order foundOrder = orderRepo.findOrderByEmailAndOrderId(userIdentifier, orderIdentifier);

        if (foundOrder == null) {
            throw new ResourceNotFoundException("Order not found", "orderIdentifier", orderIdentifier);
        }

        return modelMapper.map(foundOrder, OrderDTO.class);
    }

   @Override
    public OrderDTO fetchOrder(Long id) {
        Order foundOrder = orderRepo.findOrderByOrderId(id);
        if (foundOrder == null) {
            throw new ResourceNotFoundException("Order not found", "orderIdentifier", id);
        }
        return modelMapper.map(foundOrder, OrderDTO.class);
    }
    


}
