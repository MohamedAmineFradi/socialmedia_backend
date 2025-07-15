package com.example.socialmediabackend.service.impl;

import com.example.socialmediabackend.dto.ProfileDto;
import com.example.socialmediabackend.dto.ProfileResponseDto;
import com.example.socialmediabackend.entity.Profile;
import com.example.socialmediabackend.entity.User;
import com.example.socialmediabackend.repository.ProfileRepository;
import com.example.socialmediabackend.repository.UserRepository;
import com.example.socialmediabackend.service.ProfileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class ProfileServiceImpl implements ProfileService {
    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;

    public ProfileServiceImpl(ProfileRepository profileRepository, UserRepository userRepository) {
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
    }

    private ProfileResponseDto toProfileResponseDto(Profile profile) {
        Long userId = profile.getUser() != null ? profile.getUser().getId() : null;

        // Get user statistics if user exists
        Integer postCount = 0;
        Integer commentCount = 0;
        Integer reactionCount = 0;

        if (profile.getUser() != null) {
            User user = profile.getUser();
            postCount = user.getPosts() != null ? user.getPosts().size() : 0;
            commentCount = user.getComments() != null ? user.getComments().size() : 0;
            reactionCount = user.getReactions() != null ? user.getReactions().size() : 0;
        }

        ProfileResponseDto dto = new ProfileResponseDto(
                profile.getId(),
                profile.getName(),
                profile.getUsername(),
                profile.getBio(),
                profile.getLocation(),
                profile.getWebsite(),
                profile.getBirthday(),
                profile.getAvatar(),
                profile.getInfo(),
                userId,
                postCount,
                commentCount,
                reactionCount
        );
        log.info("Created ProfileResponseDto for user {}: avatar={}, stats: posts={}, comments={}, reactions={}",
                userId, profile.getAvatar(), postCount, commentCount, reactionCount);
        return dto;
    }

    @Override
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

    @Override
    public boolean deleteProfile(Long profileId) {
        return profileRepository.findById(profileId).map(profile -> {
            profileRepository.delete(profile);
            return true;
        }).orElse(false);
    }

    @Override
    public Optional<ProfileResponseDto> getProfileResponseByUserId(Long userId) {
        log.info("Getting profile for user ID: {}", userId);
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            log.info("Found user: {} {}", user.getFirstName(), user.getLastName());
            if (user.getProfile() != null) {
                log.info("User has profile with avatar: {}", user.getProfile().getAvatar());
                return Optional.of(toProfileResponseDto(user.getProfile()));
            } else {
                log.warn("User {} has no profile", userId);
            }
        } else {
            log.warn("User not found with ID: {}", userId);
        }
        return Optional.empty();
    }

    @Override
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

    @Override
    public Optional<ProfileResponseDto> findProfileByUserId(Long userId) {
        log.info("Finding profile by user ID: {}", userId);
        Optional<Profile> profileOpt = profileRepository.findByUserId(userId);
        if (profileOpt.isPresent()) {
            Profile profile = profileOpt.get();
            log.info("Found profile with avatar: {}", profile.getAvatar());
            return Optional.of(toProfileResponseDto(profile));
        } else {
            log.warn("No profile found for user ID: {}", userId);
            return Optional.empty();
        }
    }
} 