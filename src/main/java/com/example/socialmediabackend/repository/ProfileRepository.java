package com.example.socialmediabackend.repository;

import com.example.socialmediabackend.entity.Profile;
import com.example.socialmediabackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Optional<Profile> findByUserId(Long userId);

    Optional<Profile> findByUsername(String username);

    Profile findByUser(User user);
}