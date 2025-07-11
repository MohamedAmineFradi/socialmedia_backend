package com.example.socialmediabackend.controller;

import com.example.socialmediabackend.dto.ProfileDto;
import com.example.socialmediabackend.dto.ProfileResponseDto;
import com.example.socialmediabackend.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profiles")
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;

    @PostMapping("/user/{userId}")
    public ResponseEntity<ProfileResponseDto> createProfileForUser(@PathVariable Long userId, @RequestBody ProfileDto profileDto) {
        return profileService.createProfileForUser(userId, profileDto)
                .map(profile -> ResponseEntity.status(HttpStatus.CREATED).body(profile))
                .orElse(ResponseEntity.badRequest().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ProfileResponseDto> getProfileByUserId(@PathVariable Long userId) {
        return profileService.getProfileResponseByUserId(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/find/user/{userId}")
    public ResponseEntity<ProfileResponseDto> findProfileByUserId(@PathVariable Long userId) {
        return profileService.findProfileByUserId(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{profileId}")
    public ResponseEntity<ProfileResponseDto> updateProfile(@PathVariable Long profileId, @RequestBody ProfileDto profileDto) {
        return profileService.updateProfile(profileId, profileDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{profileId}")
    public ResponseEntity<Void> deleteProfile(@PathVariable Long profileId) {
        boolean deleted = profileService.deleteProfile(profileId);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
} 