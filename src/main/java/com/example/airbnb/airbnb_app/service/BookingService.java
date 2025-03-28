package com.example.airbnb.airbnb_app.service;

import com.example.airbnb.airbnb_app.dto.BookingDto;
import com.example.airbnb.airbnb_app.dto.BookingRequest;
import com.example.airbnb.airbnb_app.dto.HotelReportDto;
import com.example.airbnb.airbnb_app.enums.BookingStatus;

import java.time.LocalDate;
import java.util.List;

public interface BookingService {

    BookingDto initialiseBooking(BookingRequest bookingRequest);

    BookingDto addGuests(Long bookingId, List<Long> guestIdList);

    String initiatePayments(Long bookingId);

    void capturePayment(String paymentOrderId);

    void cancelBooking(Long bookingId);

    BookingStatus getBookingStatus(Long bookingId);

    List<BookingDto> getAllBookingsByHotelId(Long hotelId);

    HotelReportDto getHotelReport(Long hotelId, LocalDate startDate, LocalDate endDate);

    List<BookingDto> getMyBookings();
}
