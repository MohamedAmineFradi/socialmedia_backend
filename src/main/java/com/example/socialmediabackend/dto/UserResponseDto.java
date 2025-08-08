package com.example.socialmediabackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {
    private Long id;
    private String keycloakId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private Boolean isActive;
    private Boolean enabled;
    private Long createdTimestamp;
    private List<String> roles;
    

    private Integer postCount;
    private Integer commentCount;
    private Integer reactionCount;
} 