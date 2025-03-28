package com.example.airbnb.airbnb_app.service;


import com.example.airbnb.airbnb_app.dto.GuestDto;
import com.example.airbnb.airbnb_app.entity.Guest;
import com.example.airbnb.airbnb_app.entity.User;
import com.example.airbnb.airbnb_app.exception.ResourceNotFoundException;
import com.example.airbnb.airbnb_app.repository.GuestRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.example.airbnb.airbnb_app.util.AppUtils.getCurrentUser;

@Service
@RequiredArgsConstructor
@Slf4j
public class GuestServiceImpl implements GuestService {

    private final GuestRepository guestRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<GuestDto> getAllGuests() {
        User user = getCurrentUser();
        log.info("Fetching all guests of user with id: {}", user.getId());
        List<Guest> guests = guestRepository.findByUser(user);
        return guests.stream()
                .map(guest -> modelMapper.map(guest, GuestDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public GuestDto addNewGuest(GuestDto guestDto) {
        log.info("Adding new guest: {}", guestDto);
        User user = getCurrentUser();
        Guest guest = modelMapper.map(guestDto, Guest.class);
        guest.setUser(user);
        Guest savedGuest = guestRepository.save(guest);
        log.info("Guest added with ID: {}", savedGuest.getId());
        return modelMapper.map(savedGuest, GuestDto.class);
    }

    @Override
    public GuestDto getGuestById(Long guestId) {
        Guest guest = guestRepository.findById(guestId).orElseThrow(() -> new ResourceNotFoundException("Guest Not found with gustId: " + guestId));
        System.out.println(guest);
        GuestDto guestDto =  modelMapper.map(guest, GuestDto.class);
        System.out.println(guestDto);
        return guestDto;

    }

    @Override
    public GuestDto updateGuest(Long guestId, GuestDto guestDto) {
        log.info("Updating guest with ID: {}", guestId);
        System.out.println(guestDto);
        Guest guest = guestRepository.findById(guestId)
                .orElseThrow(() -> new EntityNotFoundException("Guest not found"));

        User user = getCurrentUser();
        if(!user.equals(guest.getUser())) throw new AccessDeniedException("You are not the owner of this guest");

        guest.setName(guestDto.getName());
        guest.setGender(guestDto.getGender());
        guest.setDateOfBirth(guestDto.getDateOfBirth());

        guestRepository.save(guest);
        log.info("Guest with ID: {} updated successfully", guestId);

        return modelMapper.map(guest, GuestDto.class);
    }

    @Override
    public void deleteGuest(Long guestId) {
        log.info("Deleting guest with ID: {}", guestId);
        Guest guest = guestRepository.findById(guestId)
                .orElseThrow(() -> new EntityNotFoundException("Guest not found"));

        User user = getCurrentUser();
        if(!user.equals(guest.getUser())) throw new AccessDeniedException("You are not the owner of this guest");

        guestRepository.deleteById(guestId);
        log.info("Guest with ID: {} deleted successfully", guestId);
    }
}

