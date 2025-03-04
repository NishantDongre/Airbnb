package com.example.airbnb.airbnb_app.service;


import com.razorpay.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CheckoutServiceImpl implements CheckoutService {

    @Value("${razorpay.api.key}")
    private String apiKey;

    @Value("${razorpay.api.secret}")
    private String apiSecret;

    @Override
    public Order createRazorpayOrder(Long bookingId, BigDecimal amount) {
        try {
            RazorpayClient razorpay = new RazorpayClient(apiKey, apiSecret);
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amount.multiply(new BigDecimal(100))); // Convert to paise
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", bookingId.toString());

            Order paymentorder = razorpay.orders.create(orderRequest);
            log.info("Order created successfully for booking with ID: {}", bookingId);
            return paymentorder;
        } catch (Exception ex) {
            log.error("RazorpayException: ", ex);
            throw new RuntimeException("Error creating Razorpay order", ex);
        }
    }

    @Override
    public void cancelRazorPayOrder(String orderId) {
        try{
            RazorpayClient razorpay = new RazorpayClient(apiKey, apiSecret);

            List<Payment> paymentList = razorpay.orders.fetchPayments(orderId);
            String paymentId = paymentList.getFirst().get("id");

            Refund refund = razorpay.payments.refund(paymentId);
            log.info("Payment refunded successfully for order with ID: {}", orderId);
        }catch (RazorpayException ex){
            log.error("RazorpayException: ", ex);
            throw new RuntimeException("Error cancelling Razorpay order", ex);
        }
    }
}
