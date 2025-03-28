package com.example.airbnb.airbnb_app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotelReportDto {
    private Long totalConfirmedBookings;
    private Long totalCanceledBookings;
    private BigDecimal totalRevenueOfConfirmedBookings;
    private BigDecimal avgRevenue;
    private String mostBookedRoomType;
    private String peakBookingHour;
    private Map<String, BigDecimal> revenueByRoomType;
    private BigDecimal repeatCustomersRate;
}
