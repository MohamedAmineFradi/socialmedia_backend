package com.example.socialmediabackend.dto;

import java.time.Instant;
import com.example.socialmediabackend.entity.ReactionType;

public class ReactionResponseDto {
    private Long id;
    private ReactionType type;
    private Instant createdAt;
    private Long postId;
    private Long userId;


    public ReactionResponseDto(Long id, ReactionType type, Instant createdAt, Long postId, Long userId) {
        this.id = id;
        this.type = type;
        this.createdAt = createdAt;
        this.postId = postId;
        this.userId = userId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public ReactionType getType() { return type; }
    public void setType(ReactionType type) { this.type = type; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Long getPostId() { return postId; }
    public void setPostId(Long postId) { this.postId = postId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
} 