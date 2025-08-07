package com.example.socialmediabackend.controller;

import com.example.socialmediabackend.dto.UserResponseDto;
import com.example.socialmediabackend.dto.UpdateUserDto;
import com.example.socialmediabackend.dto.UserBasicInfoDto;
import com.example.socialmediabackend.service.UserService;
import com.example.socialmediabackend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * UserController handles linking Keycloak users to application data.
 *
 * For user management (create, delete, update users, roles, etc.),
 * use Keycloak Admin Console: http://localhost:8081
 *
 * This controller focuses on:
 * - Getting current user info
 * - Syncing user data from Keycloak
 * - Linking Keycloak users to app profiles/posts/comments
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @GetMapping
    @PreAuthorize("hasAnyRole('user', 'superAdmin')")
    public ResponseEntity<List<UserBasicInfoDto>> getAllUsers() {
        log.info("[UserController] getAllUsers called");
        List<UserBasicInfoDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/super-admin")
    @PreAuthorize("hasRole('superAdmin')")
    public ResponseEntity<List<UserResponseDto>> getAllUsersWithRoles() {
        log.info("[UserController] getAllUsers called");
        List<UserResponseDto> users = userService.getAllUsersWithRoles();
        log.info("[UserController] Returning {} users", users.size());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('user', 'superAdmin')")
    public ResponseEntity<UserResponseDto> getCurrentUser() {
        log.info("[UserController] /users/me called");
        String keycloakId = jwtUtil.getCurrentUserId();
        String username = jwtUtil.getCurrentUsername();
        String email = jwtUtil.getCurrentUserEmail();
        String firstName = jwtUtil.getCurrentUserFirstName();
        String lastName = jwtUtil.getCurrentUserLastName();
        log.info("[UserController] JWT info: keycloakId={}, username={}, email={}, firstName={}, lastName={}", keycloakId, username, email, firstName, lastName);

        Optional<UserResponseDto> currentUser = userService.getCurrentUser();
        if (currentUser.isPresent()) {
            log.info("[UserController] Found user in DB: {} (id={})", currentUser.get().getUsername(), currentUser.get().getId());
            return ResponseEntity.ok(currentUser.get());
        } else {
            log.info("[UserController] User not found in DB, attempting to create from Keycloak data...");
            if (keycloakId != null && username != null) {
                Optional<UserResponseDto> newUser = userService.createOrUpdateUserFromKeycloak(
                        keycloakId, username, email, firstName, lastName);
                if (newUser.isPresent()) {
                    log.info("[UserController] Successfully created user: {} (id={})", newUser.get().getUsername(), newUser.get().getId());
                    return ResponseEntity.ok(newUser.get());
                } else {
                    log.error("[UserController] Failed to create user from Keycloak data");
                }
            } else {
                log.error("[UserController] Insufficient Keycloak data to create user (keycloakId or username is null)");
            }
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/keycloak/{keycloakId}")
    @PreAuthorize("hasAnyRole('user', 'superAdmin')")
    public ResponseEntity<UserResponseDto> getUserByKeycloakId(@PathVariable String keycloakId) {
        log.info("[UserController] getUserByKeycloakId called with keycloakId={}", keycloakId);
        Optional<UserResponseDto> user = userService.getUserByKeycloakId(keycloakId);
        if (user.isPresent()) {
            log.info("[UserController] Found user: {} (id={})", user.get().getUsername(), user.get().getId());
            return ResponseEntity.ok(user.get());
        } else {
            log.warn("[UserController] No user found for keycloakId={}", keycloakId);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/sync")
    @PreAuthorize("hasAnyRole('user', 'superAdmin')")
    public ResponseEntity<UserResponseDto> syncUserFromKeycloak(
            @RequestParam String keycloakId,
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String firstName,
            @RequestParam String lastName) {
        log.info("[UserController] syncUserFromKeycloak called with keycloakId={}, username={}", keycloakId, username);
        Optional<UserResponseDto> user = userService.createOrUpdateUserFromKeycloak(
                keycloakId, username, email, firstName, lastName);
        if (user.isPresent()) {
            log.info("[UserController] Synced user: {} (id={})", user.get().getUsername(), user.get().getId());
            return ResponseEntity.ok(user.get());
        } else {
            log.error("[UserController] Failed to sync user from Keycloak data");
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('user', 'superAdmin')")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        log.info("[UserController] getUserById called with id={}", id);
        Optional<UserResponseDto> user = userService.getUserById(id);
        if (user.isPresent()) {
            log.info("[UserController] Found user: {} (id={})", user.get().getUsername(), user.get().getId());
            return ResponseEntity.ok(user.get());
        } else {
            log.warn("[UserController] No user found for id={}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('user', 'superAdmin')")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable Long id, @RequestBody UpdateUserDto dto) {
        boolean isSuperAdmin = jwtUtil.isSuperAdmin();
        Long currentDbId = userService.getUserByKeycloakId(jwtUtil.getCurrentUserId())
                .map(UserResponseDto::getId).orElse(null);
        if (!isSuperAdmin && (currentDbId == null || !currentDbId.equals(id))) {
            return ResponseEntity.status(403).build();
        }
        return userService.updateUserFields(id, dto.username(), dto.email(), dto.firstName(), dto.lastName(), dto.enabled())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}