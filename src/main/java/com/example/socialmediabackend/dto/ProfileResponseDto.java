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
    
    // User statistics
    private Integer postCount;
    private Integer commentCount;
    private Integer reactionCount;

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

    public ProfileResponseDto(Long id, String name, String username, String bio, String location, String website, String birthday, String avatar, String info, Long userId, Integer postCount, Integer commentCount, Integer reactionCount) {
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
        this.postCount = postCount;
        this.commentCount = commentCount;
        this.reactionCount = reactionCount;
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
    
    public Integer getPostCount() { return postCount; }
    public void setPostCount(Integer postCount) { this.postCount = postCount; }
    public Integer getCommentCount() { return commentCount; }
    public void setCommentCount(Integer commentCount) { this.commentCount = commentCount; }
    public Integer getReactionCount() { return reactionCount; }
    public void setReactionCount(Integer reactionCount) { this.reactionCount = reactionCount; }
} 