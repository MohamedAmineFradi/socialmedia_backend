# Keycloak Setup Guide for Social Media Backend

## 🚀 **Quick Start**

### 1. **Start Keycloak**
```bash
# Using Docker
docker run -p 8081:8080 \
  -e KEYCLOAK_ADMIN=admin \
  -e KEYCLOAK_ADMIN_PASSWORD=admin \
  quay.io/keycloak/keycloak:latest \
  start-dev
```

### 2. **Access Keycloak Admin Console**
- **URL**: `http://localhost:8081`
- **Username**: `admin`
- **Password**: `admin`

## 📋 **Step-by-Step Configuration**

### Step 1: Create Realm

1. **Login to Admin Console**
   - Go to `http://localhost:8081`
   - Login with `admin` / `admin`

2. **Create New Realm**
   - Click dropdown in top-left corner
   - Select "Create Realm"
   - **Realm Name**: `libertalk`
   - Click "Create"

### Step 2: Create Client

1. **Navigate to Clients**
   - In left sidebar, click "Clients"
   - Click "Create"

2. **Configure Client**
   - **Client ID**: `social-media-backend`
   - **Client Protocol**: `openid-connect`
   - Click "Save"

3. **Configure Client Settings**
   - **Access Type**: `confidential`
   - **Valid Redirect URIs**: 
     - `http://localhost:3000/*`
     - `http://localhost:3001/*`
   - **Web Origins**: 
     - `http://localhost:3000`
     - `http://localhost:3001`
   - **Client authentication**: `ON`
   - Click "Save"

4. **Get Client Secret**
   - Go to "Credentials" tab
   - Copy the **Secret**: `Acsxk5rTwGWjgJbo0eLTx4AeIrYIWUEt`

### Step 3: Create Roles

1. **Navigate to Roles**
   - In left sidebar, click "Roles"
   - Click "Add Role"

2. **Create superAdmin Role**
   - **Role Name**: `superAdmin`
   - **Description**: `Super Administrator with full access`
   - Click "Save"

3. **Create user Role**
   - **Role Name**: `user`
   - **Description**: `Regular user with limited access`
   - Click "Save"

### Step 4: Create Users

#### Create Super Admin User

1. **Navigate to Users**
   - In left sidebar, click "Users"
   - Click "Add User"

2. **Configure User**
   - **Username**: `admin`
   - **Email**: `admin@socialmedia.com`
   - **First Name**: `Super`
   - **Last Name**: `Admin`
   - **Email Verified**: `ON`
   - Click "Save"

3. **Set Password**
   - Go to "Credentials" tab
   - **Password**: `admin123`
   - **Temporary**: `OFF`
   - Click "Set Password"

4. **Assign Roles**
   - Go to "Role Mappings" tab
   - In "Realm Roles" section, click "Assign role"
   - Select `superAdmin`
   - Click "Assign"

#### Create Regular Users

1. **Create User 1**
   - **Username**: `aminfradi`
   - **Email**: `user1@example.com`
   - **First Name**: `Amin`
   - **Last Name**: `Fradi`
   - **Password**: `user123`
   - **Role**: `user`

2. **Create User 2**
   - **Username**: `johndoe`
   - **Email**: `user2@example.com`
   - **First Name**: `John`
   - **Last Name**: `Doe`
   - **Password**: `user123`
   - **Role**: `user`

3. **Create User 3**
   - **Username**: `janesmith`
   - **Email**: `user3@example.com`
   - **First Name**: `Jane`
   - **Last Name**: `Smith`
   - **Password**: `user123`
   - **Role**: `user`

### Step 5: Configure Token Settings

1. **Navigate to Realm Settings**
   - In left sidebar, click "Realm Settings"

2. **Configure Tokens**
   - Go to "Tokens" tab
   - **Access Token Lifespan**: `15 Minutes`
   - **Refresh Token Lifespan**: `30 Days`
   - Click "Save"

### Step 6: Configure Client Scopes

