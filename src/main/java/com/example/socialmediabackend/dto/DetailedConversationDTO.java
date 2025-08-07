package com.example.socialmediabackend.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Data
@Getter
@Setter
public class DetailedConversationDTO {
    private Long id;
    private Long user1Id;
    private Long user2Id;
    private Instant createdAt;
    private Instant lastUpdated;
}
