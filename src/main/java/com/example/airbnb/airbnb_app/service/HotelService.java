package com.example.airbnb.airbnb_app.service;

import com.example.airbnb.airbnb_app.dto.HotelDto;
import com.example.airbnb.airbnb_app.dto.HotelInfoDto;
import com.example.airbnb.airbnb_app.dto.HotelInfoRequestDto;

import java.util.List;

public interface HotelService {
    HotelDto createNewHotel(HotelDto hotelDto);

    HotelDto getHotelById(Long id);

    List<HotelDto> getAllHotels();

    HotelDto updateHotelById(Long id, HotelDto hotelDto);

    void deleteHotelById(Long id);

    void activateHotel(Long hotelId);

    HotelInfoDto getHotelInfoById(Long hotelId, HotelInfoRequestDto hotelInfoRequestDto);
}
