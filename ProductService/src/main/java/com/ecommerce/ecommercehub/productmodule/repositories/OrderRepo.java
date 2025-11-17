package com.ecommerce.ecommercehub.productmodule.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ecommerce.ecommercehub.productmodule.entities.Order;

@Repository
public interface OrderRepo extends JpaRepository<Order, Long> {

    List<Order> findAllByUserId(Long userId);

    @Query("SELECT o FROM Order o WHERE o.userId = ?1 AND o.orderId = ?2")
	Order findOrderByEmailAndOrderId(Long userId, Long cartId);

    Order findOrderByOrderId(Long orderId);
    

}
