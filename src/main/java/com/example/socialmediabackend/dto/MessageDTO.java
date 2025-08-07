package com.example.socialmediabackend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageDTO {
    private Long conversationId;
    private Long senderId;
    private String content;
    private Long timestamp;

    @Override
    public String toString() {
        return "MessageDTO{" +
                "conversationId=" + conversationId +
                ", senderId=" + senderId +
                ", content='" + content + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
