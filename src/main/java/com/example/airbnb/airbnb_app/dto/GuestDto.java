package com.example.airbnb.airbnb_app.dto;

import com.example.airbnb.airbnb_app.entity.User;
import com.example.airbnb.airbnb_app.enums.Gender;
import lombok.Data;

@Data
public class GuestDto {
    private Long id;
    private User user;
    private String name;
    private Gender gender;
    private Integer age;
}
