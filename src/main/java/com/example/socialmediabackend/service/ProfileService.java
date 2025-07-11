package com.example.socialmediabackend.service;

import com.example.socialmediabackend.dto.ProfileDto;
import com.example.socialmediabackend.dto.ProfileResponseDto;
import com.example.socialmediabackend.entity.Profile;
import com.example.socialmediabackend.entity.User;
import com.example.socialmediabackend.repository.ProfileRepository;
import com.example.socialmediabackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;

    @Autowired
    public ProfileService(ProfileRepository profileRepository, UserRepository userRepository) {
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
    }

    public ProfileResponseDto toProfileResponseDto(Profile profile) {
        Long userId = profile.getUser() != null ? profile.getUser().getId() : null;
        return new ProfileResponseDto(
            profile.getId(),
            profile.getName(),
            profile.getUsername(),
            profile.getBio(),
            profile.getLocation(),
            profile.getWebsite(),
            profile.getBirthday(),
            profile.getAvatar(),
            profile.getInfo(),
            userId
        );
    }

    public Optional<ProfileResponseDto> updateProfile(Long profileId, ProfileDto profileDto) {
        return profileRepository.findById(profileId).map(profile -> {
            profile.setName(profileDto.getName());
            profile.setUsername(profileDto.getUsername());
            profile.setBio(profileDto.getBio());
            profile.setLocation(profileDto.getLocation());
            profile.setWebsite(profileDto.getWebsite());
            profile.setBirthday(profileDto.getBirthday());
            profile.setAvatar(profileDto.getAvatar());
            profile.setInfo(profileDto.getInfo());
            return toProfileResponseDto(profileRepository.save(profile));
        });
    }

    public boolean deleteProfile(Long profileId) {
        return profileRepository.findById(profileId).map(profile -> {
            profileRepository.delete(profile);
            return true;
        }).orElse(false);
    }

    public Optional<ProfileResponseDto> getProfileResponseByUserId(Long userId) {
        return userRepository.findById(userId)
            .map(User::getProfile)
            .filter(profile -> profile != null)
            .map(this::toProfileResponseDto);
    }

    public Optional<ProfileResponseDto> createProfileForUser(Long userId, ProfileDto profileDto) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getProfile() != null) {
                // L'utilisateur a déjà un profil
                return Optional.empty();
            }
            Profile profile = new Profile();
            profile.setName(profileDto.getName());
            profile.setUsername(profileDto.getUsername());
            profile.setBio(profileDto.getBio());
            profile.setLocation(profileDto.getLocation());
            profile.setWebsite(profileDto.getWebsite());
            profile.setBirthday(profileDto.getBirthday());
            profile.setAvatar(profileDto.getAvatar());
            profile.setInfo(profileDto.getInfo());
            profile.setUser(user);
            user.setProfile(profile);
            profileRepository.save(profile);
            userRepository.save(user);
            return Optional.of(toProfileResponseDto(profile));
        }
        return Optional.empty();
    }

    public Optional<ProfileResponseDto> findProfileByUserId(Long userId) {
        return profileRepository.findByUserId(userId).map(this::toProfileResponseDto);
    }
} 