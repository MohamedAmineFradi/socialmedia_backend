package com.example.socialmediabackend.service.impl;

import com.example.socialmediabackend.dto.MessageCreateDTO;
import com.example.socialmediabackend.dto.MessageResponseDTO;
import com.example.socialmediabackend.entity.Conversation;
import com.example.socialmediabackend.entity.Message;
import com.example.socialmediabackend.entity.User;
import com.example.socialmediabackend.exception.ConversationNotFoundException;
import com.example.socialmediabackend.exception.UnauthorizedSenderException;
import com.example.socialmediabackend.exception.UserNotParticipantException;
import com.example.socialmediabackend.repository.ConversationRepository;
import com.example.socialmediabackend.repository.MessageRepository;
import com.example.socialmediabackend.repository.UserRepository;
import com.example.socialmediabackend.service.ConversationService;
import com.example.socialmediabackend.service.MessageService;
import com.example.socialmediabackend.util.JwtUtil;
import com.example.socialmediabackend.util.MessageConstants;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import lombok.extern.slf4j.Slf4j;

import java.security.Principal;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageServiceImpl implements MessageService {
    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public MessageResponseDTO processAndSendMessage(MessageCreateDTO dto) {
        Conversation conv = conversationRepository.findById(dto.getConversationId())
                .orElseThrow(() -> new ConversationNotFoundException("Conversation not found"));

        User authenticatedUser = null;
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                String keycloakId = userDetails.getUsername();
                authenticatedUser = userRepository.findByKeycloakId(keycloakId)
                        .orElseThrow(() -> new EntityNotFoundException("Authenticated user not found"));
            }
        } catch (Exception e) {
            log.debug("Could not get authentication from SecurityContextHolder: {}", e.getMessage());
        }

        if (authenticatedUser == null) {
            if (dto.getSenderId() == null) {
                throw new SecurityException("Sender ID is required when authentication context is not available");
            }
            authenticatedUser = userRepository.findById(dto.getSenderId())
                    .orElseThrow(() -> new EntityNotFoundException("Sender not found"));
        } else {
            if (dto.getSenderId() == null || !dto.getSenderId().equals(authenticatedUser.getId())) {
                log.warn("Security violation: User {} attempted to send message as user {} via REST API",
                        authenticatedUser.getId(), dto.getSenderId());
                throw new UnauthorizedSenderException(MessageConstants.UNAUTHORIZED_SENDER_MESSAGE);
            }
        }

        User sender = authenticatedUser;

        validateUserIsParticipant(conv, sender);

        Message msg = createMessage(dto, conv, sender);
        Message saved = messageRepository.save(msg);

        updateConversationTimestamp(conv);

        log.debug("Message sent successfully: ID={}, Conversation={}, Sender={}",
                saved.getId(), conv.getId(), sender.getId());

        return buildMessageResponse(saved, dto.getClientMessageId());
    }

    @Override
    @Transactional
    public void handleWebSocketMessage(MessageCreateDTO dto, Principal principal) {
        log.debug("Handling WebSocket message with principal: {}", principal != null ? principal.getName() : "null");

        String keycloakId = null;

        if (principal != null) {
            keycloakId = principal.getName();
            log.debug("Got Keycloak ID from principal: {}", keycloakId);
        }

        if (keycloakId == null) {
            try {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                log.debug("SecurityContext authentication: {}", authentication != null ? authentication.getClass().getSimpleName() : "null");

                if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
                    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                    keycloakId = userDetails.getUsername();
                    log.debug("Got Keycloak ID from SecurityContextHolder: {}", keycloakId);
                } else if (authentication != null && authentication.getPrincipal() instanceof String) {
                    keycloakId = (String) authentication.getPrincipal();
                    log.debug("Got Keycloak ID from SecurityContextHolder (String): {}", keycloakId);
                }
            } catch (Exception e) {
                log.debug("Could not get authentication from SecurityContextHolder: {}", e.getMessage());
            }
        }

        if (keycloakId == null) {
            log.error("No authentication found in WebSocket message - authentication failed");
            throw new SecurityException("Authentication required for WebSocket messages");
        }

        log.debug("Processing message for Keycloak ID: {}", keycloakId);

        if (dto.getConversationId() == null || dto.getContent() == null || dto.getContent().trim().isEmpty()) {
            log.warn("Invalid message data received: conversationId={}, content={}",
                    dto.getConversationId(), dto.getContent());
            throw new IllegalArgumentException("Invalid message data");
        }

        String finalKeycloakId = keycloakId;
        User authenticatedUser = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> {
                    log.error("User not found for Keycloak ID: {}", finalKeycloakId);
                    return new EntityNotFoundException("User not found");
                });

        log.debug("Found authenticated user: {} (ID: {})", authenticatedUser.getUsername(), authenticatedUser.getId());

        if (dto.getSenderId() == null || !dto.getSenderId().equals(authenticatedUser.getId())) {
            log.warn("Security violation: User {} (Keycloak ID: {}) attempted to send message as user {}",
                    authenticatedUser.getId(), keycloakId, dto.getSenderId());
            throw new UnauthorizedSenderException(MessageConstants.UNAUTHORIZED_SENDER_MESSAGE);
        }

        Conversation conversation = conversationRepository.findById(dto.getConversationId())
                .orElseThrow(() -> {
                    log.error("Conversation not found: {}", dto.getConversationId());
                    return new ConversationNotFoundException("Conversation not found");
                });

        validateUserIsParticipant(conversation, authenticatedUser);

        log.debug("Processing WebSocket message from user {} in conversation {}",
                authenticatedUser.getId(), dto.getConversationId());

        MessageResponseDTO response = this.processAndSendMessage(dto);
        messagingTemplate.convertAndSend("/topic/conversation." + dto.getConversationId(), response);
    }

    private void validateUserIsParticipant(Conversation conversation, User user) {
        if (!conversation.getUser1().getId().equals(user.getId()) &&
                !conversation.getUser2().getId().equals(user.getId())) {
            throw new UserNotParticipantException("Sender is not a participant");
        }
    }

    private Message createMessage(MessageCreateDTO dto, Conversation conversation, User sender) {
        Message message = new Message();
        String sanitizedContent = sanitizeMessageContent(dto.getContent());
        message.setBody(sanitizedContent);
        message.setSentAt(Instant.now());
        message.setConversation(conversation);
        message.setSender(sender);
        return message;
    }

    private String sanitizeMessageContent(String content) {
        if (content == null) {
            throw new IllegalArgumentException("Message content cannot be null");
        }

        String sanitized = content.trim();

        if (sanitized.isEmpty()) {
            throw new IllegalArgumentException("Message content cannot be empty");
        }

        if (sanitized.length() > MessageConstants.MAX_MESSAGE_LENGTH) {
            throw new IllegalArgumentException("Message content cannot exceed " + MessageConstants.MAX_MESSAGE_LENGTH + " characters");
        }

        return sanitized;
    }

    private void updateConversationTimestamp(Conversation conversation) {
        conversation.setLastUpdated(Instant.now());
        conversationRepository.save(conversation);
    }

    private MessageResponseDTO buildMessageResponse(Message message, String clientMessageId) {
        MessageResponseDTO response = new MessageResponseDTO();
        response.setId(message.getId());
        response.setContent(message.getBody());
        response.setSentAt(message.getSentAt());
        response.setConversationId(message.getConversation().getId());
        response.setSenderId(message.getSender().getId());
        response.setClientMessageId(clientMessageId);
        return response;
    }
}