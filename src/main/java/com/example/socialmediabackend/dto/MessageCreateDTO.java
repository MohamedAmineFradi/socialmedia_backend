package com.example.socialmediabackend.dto;

import com.example.socialmediabackend.util.MessageConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageCreateDTO {
    @NotBlank(message = "Content cannot be blank")
    @Size(max = MessageConstants.MAX_MESSAGE_LENGTH, message = "Content cannot exceed " + MessageConstants.MAX_MESSAGE_LENGTH + " characters")
    private String content;

    @NotNull(message = "Conversation ID is required")
    private Long conversationId;

    @NotNull(message = "Sender ID is required")
    private Long senderId;

    private String clientMessageId;
}