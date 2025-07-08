package com.example.socialmediabackend.dto;

import com.example.socialmediabackend.entity.ReactionType;

public class ReactionDto {
    private ReactionType type;

    public ReactionType getType() {
        return type;
    }

    public void setType(ReactionType type) {
        this.type = type;
    }
} 