package com.ecommerce.ecommercehub.productmodule.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class APIResponseDTO {
	private String message;
	private boolean status;
}
