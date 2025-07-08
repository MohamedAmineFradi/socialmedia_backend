package com.example.socialmediabackend.service;

import com.example.socialmediabackend.dto.UserDto;
import com.example.socialmediabackend.entity.Profile;
import com.example.socialmediabackend.entity.User;
import com.example.socialmediabackend.repository.ProfileRepository;
import com.example.socialmediabackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;

    public User createUser(UserDto userDto) {
        User user = new User();
        user.setEmail(userDto.getEmail());
        if (userDto.getProfileId() != null) {
            profileRepository.findById(userDto.getProfileId()).ifPresent(user::setProfile);
        }
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
    }

    public Optional<User> updateUser(Long userId, UserDto userDto) {
        return userRepository.findById(userId).map(user -> {
            user.setEmail(userDto.getEmail());
            if (userDto.getProfileId() != null) {
                profileRepository.findById(userDto.getProfileId()).ifPresent(user::setProfile);
            }
            return userRepository.save(user);
        });
    }

    public boolean deleteUser(Long userId) {
        return userRepository.findById(userId).map(user -> {
            userRepository.delete(user);
            return true;
        }).orElse(false);
    }
}
