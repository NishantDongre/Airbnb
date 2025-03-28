package com.example.airbnb.airbnb_app.service;

import com.example.airbnb.airbnb_app.dto.RoleDto;
import com.example.airbnb.airbnb_app.dto.UserDto;

public interface AdminService {
    void addRole(Long userId, RoleDto roleDto);
}
