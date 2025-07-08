package com.example.socialmediabackend.service;

import com.example.socialmediabackend.entity.Conversation;
import com.example.socialmediabackend.entity.Message;
import com.example.socialmediabackend.repository.ConversationRepository;
import com.example.socialmediabackend.repository.MessageRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;

}