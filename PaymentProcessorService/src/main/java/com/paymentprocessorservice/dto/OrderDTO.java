package com.paymentprocessorservice.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
	
	private Long orderId;
	private String email;
	private LocalDate orderDate;
	private Double totalAmount;
	private String orderStatus;

}
