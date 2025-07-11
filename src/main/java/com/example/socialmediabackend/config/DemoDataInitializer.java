package com.example.socialmediabackend.config;

import com.example.socialmediabackend.entity.*;
import com.example.socialmediabackend.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;

@Configuration
public class DemoDataInitializer {

    @Bean
    CommandLineRunner initDemoData(UserRepository userRepository, ProfileRepository profileRepository, PostRepository postRepository, CommentRepository commentRepository, ReactionRepository reactionRepository) {
        return args -> {
            if (userRepository.count() == 0) {
                // User 1
                User user1 = new User();
                user1.setEmail("aminfradi@example.com");
                Profile profile1 = new Profile();
                profile1.setName("Amin fradi 2");
                profile1.setUsername("@aminfradi");
                profile1.setBio("Software Engineer | React & Spring Boot Developer | Building social media experiences");
                profile1.setLocation("Tunisia");
                profile1.setWebsite("github.com/mohamedaminefradi");
                profile1.setBirthday("August 6th 2002");
                profile1.setAvatar("https://avatars.githubusercontent.com/u/213040804?v=4");
                profile1.setInfo("");
                profile1.setUser(user1);
                user1.setProfile(profile1);

                // User 2
                User user2 = new User();
                user2.setEmail("johndoe@example.com");
                Profile profile2 = new Profile();
                profile2.setName("John Doe");
                profile2.setUsername("@johndoe");
                profile2.setBio("Full Stack Developer | Java & React Enthusiast");
                profile2.setLocation("USA");
                profile2.setWebsite("johndoe.dev");
                profile2.setBirthday("January 1st 1990");
                profile2.setAvatar("https://randomuser.me/api/portraits/men/1.jpg");
                profile2.setInfo("");
                profile2.setUser(user2);
                user2.setProfile(profile2);

                userRepository.save(user1);
                userRepository.save(user2);

                // Posts
                Post post1 = new Post();
                post1.setContent("Hello, this is my first post!");
                post1.setCreatedAt(Instant.now());
                post1.setAuthor(user1);
                postRepository.save(post1);

                Post post2 = new Post();
                post2.setContent("Excited to join this platform. Cheers!");
                post2.setCreatedAt(Instant.now());
                post2.setAuthor(user2);
                postRepository.save(post2);

                // Comments
                Comment comment1 = new Comment();
                comment1.setContent("Welcome to the platform!");
                comment1.setCreatedAt(Instant.now());
                comment1.setPost(post1);
                comment1.setUser(user2);
                commentRepository.save(comment1);

                Comment comment2 = new Comment();
                comment2.setContent("Thank you!");
                comment2.setCreatedAt(Instant.now());
                comment2.setPost(post1);
                comment2.setUser(user1);
                commentRepository.save(comment2);

                // Reactions
                Reaction reaction1 = new Reaction();
                reaction1.setType(ReactionType.LIKE);
                reaction1.setCreatedAt(Instant.now());
                reaction1.setPost(post1);
                reaction1.setUser(user2);
                reactionRepository.save(reaction1);

                Reaction reaction2 = new Reaction();
                reaction2.setType(ReactionType.LIKE);
                reaction2.setCreatedAt(Instant.now());
                reaction2.setPost(post2);
                reaction2.setUser(user1);
                reactionRepository.save(reaction2);
            }
        };
    }
}