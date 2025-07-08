package com.example.socialmediabackend.dto;

public class ProfileResponseDto {
    private Long id;
    private String userName;
    private String imageUrl;
    private String bio;
    private String info;
    private Long userId;

    public ProfileResponseDto() {}

    public ProfileResponseDto(Long id, String userName, String imageUrl, String bio, String info, Long userId) {
        this.id = id;
        this.userName = userName;
        this.imageUrl = imageUrl;
        this.bio = bio;
        this.info = info;
        this.userId = userId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public String getInfo() { return info; }
    public void setInfo(String info) { this.info = info; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
} 