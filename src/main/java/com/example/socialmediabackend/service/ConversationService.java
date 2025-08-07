package com.example.socialmediabackend.service;

import com.example.socialmediabackend.dto.DetailedConversationDTO;
import com.example.socialmediabackend.dto.MessageResponseDTO;
import com.example.socialmediabackend.entity.Conversation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ConversationService {
    List<DetailedConversationDTO> getUserConversations(Long userId);
    DetailedConversationDTO createConversation(DetailedConversationDTO dto);
    DetailedConversationDTO getConversationById(Long id);
    Conversation getRawConversationById(Long id);
    Page<MessageResponseDTO> getMessages(Long conversationId, Pageable pageable);
}
