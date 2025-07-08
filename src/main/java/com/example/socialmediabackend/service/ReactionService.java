package com.example.socialmediabackend.service;

import com.example.socialmediabackend.entity.Reaction;
import com.example.socialmediabackend.repository.ReactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReactionService {
    private final ReactionRepository reactionRepository;


}