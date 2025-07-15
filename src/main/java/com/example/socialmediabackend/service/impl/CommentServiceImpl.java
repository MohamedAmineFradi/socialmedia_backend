package com.example.socialmediabackend.service.impl;

import com.example.socialmediabackend.dto.CommentDto;
import com.example.socialmediabackend.dto.CommentResponseDto;
import com.example.socialmediabackend.entity.Comment;
import com.example.socialmediabackend.entity.Post;
import com.example.socialmediabackend.entity.User;
import com.example.socialmediabackend.repository.CommentRepository;
import com.example.socialmediabackend.repository.PostRepository;
import com.example.socialmediabackend.repository.UserRepository;
import com.example.socialmediabackend.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class CommentServiceImpl implements CommentService {
    private static final Logger logger = LoggerFactory.getLogger(CommentServiceImpl.class);

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public CommentServiceImpl(CommentRepository commentRepository, PostRepository postRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    private CommentResponseDto toCommentResponseDto(Comment comment) {
        String authorName = comment.getUser() != null ?
                (comment.getUser().getFirstName() + " " + comment.getUser().getLastName()).trim() : "Unknown User";
        String authorUsername = comment.getUser() != null ? comment.getUser().getUsername() : null;

        return new CommentResponseDto(
                comment.getId(),
                comment.getContent(),
                comment.getCreatedAt(),
                comment.getPost() != null ? comment.getPost().getId() : null,
                comment.getUser() != null ? comment.getUser().getId() : null,
                authorName,
                authorUsername
        );
    }

    @Override
    public List<CommentResponseDto> getAllCommentResponsesByPostId(Long postId) {
        return commentRepository.findAll().stream()
                .filter(comment -> comment.getPost() != null && comment.getPost().getId().equals(postId))
                .map(this::toCommentResponseDto).toList();
    }

    @Override
    public Optional<CommentResponseDto> getCommentResponseById(Long commentId) {
        return commentRepository.findById(commentId).map(this::toCommentResponseDto);
    }

    @Override
    public Optional<CommentResponseDto> createCommentResponse(Long postId, Long userId, CommentDto commentDto) {
        Optional<Post> postOpt = postRepository.findById(postId);
        Optional<User> userOpt = userRepository.findById(userId);
        if (postOpt.isPresent() && userOpt.isPresent()) {
            Comment comment = new Comment();
            comment.setContent(commentDto.getContent());
            comment.setCreatedAt(Instant.now());
            comment.setPost(postOpt.get());
            comment.setUser(userOpt.get());
            return Optional.of(toCommentResponseDto(commentRepository.save(comment)));
        }
        return Optional.empty();
    }

    @Override
    public Optional<CommentResponseDto> updateCommentResponse(Long commentId, Long userId, CommentDto commentDto) {
        return updateCommentResponse(commentId, userId, commentDto, false);
    }

    @Override
    public Optional<CommentResponseDto> updateCommentResponse(Long commentId, Long userId, CommentDto commentDto, boolean isSuperAdmin) {
        return commentRepository.findById(commentId)
                .filter(comment -> (comment.getUser() != null && comment.getUser().getId().equals(userId)) || isSuperAdmin)
                .map(comment -> {
                    comment.setContent(commentDto.getContent());
                    return toCommentResponseDto(commentRepository.save(comment));
                });
    }

    @Override
    public boolean deleteComment(Long commentId, Long userId) {
        return deleteComment(commentId, userId, false);
    }

    @Override
    public boolean deleteComment(Long commentId, Long userId, boolean isSuperAdmin) {
        return commentRepository.findById(commentId)
                .filter(comment -> (comment.getUser() != null && comment.getUser().getId().equals(userId)) || isSuperAdmin)
                .map(comment -> {
                    commentRepository.delete(comment);
                    return true;
                }).orElse(false);
    }
} 