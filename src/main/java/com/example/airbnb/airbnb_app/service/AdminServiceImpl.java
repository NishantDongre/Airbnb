package com.example.airbnb.airbnb_app.service;

import com.example.airbnb.airbnb_app.dto.RoleDto;
import com.example.airbnb.airbnb_app.entity.User;
import com.example.airbnb.airbnb_app.enums.Role;
import com.example.airbnb.airbnb_app.exception.ResourceNotFoundException;
import com.example.airbnb.airbnb_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService{

    private final UserRepository userRepository;

    @Override
    public void addRole(Long userId, RoleDto roleDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found with id: "+ userId));
        List<Role> rolesList = roleDto.getRolesList();
         for(Role role : rolesList){
             user.getRoles().add(role);
         }
        userRepository.save(user);
        log.info("Successfully Added roles to user with ID: {}", user.getId());
    }
}
