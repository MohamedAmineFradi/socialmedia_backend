package com.example.socialmediabackend.service;

import com.example.socialmediabackend.dto.UserResponseDto;
import com.example.socialmediabackend.dto.UserBasicInfoDto;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<UserBasicInfoDto> getAllUsers();

    List<UserResponseDto> getAllUsersWithRoles();

    Optional<UserResponseDto> getUserById(Long id);

    Optional<UserResponseDto> getUserByKeycloakId(String keycloakId);

    Optional<UserResponseDto> getUserByUsername(String username);

    Optional<UserResponseDto> getCurrentUser();

    boolean deleteUser(Long id);

    boolean deleteUserByKeycloakId(String keycloakId);


    Optional<UserResponseDto> createOrUpdateUserFromKeycloak(String keycloakId, String username, String email, String firstName, String lastName);

    Optional<UserResponseDto> updateUserFields(Long userId, String username, String email, String firstName, String lastName, Boolean enabled);
}
