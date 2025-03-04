package com.example.airbnb.airbnb_app.service;

import com.razorpay.Order;

import java.math.BigDecimal;

public interface CheckoutService {
    Order createRazorpayOrder(Long bookingId, BigDecimal amount);

    void cancelRazorPayOrder(String orderId);
}

