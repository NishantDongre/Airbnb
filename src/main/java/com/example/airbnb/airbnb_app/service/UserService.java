package com.example.airbnb.airbnb_app.service;

import com.example.airbnb.airbnb_app.dto.ProfileUpdateRequestDto;
import com.example.airbnb.airbnb_app.dto.UserDto;
import com.example.airbnb.airbnb_app.entity.User;

public interface UserService {
    User getUserById(Long id);

    UserDto updateProfile(ProfileUpdateRequestDto profileUpdateRequestDto);

    UserDto getMyProfile();
}
