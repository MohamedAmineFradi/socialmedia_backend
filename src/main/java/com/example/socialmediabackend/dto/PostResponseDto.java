package com.example.socialmediabackend.dto;

import java.time.Instant;

public class PostResponseDto {
    private Long id;
    private String content;
    private Instant createdAt;
    private Long authorId;

    public PostResponseDto() {}

    public PostResponseDto(Long id, String content, Instant createdAt, Long authorId) {
        this.id = id;
        this.content = content;
        this.createdAt = createdAt;
        this.authorId = authorId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }
} 