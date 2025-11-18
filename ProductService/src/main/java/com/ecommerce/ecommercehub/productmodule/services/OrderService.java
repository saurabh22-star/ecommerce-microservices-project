package com.ecommerce.ecommercehub.productmodule.services;

import java.util.List;

import com.ecommerce.ecommercehub.productmodule.dtos.OrderDTO;
import com.ecommerce.ecommercehub.productmodule.dtos.OrderResponseDTO;
import com.ecommerce.ecommercehub.productmodule.dtos.UserDTO;


public interface OrderService {

    OrderResponseDTO fetchAllOrders(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
	
    List<OrderDTO> fetchOrdersForUser(UserDTO userDTO);

    OrderDTO fetchOrder(Long userId, Long orderId);

    OrderDTO fetchOrder(Long orderId);

    OrderDTO placeOrder(Long userId, Long cartId, String paymentMethod);

    OrderDTO modifyUserOrderStatus(Long userId, Long orderId, String orderStatus);

}
