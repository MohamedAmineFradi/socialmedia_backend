package com.example.socialmediabackend.service;

import com.example.socialmediabackend.dto.UserResponseDto;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<UserResponseDto> getAllUsers();

    List<UserResponseDto> getAllUsersWithRoles();

    Optional<UserResponseDto> getUserById(Long id);

    Optional<UserResponseDto> getUserByKeycloakId(String keycloakId);

    Optional<UserResponseDto> getUserByUsername(String username);

    Optional<UserResponseDto> getCurrentUser();



    Optional<UserResponseDto> createOrUpdateUserFromKeycloak(String keycloakId, String username, String email, String firstName, String lastName);
}
