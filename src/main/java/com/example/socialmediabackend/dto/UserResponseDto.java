package com.example.socialmediabackend.dto;

public class UserResponseDto {
    private Long id;
    private String email;
    private Long profileId;

    public UserResponseDto() {}

    public UserResponseDto(Long id, String email, Long profileId) {
        this.id = id;
        this.email = email;
        this.profileId = profileId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getProfileId() {
        return profileId;
    }

    public void setProfileId(Long profileId) {
        this.profileId = profileId;
    }
} 