package com.example.socialmediabackend.service;

import com.example.socialmediabackend.dto.ReactionDto;
import com.example.socialmediabackend.dto.ReactionResponseDto;

import java.util.List;
import java.util.Optional;

public interface ReactionService {
    List<ReactionResponseDto> getAllReactionResponsesByPostId(Long postId);

    Optional<ReactionResponseDto> getReactionResponseById(Long reactionId);

    Optional<ReactionResponseDto> getReactionResponseByPostAndUser(Long postId, Long userId);

    Optional<ReactionResponseDto> createOrUpdateReactionResponse(Long postId, Long userId, ReactionDto reactionDto);

    boolean deleteReaction(Long reactionId, Long userId);

    boolean deleteReaction(Long reactionId, Long userId, boolean isSuperAdmin);
}