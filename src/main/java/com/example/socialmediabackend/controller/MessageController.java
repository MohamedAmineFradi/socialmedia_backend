package com.example.socialmediabackend.controller;

import com.example.socialmediabackend.dto.MessageCreateDTO;
import com.example.socialmediabackend.dto.MessageResponseDTO;
import com.example.socialmediabackend.dto.UserResponseDto;
import com.example.socialmediabackend.service.MessageService;
import com.example.socialmediabackend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {
    private static final Logger log = LoggerFactory.getLogger(MessageController.class);
    private final MessageService messageService;
    private final UserService userService;


    @PostMapping
    public ResponseEntity<MessageResponseDTO> createMessage(@Valid @RequestBody MessageCreateDTO dto, @AuthenticationPrincipal Jwt jwt) {
        log.info("Creating message: {}", dto);
        String keycloakId = jwt.getSubject();
        log.debug("Authenticated user keycloakId: {}", keycloakId);

        try {
            Optional<UserResponseDto> user = userService.getUserByKeycloakId(keycloakId);
            if (!user.isPresent()) {
                log.error("User not found for keycloakId: {}", keycloakId);
                return ResponseEntity.status(403).build();
            }

            log.debug("Found user ID: {}", user.get().getId());
            if (!user.get().getId().equals(dto.getSenderId())) {
                log.warn("Sender ID mismatch: {} vs {}", user.get().getId(), dto.getSenderId());
                return ResponseEntity.status(403).build();
            }

            MessageResponseDTO response = messageService.processAndSendMessage(dto);
            log.info("Message created successfully: {}", response.getId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error creating message", e);
            return ResponseEntity.status(400).build();
        }
    }
}