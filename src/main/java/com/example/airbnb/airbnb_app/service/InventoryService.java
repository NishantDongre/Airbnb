package com.example.airbnb.airbnb_app.service;

import com.example.airbnb.airbnb_app.dto.*;
import com.example.airbnb.airbnb_app.entity.Hotel;
import com.example.airbnb.airbnb_app.entity.Room;
import org.springframework.data.domain.Page;

import java.util.List;

public interface InventoryService {
    void initializeRoomForAYear(Hotel hotel, Room room);

    void deleteAllInventories(Room room);

    Page<HotelPriceResponseDto> searchHotels(HotelSearchRequest hotelSearchRequest);

    List<InventoryDto> getAllInventoryByRoom(Long roomId);

    void updateInventory(Long roomId, UpdateInventoryRequestDto updateInventoryRequestDto);
}
