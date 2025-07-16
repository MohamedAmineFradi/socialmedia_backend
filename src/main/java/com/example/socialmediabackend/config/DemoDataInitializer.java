package com.example.socialmediabackend.config;

import com.example.socialmediabackend.entity.*;
import com.example.socialmediabackend.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;

@Configuration
@Slf4j
public class DemoDataInitializer {

    @Bean
    CommandLineRunner initDemoData(UserRepository userRepository, ProfileRepository profileRepository, PostRepository postRepository, CommentRepository commentRepository, ReactionRepository reactionRepository) {
        return args -> {
            try {
                // Only create demo data if no users, profiles, posts, comments, or reactions exist
                boolean usersEmpty = userRepository.count() == 0;
                boolean profilesEmpty = profileRepository.count() == 0;
                boolean postsEmpty = postRepository.count() == 0;
                boolean commentsEmpty = commentRepository.count() == 0;
                boolean reactionsEmpty = reactionRepository.count() == 0;
                if (usersEmpty && profilesEmpty && postsEmpty && commentsEmpty && reactionsEmpty) {
                    log.info("Initializing demo data...");
                    
                    // Create database users with real Keycloak IDs
                    User superAdminUser = createUser("fd45307e-4888-4d03-a920-ea984e18cb8b", "admin", "admin@socialmedia.com", "Super", "Admin", userRepository);
                    User user1 = createUser("377b77d5-da05-480c-96c7-3fc9c55028cc", "aminfradi", "user1@example.com", "Amin", "Fradi", userRepository);
                    User user2 = createUser("f19891df-fffd-4b77-85c2-07fdb30f4fc4", "johndoe", "user2@example.com", "John", "Doe", userRepository);
                    User user3 = createUser("d2152ec9-d13b-402b-bc7c-fe26dde2df2c", "janesmith", "user3@example.com", "Jane", "Smith", userRepository);
                    
                    // Create profiles linked to users
                    createDemoProfiles(profileRepository, superAdminUser, user1, user2, user3);
                    createDemoPosts(postRepository, user1, user2, user3);
                    createDemoComments(commentRepository, postRepository, user1, user2);
                    createDemoReactions(reactionRepository, postRepository, user1, user2);
                    
                    log.info("Demo data initialization completed successfully!");
                } else {
                    log.info("Demo data already exists, skipping initialization.");
                }
            } catch (Exception e) {
                log.error("Error initializing demo data: {}", e.getMessage(), e);
            }
        };
    }

    private User createUser(String keycloakId, String username, String email, String firstName, String lastName, UserRepository userRepository) {
        User user = new User(keycloakId, username, email, firstName, lastName);
        return userRepository.save(user);
    }

    private void createDemoProfiles(ProfileRepository profileRepository, User superAdminUser, User user1, User user2, User user3) {
        // Profile for superAdmin (Keycloak user)
        Profile superAdminProfile = new Profile();
        superAdminProfile.setName("Super Admin");
        superAdminProfile.setUsername("@superAdmin");
        superAdminProfile.setBio("System Administrator | Platform Manager");
        superAdminProfile.setLocation("System");
        superAdminProfile.setWebsite("admin.socialmedia.com");
        superAdminProfile.setBirthday("January 1st 1990");
        superAdminProfile.setAvatar("https://imgs.search.brave.com/yJsR0jQiY21ji0g8M73XX4I0C8B5XdB1Ag3psadzeog/rs:fit:500:0:1:0/g:ce/aHR0cHM6Ly91cGxv/YWQud2lraW1lZGlh/Lm9yZy93aWtpcGVk/aWEvZW4vdGh1bWIv/Zi9mNi9GcmllbmRz/cGhvZWJlLmpwZy8y/NTBweC1GcmllbmRz/cGhvZWJlLmpwZw");
        superAdminProfile.setInfo("Platform administrator with full system access");
        superAdminProfile.setUser(superAdminUser);
        profileRepository.save(superAdminProfile);

        // Profile for user1 (Keycloak user)
        Profile user1Profile = new Profile();
        user1Profile.setName("Amin Fradi");
        user1Profile.setUsername("@aminfradi");
        user1Profile.setBio("Software Engineer | React & Spring Boot Developer | Building social media experiences");
        user1Profile.setLocation("Tunisia");
        user1Profile.setWebsite("github.com/mohamedaminefradi");
        user1Profile.setBirthday("August 6th 2002");
        user1Profile.setAvatar("https://avatars.githubusercontent.com/u/213040804?v=4");
        user1Profile.setInfo("Passionate developer creating innovative web solutions");
        user1Profile.setUser(user1);
        profileRepository.save(user1Profile);

        // Profile for user2 (Keycloak user)
        Profile user2Profile = new Profile();
        user2Profile.setName("John Doe");
        user2Profile.setUsername("@johndoe");
        user2Profile.setBio("Full Stack Developer | Java & React Enthusiast");
        user2Profile.setLocation("USA");
        user2Profile.setWebsite("johndoe.dev");
        user2Profile.setBirthday("January 1st 1990");
        user2Profile.setAvatar("https://randomuser.me/api/portraits/men/1.jpg");
        user2Profile.setInfo("Experienced developer with expertise in modern web technologies");
        user2Profile.setUser(user2);
        profileRepository.save(user2Profile);

        // Profile for user3 (Keycloak user)
        Profile user3Profile = new Profile();
        user3Profile.setName("Jane Smith");
        user3Profile.setUsername("@janesmith");
        user3Profile.setBio("UI/UX Designer | Creative Developer | Design Systems Expert");
        user3Profile.setLocation("Canada");
        user3Profile.setWebsite("janesmith.design");
        user3Profile.setBirthday("March 15th 1995");
        user3Profile.setAvatar("https://randomuser.me/api/portraits/women/1.jpg");
        user3Profile.setInfo("Creative professional focused on user experience and design");
        user3Profile.setUser(user3);
        profileRepository.save(user3Profile);
    }

