package com.example.airbnb.airbnb_app.repository;

import com.example.airbnb.airbnb_app.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {
}