package com.example.socialmediabackend.controller;

import com.example.socialmediabackend.dto.ProfileDto;
import com.example.socialmediabackend.dto.ProfileResponseDto;
import com.example.socialmediabackend.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
@Slf4j
public class ProfileController {
    private final ProfileService profileService;

    @PostMapping("/user/{userId}")
    // @PreAuthorize("hasAnyRole('user', 'superAdmin') and (#userId == authentication.principal.subject or hasRole('superAdmin'))")
    public ResponseEntity<ProfileResponseDto> createProfileForUser(@PathVariable Long userId, @RequestBody ProfileDto profileDto) {
        return profileService.createProfileForUser(userId, profileDto)
                .map(profile -> ResponseEntity.status(HttpStatus.CREATED).body(profile))
                .orElse(ResponseEntity.badRequest().build());
    }

    @GetMapping("/user/{userId}")
    // @PreAuthorize("hasAnyRole('user', 'superAdmin')")
    public ResponseEntity<ProfileResponseDto> getProfileByUserId(@PathVariable Long userId) {
        log.info("Fetching profile for user ID: {}", userId);
        Optional<ProfileResponseDto> profileOpt = profileService.getProfileResponseByUserId(userId);
        if (profileOpt.isPresent()) {
            ProfileResponseDto profile = profileOpt.get();
            log.info("Found profile for user {}: avatar={}", userId, profile.getAvatar());
            return ResponseEntity.ok(profile);
        } else {
            log.warn("Profile not found for user ID: {}", userId);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/find/user/{userId}")
    // @PreAuthorize("hasAnyRole('user', 'superAdmin')")
    public ResponseEntity<ProfileResponseDto> findProfileByUserId(@PathVariable Long userId) {
        log.info("Finding profile for user ID: {}", userId);
        Optional<ProfileResponseDto> profileOpt = profileService.findProfileByUserId(userId);
        if (profileOpt.isPresent()) {
            ProfileResponseDto profile = profileOpt.get();
            log.info("Found profile for user {}: avatar={}", userId, profile.getAvatar());
            return ResponseEntity.ok(profile);
        } else {
            log.warn("Profile not found for user ID: {}", userId);
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{profileId}")
    // @PreAuthorize("hasAnyRole('user', 'superAdmin')")
    public ResponseEntity<ProfileResponseDto> updateProfile(@PathVariable Long profileId, @RequestBody ProfileDto profileDto) {
        return profileService.updateProfile(profileId, profileDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{profileId}")
    // @PreAuthorize("hasRole('superAdmin')")
    public ResponseEntity<Void> deleteProfile(@PathVariable Long profileId) {
        boolean deleted = profileService.deleteProfile(profileId);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
} 