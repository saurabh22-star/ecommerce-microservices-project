package com.paymentprocessorservice.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stripe.model.Event;

@RestController
@RequestMapping("/stripeWebhook")
public class StripeWebhookController {

    @PostMapping
    public void handleStripeEvent(@RequestBody Event stripeEvent) {
        System.out.println("Received Stripe event: " + stripeEvent.getType());
    }
}
