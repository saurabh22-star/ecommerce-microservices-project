package com.paymentprocessorservice.paymentgateway;

import com.paymentprocessorservice.dto.OrderDTO;
import com.paymentprocessorservice.exceptions.PaymentLinkGenerationException;
import com.razorpay.PaymentLink;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
public class RazorpayPaymentGateway implements PaymentGateway {

    private final RazorpayClient razorpayClient;

    public RazorpayPaymentGateway(RazorpayClient razorpayClient) {
        this.razorpayClient = razorpayClient;
    }

    @Override
    public String generatePaymentLink(OrderDTO orderDTO) throws PaymentLinkGenerationException {
        String paymentLink = null;
        Long expireBy = LocalDateTime.now().plusDays(1).atZone(ZoneId.of("Asia/Kolkata")).toInstant().toEpochMilli();

        try {
            JSONObject paymentLinkRequest = getJsonObject(orderDTO, expireBy);

            PaymentLink payment = razorpayClient.paymentLink.create(paymentLinkRequest);
            paymentLink = payment.get("short_url");
        } catch (RazorpayException ex) {
            throw new PaymentLinkGenerationException(ex);
        }
        return paymentLink;
    }

    private static JSONObject getJsonObject(OrderDTO orderDTO, Long expireBy) {
        JSONObject paymentLinkRequest = new JSONObject();
        paymentLinkRequest.put("amount", orderDTO.getTotalAmount());
        paymentLinkRequest.put("currency", "INR");
        paymentLinkRequest.put("expire_by", expireBy);
        paymentLinkRequest.put("reference_id", orderDTO.getOrderId());
        paymentLinkRequest.put("description", "Payment service requesting payment for orderId : " + orderDTO.getOrderId());

        paymentLinkRequest.put("callback_url", "http://localhost:8484");
        paymentLinkRequest.put("callback_method", "get");
        return paymentLinkRequest;
    }
}
