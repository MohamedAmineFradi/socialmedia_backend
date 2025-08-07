package com.example.socialmediabackend.websocket;

import com.example.socialmediabackend.dto.MessageCreateDTO;
import com.example.socialmediabackend.dto.MessageResponseDTO;
import com.example.socialmediabackend.exception.ConversationNotFoundException;
import com.example.socialmediabackend.exception.UnauthorizedSenderException;
import com.example.socialmediabackend.exception.UserNotParticipantException;
import com.example.socialmediabackend.service.MessageService;
import com.example.socialmediabackend.service.UserService;
import com.example.socialmediabackend.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.security.Principal; 
import java.util.Map;

import static java.util.concurrent.CompletableFuture.runAsync;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WebSocketMessageController {
    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;
    private final JwtDecoder jwtDecoder;
    private final UserRepository userRepository;

    @MessageMapping("/chat")
    public void handleMessage(@Payload MessageCreateDTO message, SimpMessageHeaderAccessor headerAccessor, Principal principal) {
        log.info("Received WebSocket message for conversation {}", message.getConversationId());
        log.debug("Principal: {}, User: {}", principal != null ? principal.getName() : "null", headerAccessor.getUser());
        log.debug("Message headers: {}", headerAccessor.toNativeHeaderMap());

        // Check SecurityContextHolder directly
        try {
            org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
            log.debug("SecurityContext authentication: {}", auth != null ? auth.getClass().getSimpleName() : "null");
            if (auth != null) {
                log.debug("SecurityContext principal: {}", auth.getPrincipal());
            }
        } catch (Exception e) {
            log.debug("Error accessing SecurityContext: {}", e.getMessage());
        }

        // If principal is null, try to get authentication from headers
        if (principal == null) {
            log.info("Principal is null, attempting to get authentication from headers");
            String authHeader = headerAccessor.getFirstNativeHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                try {
                    String token = authHeader.substring(7);
                    Jwt jwt = jwtDecoder.decode(token);
                    String keycloakId = jwt.getSubject();
                    log.info("Extracted Keycloak ID from JWT: {}", keycloakId);

                    // Create a simple principal for the message service
                    principal = new Principal() {
                        @Override
                        public String getName() {
                            return keycloakId;
                        }
                    };
                } catch (Exception e) {
                    log.error("Failed to decode JWT from header: {}", e.getMessage());
                }
            }
        }

        try {
            messageService.handleWebSocketMessage(message, principal);
            log.info("Successfully processed WebSocket message for conversation {}", message.getConversationId());
        } catch (ConversationNotFoundException e) {
            log.warn("Conversation not found: {}", e.getMessage());
            // Could send error message back to specific user
        } catch (UserNotParticipantException e) {
            log.warn("User not participant: {}", e.getMessage());
            // Could send error message back to specific user
        } catch (UnauthorizedSenderException e) {
            log.warn("Unauthorized sender attempt: {}", e.getMessage());
            // Could send error message back to specific user
        } catch (SecurityException e) {
            log.warn("Security violation: {}", e.getMessage());
            // Could send error message back to specific user
        } catch (Exception e) {
            log.error("Unexpected error processing WebSocket message", e);
            // Could send generic error message back to specific user
        }
    }

    @SubscribeMapping("/topic/conversation.{conversationId}")
    public void handleSubscribe(SimpMessageHeaderAccessor headerAccessor) {
        String conversationId = headerAccessor.getDestination()
                .replace("/topic/conversation.", "");
        log.info("New subscription to conversation {}", conversationId);
    }
}