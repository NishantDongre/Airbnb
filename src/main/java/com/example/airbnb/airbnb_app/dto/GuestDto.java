package com.example.airbnb.airbnb_app.dto;

import com.example.airbnb.airbnb_app.enums.Gender;
import lombok.Data;

import java.time.LocalDate;

@Data
public class GuestDto {
    private Long id;
    private UserDto user;
    private String name;
    private Gender gender;
    private LocalDate dateOfBirth;
}
