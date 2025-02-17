package com.example.airbnb.airbnb_app.repository;

import com.example.airbnb.airbnb_app.entity.Inventory;
import com.example.airbnb.airbnb_app.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    void deleteByRoom(Room room);
}
