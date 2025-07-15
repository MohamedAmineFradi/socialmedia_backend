package com.example.socialmediabackend.service;

import com.example.socialmediabackend.dto.PostDto;
import com.example.socialmediabackend.dto.PostResponseDto;
import com.example.socialmediabackend.entity.Post;

import java.util.List;
import java.util.Optional;

public interface PostService {
    boolean deletePost(Long postId, Long userId);

    boolean deletePost(Long postId, Long userId, boolean isSuperAdmin);

    PostResponseDto toPostResponseDto(Post post);

    PostResponseDto toPostResponseDto(Post post, Long currentUserId);

    List<PostResponseDto> getAllPostResponses();

    List<PostResponseDto> getAllPostResponsesWithUserReactions(Long currentUserId);

    List<PostResponseDto> getPostResponsesByUserId(Long userId);

    Optional<PostResponseDto> getPostResponseById(Long postId);

    Optional<PostResponseDto> createPostResponse(Long userId, PostDto postDto);

    Optional<PostResponseDto> updatePostResponse(Long postId, Long userId, PostDto postDto);

    Optional<PostResponseDto> updatePostResponse(Long postId, Long userId, PostDto postDto, boolean isSuperAdmin);
}