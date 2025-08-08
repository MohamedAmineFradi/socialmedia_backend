package com.example.socialmediabackend.controller;

import com.example.socialmediabackend.dto.CommentDto;
import com.example.socialmediabackend.dto.CommentResponseDto;
import com.example.socialmediabackend.service.CommentService;
import com.example.socialmediabackend.util.JwtUtil;
import com.example.socialmediabackend.service.UserService;
import com.example.socialmediabackend.dto.UserResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Slf4j
public class CommentController {
    private final CommentService commentService;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping
    @PreAuthorize("hasAnyRole('user', 'superAdmin')")
    public ResponseEntity<CommentResponseDto> addComment(@RequestParam Long postId, @RequestBody CommentDto commentDto) {
        Long currentUserId = null;
        String keycloakId = jwtUtil.getCurrentUserId();
        if (keycloakId != null) {
            currentUserId = userService.getUserByKeycloakId(keycloakId)
                    .map(UserResponseDto::getId)
                    .orElse(null);
        }
        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return commentService.createCommentResponse(postId, currentUserId, commentDto)
                .map(comment -> ResponseEntity.status(HttpStatus.CREATED).body(comment))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/post/{postId}")
    @PreAuthorize("hasAnyRole('user', 'superAdmin')")
    public ResponseEntity<List<CommentResponseDto>> getCommentsByPost(@PathVariable Long postId) {
        return ResponseEntity.ok(commentService.getAllCommentResponsesByPostId(postId));
    }

    @GetMapping("/{commentId}")
    @PreAuthorize("hasAnyRole('user', 'superAdmin')")
    public ResponseEntity<CommentResponseDto> getCommentById(@PathVariable Long commentId) {
        return commentService.getCommentResponseById(commentId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{commentId}/user/{userId}")
    @PreAuthorize("hasAnyRole('user', 'superAdmin')")
    public ResponseEntity<CommentResponseDto> updateComment(@PathVariable Long commentId, @PathVariable Long userId, @RequestBody CommentDto commentDto, HttpServletRequest request) {
        boolean isSuperAdmin = jwtUtil.isSuperAdmin();

        Long currentDbId = userService.getUserByKeycloakId(jwtUtil.getCurrentUserId())
                .map(UserResponseDto::getId).orElse(null);

        if (!isSuperAdmin && (currentDbId == null || !currentDbId.equals(userId))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        log.info("Updating comment {} by user {} (superAdmin: {})", commentId, userId, isSuperAdmin);
        final boolean finalIsSuperAdmin = isSuperAdmin;
        return commentService.updateCommentResponse(commentId, userId, commentDto, finalIsSuperAdmin)
                .map(comment -> {
                    log.info("Successfully updated comment with ID: {}. Returning DTO: {}", comment.getId(), comment);
                    return ResponseEntity.ok(comment);
                })
                .orElseGet(() -> {
                    log.error("Failed to update comment {} for user {} (superAdmin: {})", commentId, userId, finalIsSuperAdmin);
                    return ResponseEntity.notFound().build();
                });
    }

    @DeleteMapping("/{commentId}/user/{userId}")
    @PreAuthorize("hasAnyRole('user', 'superAdmin')")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId, @PathVariable Long userId) {
        Long currentUserId = null;
        String keycloakId = jwtUtil.getCurrentUserId();
        if (keycloakId != null) {
            currentUserId = userService.getUserByKeycloakId(keycloakId)
                    .map(UserResponseDto::getId)
                    .orElse(null);
        }
        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        boolean isSuperAdmin = jwtUtil.isSuperAdmin();
        if (!isSuperAdmin && !currentUserId.equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        boolean deleted = commentService.deleteComment(commentId, userId, isSuperAdmin);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
} 