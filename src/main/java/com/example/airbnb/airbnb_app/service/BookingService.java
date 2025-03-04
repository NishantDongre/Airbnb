package com.example.airbnb.airbnb_app.service;

import com.example.airbnb.airbnb_app.dto.BookingDto;
import com.example.airbnb.airbnb_app.dto.BookingRequest;
import com.example.airbnb.airbnb_app.dto.GuestDto;

import java.util.List;

public interface BookingService {

    BookingDto initialiseBooking(BookingRequest bookingRequest);

    BookingDto addGuests(Long bookingId, List<GuestDto> guestDtoList);

    String initiatePayments(Long bookingId);

    void capturePayment(String paymentOrderId);

    void cancelBooking(Long bookingId);

    String getBookingStatus(Long bookingId);
}
