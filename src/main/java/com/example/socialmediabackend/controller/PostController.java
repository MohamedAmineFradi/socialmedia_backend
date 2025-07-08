package com.example.socialmediabackend.controller;

import com.example.socialmediabackend.entity.Post;
import com.example.socialmediabackend.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import com.example.socialmediabackend.dto.PostDto;
import org.springframework.http.HttpStatus;
import com.example.socialmediabackend.dto.PostResponseDto;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @GetMapping
    public ResponseEntity<List<PostResponseDto>> getAllPosts() {
        return ResponseEntity.ok(postService.getAllPostResponses());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PostResponseDto>> getPostsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(postService.getPostResponsesByUserId(userId));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponseDto> getPostById(@PathVariable Long postId) {
        return postService.getPostResponseById(postId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<PostResponseDto> createPost(@PathVariable Long userId, @RequestBody PostDto postDto) {
        return postService.createPostResponse(userId, postDto)
                .map(post -> ResponseEntity.status(HttpStatus.CREATED).body(post))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{postId}/user/{userId}")
    public ResponseEntity<PostResponseDto> updatePost(@PathVariable Long postId, @PathVariable Long userId, @RequestBody PostDto postDto) {
        return postService.updatePostResponse(postId, userId, postDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{postId}/user/{userId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId, @PathVariable Long userId) {
        boolean deleted = postService.deletePost(postId, userId);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}