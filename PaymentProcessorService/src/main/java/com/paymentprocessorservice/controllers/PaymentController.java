package com.paymentprocessorservice.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paymentprocessorservice.dto.APIResponse;
import com.paymentprocessorservice.dto.APIResponseFailure;
import com.paymentprocessorservice.dto.APIResponseSuccess;
import com.paymentprocessorservice.dto.GeneratePaymentLinkRequestDTO;
import com.paymentprocessorservice.dto.GeneratePaymentLinkResponseDTO;
import com.paymentprocessorservice.exceptions.InvalidPaymentGatewayException;
import com.paymentprocessorservice.exceptions.PaymentLinkGenerationException;
import com.paymentprocessorservice.services.PaymentService;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

   @PostMapping("/generatePaymentLink")
    public ResponseEntity<APIResponse> createPaymentLink(@RequestBody GeneratePaymentLinkRequestDTO request) {
        APIResponse apiResponse;
        HttpStatus status = HttpStatus.CREATED;
        try {
            String link = paymentService.createPaymentLink(request.getOrderId(), request.getPgVendor());
            apiResponse = new APIResponseSuccess<>(new GeneratePaymentLinkResponseDTO(request.getOrderId(), link));
        } catch (PaymentLinkGenerationException | InvalidPaymentGatewayException ex) {
            apiResponse = new APIResponseFailure(ex);
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return ResponseEntity.status(status).body(apiResponse);
    }
}
