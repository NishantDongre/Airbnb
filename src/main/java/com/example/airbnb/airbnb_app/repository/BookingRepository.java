package com.example.airbnb.airbnb_app.repository;

import com.example.airbnb.airbnb_app.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByPaymentOrderId(String sessionId);
}