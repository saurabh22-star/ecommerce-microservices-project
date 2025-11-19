package com.paymentprocessorservice.utility;

import org.springframework.stereotype.Component;

import com.paymentprocessorservice.dto.PGVendor;
import com.paymentprocessorservice.exceptions.InvalidPaymentGatewayException;
import com.paymentprocessorservice.paymentgateway.PaymentGateway;
import com.paymentprocessorservice.paymentgateway.RazorpayPaymentGateway;
import com.paymentprocessorservice.paymentgateway.StripePaymentGateway;

@Component
public class PaymentGatewaySelectorStrategy {
    private final RazorpayPaymentGateway razorpayPaymentGateway;
    private final StripePaymentGateway stripePaymentGateway;

    public PaymentGatewaySelectorStrategy(RazorpayPaymentGateway razorpayPaymentGateway, StripePaymentGateway stripePaymentGateway) {
        this.razorpayPaymentGateway = razorpayPaymentGateway;
        this.stripePaymentGateway = stripePaymentGateway;
    }

    public PaymentGateway getPaymentGateway(PGVendor pgVendor) throws InvalidPaymentGatewayException {
        if (pgVendor == PGVendor.RAZORPAY) {
            return razorpayPaymentGateway;
        } else if (pgVendor == PGVendor.STRIPE) {
            return stripePaymentGateway;
        }
        throw new InvalidPaymentGatewayException("Unable to generate object of payment gateway of type : "+ pgVendor);
    }
}
