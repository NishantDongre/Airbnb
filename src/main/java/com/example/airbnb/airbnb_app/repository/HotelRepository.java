package com.example.airbnb.airbnb_app.repository;

import com.example.airbnb.airbnb_app.entity.Hotel;
import com.example.airbnb.airbnb_app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {
    List<Hotel> findByOwner(User user);

}