    private void createDemoPosts(PostRepository postRepository, User user1, User user2, User user3) {
        Post post1 = new Post();
        post1.setContent("Hello everyone! Excited to be part of this amazing social media platform. Looking forward to connecting with fellow developers and tech enthusiasts! 🚀");
        post1.setCreatedAt(Instant.now().minusSeconds(3600)); // 1 hour ago
        post1.setAuthor(user1);
        postRepository.save(post1);

        Post post2 = new Post();
        post2.setContent("Just finished building a new feature using React and Spring Boot. The integration is working perfectly! #FullStack #React #SpringBoot");
        post2.setCreatedAt(Instant.now().minusSeconds(1800)); // 30 minutes ago
        post2.setAuthor(user2);
        postRepository.save(post2);

        Post post3 = new Post();
        post3.setContent("Designing user interfaces is such a rewarding experience. Every pixel matters when creating intuitive user experiences! #UI #UX #Design");
        post3.setCreatedAt(Instant.now().minusSeconds(900)); // 15 minutes ago
        post3.setAuthor(user3);
        postRepository.save(post3);

        Post post4 = new Post();
        post4.setContent("Working on some exciting new features for our platform. Can't wait to share them with you all! Stay tuned for updates! 💻");
        post4.setCreatedAt(Instant.now().minusSeconds(300)); // 5 minutes ago
        post4.setAuthor(user1);
        postRepository.save(post4);
    }

    private void createDemoComments(CommentRepository commentRepository, PostRepository postRepository, User user1, User user2) {
        // Get the first two posts for comments
        var posts = postRepository.findAll();
        if (posts.size() >= 2) {
            Post post1 = posts.get(0);
            Post post2 = posts.get(1);

            Comment comment1 = new Comment();
            comment1.setContent("Welcome to the platform! Looking forward to seeing your contributions! 👋");
            comment1.setCreatedAt(Instant.now().minusSeconds(3500));
            comment1.setPost(post1);
            comment1.setUser(user2);
            commentRepository.save(comment1);

            Comment comment2 = new Comment();
            comment2.setContent("That's awesome! React and Spring Boot make such a powerful combination.");
            comment2.setCreatedAt(Instant.now().minusSeconds(1700));
            comment2.setPost(post2);
            comment2.setUser(user1);
            commentRepository.save(comment2);

            Comment comment3 = new Comment();
            comment3.setContent("Design is indeed crucial! Great to see someone who understands the importance of UX.");
            comment3.setCreatedAt(Instant.now().minusSeconds(800));
            comment3.setPost(post1);
            comment3.setUser(user1);
            commentRepository.save(comment3);

            Comment comment4 = new Comment();
            comment4.setContent("Can't wait to see what you're building! Always excited about new features.");
            comment4.setCreatedAt(Instant.now().minusSeconds(200));
            comment4.setPost(post2);
            comment4.setUser(user2);
            commentRepository.save(comment4);
        }
    }

    private void createDemoReactions(ReactionRepository reactionRepository, PostRepository postRepository, User user1, User user2) {
        // Get the first two posts for reactions
        var posts = postRepository.findAll();
        if (posts.size() >= 2) {
            Post post1 = posts.get(0);
            Post post2 = posts.get(1);

            Reaction reaction1 = new Reaction();
            reaction1.setType(ReactionType.LIKE);
            reaction1.setCreatedAt(Instant.now().minusSeconds(3400));
            reaction1.setPost(post1);
            reaction1.setUser(user2);
            reactionRepository.save(reaction1);

            Reaction reaction2 = new Reaction();
            reaction2.setType(ReactionType.LIKE);
            reaction2.setCreatedAt(Instant.now().minusSeconds(1600));
            reaction2.setPost(post2);
            reaction2.setUser(user1);
            reactionRepository.save(reaction2);

            Reaction reaction3 = new Reaction();
            reaction3.setType(ReactionType.DISLIKE);
            reaction3.setCreatedAt(Instant.now().minusSeconds(700));
            reaction3.setPost(post1);
            reaction3.setUser(user1);
            reactionRepository.save(reaction3);

            Reaction reaction4 = new Reaction();
            reaction4.setType(ReactionType.LIKE);
            reaction4.setCreatedAt(Instant.now().minusSeconds(100));
            reaction4.setPost(post2);
            reaction4.setUser(user2);
            reactionRepository.save(reaction4);
        }
    }
}