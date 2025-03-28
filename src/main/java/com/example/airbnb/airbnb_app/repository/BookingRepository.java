package com.example.airbnb.airbnb_app.repository;

import com.example.airbnb.airbnb_app.entity.Booking;
import com.example.airbnb.airbnb_app.entity.Hotel;
import com.example.airbnb.airbnb_app.entity.User;
import com.example.airbnb.airbnb_app.enums.BookingStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByPaymentOrderId(String sessionId);

    List<Booking> findByHotel(Hotel hotel);

    List<Booking> findByHotelAndCreatedAtBetween(Hotel hotel, LocalDateTime startDateTime, LocalDateTime endDateTime);

    List<Booking> findByUser(User user);

    @Query("""
            SELECT b
            FROM Booking b
            LEFT JOIN FETCH b.guests g
            WHERE b.id = :bookingId
            """)
    Optional<Booking> findBookingById(Long bookingId);

    @Query("SELECT b FROM Booking b WHERE b.bookingStatus IN :statuses AND b.createdAt <= :expiryTime")
    List<Booking> findExpiredBookings(List<BookingStatus> statuses, LocalDateTime expiryTime);
}