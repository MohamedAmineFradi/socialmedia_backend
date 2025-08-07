package com.example.socialmediabackend.controller.internal;

import com.example.socialmediabackend.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/sync")
@RequiredArgsConstructor
public class UserSyncController {

    private final UserServiceImpl userService;

    record EmailPayload(String keycloakId, String email) {}
    record DeletePayload(String keycloakId) {}

    @PostMapping("/email")
    public ResponseEntity<Void> syncEmail(@RequestBody EmailPayload payload) {
        userService.updateEmailFromKeycloak(payload.keycloakId(), payload.email());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/delete")
    public ResponseEntity<Void> syncDelete(@RequestBody DeletePayload payload) {
        userService.deleteUserByKeycloakId(payload.keycloakId());
        return ResponseEntity.noContent().build();
    }
} 