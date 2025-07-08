package com.example.socialmediabackend.repository;

import com.example.socialmediabackend.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {
}