package com.example.socialmediabackend.service.impl;

import com.example.socialmediabackend.dto.ReactionDto;
import com.example.socialmediabackend.dto.ReactionResponseDto;
import com.example.socialmediabackend.entity.Post;
import com.example.socialmediabackend.entity.Reaction;
import com.example.socialmediabackend.entity.User;
import com.example.socialmediabackend.repository.PostRepository;
import com.example.socialmediabackend.repository.ReactionRepository;
import com.example.socialmediabackend.repository.UserRepository;
import com.example.socialmediabackend.service.ReactionService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class ReactionServiceImpl implements ReactionService {
    private final ReactionRepository reactionRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public ReactionServiceImpl(ReactionRepository reactionRepository, PostRepository postRepository, UserRepository userRepository) {
        this.reactionRepository = reactionRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    private ReactionResponseDto toReactionResponseDto(Reaction reaction) {
        return new ReactionResponseDto(
            reaction.getId(),
            reaction.getType(),
            reaction.getCreatedAt(),
            reaction.getPost() != null ? reaction.getPost().getId() : null,
            reaction.getUser() != null ? reaction.getUser().getId() : null
        );
    }

    @Override
    public List<ReactionResponseDto> getAllReactionResponsesByPostId(Long postId) {
        return reactionRepository.findAll().stream()
            .filter(reaction -> reaction.getPost() != null && reaction.getPost().getId().equals(postId))
            .map(this::toReactionResponseDto).toList();
    }

    @Override
    public Optional<ReactionResponseDto> getReactionResponseById(Long reactionId) {
        return reactionRepository.findById(reactionId).map(this::toReactionResponseDto);
    }

    @Override
    public Optional<ReactionResponseDto> getReactionResponseByPostAndUser(Long postId, Long userId) {
        return reactionRepository.findByPostIdAndUserId(postId, userId)
                .map(this::toReactionResponseDto);
    }

    @Override
    public Optional<ReactionResponseDto> createOrUpdateReactionResponse(Long postId, Long userId, ReactionDto reactionDto) {
        Optional<Post> postOpt = postRepository.findById(postId);
        Optional<User> userOpt = userRepository.findById(userId);
        if (postOpt.isPresent() && userOpt.isPresent()) {
            Optional<Reaction> existing = reactionRepository.findByPostIdAndUserId(postId, userId);
            if (existing.isPresent()) {
                Reaction reaction = existing.get();
                if (reaction.getType() == reactionDto.getType()) {
                    reactionRepository.delete(reaction);
                    return Optional.empty();
                } else {
                    reaction.setType(reactionDto.getType());
                    reaction.setCreatedAt(Instant.now());
                    return Optional.of(toReactionResponseDto(reactionRepository.save(reaction)));
                }
            } else {
                Reaction reaction = new Reaction();
                reaction.setPost(postOpt.get());
                reaction.setUser(userOpt.get());
                reaction.setType(reactionDto.getType());
                reaction.setCreatedAt(Instant.now());
                return Optional.of(toReactionResponseDto(reactionRepository.save(reaction)));
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean deleteReaction(Long reactionId, Long userId) {
        return deleteReaction(reactionId, userId, false);
    }

    @Override
    public boolean deleteReaction(Long reactionId, Long userId, boolean isSuperAdmin) {
        return reactionRepository.findById(reactionId)
            .filter(reaction ->
                (reaction.getUser() != null && reaction.getUser().getId().equals(userId)) || isSuperAdmin)
            .map(reaction -> {
                reactionRepository.delete(reaction);
                return true;
            }).orElse(false);
    }
} 