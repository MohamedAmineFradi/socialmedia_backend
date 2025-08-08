package com.example.socialmediabackend.repository;

import com.example.socialmediabackend.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}