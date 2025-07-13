package com.example.socialmediabackend.controller;

import com.example.socialmediabackend.dto.UserResponseDto;
import com.example.socialmediabackend.service.UserService;
import com.example.socialmediabackend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import jakarta.servlet.http.HttpServletRequest;

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

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @GetMapping
    // @PreAuthorize("hasRole('superAdmin')")
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        List<UserResponseDto> users = userService.getAllUsersWithRoles();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/me")
    // @PreAuthorize("hasAnyRole('user', 'superAdmin')")
    public ResponseEntity<UserResponseDto> getCurrentUser(HttpServletRequest request) {
        System.out.println("=== /users/me endpoint called ===");
        System.out.println("JWT Current User ID: " + jwtUtil.getCurrentUserId());
        System.out.println("JWT Current Username: " + jwtUtil.getCurrentUsername());
        System.out.println("JWT Current Roles: " + jwtUtil.getCurrentUserRoles());
        System.out.println("JWT Is SuperAdmin: " + jwtUtil.isSuperAdmin());
        
        // For libertalk profile, try to find user by username from frontend
        if (jwtUtil.getCurrentUserId() == null && jwtUtil.getCurrentUsername() == null) {
            System.out.println("No JWT authentication found - using libertalk profile");
            
            // Try to get username from request headers (frontend might send it)
            String frontendUsername = request.getHeader("X-Frontend-User");
            if (frontendUsername != null) {
                System.out.println("Frontend username from header: " + frontendUsername);
                Optional<UserResponseDto> userByUsername = userService.getUserByUsername(frontendUsername);
                if (userByUsername.isPresent()) {
                    System.out.println("Found user by frontend username: " + userByUsername.get().getUsername());
                    return ResponseEntity.ok(userByUsername.get());
                }
            }
            
            // Fallback: return first user but log a warning
            System.out.println("No frontend username found, returning first user (this may cause privilege issues)");
            Optional<UserResponseDto> firstUser = userService.getFirstUserForDev();
            if (firstUser.isPresent()) {
                System.out.println("Returning first user: " + firstUser.get().getUsername());
                return ResponseEntity.ok(firstUser.get());
            } else {
                System.out.println("No users found in database");
                return ResponseEntity.notFound().build();
            }
        }
        
        Optional<UserResponseDto> currentUser = userService.getCurrentUser();
        
        if (currentUser.isPresent()) {
            System.out.println("Found user in database: " + currentUser.get().getUsername());
            System.out.println("User roles: " + currentUser.get().getRoles());
            return ResponseEntity.ok(currentUser.get());
        } else {
            System.out.println("No user found in database for current user");
            System.out.println("Attempting to create user from Keycloak data...");
            
            // Try to create user from Keycloak data
            String keycloakId = jwtUtil.getCurrentUserId();
            String username = jwtUtil.getCurrentUsername();
            String email = jwtUtil.getCurrentUserEmail();
            String firstName = jwtUtil.getCurrentUserFirstName();
            String lastName = jwtUtil.getCurrentUserLastName();
            
            if (keycloakId != null && username != null) {
                Optional<UserResponseDto> newUser = userService.createOrUpdateUserFromKeycloak(
                    keycloakId, username, email, firstName, lastName);
                
                if (newUser.isPresent()) {
                    System.out.println("Successfully created user: " + newUser.get().getUsername());
                    System.out.println("User roles: " + newUser.get().getRoles());
                    return ResponseEntity.ok(newUser.get());
                }
            }
            
            System.out.println("Failed to create user from Keycloak data");
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/keycloak/{keycloakId}")
    // @PreAuthorize("hasAnyRole('user', 'superAdmin')")
    public ResponseEntity<UserResponseDto> getUserByKeycloakId(@PathVariable String keycloakId) {
        Optional<UserResponseDto> user = userService.getUserByKeycloakId(keycloakId);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/sync")
    // @PreAuthorize("hasAnyRole('user', 'superAdmin')")
    public ResponseEntity<UserResponseDto> syncUserFromKeycloak(
            @RequestParam String keycloakId,
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String firstName,
            @RequestParam String lastName) {
        
        Optional<UserResponseDto> user = userService.createOrUpdateUserFromKeycloak(
                keycloakId, username, email, firstName, lastName);
        
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
    }

    @GetMapping("/{id}")
    // @PreAuthorize("hasAnyRole('user', 'superAdmin')")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        Optional<UserResponseDto> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}