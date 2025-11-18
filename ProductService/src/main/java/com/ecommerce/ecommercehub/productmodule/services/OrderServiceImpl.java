package com.ecommerce.ecommercehub.productmodule.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.ecommerce.ecommercehub.productmodule.dtos.OrderDTO;
import com.ecommerce.ecommercehub.productmodule.dtos.OrderItemDTO;
import com.ecommerce.ecommercehub.productmodule.dtos.OrderResponseDTO;
import com.ecommerce.ecommercehub.productmodule.dtos.UserDTO;
import com.ecommerce.ecommercehub.productmodule.entities.Cart;
import com.ecommerce.ecommercehub.productmodule.entities.CartItem;
import com.ecommerce.ecommercehub.productmodule.entities.Order;
import com.ecommerce.ecommercehub.productmodule.entities.OrderItem;
import com.ecommerce.ecommercehub.productmodule.entities.Payment;
import com.ecommerce.ecommercehub.productmodule.entities.Product;
import com.ecommerce.ecommercehub.productmodule.entities.PurchaseStatus;
import com.ecommerce.ecommercehub.productmodule.exceptions.APIException;
import com.ecommerce.ecommercehub.productmodule.exceptions.ResourceNotFoundException;
import com.ecommerce.ecommercehub.productmodule.repositories.CartRepo;
import com.ecommerce.ecommercehub.productmodule.repositories.OrderItemRepo;
import com.ecommerce.ecommercehub.productmodule.repositories.OrderRepo;

import jakarta.transaction.Transactional;


@Transactional
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
	public OrderRepo orderRepo;

    @Autowired
	public ModelMapper modelMapper;

    @Autowired
	public CartRepo cartRepo;

    @Autowired
	public OrderItemRepo orderItemRepo;

    @Autowired
	public CartService cartService;

    @Autowired
	public PaymentService paymentService;

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

    @Override
    public OrderDTO placeOrder(Long userIdentifier, Long cartIdentifier, String paymentType) {
        Cart userCart = cartRepo.findCartByEmailAndCartId(userIdentifier, cartIdentifier);

        if (userCart == null) {
            throw new ResourceNotFoundException("Cart not found", "cartIdentifier", cartIdentifier);
        }

        if (userCart.getCartItems().isEmpty()) {
            throw new APIException("The cart contains no items.");
        }

        Order newOrder = new Order();
        newOrder.setUserId(userIdentifier);
        newOrder.setOrderDate(LocalDate.now());
        newOrder.setTotalAmount(userCart.getTotalPrice());
        newOrder.setPurchaseStatus(PurchaseStatus.AWAITING_CONFIRMATION);

        Order persistedOrder = orderRepo.save(newOrder);

        List<OrderItem> itemsToOrder = new ArrayList<>();
        for (CartItem cartEntry : userCart.getCartItems()) {
            OrderItem item = new OrderItem();
            item.setProduct(cartEntry.getProduct());
            item.setQuantity(cartEntry.getQuantity());
            item.setDiscount(cartEntry.getDiscount());
            item.setOrderedProductPrice(cartEntry.getProductPrice());
            item.setOrder(persistedOrder);
            itemsToOrder.add(item);
        }

        itemsToOrder = orderItemRepo.saveAll(itemsToOrder);

        userCart.getCartItems().forEach(entry -> {
            int qty = entry.getQuantity();
            Product prod = entry.getProduct();
            cartService.removeProductFromCart(cartIdentifier, prod.getProductId());
            prod.setQuantity(prod.getQuantity() - qty);
        });

        Payment paymentResult = paymentService.processPayment(newOrder, paymentType);
        newOrder.setPayment(paymentResult);

        OrderDTO resultOrderDTO = modelMapper.map(newOrder, OrderDTO.class);
        itemsToOrder.forEach(item -> resultOrderDTO.getOrderItems().add(modelMapper.map(item, OrderItemDTO.class)));

        newOrder.setPurchaseStatus(PurchaseStatus.COMPLETED_ORDER);
        orderRepo.save(newOrder);

        return resultOrderDTO;
    }

    @Override
    public OrderDTO modifyUserOrderStatus(Long userIdentifier, Long orderIdentifier, String status) {
        Order foundOrder = orderRepo.findOrderByEmailAndOrderId(userIdentifier, orderIdentifier);

        if (foundOrder == null) {
            throw new ResourceNotFoundException("Order not found", "orderIdentifier", orderIdentifier);
        }

        switch (status) {
            case "COMPLETED_ORDER":
                foundOrder.setPurchaseStatus(PurchaseStatus.COMPLETED_ORDER);
                break;
            case "DISPATCHED":
                foundOrder.setPurchaseStatus(PurchaseStatus.DISPATCHED);
                break;
            case "ORDER_CANCELLED":
                foundOrder.setPurchaseStatus(PurchaseStatus.ORDER_CANCELLED);
                break;
            default:
                throw new APIException("Provided order status is invalid");
        }

        orderRepo.save(foundOrder); // Persist the changes
        return modelMapper.map(foundOrder, OrderDTO.class);
    }
    


}
