package com.example.socialmediabackend.controller;

import com.example.socialmediabackend.dto.CommentDto;
import com.example.socialmediabackend.dto.CommentResponseDto;
import com.example.socialmediabackend.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/post/{postId}/user/{userId}")
    public ResponseEntity<CommentResponseDto> createComment(@PathVariable Long postId, @PathVariable Long userId, @RequestBody CommentDto commentDto) {
        return commentService.createCommentResponse(postId, userId, commentDto)
                .map(comment -> ResponseEntity.status(HttpStatus.CREATED).body(comment))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<CommentResponseDto>> getCommentsByPost(@PathVariable Long postId) {
        return ResponseEntity.ok(commentService.getAllCommentResponsesByPostId(postId));
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<CommentResponseDto> getCommentById(@PathVariable Long commentId) {
        return commentService.getCommentResponseById(commentId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{commentId}/user/{userId}")
    public ResponseEntity<CommentResponseDto> updateComment(@PathVariable Long commentId, @PathVariable Long userId, @RequestBody CommentDto commentDto) {
        return commentService.updateCommentResponse(commentId, userId, commentDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{commentId}/user/{userId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId, @PathVariable Long userId) {
        boolean deleted = commentService.deleteComment(commentId, userId);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
} 