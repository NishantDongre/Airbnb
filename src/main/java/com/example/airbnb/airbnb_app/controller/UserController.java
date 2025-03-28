package com.example.airbnb.airbnb_app.controller;

import com.example.airbnb.airbnb_app.dto.BookingDto;
import com.example.airbnb.airbnb_app.dto.GuestDto;
import com.example.airbnb.airbnb_app.dto.ProfileUpdateRequestDto;
import com.example.airbnb.airbnb_app.dto.UserDto;
import com.example.airbnb.airbnb_app.service.BookingService;
import com.example.airbnb.airbnb_app.service.GuestService;
import com.example.airbnb.airbnb_app.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final BookingService bookingService;
    private final GuestService guestService;

    @GetMapping("/profile")
    @Operation(summary = "Get my Profile", tags = {"User Profile"})
    public ResponseEntity<UserDto> getMyProfile() {
        return ResponseEntity.ok(userService.getMyProfile());
    }

    @PatchMapping("/profile")
    @Operation(summary = "Update the user profile", tags = {"User Profile"})
    public ResponseEntity<UserDto> updateProfile(@RequestBody ProfileUpdateRequestDto profileUpdateRequestDto) {
        return ResponseEntity.ok(userService.updateProfile(profileUpdateRequestDto));
    }

    @GetMapping("/myBookings")
    @Operation(summary = "Get all my previous bookings", tags = {"User Profile"})
    public ResponseEntity<List<BookingDto>> getMyBookings() {
        return ResponseEntity.ok(bookingService.getMyBookings());
    }

    @GetMapping("/guests")
    @Operation(summary = "Get all my guests", tags = {"Booking Guests"})
    public ResponseEntity<List<GuestDto>> getAllGuests() {
        return ResponseEntity.ok(guestService.getAllGuests());
    }

    @PostMapping("/guests")
    @Operation(summary = "Add a new guest to my guests list", tags = {"Booking Guests"})
    public ResponseEntity<GuestDto> addNewGuest(@RequestBody GuestDto guestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(guestService.addNewGuest(guestDto));
    }

    @PutMapping("guests/{guestId}")
    @Operation(summary = "Update a guest", tags = {"Booking Guests"})
    public ResponseEntity<GuestDto> updateGuest(@PathVariable Long guestId, @RequestBody GuestDto guestDto) {
        return ResponseEntity.ok(guestService.updateGuest(guestId, guestDto));
    }

    @DeleteMapping("guests/{guestId}")
    @Operation(summary = "Remove a guest", tags = {"Booking Guests"})
    public ResponseEntity<Void> deleteGuest(@PathVariable Long guestId) {
        guestService.deleteGuest(guestId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/guests/{guestId}")
    @Operation(summary = "Get Guest By Id", tags = {"Booking Guests"})
    public ResponseEntity<GuestDto> getGuestById(@PathVariable Long guestId){
        GuestDto guestDto = guestService.getGuestById(guestId);
        return ResponseEntity.ok(guestDto);
    }
}

