package com.example.socialmediabackend.controller;

import com.example.socialmediabackend.dto.ReactionDto;
import com.example.socialmediabackend.dto.ReactionResponseDto;
import com.example.socialmediabackend.service.ReactionService;
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
@RequestMapping("/api/reactions")
@RequiredArgsConstructor
@Slf4j
public class ReactionController {
    private final ReactionService reactionService;
    private final JwtUtil jwtUtil;

    @PostMapping("/post/{postId}/user/{userId}")
    // @PreAuthorize("hasAnyRole('user', 'superAdmin') and (#userId == authentication.principal.subject or hasRole('superAdmin'))")
    public ResponseEntity<ReactionResponseDto> createOrUpdateReaction(@PathVariable Long postId, @PathVariable Long userId, @RequestBody ReactionDto reactionDto, HttpServletRequest request) {
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
        
        log.info("Creating/updating reaction for post {} by user {} (superAdmin: {})", postId, userId, isSuperAdmin);
        
        // For now, reactions don't need superadmin check for creation/update
        // Users can only create reactions for themselves
        final boolean finalIsSuperAdmin = isSuperAdmin;
        return reactionService.createOrUpdateReactionResponse(postId, userId, reactionDto)
                .map(reaction -> {
                    log.info("Successfully created/updated reaction with ID: {}", reaction.getId());
                    return ResponseEntity.status(HttpStatus.CREATED).body(reaction);
                })
                .orElseGet(() -> {
                    log.error("Failed to create/update reaction for post {} by user {} (superAdmin: {})", postId, userId, finalIsSuperAdmin);
                    return ResponseEntity.notFound().build();
                });
    }

    @GetMapping("/post/{postId}")
    // @PreAuthorize("hasAnyRole('user', 'superAdmin')")
    public ResponseEntity<List<ReactionResponseDto>> getReactionsByPost(@PathVariable Long postId) {
        return ResponseEntity.ok(reactionService.getAllReactionResponsesByPostId(postId));
    }

    @GetMapping("/{reactionId}")
    // @PreAuthorize("hasAnyRole('user', 'superAdmin')")
    public ResponseEntity<ReactionResponseDto> getReactionById(@PathVariable Long reactionId) {
        return reactionService.getReactionResponseById(reactionId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/post/{postId}/user/{userId}")
    // @PreAuthorize("hasAnyRole('user', 'superAdmin') and (#userId == authentication.principal.subject or hasRole('superAdmin'))")
    public ResponseEntity<ReactionResponseDto> getReactionByPostAndUser(@PathVariable Long postId, @PathVariable Long userId) {
        return reactionService.getReactionResponseByPostAndUser(postId, userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{reactionId}/user/{userId}")
    // @PreAuthorize("hasAnyRole('user', 'superAdmin') and (#userId == authentication.principal.subject or hasRole('superAdmin'))")
    public ResponseEntity<Void> deleteReaction(@PathVariable Long reactionId, @PathVariable Long userId, HttpServletRequest request) {
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
        
        log.info("Deleting reaction {} by user {} (superAdmin: {})", reactionId, userId, isSuperAdmin);
        
        final boolean finalIsSuperAdmin = isSuperAdmin;
        boolean deleted = reactionService.deleteReaction(reactionId, userId, finalIsSuperAdmin);
        if (deleted) {
            log.info("Successfully deleted reaction {}", reactionId);
            return ResponseEntity.noContent().build();
        } else {
            log.error("Failed to delete reaction {} for user {} (superAdmin: {})", reactionId, userId, finalIsSuperAdmin);
            return ResponseEntity.notFound().build();
        }
    }
}