package com.example.airbnb.airbnb_app.strategy;

import com.example.airbnb.airbnb_app.entity.Inventory;

import java.math.BigDecimal;
public interface PricingStrategy {

    BigDecimal calculatePrice(Inventory inventory);
}
