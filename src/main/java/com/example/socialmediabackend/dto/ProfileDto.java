package com.example.socialmediabackend.dto;

public class ProfileDto {
    private String userName;
    private String imageUrl;
    private String bio;
    private String info;

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getInfo() { return info; }
    public void setInfo(String info) { this.info = info; }
} 