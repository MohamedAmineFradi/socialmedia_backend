package com.example.socialmediabackend.controller;

import com.example.socialmediabackend.dto.ReactionDto;
import com.example.socialmediabackend.dto.ReactionResponseDto;
import com.example.socialmediabackend.service.ReactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reactions")
@RequiredArgsConstructor
public class ReactionController {
    private final ReactionService reactionService;

    @PostMapping("/post/{postId}/user/{userId}")
    public ResponseEntity<ReactionResponseDto> createOrUpdateReaction(@PathVariable Long postId, @PathVariable Long userId, @RequestBody ReactionDto reactionDto) {
        return reactionService.createOrUpdateReactionResponse(postId, userId, reactionDto)
                .map(reaction -> ResponseEntity.status(HttpStatus.CREATED).body(reaction))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<ReactionResponseDto>> getReactionsByPost(@PathVariable Long postId) {
        return ResponseEntity.ok(reactionService.getAllReactionResponsesByPostId(postId));
    }

    @GetMapping("/{reactionId}")
    public ResponseEntity<ReactionResponseDto> getReactionById(@PathVariable Long reactionId) {
        return reactionService.getReactionResponseById(reactionId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @GetMapping("/post/{postId}/user/{userId}")
    public ResponseEntity<ReactionResponseDto> getReactionByPostAndUser(@PathVariable Long postId, @PathVariable Long userId) {
        return reactionService.getReactionResponseByPostAndUser(postId, userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @DeleteMapping("/{reactionId}/user/{userId}")
    public ResponseEntity<Void> deleteReaction(@PathVariable Long reactionId, @PathVariable Long userId) {
        boolean deleted = reactionService.deleteReaction(reactionId, userId);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }


}