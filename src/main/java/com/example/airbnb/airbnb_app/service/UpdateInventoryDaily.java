package com.example.airbnb.airbnb_app.service;

import com.example.airbnb.airbnb_app.entity.Inventory;
import com.example.airbnb.airbnb_app.entity.Room;
import com.example.airbnb.airbnb_app.repository.HotelMinPriceRepository;
import com.example.airbnb.airbnb_app.repository.InventoryRepository;
import com.example.airbnb.airbnb_app.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class UpdateInventoryDaily {


    private final InventoryRepository inventoryRepository;
    private final HotelMinPriceRepository hotelMinPriceRepository;
    private final RoomRepository roomRepository;

    @Scheduled(cron = "0 0 0 * * ?") // At 12:00 AM
    @Transactional
    public void updateInventory() {
        LocalDate today = LocalDate.now();
        LocalDate newEndDate = today.plusYears(1);
        System.out.println(newEndDate);

       // Delete old inventory and hotelMinPrice(yesterday and older)
        inventoryRepository.deleteByDateBefore(today);
        hotelMinPriceRepository.deleteByDateBefore(today);

        // Add new inventory for the new date (1 year ahead)
        List<Room> rooms = roomRepository.findAll();
        for (Room room : rooms) {
            Inventory inventory = Inventory.builder()
                    .hotel(room.getHotel())
                    .room(room)
                    .bookedCount(0)
                    .reservedCount(0)
                    .city(room.getHotel().getCity())
                    .date(newEndDate)
                    .price(room.getBasePrice())
                    .surgeFactor(BigDecimal.ONE)
                    .totalCount(room.getTotalCount())
                    .closed(false)
                    .build();
            inventoryRepository.save(inventory);
        }
    }
}
