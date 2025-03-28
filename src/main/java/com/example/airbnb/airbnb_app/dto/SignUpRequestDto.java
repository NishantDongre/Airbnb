package com.example.airbnb.airbnb_app.dto;

import com.example.airbnb.airbnb_app.enums.Gender;
import lombok.Data;

import java.time.LocalDate;

@Data
public class SignUpRequestDto {
    private String email;
    private String password;
    private String name;
    private LocalDate dateOfBirth;
    private Gender gender;
}