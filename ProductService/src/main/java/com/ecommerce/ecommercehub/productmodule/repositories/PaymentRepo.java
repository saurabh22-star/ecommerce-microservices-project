package com.ecommerce.ecommercehub.productmodule.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecommerce.ecommercehub.productmodule.entities.Payment;

@Repository
public interface PaymentRepo extends JpaRepository<Payment, Long>{

}

