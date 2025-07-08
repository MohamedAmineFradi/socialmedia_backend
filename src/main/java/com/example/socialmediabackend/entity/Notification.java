package com.example.socialmediabackend.entity;

import jakarta.persistence.*;

@Entity
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private NotificationSourceType sourceType;

    private boolean isRead;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}