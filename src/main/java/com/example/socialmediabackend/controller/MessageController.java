package com.example.socialmediabackend.controller;

import com.example.socialmediabackend.entity.Message;
import com.example.socialmediabackend.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    // TODO: Implement message functionality with proper security
    // Messages should be between authenticated users only
    // Users can only see messages they are part of (sender or receiver)
    
    @GetMapping("/test")
    // @PreAuthorize("hasAnyRole('user', 'superAdmin')")
    public ResponseEntity<String> testEndpoint() {
        return ResponseEntity.ok("Message controller is working");
    }
}