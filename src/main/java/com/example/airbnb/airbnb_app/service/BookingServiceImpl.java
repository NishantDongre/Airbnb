package com.example.airbnb.airbnb_app.service;

import com.example.airbnb.airbnb_app.dto.BookingDto;
import com.example.airbnb.airbnb_app.dto.BookingRequest;
import com.example.airbnb.airbnb_app.dto.HotelReportDto;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.example.airbnb.airbnb_app.util.AppUtils.getCurrentUser;

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
    public BookingDto addGuests(Long bookingId, List<Long> guestIdList) {

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

        for (Long guestId: guestIdList) {
            Guest guest = guestRepository.findById(guestId).orElseThrow(() -> new ResourceNotFoundException("Guest Not found with gustId: " + guestId));
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

        LocalDate currentDate = LocalDate.now();
        if (!currentDate.isBefore(booking.getCheckInDate())) {
            throw new IllegalStateException("Booking cannot be cancelled on or after the check-in date");
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

    public BookingStatus getBookingStatus(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new ResourceNotFoundException("Booking not found with id: "+bookingId)
        );
        User user = getCurrentUser();
        if (!user.equals(booking.getUser())) {
            throw new UnAuthorisedException("Booking does not belong to this user with id: "+user.getId());
        }

        return booking.getBookingStatus();
    }


    @Override
    public List<BookingDto> getAllBookingsByHotelId(Long hotelId) {
        Hotel hotel = hotelRepository.findById(hotelId).orElseThrow(() -> new ResourceNotFoundException("Hotel not " +
                "found with ID: "+ hotelId));
        User user = getCurrentUser();

        log.info("Getting all booking for the hotel with ID: {}", hotelId);

        if(!user.equals(hotel.getOwner())) throw new AccessDeniedException("You are not the owner of hotel with id: "+ hotelId);

        List<Booking> bookings = bookingRepository.findByHotel(hotel);

        return bookings.stream()
                .map((element) -> modelMapper.map(element, BookingDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public HotelReportDto getHotelReport(Long hotelId, LocalDate startDate, LocalDate endDate) {

        Hotel hotel = hotelRepository.findById(hotelId).orElseThrow(() -> new ResourceNotFoundException("Hotel not " +
                "found with ID: "+ hotelId));
        User user = getCurrentUser();

        log.info("Generating report for hotel with ID: {}", hotelId);

        if(!user.equals(hotel.getOwner())) throw new AccessDeniedException("You are not the owner of hotel with id: "+ hotelId);

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        List<Booking> bookings = bookingRepository.findByHotelAndCreatedAtBetween(hotel, startDateTime, endDateTime);

        // Total Confirm Booking
        long totalConfirmedBookings = bookings
                .stream()
                .filter(booking -> booking.getBookingStatus() == BookingStatus.CONFIRMED)
                .count();

        // Total revenue of the confirm booking
        BigDecimal totalRevenueOfConfirmedBookings = bookings.stream()
                .filter(booking -> booking.getBookingStatus() == BookingStatus.CONFIRMED)
                .map(Booking::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Average Revenue
        BigDecimal avgRevenue = totalConfirmedBookings == 0 ? BigDecimal.ZERO :
                totalRevenueOfConfirmedBookings.divide(BigDecimal.valueOf(totalConfirmedBookings), RoundingMode.HALF_UP);

        // Booking Cancellation
        Long totalCanceledBookings = bookings.stream()
                .filter(booking -> booking.getBookingStatus() == BookingStatus.CANCELLED)
                .count();

        // Most Booked Room Type
        Map<String, Long> roomTypeCounts = bookings.stream()
                .filter(booking -> booking.getBookingStatus() == BookingStatus.CONFIRMED)
                .map(booking -> booking.getRoom().getType())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        String mostBookedRoomType = roomTypeCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");

        // Peak Booking Time
        Map<Integer, Long> bookingHours = bookings.stream()
                .map(booking -> booking.getCreatedAt().getHour()) // Extracting booking hour
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        String peakBookingHour = bookingHours.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(entry -> {
                    int hour = entry.getKey();
                    return String.format("%02d:00 %s", (hour % 12 == 0) ? 12 : (hour % 12), (hour < 12) ? "AM" : "PM");
                })
                .orElse("No bookings");


        // Revenue Per Room Type
        Map<String, BigDecimal> revenueByRoomType = bookings.stream()
                .filter(booking -> booking.getBookingStatus() == BookingStatus.CONFIRMED)
                .collect(Collectors.groupingBy(
                        booking -> booking.getRoom().getType(),
                        Collectors.reducing(BigDecimal.ZERO, Booking::getAmount, BigDecimal::add)
                ));

        // Repeat Customer Rate
        Map<Long, Long> customerBookingCounts = bookings.stream()
                .collect(Collectors.groupingBy(booking -> booking.getUser().getId(), Collectors.counting()));

        long repeatCustomers = customerBookingCounts.values().stream()
                .filter(count -> count > 1)
                .count();

        long totalUniqueCustomers = customerBookingCounts.size();

        BigDecimal repeatCustomerRate = (totalUniqueCustomers == 0) ? BigDecimal.ZERO :
                BigDecimal.valueOf(repeatCustomers * 100)
                        .divide(BigDecimal.valueOf(totalUniqueCustomers), 2, RoundingMode.HALF_UP);

        return new HotelReportDto(totalConfirmedBookings, totalCanceledBookings, totalRevenueOfConfirmedBookings, avgRevenue, mostBookedRoomType, peakBookingHour, revenueByRoomType, repeatCustomerRate);
    }

    @Override
    public List<BookingDto> getMyBookings() {
        User user = getCurrentUser();

        return bookingRepository.findByUser(user)
                .stream()
                .map((element) -> modelMapper.map(element, BookingDto.class))
                .collect(Collectors.toList());
    }

    public boolean hasBookingExpired(Booking booking) {
        return booking.getCreatedAt().plusMinutes(10).isBefore(LocalDateTime.now());
    }
}