package com.example.socialmediabackend.controller;

import com.example.socialmediabackend.dto.DetailedConversationDTO;
import com.example.socialmediabackend.dto.MessageResponseDTO;
import com.example.socialmediabackend.entity.Conversation;
import com.example.socialmediabackend.entity.User;
import com.example.socialmediabackend.repository.UserRepository;
import com.example.socialmediabackend.service.ConversationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conversations")
public class ConversationController {
    private static final Logger log = LoggerFactory.getLogger(ConversationController.class);
    private final ConversationService conversationService;
    private final UserRepository userRepository;

    public ConversationController(ConversationService conversationService, UserRepository userRepository) {
        this.conversationService = conversationService;
        this.userRepository = userRepository;
    }

    @GetMapping("/me")
    public ResponseEntity<List<DetailedConversationDTO>> getUserConversations(@AuthenticationPrincipal Jwt jwt) {
        log.info("Fetching conversations for user: {}", jwt.getSubject());
        try {
            String keycloakId = jwt.getSubject();
            User user = userRepository.findByKeycloakId(keycloakId)
                    .orElseThrow(() -> {
                        log.error("User not found for keycloakId: {}", keycloakId);
                        return new RuntimeException("User not found");
                    });
            
            List<DetailedConversationDTO> conversations = conversationService.getUserConversations(user.getId());
            log.debug("Found {} conversations for user {}", conversations.size(), user.getId());
            return ResponseEntity.ok(conversations);
        } catch (Exception e) {
            log.error("Error fetching conversations", e);
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping
    public ResponseEntity<DetailedConversationDTO> createConversation(
            @Validated @RequestBody DetailedConversationDTO dto,
            @AuthenticationPrincipal Jwt jwt) {
        List<Long> participantIds = createParticipantIds(dto);
        log.info("Creating conversation with participants: {}", participantIds);
        try {
            String keycloakId = jwt.getSubject();
            User user = userRepository.findByKeycloakId(keycloakId)
                    .orElseThrow(() -> {
                        log.error("User not found for keycloakId: {}", keycloakId);
                        return new RuntimeException("User not found");
                    });

            if (participantIds == null || participantIds.size() != 2) {
                log.error("Invalid participantIds: {}", participantIds);
                return ResponseEntity.badRequest().build();
            }

            if (!participantIds.contains(user.getId())) {
                log.warn("Authenticated user {} not in conversation participants {}",
                        user.getId(), participantIds);
                return ResponseEntity.status(403).build();
            }

            DetailedConversationDTO conversation = conversationService.createConversation(dto);
            log.info("Created conversation {} with participants {}",
                    conversation.getId(), participantIds);
            return ResponseEntity.ok(conversation);
        } catch (Exception e) {
            log.error("Error creating conversation", e);
            return ResponseEntity.status(400).build();
        }
    }

    private List<Long> createParticipantIds(DetailedConversationDTO dto) {
        if (dto.getUser1Id() == null || dto.getUser2Id() == null) {
            log.error("Null participant IDs detected: user1Id={}, user2Id={}", dto.getUser1Id(), dto.getUser2Id());
            return null;
        }
        return List.of(dto.getUser1Id(), dto.getUser2Id());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetailedConversationDTO> getConversation(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        log.info("Fetching conversation with id: {}", id);
        try {
            String keycloakId = jwt.getSubject();
            User user = userRepository.findByKeycloakId(keycloakId).orElseThrow();
            DetailedConversationDTO dto = conversationService.getConversationById(id);
            if (!user.getId().equals(dto.getUser1Id()) && !user.getId().equals(dto.getUser2Id())) {
                log.warn("User {} not authorized to access conversation {}", user.getId(), id);
                return ResponseEntity.status(403).build(); // Authorization check
            }
            log.debug("Found conversation {} for user {}", id, user.getId());
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            log.error("Error fetching conversation", e);
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/{id}/messages")
    public ResponseEntity<Page<MessageResponseDTO>> getMessages(
            @PathVariable Long id, @PageableDefault Pageable pageable, @AuthenticationPrincipal Jwt jwt) {
        log.info("Fetching messages for conversation with id: {}", id);
        try {
            String keycloakId = jwt.getSubject();
            User user = userRepository.findByKeycloakId(keycloakId).orElseThrow();
            Conversation conv = conversationService.getRawConversationById(id);
            if (!conv.getUser1().getId().equals(user.getId()) && !conv.getUser2().getId().equals(user.getId())) {
                log.warn("User {} not authorized to access conversation {}", user.getId(), id);
                return ResponseEntity.status(403).build();
            }
            Page<MessageResponseDTO> messages = conversationService.getMessages(id, pageable);
            log.debug("Found {} messages for conversation {} and user {}", messages.getSize(), id, user.getId());
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            log.error("Error fetching messages", e);
            return ResponseEntity.status(500).build();
        }
    }
}