
package com.example.socialmediabackend.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.context.annotation.Bean;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> {})
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/swagger-ui-custom.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/posts/**").permitAll()
                        .requestMatchers("/users/**").permitAll()
                        .requestMatchers("/comments/**").permitAll()
                        .requestMatchers("/reactions/**").permitAll()
                        .requestMatchers("/profiles/**").permitAll()//.hasRole("USER")
                        .requestMatchers("/admin/**").permitAll()// .hasRole("ADMIN")
                        .anyRequest().permitAll()
                        //.anyRequest().authenticated()
                )
                .csrf(csrf -> csrf.disable());

        return http.build();
    }
}