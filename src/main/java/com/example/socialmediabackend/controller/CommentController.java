package com.example.socialmediabackend.controller;

import com.example.socialmediabackend.dto.CommentDto;
import com.example.socialmediabackend.dto.CommentResponseDto;
import com.example.socialmediabackend.service.CommentService;
import com.example.socialmediabackend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Slf4j
public class CommentController {
    private final CommentService commentService;
    private final JwtUtil jwtUtil;

    @PostMapping("/post/{postId}/user/{userId}")
    // @PreAuthorize("hasAnyRole('user', 'superAdmin')")
    public ResponseEntity<CommentResponseDto> createComment(@PathVariable Long postId, @PathVariable Long userId, @RequestBody CommentDto commentDto) {
        return commentService.createCommentResponse(postId, userId, commentDto)
                .map(comment -> ResponseEntity.status(HttpStatus.CREATED).body(comment))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/post/{postId}")
    // @PreAuthorize("hasAnyRole('user', 'superAdmin')")
    public ResponseEntity<List<CommentResponseDto>> getCommentsByPost(@PathVariable Long postId) {
        return ResponseEntity.ok(commentService.getAllCommentResponsesByPostId(postId));
    }

    @GetMapping("/{commentId}")
    // @PreAuthorize("hasAnyRole('user', 'superAdmin')")
    public ResponseEntity<CommentResponseDto> getCommentById(@PathVariable Long commentId) {
        return commentService.getCommentResponseById(commentId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{commentId}/user/{userId}")
    // @PreAuthorize("hasAnyRole('user', 'superAdmin')")
    public ResponseEntity<CommentResponseDto> updateComment(@PathVariable Long commentId, @PathVariable Long userId, @RequestBody CommentDto commentDto, HttpServletRequest request) {
        // Try to get superAdmin status from JWT first
        boolean isSuperAdmin = jwtUtil.isSuperAdmin();
        
        // If no JWT (libertalk profile), check if the user is superAdmin in database
        if (!isSuperAdmin && jwtUtil.getCurrentUserId() == null) {
            String frontendUsername = request.getHeader("X-Frontend-User");
            if (frontendUsername != null) {
                // Check if the frontend user is admin in the database
                isSuperAdmin = "admin".equals(frontendUsername);
                log.info("No JWT found, checking frontend username: {} (superAdmin: {})", frontendUsername, isSuperAdmin);
            }
        }
        
        log.info("Updating comment {} by user {} (superAdmin: {})", commentId, userId, isSuperAdmin);
        
        final boolean finalIsSuperAdmin = isSuperAdmin;
        return commentService.updateCommentResponse(commentId, userId, commentDto, finalIsSuperAdmin)
                .map(comment -> {
                    log.info("Successfully updated comment with ID: {}", comment.getId());
                    return ResponseEntity.ok(comment);
                })
                .orElseGet(() -> {
                    log.error("Failed to update comment {} for user {} (superAdmin: {})", commentId, userId, finalIsSuperAdmin);
                    return ResponseEntity.notFound().build();
                });
    }

    @DeleteMapping("/{commentId}/user/{userId}")
    // @PreAuthorize("hasAnyRole('user', 'superAdmin')")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId, @PathVariable Long userId, HttpServletRequest request) {
        // Try to get superAdmin status from JWT first
        boolean isSuperAdmin = jwtUtil.isSuperAdmin();
        
        // If no JWT (libertalk profile), check if the user is superAdmin in database
        if (!isSuperAdmin && jwtUtil.getCurrentUserId() == null) {
            String frontendUsername = request.getHeader("X-Frontend-User");
            if (frontendUsername != null) {
                // Check if the frontend user is admin in the database
                isSuperAdmin = "admin".equals(frontendUsername);
                log.info("No JWT found, checking frontend username: {} (superAdmin: {})", frontendUsername, isSuperAdmin);
            }
        }
        
        log.info("Deleting comment {} by user {} (superAdmin: {})", commentId, userId, isSuperAdmin);
        
        final boolean finalIsSuperAdmin = isSuperAdmin;
        boolean deleted = commentService.deleteComment(commentId, userId, finalIsSuperAdmin);
        if (deleted) {
            log.info("Successfully deleted comment {}", commentId);
            return ResponseEntity.noContent().build();
        } else {
            log.error("Failed to delete comment {} for user {} (superAdmin: {})", commentId, userId, finalIsSuperAdmin);
            return ResponseEntity.notFound().build();
        }
    }
} 