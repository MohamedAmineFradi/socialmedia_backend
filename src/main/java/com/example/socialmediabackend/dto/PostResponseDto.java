package com.example.socialmediabackend.dto;

import java.time.Instant;

public class PostResponseDto {
    private Long id;
    private String content;
    private Instant createdAt;
    private Long authorId;
    private String authorName;
    private String authorUsername;
    private int likes;
    private int dislikes;
    private int commentCount;
    private UserReactionDto userReaction;

    public PostResponseDto() {}

    public PostResponseDto(Long id, String content, Instant createdAt, Long authorId, String authorName, String authorUsername, int likes, int dislikes, int commentCount, UserReactionDto userReaction) {
        this.id = id;
        this.content = content;
        this.createdAt = createdAt;
        this.authorId = authorId;
        this.authorName = authorName;
        this.authorUsername = authorUsername;
        this.likes = likes;
        this.dislikes = dislikes;
        this.commentCount = commentCount;
        this.userReaction = userReaction;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }
    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }
    public String getAuthorUsername() { return authorUsername; }
    public void setAuthorUsername(String authorUsername) { this.authorUsername = authorUsername; }
    public int getLikes() { return likes; }
    public void setLikes(int likes) { this.likes = likes; }
    public int getDislikes() { return dislikes; }
    public void setDislikes(int dislikes) { this.dislikes = dislikes; }
    public int getCommentCount() { return commentCount; }
    public void setCommentCount(int commentCount) { this.commentCount = commentCount; }
    public UserReactionDto getUserReaction() { return userReaction; }
    public void setUserReaction(UserReactionDto userReaction) { this.userReaction = userReaction; }

    // Inner class for user reaction
    public static class UserReactionDto {
        private Long id;
        private String type;

        public UserReactionDto() {}

        public UserReactionDto(Long id, String type) {
            this.id = id;
            this.type = type;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
    }
} 