package com.paymentprocessorservice.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paymentprocessorservice.dto.OrderDTO;
import com.paymentprocessorservice.dto.PGVendor;
import com.paymentprocessorservice.exceptions.InvalidPaymentGatewayException;
import com.paymentprocessorservice.exceptions.PaymentLinkGenerationException;
import com.paymentprocessorservice.paymentgateway.PaymentGateway;
import com.paymentprocessorservice.utility.PaymentGatewaySelectorStrategy;

@Service
public class PaymentService {

    private final PaymentGatewaySelectorStrategy gatewaySelector;

    @Autowired
    private OrderService orderHandler;

    public PaymentService(PaymentGatewaySelectorStrategy gatewaySelector) {
        this.gatewaySelector = gatewaySelector;
    }

    public String createPaymentLink(Long orderId, PGVendor vendor) throws PaymentLinkGenerationException, InvalidPaymentGatewayException {
        OrderDTO order = orderHandler.getOrderById(orderId);
        PaymentGateway gateway = gatewaySelector.getPaymentGateway(vendor);
        return gateway.generatePaymentLink(order);
    }
}
