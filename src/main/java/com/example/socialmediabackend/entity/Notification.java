package com.example.socialmediabackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
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