1. **Navigate to Client Scopes**
   - In left sidebar, click "Client Scopes"

2. **Verify Default Scopes**
   - Ensure these scopes exist:
     - `openid`
     - `profile`
     - `email`
     - `roles`

### Step 7: Configure Client Scopes for Your Client

1. **Navigate to Your Client**
   - Go to "Clients" → `social-media-backend`

2. **Configure Client Scopes**
   - Go to "Client Scopes" tab
   - **Assigned Default Client Scopes**:
     - `openid`
     - `profile`
     - `email`
     - `roles`

## 🔧 **Backend Configuration Verification**

### Application Properties
Your `application.properties` should match:

```properties
# Keycloak Configuration
keycloak.realm=libertalk
keycloak.auth-server-url=http://localhost:8081
keycloak.ssl-required=external
keycloak.resource=social-media-backend
keycloak.credentials.secret=Acsxk5rTwGWjgJbo0eLTx4AeIrYIWUEt
keycloak.use-resource-role-mappings=true
keycloak.bearer-only=true

# OAuth2 Resource Server Configuration
spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:8081/realms/libertalk
spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://localhost:8081/realms/libertalk/protocol/openid_connect/certs

# Keycloak Admin Client Configuration
keycloak.admin.server-url=http://localhost:8081
keycloak.admin.realm=libertalk
keycloak.admin.client-id=social-media-backend
keycloak.admin.client-secret=Acsxk5rTwGWjgJbo0eLTx4AeIrYIWUEt
keycloak.admin.username=admin
keycloak.admin.password=admin
```

## 🧪 **Testing Configuration**

### 1. **Test Keycloak Login**
- Go to: `http://localhost:8081/realms/libertalk/protocol/openid-connect/auth?client_id=social-media-backend&response_type=code&scope=openid&redirect_uri=http://localhost:3000`
- Login with any user credentials

### 2. **Test Backend Authentication**
```bash
# Get token
curl -X POST http://localhost:8081/realms/libertalk/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=password" \
  -d "client_id=social-media-backend" \
  -d "client_secret=Acsxk5rTwGWjgJbo0eLTx4AeIrYIWUEt" \
  -d "username=admin" \
  -d "password=admin123"

# Use token to access backend
curl -X GET http://localhost:8084/api/users/me \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

### 3. **Test Frontend Integration**
- Start your React frontend
- Navigate to login page
- Should redirect to Keycloak login
- After login, should return to your app

## 🔍 **Troubleshooting**

### Common Issues:

1. **CORS Errors**
   - Ensure Web Origins are configured in Keycloak client
   - Check backend CORS configuration

2. **Invalid Token**
   - Verify client secret matches
   - Check token expiration settings

3. **Role Not Found**
   - Ensure roles are created in Keycloak
   - Verify role assignments to users

4. **Client Not Found**
   - Verify client ID matches
   - Check client protocol is `openid-connect`

### Debug Steps:

1. **Check Keycloak Logs**
   ```bash
   docker logs keycloak-container
   ```

2. **Check Backend Logs**
   ```bash
   # Look for JWT validation errors
   tail -f backend.log | grep -i jwt
   ```

3. **Verify Token Content**
   - Decode JWT at jwt.io
   - Check roles in `realm_access.roles`

## ✅ **Configuration Checklist**

- [ ] Keycloak server running on port 8081
- [ ] Realm `libertalk` created
- [ ] Client `social-media-backend` configured
- [ ] Roles `superAdmin` and `user` created
- [ ] Users created with proper roles
- [ ] Backend application.properties configured
- [ ] Frontend Keycloak configuration updated
- [ ] Test authentication flow working

## 🎯 **Security Best Practices**

1. **Use HTTPS in Production**
2. **Rotate Client Secrets Regularly**
3. **Set Appropriate Token Lifespans**
4. **Enable Audit Logging**
5. **Use Strong Passwords**
6. **Enable MFA for Admin Users**
7. **Regular Security Updates**

Your Keycloak setup is now ready for production use with your social media backend! 