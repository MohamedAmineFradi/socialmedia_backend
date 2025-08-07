package com.example.socialmediabackend.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(UnauthorizedSenderException.class)
  public ResponseEntity<Map<String, Object>> handleUnauthorizedSender(UnauthorizedSenderException e) {
    log.warn("Unauthorized sender attempt: {}", e.getMessage());

    Map<String, Object> errorResponse = new HashMap<>();
    errorResponse.put("timestamp", Instant.now());
    errorResponse.put("status", HttpStatus.FORBIDDEN.value());
    errorResponse.put("error", "Forbidden");
    errorResponse.put("message", e.getMessage());
    errorResponse.put("code", "UNAUTHORIZED_SENDER");

    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
  }

  @ExceptionHandler(ConversationNotFoundException.class)
  public ResponseEntity<Map<String, Object>> handleConversationNotFound(ConversationNotFoundException e) {
    log.warn("Conversation not found: {}", e.getMessage());

    Map<String, Object> errorResponse = new HashMap<>();
    errorResponse.put("timestamp", Instant.now());
    errorResponse.put("status", HttpStatus.NOT_FOUND.value());
    errorResponse.put("error", "Not Found");
    errorResponse.put("message", e.getMessage());
    errorResponse.put("code", "CONVERSATION_NOT_FOUND");

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }

  @ExceptionHandler(UserNotParticipantException.class)
  public ResponseEntity<Map<String, Object>> handleUserNotParticipant(UserNotParticipantException e) {
    log.warn("User not participant: {}", e.getMessage());

    Map<String, Object> errorResponse = new HashMap<>();
    errorResponse.put("timestamp", Instant.now());
    errorResponse.put("status", HttpStatus.FORBIDDEN.value());
    errorResponse.put("error", "Forbidden");
    errorResponse.put("message", e.getMessage());
    errorResponse.put("code", "USER_NOT_PARTICIPANT");

    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
  }

  @ExceptionHandler(SecurityException.class)
  public ResponseEntity<Map<String, Object>> handleSecurityException(SecurityException e) {
    log.warn("Security exception: {}", e.getMessage());

    Map<String, Object> errorResponse = new HashMap<>();
    errorResponse.put("timestamp", Instant.now());
    errorResponse.put("status", HttpStatus.FORBIDDEN.value());
    errorResponse.put("error", "Forbidden");
    errorResponse.put("message", e.getMessage());
    errorResponse.put("code", "SECURITY_VIOLATION");

    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
  }
}