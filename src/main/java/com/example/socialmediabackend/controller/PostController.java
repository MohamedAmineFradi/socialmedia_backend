package com.example.socialmediabackend.controller;

import com.example.socialmediabackend.dto.PostDto;
import com.example.socialmediabackend.dto.PostResponseDto;
import com.example.socialmediabackend.service.PostService;
import com.example.socialmediabackend.service.UserService;
import com.example.socialmediabackend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.prepost.PreAuthorize;
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
    // @PreAuthorize("hasAnyRole('user', 'superAdmin')")
    public ResponseEntity<List<PostResponseDto>> getAllPosts() {
        String currentUser = jwtUtil.getCurrentUsername();
        boolean isSuperAdmin = jwtUtil.isSuperAdmin();
        
        // Get current user ID for reactions
        Long currentUserId = null;
        if (currentUser != null) {
            currentUserId = userService.getFirstUserForDev()
                    .map(user -> user.getId())
                    .orElse(1L);
        }
        
        List<PostResponseDto> posts = currentUserId != null ? 
            postService.getAllPostResponsesWithUserReactions(currentUserId) : 
            postService.getAllPostResponses();
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{id}")
    // @PreAuthorize("hasAnyRole('user', 'superAdmin')")
    public ResponseEntity<PostResponseDto> getPostById(@PathVariable Long id) {
        String currentUser = jwtUtil.getCurrentUsername();
        boolean isSuperAdmin = jwtUtil.isSuperAdmin();
        
        return postService.getPostResponseById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    // @PreAuthorize("hasAnyRole('user', 'superAdmin')")
    public ResponseEntity<PostResponseDto> createPost(@RequestBody PostDto postDto) {
        log.info("Creating post with content: {}", postDto.getContent());
        
        String currentUserId = jwtUtil.getCurrentUserId();
        Long userId;
        
        if (currentUserId != null) {
            // In production mode, use the JWT user ID
            log.info("Using JWT user ID: {}", currentUserId);
            userId = Long.valueOf(currentUserId);
        } else {
            // In dev mode, use the first user from database
            log.info("No JWT user ID, using first user from database");
            userId = userService.getFirstUserForDev()
                    .map(user -> {
                        log.info("Found first user: {} (ID: {})", user.getUsername(), user.getId());
                        return user.getId();
                    })
                    .orElse(1L); // Fallback to user ID 1
            log.info("Using user ID: {}", userId);
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
    // @PreAuthorize("hasAnyRole('user', 'superAdmin')")
    public ResponseEntity<PostResponseDto> updatePost(@PathVariable Long id, @RequestBody PostDto postDto) {
        String currentUserId = jwtUtil.getCurrentUserId();
        Long userId;
        boolean isSuperAdmin = jwtUtil.isSuperAdmin();
        
        if (currentUserId != null) {
            // In production mode, use the JWT user ID
            userId = Long.valueOf(currentUserId);
        } else {
            // In dev mode, use the first user from database
            userId = userService.getFirstUserForDev()
                    .map(user -> user.getId())
                    .orElse(1L); // Fallback to user ID 1
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
    // @PreAuthorize("hasAnyRole('user', 'superAdmin')")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        String currentUserId = jwtUtil.getCurrentUserId();
        Long userId;
        boolean isSuperAdmin = jwtUtil.isSuperAdmin();
        
        if (currentUserId != null) {
            // In production mode, use the JWT user ID
            userId = Long.valueOf(currentUserId);
        } else {
            // In dev mode, use the first user from database
            userId = userService.getFirstUserForDev()
                    .map(user -> user.getId())
                    .orElse(1L); // Fallback to user ID 1
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
    // @PreAuthorize("hasAnyRole('user', 'superAdmin')")
    public ResponseEntity<List<PostResponseDto>> getPostsByUserId(@PathVariable String userId) {
        String currentUser = jwtUtil.getCurrentUsername();
        boolean isSuperAdmin = jwtUtil.isSuperAdmin();
        
        List<PostResponseDto> posts = postService.getPostResponsesByUserId(Long.valueOf(userId));
        return ResponseEntity.ok(posts);
    }
}