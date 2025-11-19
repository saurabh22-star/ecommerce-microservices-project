package com.paymentprocessorservice.dto;

import lombok.Getter;

@Getter
public class APIResponse {
    private final String traceId;

    public APIResponse() {
        this.traceId = Long.valueOf(System.currentTimeMillis()).toString();
    }
}

