package com.example.socialmediabackend.service;

import com.example.socialmediabackend.entity.Post;
import com.example.socialmediabackend.entity.User;
import com.example.socialmediabackend.dto.PostDto;
import com.example.socialmediabackend.repository.PostRepository;
import com.example.socialmediabackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserService userService;
    private final UserRepository userRepository;


    public List<Post> getPostsByUserId(Long userId) {
        return postRepository.findByAuthorId(userId);
    }

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public Optional<Post> getPostById(Long postId) {
        return postRepository.findById(postId);
    }

    public Optional<Post> createPost(Long userId, PostDto postDto) {
        return userRepository.findById(userId).map(user -> {
            Post post = new Post();
            post.setContent(postDto.getContent());
            post.setAuthor(user);
            post.setCreatedAt(Instant.now());
            return postRepository.save(post);
        });
    }

    public Optional<Post> updatePost(Long postId, Long userId, PostDto postDto) {
        return postRepository.findById(postId).filter(post -> post.getAuthor().getId().equals(userId)).map(post -> {
            post.setContent(postDto.getContent());
            return postRepository.save(post);
        });
    }

    public boolean deletePost(Long postId, Long userId) {
        return postRepository.findById(postId).filter(post -> post.getAuthor().getId().equals(userId)).map(post -> {
            postRepository.delete(post);
            return true;
        }).orElse(false);
    }
}