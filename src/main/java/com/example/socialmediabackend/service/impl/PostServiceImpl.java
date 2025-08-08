package com.example.socialmediabackend.service.impl;

import com.example.socialmediabackend.dto.PostDto;
import com.example.socialmediabackend.dto.PostResponseDto;
import com.example.socialmediabackend.entity.Post;
import com.example.socialmediabackend.entity.Reaction;
import com.example.socialmediabackend.entity.User;
import com.example.socialmediabackend.repository.PostRepository;
import com.example.socialmediabackend.repository.UserRepository;
import com.example.socialmediabackend.repository.CommentRepository;
import com.example.socialmediabackend.repository.ReactionRepository;
import com.example.socialmediabackend.service.PostService;
import com.example.socialmediabackend.service.UserService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
@Slf4j
public class PostServiceImpl implements PostService {

    @PersistenceContext
    private EntityManager entityManager;

    private final PostRepository postRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final ReactionRepository reactionRepository;

    @Override
    public boolean deletePost(Long postId, Long userId) {
        return deletePost(postId, userId, false);
    }

    @Override
    public boolean deletePost(Long postId, Long userId, boolean isSuperAdmin) {
        return postRepository.findById(postId)
                .filter(post -> post.getAuthor().getId().equals(userId) || isSuperAdmin)
                .map(post -> {
                    commentRepository.deleteByPostId(postId);
                    reactionRepository.deleteByPostId(postId);

                    entityManager.flush();
                    entityManager.clear();

                    postRepository.deleteById(postId);
                    return true;
                })
                .orElse(false);
    }

    @Override
    public PostResponseDto toPostResponseDto(Post post) {
        return toPostResponseDto(post, null);
    }

    @Override
    public PostResponseDto toPostResponseDto(Post post, Long currentUserId) {
        Long authorId = post.getAuthor() != null ? post.getAuthor().getId() : null;
        String authorName = post.getAuthor() != null ?
                (post.getAuthor().getFirstName() + " " + post.getAuthor().getLastName()).trim() : "Unknown User";
        String authorUsername = post.getAuthor() != null ? post.getAuthor().getUsername() : null;
        int likes = (int) post.getReactions().stream().filter(r -> r.getType() != null && r.getType().name().equals("LIKE")).count();
        int dislikes = (int) post.getReactions().stream().filter(r -> r.getType() != null && r.getType().name().equals("DISLIKE")).count();
        int commentCount = post.getComments() != null ? post.getComments().size() : 0;

        PostResponseDto.UserReactionDto userReaction = null;
        if (currentUserId != null) {
            Optional<Reaction> userReactionOpt = post.getReactions().stream()
                    .filter(r -> r.getUser() != null && r.getUser().getId().equals(currentUserId))
                    .findFirst();
            if (userReactionOpt.isPresent()) {
                Reaction reaction = userReactionOpt.get();
                userReaction = new PostResponseDto.UserReactionDto(reaction.getId(), reaction.getType().name());
            }
        }

        return new PostResponseDto(post.getId(), post.getContent(), post.getCreatedAt(), authorId, authorName, authorUsername,
                likes, dislikes, commentCount, userReaction);
    }

    @Override
    public List<PostResponseDto> getAllPostResponses() {
        return postRepository.findAll().stream().map(this::toPostResponseDto).toList();
    }

    @Override
    public List<PostResponseDto> getAllPostResponsesWithUserReactions(Long currentUserId) {
        return postRepository.findAll().stream().map(post -> toPostResponseDto(post, currentUserId)).toList();
    }

    @Override
    public List<PostResponseDto> getPostResponsesByUserId(Long userId) {
        return postRepository.findByAuthorId(userId).stream().map(this::toPostResponseDto).toList();
    }

    @Override
    public Optional<PostResponseDto> getPostResponseById(Long postId) {
        return postRepository.findById(postId).map(this::toPostResponseDto);
    }

    @Override
    public Optional<PostResponseDto> createPostResponse(Long userId, PostDto postDto) {
        log.info("Creating post for user ID: {} with content: {}", userId, postDto.getContent());

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            log.info("Found user: {} (ID: {})", user.getUsername(), user.getId());

            Post post = new Post();
            post.setContent(postDto.getContent());
            post.setAuthor(user);
            post.setCreatedAt(Instant.now());

            Post savedPost = postRepository.save(post);
            log.info("Successfully saved post with ID: {}", savedPost.getId());

            return Optional.of(toPostResponseDto(savedPost));
        } else {
            log.error("User not found with ID: {}", userId);
            return Optional.empty();
        }
    }

    @Override
    public Optional<PostResponseDto> updatePostResponse(Long postId, Long userId, PostDto postDto) {
        return updatePostResponse(postId, userId, postDto, false);
    }

    @Override
    public Optional<PostResponseDto> updatePostResponse(Long postId, Long userId, PostDto postDto, boolean isSuperAdmin) {
        return postRepository.findById(postId).filter(post ->
                post.getAuthor().getId().equals(userId) || isSuperAdmin)
            .map(post -> {
                post.setContent(postDto.getContent());
                return toPostResponseDto(postRepository.save(post));
            });
    }
} 