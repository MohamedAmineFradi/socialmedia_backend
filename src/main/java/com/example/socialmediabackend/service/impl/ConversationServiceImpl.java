package com.example.socialmediabackend.service.impl;

import com.example.socialmediabackend.dto.DetailedConversationDTO;
import com.example.socialmediabackend.dto.MessageResponseDTO;
import com.example.socialmediabackend.entity.Conversation;
import com.example.socialmediabackend.entity.User;
import com.example.socialmediabackend.exception.ConversationNotFoundException;
import com.example.socialmediabackend.repository.ConversationRepository;
import com.example.socialmediabackend.repository.MessageRepository;
import com.example.socialmediabackend.repository.UserRepository;
import com.example.socialmediabackend.service.ConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConversationServiceImpl implements ConversationService {
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    @Transactional(readOnly = true)
    public List<DetailedConversationDTO> getUserConversations(Long userId) {
        List<Conversation> conversations = conversationRepository.findByUser1IdOrUser2Id(userId, userId);
        return conversations.stream().map(conv -> {
            DetailedConversationDTO dto = new DetailedConversationDTO();
            dto.setId(conv.getId());
            dto.setUser1Id(conv.getUser1().getId());
            dto.setUser2Id(conv.getUser2().getId());
            dto.setCreatedAt(conv.getCreatedAt());
            dto.setLastUpdated(conv.getLastUpdated());
            return dto;
        }).collect(Collectors.toList());
    }

    @Transactional
    public DetailedConversationDTO createConversation(DetailedConversationDTO dto) {
        User user1 = userRepository.findById(dto.getUser1Id()).orElseThrow();
        User user2 = userRepository.findById(dto.getUser2Id()).orElseThrow();
        if (user1.getId().equals(user2.getId())) {
            throw new IllegalArgumentException("Users must be different");
        }
        // Check for existing conversation
        List<Conversation> existing = conversationRepository.findByUser1IdOrUser2Id(user1.getId(), user1.getId())
                .stream()
                .filter(c -> (c.getUser1().getId().equals(user2.getId()) || c.getUser2().getId().equals(user2.getId())))
                .toList();
        if (!existing.isEmpty()) {
            throw new IllegalStateException("Conversation already exists");
        }
        Conversation conv = new Conversation();
        conv.setUser1(user1);
        conv.setUser2(user2);
        conv.setCreatedAt(Instant.now());
        conv.setLastUpdated(Instant.now());
        Conversation saved = conversationRepository.save(conv);
        dto.setId(saved.getId());
        dto.setCreatedAt(saved.getCreatedAt());
        dto.setLastUpdated(saved.getLastUpdated());
        return dto;
    }

    @Transactional(readOnly = true)
    public DetailedConversationDTO getConversationById(Long id) {
        Conversation conv = conversationRepository.findById(id).orElseThrow();
        DetailedConversationDTO dto = new DetailedConversationDTO();
        dto.setId(conv.getId());
        dto.setUser1Id(conv.getUser1().getId());
        dto.setUser2Id(conv.getUser2().getId());
        dto.setCreatedAt(conv.getCreatedAt());
        dto.setLastUpdated(conv.getLastUpdated());
        return dto;
    }

    @Transactional(readOnly = true)
    public Page<MessageResponseDTO> getMessages(Long conversationId, Pageable pageable) {
        return messageRepository.findByConversationId(conversationId, pageable)
                .map(msg -> {
                    MessageResponseDTO dto = new MessageResponseDTO();
                    dto.setId(msg.getId());
                    dto.setContent(msg.getBody());
                    dto.setSentAt(msg.getSentAt());
                    dto.setConversationId(msg.getConversation().getId());
                    dto.setSenderId(msg.getSender().getId());
                    return dto;
                });
    }

    @Override
    public Conversation getRawConversationById(Long id) {
        return conversationRepository.findById(id)
                .orElseThrow(() -> new ConversationNotFoundException("Conversation not found"));
    }
}
