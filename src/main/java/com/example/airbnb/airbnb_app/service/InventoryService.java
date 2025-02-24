package com.example.airbnb.airbnb_app.service;

import com.example.airbnb.airbnb_app.dto.HotelDto;
import com.example.airbnb.airbnb_app.dto.HotelPriceDto;
import com.example.airbnb.airbnb_app.dto.HotelSearchRequest;
import com.example.airbnb.airbnb_app.entity.Room;
import org.springframework.data.domain.Page;

public interface InventoryService {
    void initializeRoomForAYear(Room room);

    void deleteAllInventories(Room room);

    Page<HotelPriceDto> searchHotels(HotelSearchRequest hotelSearchRequest);
}
