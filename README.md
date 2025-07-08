# Social Media Backend

  [![forthebadge](http://forthebadge.com/images/badges/made-with-java.svg)](http://forthebadge.com)
  [![forthebadge](http://forthebadge.com/images/badges/built-with-love.svg)](http://forthebadge.com)


A modern Spring Boot backend for a social media application. 

---

## Table of Contents

- [Features](#features)
- [API Endpoints](#api-endpoints)
- [Installation](#installation)
- [Usage](#usage)
- [Profile Management](#profile-management)
- [Contributing](#contributing)
- [License](#license)

---

## Features

- User CRUD (create, read, update, delete)
- Post CRUD (with user association)
- Comment CRUD (with post and user association)
- Reaction CRUD (with post and user association, unique per user/post)
- Profile CRUD (strict 1-to-1 with user, profile created/retrieved via userId)
- PostgreSQL database integration
- DTOs for all API input/output (no entity exposure)
- Basic security setup (Keycloak integration planned)

---

## API Endpoints

### User
- `POST /api/users` — Create user
- `GET /api/users` — List users
- `GET /api/users/{userId}` — Get user by ID
- `PUT /api/users/{userId}` — Update user
- `DELETE /api/users/{userId}` — Delete user

### Profile (1-to-1 with User)
- `POST /api/profiles/user/{userId}` — Create profile for user (fails if user already has a profile)
- `GET /api/profiles/user/{userId}` — Get profile by userId
- `PUT /api/profiles/{profileId}` — Update profile
- `DELETE /api/profiles/{profileId}` — Delete profile

### Post
- `POST /api/posts/user/{userId}` — Create post for user
- `GET /api/posts` — List all posts
- `GET /api/posts/user/{userId}` — List posts by user
- `GET /api/posts/{postId}` — Get post by ID
- `PUT /api/posts/{postId}/user/{userId}` — Update post
- `DELETE /api/posts/{postId}/user/{userId}` — Delete post

### Comment
- `POST /api/comments/post/{postId}/user/{userId}` — Create comment for post/user
- `GET /api/comments/post/{postId}` — List comments for a post
- `GET /api/comments/{commentId}` — Get comment by ID
- `PUT /api/comments/{commentId}/user/{userId}` — Update comment
- `DELETE /api/comments/{commentId}/user/{userId}` — Delete comment

### Reaction
- `POST /api/reactions/post/{postId}/user/{userId}` — Create or update reaction for post/user (like/dislike, unique per user/post)
- `GET /api/reactions/post/{postId}` — List reactions for a post
- `GET /api/reactions/{reactionId}` — Get reaction by ID
- `DELETE /api/reactions/{reactionId}/user/{userId}` — Delete reaction

---

## Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/MohamedAmineFradi/socialmedia_backend.git
   cd socialMediaBackend
   ```
2. Configure your database in `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/social_media_db
   spring.datasource.username=postgres
   spring.datasource.password=your_password
   ```
3. Build and run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

---

## Usage


---




