package com.example.airbnb.airbnb_app.controller;

import com.example.airbnb.airbnb_app.service.BookingService;
import com.example.airbnb.airbnb_app.service.RazorpayWebhookService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhook")
@RequiredArgsConstructor
@Slf4j
public class WebhookController {

    private final BookingService bookingService;
    private final RazorpayWebhookService razorpayWebhookService;

    @Value("${razorpay.webhook.secret}")
    private String endpointSecret;

    @PostMapping("/payment")
    @Operation(summary = "Payment confirmation from RazorPay for a Booking", tags = {"Razorpay Payment"})
    public ResponseEntity<Void> capturePayments(@RequestBody String payload, @RequestHeader("x-razorpay-signature") String sigHeader) {
        try {
            razorpayWebhookService.verifyWebhookSignature(payload, sigHeader);
            JSONObject event = new JSONObject(payload);

            JSONObject paymentEntity = new JSONObject(payload)
                    .getJSONObject("payload")
                    .getJSONObject("payment")
                    .getJSONObject("entity");


            if ("payment.captured".equals(event.getString("event"))) {
                log.info("Captured Payment Webhook called with payload: {}", payload);
                String paymentOrderId = paymentEntity.getString("order_id");
                bookingService.capturePayment(paymentOrderId);
            }
            return ResponseEntity.ok().build();
        } catch (Exception ex) {
            log.error("RazorPay Error: ", ex);
            throw new RuntimeException(ex);
        }
    }
}
