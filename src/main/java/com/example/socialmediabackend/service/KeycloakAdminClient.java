package com.example.socialmediabackend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakAdminClient {

    @Value("${keycloak.admin.server-url}")
    private String serverUrl;

    @Value("${keycloak.admin.realm}")
    private String realm;

    @Value("${keycloak.admin.client-id}")
    private String clientId;

    @Value("${keycloak.admin.client-secret}")
    private String clientSecret;

    private Keycloak getInstance() {
        return KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .build();
    }

    public void deleteUser(String keycloakId) {
        try {
            getInstance().realm(realm).users().get(keycloakId).remove();
            log.info("[KC-ADMIN] Deleted user {} in realm {}", keycloakId, realm);
        } catch (Exception e) {
            log.error("[KC-ADMIN] Failed to delete user {}: {}", keycloakId, e.getMessage());
        }
    }

    public void updateEmail(String keycloakId, String newEmail) {
        try {
            var usersResource = getInstance().realm(realm).users();
            UserRepresentation rep = usersResource.get(keycloakId).toRepresentation();
            rep.setEmail(newEmail);
            usersResource.get(keycloakId).update(rep);
            log.info("[KC-ADMIN] Updated email for user {}", keycloakId);
        } catch (Exception e) {
            log.error("[KC-ADMIN] Failed to update email for user {}: {}", keycloakId, e.getMessage());
        }
    }

    private void updateUser(String keycloakId, Consumer<UserRepresentation> mutator) {
        try {
            var users = getInstance().realm(realm).users();
            UserRepresentation rep = users.get(keycloakId).toRepresentation();
            mutator.accept(rep);
            users.get(keycloakId).update(rep);
            log.info("[KC-ADMIN] Updated user {}", keycloakId);
        } catch (Exception e) {
            log.error("[KC-ADMIN] Failed to update user {}: {}", keycloakId, e.getMessage());
        }
    }

    public void updateUsername(String keycloakId, String newUsername) {
        updateUser(keycloakId, rep -> rep.setUsername(newUsername));
    }

    public void updateFirstName(String keycloakId, String firstName) {
        updateUser(keycloakId, rep -> rep.setFirstName(firstName));
    }

    public void updateLastName(String keycloakId, String lastName) {
        updateUser(keycloakId, rep -> rep.setLastName(lastName));
    }

    public void setUserEnabled(String keycloakId, boolean enabled) {
        updateUser(keycloakId, rep -> rep.setEnabled(enabled));
    }
} 