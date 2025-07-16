package com.example.socialmediabackend.controller;

import com.example.socialmediabackend.dto.ProfileDto;
import com.example.socialmediabackend.dto.ProfileResponseDto;
import com.example.socialmediabackend.service.ProfileService;
import com.example.socialmediabackend.service.UserService;
import com.example.socialmediabackend.dto.UserResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import com.example.socialmediabackend.util.JwtUtil;

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
@Slf4j
public class ProfileController {
    private final ProfileService profileService;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('user', 'superAdmin')")
    public ResponseEntity<ProfileResponseDto> createProfileForUser(@PathVariable Long userId, @RequestBody ProfileDto profileDto) {
        Long currentDbId = userService.getUserByKeycloakId(jwtUtil.getCurrentUserId())
                .map(UserResponseDto::getId)
                .orElse(null);
        boolean isSuperAdmin = jwtUtil.isSuperAdmin();
        if (!isSuperAdmin && !userId.equals(currentDbId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return profileService.createProfileForUser(userId, profileDto)
                .map(profile -> ResponseEntity.status(HttpStatus.CREATED).body(profile))
                .orElse(ResponseEntity.badRequest().build());
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasAnyRole('user', 'superAdmin')")
    public ResponseEntity<ProfileResponseDto> getProfile(@PathVariable Long userId) {
        Long currentDbId = userService.getUserByKeycloakId(jwtUtil.getCurrentUserId())
                .map(UserResponseDto::getId).orElse(null);
        boolean isSuperAdmin = jwtUtil.isSuperAdmin();
        if (!isSuperAdmin && !userId.equals(currentDbId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return profileService.getProfileResponseByUserId(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
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

    @GetMapping("/username/{username}")
    // @PreAuthorize("hasAnyRole('user', 'superAdmin')")
    public ResponseEntity<ProfileResponseDto> getProfileByUsername(@PathVariable String username) {
        log.info("Fetching profile for username: {}", username);
        return profileService.getProfileResponseByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasAnyRole('user', 'superAdmin')")
    public ResponseEntity<ProfileResponseDto> updateProfile(@PathVariable Long userId, @RequestBody ProfileDto profileDto) {
        Long currentDbId = userService.getUserByKeycloakId(jwtUtil.getCurrentUserId())
                .map(UserResponseDto::getId).orElse(null);
        boolean isSuperAdmin = jwtUtil.isSuperAdmin();
        if (!isSuperAdmin && !userId.equals(currentDbId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Fetch the existing profile to obtain its primary key (profileId)
        return profileService.findProfileByUserId(userId)
                .flatMap(existing -> profileService.updateProfile(existing.getId(), profileDto))
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