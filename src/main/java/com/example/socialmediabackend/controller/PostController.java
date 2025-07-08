package com.example.socialmediabackend.controller;

import com.example.socialmediabackend.entity.Post;
import com.example.socialmediabackend.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import com.example.socialmediabackend.dto.PostDto;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @GetMapping
    public ResponseEntity<List<Post>> getAllPosts() {
        return ResponseEntity.ok(postService.getAllPosts());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Post>> getPostsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(postService.getPostsByUserId(userId));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<Post> getPostById(@PathVariable Long postId) {
        return postService.getPostById(postId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<Post> createPost(@PathVariable Long userId, @RequestBody PostDto postDto) {
        return postService.createPost(userId, postDto)
                .map(post -> ResponseEntity.status(HttpStatus.CREATED).body(post))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{postId}/user/{userId}")
    public ResponseEntity<Post> updatePost(@PathVariable Long postId, @PathVariable Long userId, @RequestBody PostDto postDto) {
        return postService.updatePost(postId, userId, postDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{postId}/user/{userId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId, @PathVariable Long userId) {
        boolean deleted = postService.deletePost(postId, userId);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}