package com.example.socialmediabackend.service;

import com.example.socialmediabackend.dto.MessageCreateDTO;
import com.example.socialmediabackend.dto.MessageResponseDTO;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;

public interface MessageService {
    MessageResponseDTO processAndSendMessage(MessageCreateDTO messageCreateDTO);

    void handleWebSocketMessage(MessageCreateDTO dto, Principal principal);

}