# Social Media Backend

This is a Spring Boot backend for a social media application. It provides RESTful APIs for managing users, posts, comments, reactions, notifications, and messaging.

## Features
- User CRUD (create, read, update, delete)
- Post CRUD (with user association)
- Comment, Reaction, Notification, and Messaging entities
- PostgreSQL database integration
- Basic security setup (Keycloak integration planned)

## Technologies Used
- Java 17+
- Spring Boot
- Spring Data JPA
- PostgreSQL
- Maven

## Getting Started

### Prerequisites
- Java 17 or higher
- Maven
- PostgreSQL

### Setup
1. Clone the repository:
   ```bash
   git clone <your-repo-url>
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

## API Endpoints

### User
- `POST /api/users` — Create user
- `GET /api/users` — List users
- `GET /api/users/{userId}` — Get user by ID
- `PUT /api/users/{userId}` — Update user
- `DELETE /api/users/{userId}` — Delete user

### Post
- `POST /api/posts/user/{userId}` — Create post for user
- `GET /api/posts` — List all posts
- `GET /api/posts/user/{userId}` — List posts by user
- `GET /api/posts/{postId}` — Get post by ID
- `PUT /api/posts/{postId}/user/{userId}` — Update post
- `DELETE /api/posts/{postId}/user/{userId}` — Delete post

