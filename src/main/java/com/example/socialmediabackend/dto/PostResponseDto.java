package com.example.socialmediabackend.dto;

import java.time.Instant;

public class PostResponseDto {
    private Long id;
    private String content;
    private Instant createdAt;
    private Long authorId;
    private int likes;
    private int dislikes;
    private int commentCount;

    public PostResponseDto() {}

    public PostResponseDto(Long id, String content, Instant createdAt, Long authorId, int likes, int dislikes, int commentCount) {
        this.id = id;
        this.content = content;
        this.createdAt = createdAt;
        this.authorId = authorId;
        this.likes = likes;
        this.dislikes = dislikes;
        this.commentCount = commentCount;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }
    public int getLikes() { return likes; }
    public void setLikes(int likes) { this.likes = likes; }
    public int getDislikes() { return dislikes; }
    public void setDislikes(int dislikes) { this.dislikes = dislikes; }
    public int getCommentCount() { return commentCount; }
    public void setCommentCount(int commentCount) { this.commentCount = commentCount; }
} 