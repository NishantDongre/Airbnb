package com.example.airbnb.airbnb_app.service;

import com.example.airbnb.airbnb_app.entity.Booking;
import com.example.airbnb.airbnb_app.enums.BookingStatus;
import com.example.airbnb.airbnb_app.repository.BookingRepository;
import com.example.airbnb.airbnb_app.repository.InventoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingExpirationService {

    private final BookingRepository bookingRepository;
    private final InventoryRepository inventoryRepository;

    // Runs every 10 minutes
    @Scheduled(cron = "0 */10 * * * *")
    @Transactional
    public void expireOldBookings() {
        log.info("Running Booking Expiration Job...");

        // Get current timestamp minus 10 minutes
        LocalDateTime tenMinutesAgo = LocalDateTime.now().minusMinutes(10);

        List<BookingStatus> statusesToExpire = List.of(
                BookingStatus.RESERVED,
                BookingStatus.GUESTS_ADDED,
                BookingStatus.PAYMENTS_PENDING
        );

        List<Booking> expiredBookings = bookingRepository.findExpiredBookings(statusesToExpire, tenMinutesAgo);

        if (expiredBookings.isEmpty()) {
            log.info("No bookings to expire.");
            return;
        }

        for (Booking booking : expiredBookings) {
            log.info("Expiring Booking ID: {}", booking.getId());

            booking.setBookingStatus(BookingStatus.EXPIRED);
            bookingRepository.save(booking);

            inventoryRepository.releaseExpiredInventory(booking.getRoom().getId(),
                    booking.getCheckInDate(), booking.getCheckOutDate(), booking.getRoomsCount());

            log.info("Booking ID {} expired and inventory released.", booking.getId());
        }
    }
}