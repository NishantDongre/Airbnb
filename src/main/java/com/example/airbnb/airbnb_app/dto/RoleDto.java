package com.example.airbnb.airbnb_app.dto;

import com.example.airbnb.airbnb_app.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleDto {
    private List<Role> rolesList;
}
