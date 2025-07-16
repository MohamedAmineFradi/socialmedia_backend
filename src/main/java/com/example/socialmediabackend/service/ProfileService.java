package com.example.socialmediabackend.service;

import com.example.socialmediabackend.dto.ProfileDto;
import com.example.socialmediabackend.dto.ProfileResponseDto;

import java.util.Optional;

public interface ProfileService {
    Optional<ProfileResponseDto> updateProfile(Long profileId, ProfileDto profileDto);

    boolean deleteProfile(Long profileId);

    Optional<ProfileResponseDto> getProfileResponseByUserId(Long userId);

    Optional<ProfileResponseDto> createProfileForUser(Long userId, ProfileDto profileDto);

    Optional<ProfileResponseDto> findProfileByUserId(Long userId);

    Optional<ProfileResponseDto> getProfileResponseByUsername(String username);
} 