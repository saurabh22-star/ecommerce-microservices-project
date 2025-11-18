package com.ecommerce.ecommercehub.productmodule.services;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ecommerce.ecommercehub.productmodule.dtos.GeneratePaymentLinkRequestDTO;
import com.ecommerce.ecommercehub.productmodule.dtos.GeneratePaymentLinkResponseDTO;
import com.ecommerce.ecommercehub.productmodule.entities.Order;
import com.ecommerce.ecommercehub.productmodule.entities.Payment;
import com.ecommerce.ecommercehub.productmodule.entities.TransactionStatus;
import com.ecommerce.ecommercehub.productmodule.repositories.PaymentRepo;

@Service
public class PaymentService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private PaymentRepo paymentRepo;

    public Payment processPayment(Order orderDetails, String method) {
        String paymentServiceUrl = "http://PaymentProcessorService/payment/generatePaymentLink";

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Content-Type", "application/json");

        GeneratePaymentLinkRequestDTO paymentRequest = new GeneratePaymentLinkRequestDTO(orderDetails.getOrderId(), method);
        HttpEntity<GeneratePaymentLinkRequestDTO> httpEntity = new HttpEntity<>(paymentRequest, httpHeaders);

        ResponseEntity<GeneratePaymentLinkResponseDTO> paymentResponse = restTemplate.exchange(
                paymentServiceUrl,
                HttpMethod.GET,
                httpEntity,
                GeneratePaymentLinkResponseDTO.class
        );

        Payment newPayment = new Payment();
        newPayment.setRelatedOrder(orderDetails);
        newPayment.setPaymentMethod(method);
        newPayment.setPaymentLink(Objects.requireNonNull(paymentResponse.getBody()).getPaymentLink());
        newPayment.setTransactionStatus(TransactionStatus.COMPLETED);

        paymentRepo.save(newPayment);

        return newPayment;
    }


}
