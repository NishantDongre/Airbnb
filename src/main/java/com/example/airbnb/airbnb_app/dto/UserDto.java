package com.example.airbnb.airbnb_app.dto;

import com.example.airbnb.airbnb_app.enums.Gender;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserDto {
    private Long id;
    private String email;
    private String name;
    private Gender gender;
    private LocalDate dateOfBirth;
}