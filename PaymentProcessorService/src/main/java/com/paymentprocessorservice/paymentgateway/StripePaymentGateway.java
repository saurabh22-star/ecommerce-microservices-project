package com.paymentprocessorservice.paymentgateway;

import org.springframework.stereotype.Component;

import com.paymentprocessorservice.dto.OrderDTO;
import com.paymentprocessorservice.exceptions.PaymentLinkGenerationException;
import com.stripe.StripeClient;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentLink;
import com.stripe.param.PaymentLinkCreateParams;

@Component
public class StripePaymentGateway implements PaymentGateway {
    private final StripeClient client;

    public StripePaymentGateway(StripeClient client) {
        this.client = client;
    }

    @Override
    public String generatePaymentLink(OrderDTO order) throws PaymentLinkGenerationException {
        String linkUrl = null;
        try {
            PaymentLinkCreateParams params = PaymentLinkCreateParams.builder()
                .addLineItem(
                    PaymentLinkCreateParams.LineItem.builder()
                        .setPrice(String.valueOf(order.getTotalAmount()))
                        .setQuantity(1L)
                        .build()
                )
                .setAfterCompletion(
                    PaymentLinkCreateParams.AfterCompletion.builder()
                        .setType(PaymentLinkCreateParams.AfterCompletion.Type.REDIRECT)
                        .setRedirect(
                            PaymentLinkCreateParams.AfterCompletion.Redirect.builder()
                                .setUrl("https://scalerDemo.com")
                                .build()
                        )
                        .build()
                )
                .build();

            PaymentLink createdLink = client.paymentLinks().create(params);
            linkUrl = createdLink.getUrl();
        } catch (StripeException e) {
            throw new PaymentLinkGenerationException(e);
        }
        return linkUrl;
    }
}