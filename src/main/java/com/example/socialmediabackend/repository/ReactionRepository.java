package com.example.socialmediabackend.repository;

import com.example.socialmediabackend.entity.Reaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ReactionRepository extends JpaRepository<Reaction, Long> {
    Optional<Reaction> findByPostIdAndUserId(Long postId, Long userId);

    @Modifying
    @Query("DELETE FROM Reaction r WHERE r.post.id = :postId")
    void deleteByPostId(@Param("postId") Long postId);
}