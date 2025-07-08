package com.example.socialmediabackend.entity;

import jakarta.persistence.*;

@Entity
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userName;
    private String imageUrl;
    private String bio;
    private String info;

    @OneToOne(mappedBy = "profile")
    private User user;

}