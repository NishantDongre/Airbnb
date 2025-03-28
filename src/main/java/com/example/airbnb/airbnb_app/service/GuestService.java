package com.example.airbnb.airbnb_app.service;

import com.example.airbnb.airbnb_app.dto.GuestDto;
import java.util.List;

public interface GuestService {

    List<GuestDto> getAllGuests();

    GuestDto updateGuest(Long guestId, GuestDto guestDto);

    void deleteGuest(Long guestId);

    GuestDto addNewGuest(GuestDto guestDto);

    GuestDto getGuestById(Long guestId);
}