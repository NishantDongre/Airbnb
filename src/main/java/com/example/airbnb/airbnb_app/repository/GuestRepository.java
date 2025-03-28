package com.example.airbnb.airbnb_app.repository;

import com.example.airbnb.airbnb_app.entity.Guest;
import com.example.airbnb.airbnb_app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface GuestRepository extends JpaRepository<Guest, Long> {
    List<Guest> findByUser(User user);

    @Query("""
            SELECT g
            FROM Guest g
            LEFT JOIN FETCH g.user u
            WHERE g.id = :userId
            """)
    Optional<Guest> findUserById(Long userId);
}
