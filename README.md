# Social Media Backend

[![forthebadge](http://forthebadge.com/images/badges/made-with-java.svg)](http://forthebadge.com)
[![forthebadge](http://forthebadge.com/images/badges/built-with-love.svg)](http://forthebadge.com)

A modern, modular, and secure Spring Boot backend for a social media application, designed with SOLID principles, extensibility, and production-readiness in mind.

---

## Table of Contents

- [Features](#features)
- [Architecture](#architecture)
- [Security](#security)
- [API Endpoints](#api-endpoints)
- [API Documentation (Swagger)](#api-documentation-swagger)
- [Installation](#installation)
- [Extensibility & Best Practices](#extensibility--best-practices)
- [Frontend Integration](#frontend-integration)

---

## Features

- User CRUD (create, read, update, delete)
- Post CRUD (with user association)
- Comment CRUD (with post and user association)
- Reaction CRUD (like/dislike, unique per user/post)
- Profile CRUD (strict 1-to-1 with user, profile created/retrieved via userId)
- PostgreSQL database integration (JPA/Hibernate, no manual SQL required)
- DTOs for all API input/output (no entity exposure)
- Keycloak-based security (OAuth2/OIDC, JWT, roles)
- Modular, extensible, and SOLID-compliant codebase
- Swagger/OpenAPI for API documentation and testing

---

## Architecture

- **Java 21 / Spring Boot 3.5.3 / Maven**
- **SOLID Principles**: All business logic is split into interfaces and implementations for maximum modularity and testability.
- **Package Structure**:
  - `service/` — interfaces (e.g., `PostService`)
  - `service/impl/` — implementations (e.g., `PostServiceImpl`)
  - `controller/` — REST API endpoints
  - `dto/` — Data Transfer Objects (API contracts)
  - `entity/` — JPA entities
  - `repository/` — Spring Data JPA repositories
  - `security/` — Security configuration (Keycloak, JWT)
  - `util/` — Utility classes (e.g., `JwtUtil`)
- **Database**: PostgreSQL, with JPA/Hibernate for ORM. No manual table creation needed.
- **Extensible**: Ready for multi-database support and custom SQL (iBatis/MyBatis) if needed.

---

## Security

- **Keycloak** for authentication and authorization (OAuth2/OIDC, JWT)
- **JWT validation** via Spring Security Resource Server, using Keycloak's JWKS endpoint
- **Role-based access control**: `superAdmin`, `user` (realm and client roles)
- **Stateless**: No server-side sessions, fully RESTful
- **Profile-based config**: Separate security configs for development (`libertalk`) and production
- **Utility**: `JwtUtil` for extracting user info and roles from JWT

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

## API Documentation (Swagger)

- **Swagger UI** is available at: `http://localhost:8084/swagger-ui-custom.html`
- **OpenAPI spec**: `/v3/api-docs`
- Test and explore all endpoints interactively.

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
4. (Optional) Configure Keycloak as described in the Keycloak section below.

---

## Frontend Integration

- **Frontend**: Next.js 15.3.5, Node 22.17.0, Tailwind CSS
- **Authentication**: Integrated with Keycloak (PKCE, silent SSO, role sync)
- **UI**: Responsive, modern, with modals and animations (Material UI, Flowbite, etc.)
- **API consumption**: All endpoints documented and ready for mobile/web clients

---

## Extensibility & Best Practices

- **SOLID**: Interfaces for all services, easy to extend or swap implementations
- **Open/Closed**: Add new features by creating new modules/services without breaking existing code
- **Multi-DB ready**: Architecture allows for multiple database connections if needed
- **Custom SQL**: iBatis/MyBatis can be added for advanced queries
- **Testing**: Unit and integration tests recommended for all services and controllers
- **Logging**: Comprehensive logging for debugging and audit
- **Null safety**: Systematic null checks to prevent NPEs
- **Performance**: Efficient use of JPA relationships, lazy loading, and stateless JWT auth

---

## Usage


---




