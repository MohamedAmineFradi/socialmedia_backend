package com.example.socialmediabackend.dto;

import java.time.Instant;

public class CommentResponseDto {
    private Long id;
    private String content;
    private Instant createdAt;
    private Long postId;
    private Long userId;
    private String authorName;
    private String authorUsername;


    public CommentResponseDto(Long id, String content, Instant createdAt, Long postId, Long userId, String authorName, String authorUsername) {
        this.id = id;
        this.content = content;
        this.createdAt = createdAt;
        this.postId = postId;
        this.userId = userId;
        this.authorName = authorName;
        this.authorUsername = authorUsername;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Long getPostId() { return postId; }
    public void setPostId(Long postId) { this.postId = postId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }
    public String getAuthorUsername() { return authorUsername; }
    public void setAuthorUsername(String authorUsername) { this.authorUsername = authorUsername; }
} 