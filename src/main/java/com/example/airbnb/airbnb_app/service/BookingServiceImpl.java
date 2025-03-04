package com.example.airbnb.airbnb_app.service;

import com.example.airbnb.airbnb_app.dto.BookingDto;
import com.example.airbnb.airbnb_app.dto.BookingRequest;
import com.example.airbnb.airbnb_app.dto.GuestDto;
import com.example.airbnb.airbnb_app.entity.*;
import com.example.airbnb.airbnb_app.enums.BookingStatus;
import com.example.airbnb.airbnb_app.exception.ResourceNotFoundException;
import com.example.airbnb.airbnb_app.exception.UnAuthorisedException;
import com.example.airbnb.airbnb_app.repository.*;
import com.example.airbnb.airbnb_app.strategy.PricingService;
import com.razorpay.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService{
    private final BookingRepository bookingRepository;
    private final InventoryRepository inventoryRepository;
    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final GuestRepository guestRepository;
    private final ModelMapper modelMapper;
    private final CheckoutService checkoutService;
    private final PricingService pricingService;

    @Value("${frontend.url}")
    private String frontendUrl;


    @Override
    @Transactional
    public BookingDto initialiseBooking(BookingRequest bookingRequest) {

        Long hotelId = bookingRequest.getHotelId();
        Long roomId = bookingRequest.getRoomId();
        LocalDate checkInDate = bookingRequest.getCheckInDate();
        LocalDate checkOutDate = bookingRequest.getCheckOutDate();
        Integer roomsCount = bookingRequest.getRoomsCount();

        log.info("Initialising the Booking for Hotel: {}, Room: {} between Date: {} and {}",
                hotelId,
                roomId,
                checkInDate,
                checkOutDate
                );

        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id: " +  hotelId));

        Room room = roomRepository
                .findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + roomId));

        List<Inventory> inventoryList = inventoryRepository
                .findAndLockAvailableInventory(
                        room.getId(),
                        checkInDate,
                        checkOutDate,
                        roomsCount
                );

        long dayCount = ChronoUnit.DAYS.between(checkInDate, checkOutDate) + 1;

        if(inventoryList.size() != dayCount){
            throw new IllegalStateException("Room not available anymore");
        }

        // Reserve the room
        inventoryRepository.initBooking(room.getId(), bookingRequest.getCheckInDate(),
                bookingRequest.getCheckOutDate(), bookingRequest.getRoomsCount());

        BigDecimal priceForOneRoom = pricingService.calculateTotalPrice(inventoryList);
        BigDecimal totalPrice = priceForOneRoom.multiply(BigDecimal.valueOf(bookingRequest.getRoomsCount()));


        // Create the Booking
        Booking booking = Booking.builder()
                .bookingStatus(BookingStatus.RESERVED)
                .hotel(hotel)
                .room(room)
                .user(getCurrentUser())
                .checkInDate(bookingRequest.getCheckInDate())
                .checkOutDate(bookingRequest.getCheckOutDate())
                .roomsCount(bookingRequest.getRoomsCount())
                .amount(totalPrice)
                .build();

        booking = bookingRepository.save(booking);

        return modelMapper.map(booking, BookingDto.class);
    }

    @Override
    @Transactional
    public BookingDto addGuests(Long bookingId, List<GuestDto> guestDtoList) {

        log.info("Adding guests for booking with id: {}", bookingId);

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new ResourceNotFoundException("Booking not found with id: "+ bookingId));

        User user = getCurrentUser();

        if (!user.equals(booking.getUser())) {
            throw new UnAuthorisedException("Booking does not belong to this user with id: "+user.getId());
        }

        if (hasBookingExpired(booking)) {
            throw new IllegalStateException("Booking has already expired");
        }

        if(booking.getBookingStatus() != BookingStatus.RESERVED) {
            throw new IllegalStateException("Booking is not under reserved state, cannot add guests");
        }

        for (GuestDto guestDto: guestDtoList) {
            Guest guest = modelMapper.map(guestDto, Guest.class);
            guest.setUser(user);
            guest = guestRepository.save(guest);
            booking.getGuests().add(guest);
        }

        booking.setBookingStatus(BookingStatus.GUESTS_ADDED);
        booking = bookingRepository.save(booking);
        return modelMapper.map(booking, BookingDto.class);
    }

    @Override
    @Transactional
    public String initiatePayments(Long bookingId) {
        log.info("Initialising payment for booking with Id: {}", bookingId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new ResourceNotFoundException("Booking not found with id: "+ bookingId)
        );

        User user = getCurrentUser();
        if (!user.equals(booking.getUser())) {
            throw new UnAuthorisedException("Booking does not belong to this user with id: "+ user.getId());
        }
        if (hasBookingExpired(booking)) {
            throw new IllegalStateException("Booking has already expired");
        }

        Order paymentOrder = checkoutService.createRazorpayOrder(booking.getId(),booking.getAmount());

        booking.setBookingStatus(BookingStatus.PAYMENTS_PENDING);
        booking.setPaymentOrderId(paymentOrder.get("id"));
        bookingRepository.save(booking);

        String paymentUrl = frontendUrl + "?orderId=" + paymentOrder.get("id");

        return  paymentUrl;
    }

    @Override
    @Transactional
    public void capturePayment(String paymentOrderId) {
        Booking booking =
                bookingRepository.findByPaymentOrderId(paymentOrderId).orElseThrow(() ->
                        new ResourceNotFoundException("Booking not found for payment-order ID: "+ paymentOrderId));

        booking.setBookingStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);

        inventoryRepository.findAndLockReservedInventory(booking.getRoom().getId(), booking.getCheckInDate(),
                booking.getCheckOutDate(), booking.getRoomsCount());

        inventoryRepository.confirmBooking(booking.getRoom().getId(), booking.getCheckInDate(),
                booking.getCheckOutDate(), booking.getRoomsCount());

        log.info("Successfully confirmed the booking for Booking ID: {}", booking.getId());
    }

    @Override
    @Transactional
    public void cancelBooking(Long bookingId) {
        log.info("Canceling Booking for booking with Id: {}", bookingId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new ResourceNotFoundException("Booking not found with id: "+bookingId)
        );
        User user = getCurrentUser();
        if (!user.equals(booking.getUser())) {
            throw new UnAuthorisedException("Booking does not belong to this user with id: "+user.getId());
        }

        if(booking.getBookingStatus() != BookingStatus.CONFIRMED) {
            throw new IllegalStateException("Only confirmed bookings can be cancelled");
        }

        booking.setBookingStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        inventoryRepository.findAndLockReservedInventory(booking.getRoom().getId(), booking.getCheckInDate(),
                booking.getCheckOutDate(), booking.getRoomsCount());

        inventoryRepository.cancelBooking(booking.getRoom().getId(), booking.getCheckInDate(),
                booking.getCheckOutDate(), booking.getRoomsCount());

        // handling the refund
       checkoutService.cancelRazorPayOrder(booking.getPaymentOrderId());
    }

    public String getBookingStatus(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new ResourceNotFoundException("Booking not found with id: "+bookingId)
        );
        User user = getCurrentUser();
        if (!user.equals(booking.getUser())) {
            throw new UnAuthorisedException("Booking does not belong to this user with id: "+user.getId());
        }

        return booking.getBookingStatus().name();
    }

    public boolean hasBookingExpired(Booking booking) {
        return booking.getCreatedAt().plusMinutes(10).isBefore(LocalDateTime.now());
    }

    public User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
