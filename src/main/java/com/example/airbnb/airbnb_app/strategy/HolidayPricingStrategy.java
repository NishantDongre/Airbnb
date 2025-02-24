package com.example.airbnb.airbnb_app.strategy;

import com.example.airbnb.airbnb_app.entity.Inventory;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class HolidayPricingStrategy implements PricingStrategy{

    private final PricingStrategy wrapped;

    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        BigDecimal price = wrapped.calculatePrice(inventory);
        boolean isHolidaySeason = true; // call an API or check with local data
        if (isHolidaySeason) {
            price = price.multiply(BigDecimal.valueOf(1.05));
        }
        return price;
    }
}
