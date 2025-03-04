package com.example.airbnb.airbnb_app.service;

import com.razorpay.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RazorpayWebhookService {

    @Value("${razorpay.webhook.secret}")
    private String apiSecret;

    public void verifyWebhookSignature(String payload, String signature) throws Exception {
        String expectedSignature = Utils.getHash(payload, apiSecret);
        if (!expectedSignature.equals(signature)) {
            log.error("Error while matching the razorpay signature for webhook");
            throw new RuntimeException("Invalid webhook signature");
        }
    }
}
