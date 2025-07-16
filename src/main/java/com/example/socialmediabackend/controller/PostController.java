package com.example.socialmediabackend.controller;

import com.example.socialmediabackend.dto.PostDto;
import com.example.socialmediabackend.dto.PostResponseDto;
import com.example.socialmediabackend.service.PostService;
import com.example.socialmediabackend.service.UserService;
import com.example.socialmediabackend.util.JwtUtil;
import com.example.socialmediabackend.dto.UserResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Slf4j
public class PostController {

    private final PostService postService;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @GetMapping
    @PreAuthorize("hasAnyRole('user', 'superAdmin')")
    public ResponseEntity<List<PostResponseDto>> getAllPosts() {
        String currentUser = jwtUtil.getCurrentUsername();
        boolean isSuperAdmin = jwtUtil.isSuperAdmin();
        Long currentUserId = null;
        if (currentUser != null) {
            currentUserId = userService.getUserByUsername(currentUser)
                    .map(user -> user.getId())
                    .orElse(null);
        }
        if (currentUserId == null) {
            log.error("No authenticated user found for fetching posts");
            return ResponseEntity.status(401).build();
        }
        List<PostResponseDto> posts = postService.getAllPostResponsesWithUserReactions(currentUserId);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('user', 'superAdmin')")
    public ResponseEntity<PostResponseDto> getPostById(@PathVariable Long id) {
        String currentUser = jwtUtil.getCurrentUsername();
        boolean isSuperAdmin = jwtUtil.isSuperAdmin();
        return postService.getPostResponseById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('user', 'superAdmin')")
    public ResponseEntity<PostResponseDto> createPost(@RequestBody PostDto postDto) {
        log.info("Creating post with content: {}", postDto.getContent());
        String keycloakId = jwtUtil.getCurrentUserId();
        if (keycloakId == null) {
            log.error("No authenticated user found for creating post");
            return ResponseEntity.status(401).build();
        }
        Long userId = userService.getUserByKeycloakId(keycloakId)
                .map(UserResponseDto::getId).orElse(null);
        if (userId == null) {
            log.error("No DB user found for Keycloak ID {}");
            return ResponseEntity.status(401).build();
        }
        return postService.createPostResponse(userId, postDto)
                .map(post -> {
                    log.info("Successfully created post with ID: {}", post.getId());
                    return ResponseEntity.ok(post);
                })
                .orElseGet(() -> {
                    log.error("Failed to create post for user ID: {}", userId);
                    return ResponseEntity.notFound().build();
                });
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('user', 'superAdmin')")
    public ResponseEntity<PostResponseDto> updatePost(@PathVariable Long id, @RequestBody PostDto postDto) {
        String keycloakId = jwtUtil.getCurrentUserId();
        boolean isSuperAdmin = jwtUtil.isSuperAdmin();
        if (keycloakId == null) {
            log.error("No authenticated user found for updating post");
            return ResponseEntity.status(401).build();
        }
        Long userId = userService.getUserByKeycloakId(keycloakId)
                .map(UserResponseDto::getId).orElse(null);
        if (userId == null) {
            log.error("No DB user found for Keycloak ID {}");
            return ResponseEntity.status(401).build();
        }
        log.info("Updating post {} by user {} (superAdmin: {})", id, userId, isSuperAdmin);
        return postService.updatePostResponse(id, userId, postDto, isSuperAdmin)
                .map(post -> {
                    log.info("Successfully updated post with ID: {}", post.getId());
                    return ResponseEntity.ok(post);
                })
                .orElseGet(() -> {
                    log.error("Failed to update post {} for user {} (superAdmin: {})", id, userId, isSuperAdmin);
                    return ResponseEntity.notFound().build();
                });
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('user', 'superAdmin')")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        String keycloakId = jwtUtil.getCurrentUserId();
        boolean isSuperAdmin = jwtUtil.isSuperAdmin();
        if (keycloakId == null) {
            log.error("No authenticated user found for deleting post");
            return ResponseEntity.status(401).build();
        }
        Long userId = userService.getUserByKeycloakId(keycloakId)
                .map(UserResponseDto::getId).orElse(null);
        if (userId == null) {
            log.error("No DB user found for Keycloak ID {}");
            return ResponseEntity.status(401).build();
        }
        log.info("Deleting post {} by user {} (superAdmin: {})", id, userId, isSuperAdmin);
        boolean deleted = postService.deletePost(id, userId, isSuperAdmin);
        if (deleted) {
            log.info("Successfully deleted post {}", id);
            return ResponseEntity.noContent().build();
        } else {
            log.error("Failed to delete post {} for user {} (superAdmin: {})", id, userId, isSuperAdmin);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('user', 'superAdmin')")
    public ResponseEntity<List<PostResponseDto>> getPostsByUserId(@PathVariable String userId) {
        String currentUser = jwtUtil.getCurrentUsername();
        boolean isSuperAdmin = jwtUtil.isSuperAdmin();
        List<PostResponseDto> posts = postService.getPostResponsesByUserId(Long.valueOf(userId));
        return ResponseEntity.ok(posts);
    }
}