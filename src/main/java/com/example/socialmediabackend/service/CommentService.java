package com.example.socialmediabackend.service;

import com.example.socialmediabackend.dto.CommentDto;
import com.example.socialmediabackend.dto.CommentResponseDto;

import java.util.List;
import java.util.Optional;

public interface CommentService {
    List<CommentResponseDto> getAllCommentResponsesByPostId(Long postId);

    Optional<CommentResponseDto> getCommentResponseById(Long commentId);

    Optional<CommentResponseDto> createCommentResponse(Long postId, Long userId, CommentDto commentDto);

    Optional<CommentResponseDto> updateCommentResponse(Long commentId, Long userId, CommentDto commentDto);

    Optional<CommentResponseDto> updateCommentResponse(Long commentId, Long userId, CommentDto commentDto, boolean isSuperAdmin);

    boolean deleteComment(Long commentId, Long userId);

    boolean deleteComment(Long commentId, Long userId, boolean isSuperAdmin);
} 