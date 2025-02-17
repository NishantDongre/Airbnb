package com.example.airbnb.airbnb_app.service;

import com.example.airbnb.airbnb_app.entity.Room;

public interface InventoryService {
    void initializeRoomForAYear(Room room);

    void deleteAllInventories(Room room);

}
