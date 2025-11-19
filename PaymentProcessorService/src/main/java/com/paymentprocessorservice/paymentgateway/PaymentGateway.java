package com.paymentprocessorservice.paymentgateway;

import com.paymentprocessorservice.dto.OrderDTO;
import com.paymentprocessorservice.exceptions.PaymentLinkGenerationException;

public interface PaymentGateway {
    String generatePaymentLink(OrderDTO orderDTO) throws PaymentLinkGenerationException;
}
