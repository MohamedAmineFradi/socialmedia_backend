package com.example.socialmediabackend.dto;

import lombok.Data;

@Data
public class UserBasicInfoDto {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    
    // No complex object references to prevent circular dependencies
}
