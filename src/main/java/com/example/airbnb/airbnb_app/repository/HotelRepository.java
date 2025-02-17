package com.example.airbnb.airbnb_app.repository;

import com.example.airbnb.airbnb_app.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {
}
