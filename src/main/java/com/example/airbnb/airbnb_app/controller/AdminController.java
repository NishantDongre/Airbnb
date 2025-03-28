package com.example.airbnb.airbnb_app.controller;

import com.example.airbnb.airbnb_app.dto.RoleDto;
import com.example.airbnb.airbnb_app.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/superAdmin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PatchMapping("/addRoles/{userId}")
    @Operation(summary = "Add Role", tags = {"Super Admin"})
    private ResponseEntity<Void> addRole(@PathVariable Long userId, @RequestBody RoleDto roleDto){
        adminService.addRole(userId, roleDto);
        return ResponseEntity.noContent().build();
    }
}
