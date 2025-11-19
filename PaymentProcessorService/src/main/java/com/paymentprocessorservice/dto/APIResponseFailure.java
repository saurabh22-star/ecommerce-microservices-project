package com.paymentprocessorservice.dto;

import java.util.Arrays;

import lombok.Getter;

@Getter
public class APIResponseFailure extends APIResponse {
    private final String message;
    private final String stackTrace;

    public APIResponseFailure(Exception ex) {
        this.message = ex.getMessage();
        this.stackTrace = Arrays.toString(ex.getStackTrace());
    }
}
