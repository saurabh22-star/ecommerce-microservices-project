package com.ecommerce.ecommercehub.utility.dtos;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommonApiResponse {

    private String statusMessage;
    private Object responseBody;

}
