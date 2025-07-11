package com.example.socialmediabackend.dto;

public class ProfileResponseDto {
    private Long id;
    private String name;
    private String username;
    private String bio;
    private String location;
    private String website;
    private String birthday;
    private String avatar;
    private String info;
    private Long userId;

    public ProfileResponseDto() {}

    public ProfileResponseDto(Long id, String name, String username, String bio, String location, String website, String birthday, String avatar, String info, Long userId) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.bio = bio;
        this.location = location;
        this.website = website;
        this.birthday = birthday;
        this.avatar = avatar;
        this.info = info;
        this.userId = userId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }
    public String getBirthday() { return birthday; }
    public void setBirthday(String birthday) { this.birthday = birthday; }
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    public String getInfo() { return info; }
    public void setInfo(String info) { this.info = info; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
} 