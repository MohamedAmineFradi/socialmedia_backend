package com.example.socialmediabackend.service;

import com.example.socialmediabackend.dto.ReactionDto;
import com.example.socialmediabackend.dto.ReactionResponseDto;
import com.example.socialmediabackend.entity.Post;
import com.example.socialmediabackend.entity.Reaction;
import com.example.socialmediabackend.entity.ReactionType;
import com.example.socialmediabackend.entity.User;
import com.example.socialmediabackend.repository.PostRepository;
import com.example.socialmediabackend.repository.ReactionRepository;
import com.example.socialmediabackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class ReactionService {
    private final ReactionRepository reactionRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Autowired
    public ReactionService(ReactionRepository reactionRepository, PostRepository postRepository, UserRepository userRepository) {
        this.reactionRepository = reactionRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    public ReactionResponseDto toReactionResponseDto(Reaction reaction) {
        return new ReactionResponseDto(
            reaction.getId(),
            reaction.getType(),
            reaction.getCreatedAt(),
            reaction.getPost() != null ? reaction.getPost().getId() : null,
            reaction.getUser() != null ? reaction.getUser().getId() : null
        );
    }

    public List<ReactionResponseDto> getAllReactionResponsesByPostId(Long postId) {
        return reactionRepository.findAll().stream()
            .filter(reaction -> reaction.getPost() != null && reaction.getPost().getId().equals(postId))
            .map(this::toReactionResponseDto).toList();
    }

    public Optional<ReactionResponseDto> getReactionResponseById(Long reactionId) {
        return reactionRepository.findById(reactionId).map(this::toReactionResponseDto);
    }

    public Optional<ReactionResponseDto> createOrUpdateReactionResponse(Long postId, Long userId, ReactionDto reactionDto) {
        Optional<Post> postOpt = postRepository.findById(postId);
        Optional<User> userOpt = userRepository.findById(userId);
        if (postOpt.isPresent() && userOpt.isPresent()) {
            // Check if a reaction by this user on this post already exists
            Optional<Reaction> existing = reactionRepository.findByPostIdAndUserId(postId, userId);
            Reaction reaction = existing.orElseGet(Reaction::new);
            reaction.setPost(postOpt.get());
            reaction.setUser(userOpt.get());
            reaction.setType(reactionDto.getType());
            reaction.setCreatedAt(Instant.now());
            return Optional.of(toReactionResponseDto(reactionRepository.save(reaction)));
        }
        return Optional.empty();
    }

    public boolean deleteReaction(Long reactionId, Long userId) {
        return reactionRepository.findById(reactionId)
            .filter(reaction -> reaction.getUser() != null && reaction.getUser().getId().equals(userId))
            .map(reaction -> {
                reactionRepository.delete(reaction);
                return true;
            }).orElse(false);
    }
}