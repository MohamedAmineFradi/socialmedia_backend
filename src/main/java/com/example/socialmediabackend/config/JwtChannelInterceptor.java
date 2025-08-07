package com.example.socialmediabackend.config;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.util.Collection;

@Slf4j
@RequiredArgsConstructor
public class JwtChannelInterceptor implements ChannelInterceptor {

    private final JwtDecoder jwtDecoder;

    @Override
    public Message<?> preSend(@Nonnull Message<?> message, @Nonnull MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        log.info("JWT Interceptor: Processing WebSocket message: command={}, destination={}",
                accessor.getCommand(), accessor.getDestination());

        // Log all headers for debugging
        if (accessor.getCommand() != null) {
            log.debug("JWT Interceptor: Message headers: {}", accessor.toNativeHeaderMap());
        }

        // Only require authentication for specific commands that need it
        if (StompCommand.SEND.equals(accessor.getCommand()) ||
                StompCommand.CONNECT.equals(accessor.getCommand())) {

            log.info("JWT Interceptor: Processing authentication for command: {}", accessor.getCommand());
            String rawToken = accessor.getFirstNativeHeader("Authorization");
            log.debug("JWT Interceptor: Authorization header present for {} command: {}", accessor.getCommand(), rawToken != null);

            if (rawToken == null || !rawToken.startsWith("Bearer ")) {
                log.warn("JWT Interceptor: No valid Authorization header found for WebSocket message command: {}", accessor.getCommand());
                // Only throw exception for SEND commands, allow CONNECT to pass through
                if (StompCommand.SEND.equals(accessor.getCommand())) {
                    log.error("JWT Interceptor: Authentication required for SEND command: {}", accessor.getCommand());
                    throw new IllegalArgumentException("Authentication required for sending messages");
                }
                return message;
            }
            rawToken = rawToken.substring(7);

            try {
                Jwt jwt = jwtDecoder.decode(rawToken);
                log.debug("JWT Interceptor: JWT decoded successfully for subject: {}", jwt.getSubject());

                // Convert scopes/roles to GrantedAuthority
                Collection<GrantedAuthority> authorities =
                        new JwtGrantedAuthoritiesConverter().convert(jwt);

                AbstractAuthenticationToken authentication =
                        new JwtAuthenticationToken(jwt, authorities);

                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.info("JWT Interceptor: Authentication set in SecurityContext for user: {}", jwt.getSubject());
            } catch (Exception ex) {
                log.warn("JWT Interceptor: JWT invalide sur le canal WebSocket : {}", ex.getMessage());
                if (StompCommand.SEND.equals(accessor.getCommand())) {
                    throw new IllegalArgumentException("Token JWT invalide !");
                }
            }
        } else {
            // For other commands (SUBSCRIBE, DISCONNECT, etc.), just log but don't require authentication
            log.debug("JWT Interceptor: Skipping authentication for command: {}", accessor.getCommand());
        }
        return message;
    }
}