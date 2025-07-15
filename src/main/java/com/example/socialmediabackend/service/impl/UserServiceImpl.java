package com.example.socialmediabackend.service.impl;

import com.example.socialmediabackend.dto.UserResponseDto;
import com.example.socialmediabackend.entity.User;
import com.example.socialmediabackend.repository.UserRepository;
import com.example.socialmediabackend.service.UserService;
import com.example.socialmediabackend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


//Implementation of UserService that handles linkage between Keycloak users and application data. 
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    // Basic queries    
    @Override
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToResponseDtoWithRoles)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserResponseDto> getAllUsersWithRoles() {
        return userRepository.findAll().stream()
                .map(this::convertToResponseDtoWithRoles)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<UserResponseDto> getUserById(Long id) {
        return userRepository.findById(id)
                .map(this::convertToResponseDtoWithRoles);
    }

    @Override
    public Optional<UserResponseDto> getUserByKeycloakId(String keycloakId) {
        return userRepository.findByKeycloakId(keycloakId)
                .map(this::convertToResponseDtoWithRoles);
    }

    @Override
    public Optional<UserResponseDto> getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(this::convertToResponseDtoWithRoles);
    }

    // -------------------------------------------------------------------------
    // Statistics helpers (not exposed in interface, used internally)
    // -------------------------------------------------------------------------
    public UserStats getUserStats(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            int postCount = user.getPosts() != null ? user.getPosts().size() : 0;
            int commentCount = user.getComments() != null ? user.getComments().size() : 0;
            int reactionCount = user.getReactions() != null ? user.getReactions().size() : 0;

            return new UserStats(postCount, commentCount, reactionCount);
        }
        return new UserStats(0, 0, 0);
    }

    public static class UserStats {
        private final int postCount;
        private final int commentCount;
        private final int reactionCount;

        public UserStats(int postCount, int commentCount, int reactionCount) {
            this.postCount = postCount;
            this.commentCount = commentCount;
            this.reactionCount = reactionCount;
        }

        public int getPostCount() { return postCount; }
        public int getCommentCount() { return commentCount; }
        public int getReactionCount() { return reactionCount; }
    }

    // -------------------------------------------------------------------------
    // Current user helpers
    // -------------------------------------------------------------------------
    @Override
    public Optional<UserResponseDto> getCurrentUser() {
        String currentUserId = jwtUtil.getCurrentUserId();
        String currentUsername = jwtUtil.getCurrentUsername();
        System.out.println("=== UserService.getCurrentUser() ===");
        System.out.println("JWT Current User ID: " + currentUserId);
        System.out.println("JWT Current Username: " + currentUsername);

        if (currentUsername != null) {
            // Try to find user by username first (more reliable than Keycloak ID)
            Optional<User> userOpt = userRepository.findByUsername(currentUsername);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                System.out.println("Found user in database by username: " + user.getUsername());
                System.out.println("User Keycloak ID: " + user.getKeycloakId());
                UserResponseDto response = convertToResponseDtoWithRoles(user);
                System.out.println("User roles assigned: " + response.getRoles());
                return Optional.of(response);
            } else {
                System.out.println("No user found in database for username: " + currentUsername);
            }
        }

        if (currentUserId != null) {
            // Fallback: try to find by Keycloak ID
            Optional<User> userOpt = userRepository.findByKeycloakId(currentUserId);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                System.out.println("Found user in database by Keycloak ID: " + user.getUsername());
                System.out.println("User Keycloak ID: " + user.getKeycloakId());
                UserResponseDto response = convertToResponseDtoWithRoles(user);
                System.out.println("User roles assigned: " + response.getRoles());
                return Optional.of(response);
            } else {
                System.out.println("No user found in database for Keycloak ID: " + currentUserId);
                System.out.println("Available users in database:");
                userRepository.findAll().forEach(u ->
                        System.out.println("  - " + u.getUsername() + " (Keycloak ID: " + u.getKeycloakId() + ")")
                );
            }
        } else {
            System.out.println("No JWT user ID or username found");
        }
        return Optional.empty();
    }

    @Override
    public Optional<UserResponseDto> getFirstUserForDev() {
        System.out.println("=== UserService.getFirstUserForDev() ===");
        Optional<User> firstUser = userRepository.findAll().stream().findFirst();
        if (firstUser.isPresent()) {
            User user = firstUser.get();
            System.out.println("First user in database: " + user.getUsername());
            System.out.println("First user Keycloak ID: " + user.getKeycloakId());
            UserResponseDto response = convertToResponseDtoWithRoles(user);
            System.out.println("First user roles assigned: " + response.getRoles());
            return Optional.of(response);
        } else {
            System.out.println("No users found in database");
            return Optional.empty();
        }
    }

    @Override
    public Optional<UserResponseDto> createOrUpdateUserFromKeycloak(String keycloakId, String username, String email, String firstName, String lastName) {
        Optional<User> existingUser = userRepository.findByKeycloakId(keycloakId);

        if (existingUser.isPresent()) {
            // Update existing user
            User user = existingUser.get();
            user.setUsername(username);
            user.setEmail(email);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setIsActive(true);
            return Optional.of(convertToResponseDtoWithRoles(userRepository.save(user)));
        } else {
            // Create new user
            User newUser = new User(keycloakId, username, email, firstName, lastName);
            return Optional.of(convertToResponseDtoWithRoles(userRepository.save(newUser)));
        }
    }

    // -------------------------------------------------------------------------
    // Internal helpers
    // -------------------------------------------------------------------------
    private UserResponseDto convertToResponseDtoWithRoles(User user) {
        List<String> roles;

        // For demo purposes, assign roles based on username
        if ("admin".equals(user.getUsername())) {
            roles = List.of("superAdmin");
        } else {
            roles = List.of("user");
        }

        System.out.println("User: " + user.getUsername() + " assigned roles: " + roles);

        // Get user statistics
        UserStats stats = getUserStats(user.getId());

        return new UserResponseDto(
                user.getId(),
                user.getKeycloakId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getIsActive(),
                user.getIsActive(), // enabled = isActive
                System.currentTimeMillis(), // createdTimestamp
                roles,
                stats.getPostCount(),
                stats.getCommentCount(),
                stats.getReactionCount()
        );
    }
} 