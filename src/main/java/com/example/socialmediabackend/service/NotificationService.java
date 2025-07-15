package com.example.socialmediabackend.service;

import com.example.socialmediabackend.entity.Notification;

import java.util.List;
import java.util.Optional;

public interface NotificationService {
    List<Notification> getAllNotifications();

    Optional<Notification> getNotificationById(Long id);

    Notification saveNotification(Notification notification);

    void deleteNotification(Long id);
} 