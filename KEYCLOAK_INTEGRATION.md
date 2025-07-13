# Keycloak Integration Guide

## Overview

This social media application uses Keycloak for enterprise-grade authentication and authorization, while keeping application-specific data in the backend database.

## Architecture

### Keycloak Handles:
- **User Authentication**: Login, logout, password management
- **User Management**: Create, delete, update users, roles, permissions
- **Role Management**: superAdmin, user roles
- **Session Management**: JWT tokens, refresh tokens
- **Security**: Password policies, account lockout, MFA

### Backend Handles:
- **Application Data**: Posts, comments, reactions, profiles
- **User Linking**: Connect Keycloak users to app data
- **Business Logic**: Social media features, data relationships

## User Management

### For Administrators:
Use **Keycloak Admin Console** at `http://localhost:8081`

**Access:**
- Username: `admin`
- Password: `admin`

**Features:**
- Create/delete users
- Assign/remove roles
- Enable/disable accounts
- Reset passwords
- View user sessions
- Manage user groups

### For Developers:
The backend provides these endpoints for linking Keycloak users to app data:

```bash
# Get current user info
GET /api/users/me

# Get user by Keycloak ID
GET /api/users/keycloak/{keycloakId}

# Sync user from Keycloak
POST /api/users/sync?keycloakId=...&username=...&email=...

# Get user by app ID
GET /api/users/{id}
```

## Removed Components

The following components were removed to avoid duplication with Keycloak:

### Controllers:
- `UserManagementController` - Duplicated Keycloak user management

### Services:
- `UserManagementService` - Duplicated Keycloak user management
- `UserManagementServiceImpl` - Duplicated Keycloak user management

### DTOs:
- `UserDto` - No longer needed for user management

## Kept Components

### Controllers:
- `UserController` - Links Keycloak users to app data
- `ProfileController` - Manages user profiles
- `PostController` - Manages social media posts
- `CommentController` - Manages post comments
- `ReactionController` - Manages post reactions

### Services:
- `UserService` - Handles app-specific user data
- `ProfileService` - Manages user profiles
- `PostService` - Manages posts
- `CommentService` - Manages comments
- `ReactionService` - Manages reactions

### Entities:
- `User` - Links to Keycloak users via `keycloakId`
- `Profile` - User profile data
- `Post` - Social media posts
- `Comment` - Post comments
- `Reaction` - Post reactions

## Best Practices

1. **User Creation**: Always create users in Keycloak first, then sync to app database
2. **Role Management**: Use Keycloak Admin Console for role assignments
3. **Authentication**: Always use Keycloak JWT tokens
4. **Authorization**: Use `@PreAuthorize` annotations with Keycloak roles
5. **Data Separation**: Keep authentication in Keycloak, business data in app database

## Migration Guide

If you were using the old user management endpoints:

### Before (Removed):
```bash
# Create user
POST /api/admin/users
# Delete user  
DELETE /api/admin/users/{userId}
# Update roles
PUT /api/admin/users/{userId}/roles
```

### After (Use Keycloak Admin Console):
1. Go to `http://localhost:8081`
2. Login as admin
3. Navigate to Users section
4. Use Keycloak's user management interface

## Security Benefits

- **Centralized Authentication**: Single source of truth for user management
- **Enterprise Features**: Password policies, MFA, account lockout
- **Scalability**: Keycloak handles authentication load
- **Compliance**: Enterprise-grade security features
- **Integration**: Easy integration with LDAP, Active Directory, etc.

## Development Workflow

1. **Create Users**: Use Keycloak Admin Console
2. **Test Authentication**: Use Keycloak login page
3. **Sync to App**: Call `/api/users/sync` when user first logs in
4. **Manage App Data**: Use backend APIs for posts, comments, etc.
5. **Monitor**: Use Keycloak Admin Console for user management

This approach gives you the best of both worlds: enterprise-grade authentication with custom social media features! 