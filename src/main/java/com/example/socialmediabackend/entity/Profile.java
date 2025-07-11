package com.example.socialmediabackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String username;
    private String bio;
    private String location;
    private String website;
    private String birthday;
    private String avatar;
    private String info;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}