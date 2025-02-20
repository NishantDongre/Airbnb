package com.example.airbnb.airbnb_app.repository;

import com.example.airbnb.airbnb_app.entity.Guest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuestRepository extends JpaRepository<Guest, Long> {
}
