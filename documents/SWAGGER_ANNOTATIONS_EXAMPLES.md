# 📝 Swagger Annotations - Example Code

This file shows how to properly document your API endpoints in Swagger so they appear correctly in the Swagger UI.

---

## Complete Example: User Profile Service Controller

```java
package com.ksb.micro.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/v1/users")
@Tag(name = "User Management", description = "APIs for managing users")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    // ========== GET ALL USERS ==========
    @GetMapping
    @Operation(
        summary = "Get all users",
        description = "Retrieve a list of all users in the system"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Successfully retrieved users",
        content = @Content(schema = @Schema(type = "array"))
    )
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    
    // ========== GET USER BY ID ==========
    @GetMapping("/{userId}")
    @Operation(
        summary = "Get user by ID",
        description = "Retrieve a specific user by their ID"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User found"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid token")
    })
    public ResponseEntity<User> getUserById(
        @Parameter(description = "User ID to retrieve", required = true)
        @PathVariable Long userId
    ) {
        User user = userService.getUserById(userId);
        if (user != null) {
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.notFound().build();
    }
    
    // ========== CREATE USER ==========
    @PostMapping
    @Operation(
        summary = "Create a new user",
        description = "Create a new user with the provided information"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "User created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid user data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<User> createUser(
        @Parameter(description = "User details", required = true)
        @RequestBody User user
    ) {
        User createdUser = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }
    
    // ========== UPDATE USER ==========
    @PutMapping("/{userId}")
    @Operation(
        summary = "Update an existing user",
        description = "Update user information by ID"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User updated successfully"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "400", description = "Invalid user data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<User> updateUser(
        @Parameter(description = "User ID to update", required = true)
        @PathVariable Long userId,
        @Parameter(description = "Updated user details", required = true)
        @RequestBody User user
    ) {
        User updatedUser = userService.updateUser(userId, user);
        if (updatedUser != null) {
            return ResponseEntity.ok(updatedUser);
        }
        return ResponseEntity.notFound().build();
    }
    
    // ========== DELETE USER ==========
    @DeleteMapping("/{userId}")
    @Operation(
        summary = "Delete a user",
        description = "Delete a user by ID"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "User deleted successfully"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<Void> deleteUser(
        @Parameter(description = "User ID to delete", required = true)
        @PathVariable Long userId
    ) {
        boolean deleted = userService.deleteUser(userId);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    // ========== GET USER BY BANK ID ==========
    @GetMapping("/banks/{bankId}")
    @Operation(
        summary = "Get users by bank ID",
        description = "Retrieve all users for a specific bank"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Bank not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<List<User>> getUsersByBankId(
        @Parameter(description = "Bank ID", required = true)
        @PathVariable Long bankId
    ) {
        List<User> users = userService.getUsersByBankId(bankId);
        return ResponseEntity.ok(users);
    }
}
```

---

## User Model with Swagger Annotations

```java
package com.ksb.micro.user.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(
    title = "User",
    description = "User entity representing a user in the system"
)
public class User {
    
    @Schema(
        description = "Unique user ID",
        example = "7025868222864118611"
    )
    private Long userId;
    
    @Schema(
        description = "Username for login",
        example = "DecUserOne",
        required = true
    )
    private String username;
    
    @Schema(
        description = "User's email address",
        example = "user@example.com",
        required = true
    )
    private String email;
    
    @Schema(
        description = "User's full name",
        example = "Dec User One",
        required = true
    )
    private String fullName;
    
    @Schema(
        description = "Associated bank ID",
        example = "103",
        required = true
    )
    private Long bankId;
    
    @Schema(
        description = "User status (ACTIVE/INACTIVE)",
        example = "ACTIVE",
        required = true
    )
    private String status;
    
    @Schema(
        description = "User creation timestamp",
        example = "2025-12-06T10:30:00"
    )
    private String createdAt;
    
    @Schema(
        description = "Last update timestamp",
        example = "2025-12-06T11:00:00"
    )
    private String updatedAt;
}
```

---

## Complete Example: Auth Service Controller

```java
package com.ksb.micro.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "APIs for user authentication")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    // ========== LOGIN ==========
    @PostMapping("/login")
    @Operation(
        summary = "User login",
        description = "Authenticate user and return JWT token"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Login successful, returns JWT token",
            content = @Content(schema = @Schema(implementation = LoginResponse.class))
        ),
        @ApiResponse(responseCode = "401", description = "Invalid credentials"),
        @ApiResponse(responseCode = "400", description = "Missing username or password")
    })
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.authenticate(request.getUsername(), request.getPassword());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    
    // ========== LOGOUT ==========
    @PostMapping("/logout")
    @Operation(
        summary = "User logout",
        description = "Invalidate user session and JWT token"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Logout successful"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
        authService.logout(token);
        return ResponseEntity.ok("Logged out successfully");
    }
    
    // ========== REFRESH TOKEN ==========
    @PostMapping("/refresh")
    @Operation(
        summary = "Refresh JWT token",
        description = "Generate a new JWT token using refresh token"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Token refreshed successfully",
            content = @Content(schema = @Schema(implementation = TokenResponse.class))
        ),
        @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token")
    })
    public ResponseEntity<TokenResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        try {
            TokenResponse response = authService.refreshToken(request.getRefreshToken());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    
    // ========== VALIDATE TOKEN ==========
    @GetMapping("/validate")
    @Operation(
        summary = "Validate JWT token",
        description = "Check if the provided JWT token is valid"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Token is valid",
            content = @Content(schema = @Schema(implementation = TokenValidationResponse.class))
        ),
        @ApiResponse(responseCode = "401", description = "Token is invalid or expired")
    })
    public ResponseEntity<TokenValidationResponse> validateToken(@RequestHeader("Authorization") String token) {
        boolean isValid = authService.validateToken(token);
        if (isValid) {
            return ResponseEntity.ok(new TokenValidationResponse(true, "Token is valid"));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new TokenValidationResponse(false, "Token is invalid or expired"));
    }
}
```

---

## Auth Service Models with Swagger

```java
// LoginRequest
@Schema(title = "Login Request", description = "Credentials for user login")
@Data
public class LoginRequest {
    
    @Schema(
        description = "Username",
        example = "DecUserOne",
        required = true
    )
    private String username;
    
    @Schema(
        description = "Password",
        example = "password123",
        required = true
    )
    private String password;
}

// LoginResponse
@Schema(title = "Login Response", description = "Response after successful login")
@Data
public class LoginResponse {
    
    @Schema(
        description = "JWT Access Token",
        example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
    )
    private String token;
    
    @Schema(
        description = "Refresh Token",
        example = "refreshToken123..."
    )
    private String refreshToken;
    
    @Schema(
        description = "Token expiration time in seconds",
        example = "3600"
    )
    private Long expiresIn;
    
    @Schema(
        description = "Token type",
        example = "Bearer"
    )
    private String tokenType;
}

// TokenValidationResponse
@Schema(title = "Token Validation Response")
@Data
public class TokenValidationResponse {
    
    @Schema(description = "Is token valid")
    private Boolean isValid;
    
    @Schema(description = "Validation message")
    private String message;
}
```

---

## Best Practices

### ✅ DO

- ✅ Add `@Tag` to controller classes
- ✅ Add `@Operation` to all public methods
- ✅ Add `@ApiResponse` for each possible HTTP response code
- ✅ Add `@Schema` to model classes and fields
- ✅ Add `@Parameter` descriptions for method parameters
- ✅ Provide `example` values in schema
- ✅ Document error responses (401, 404, 500, etc.)
- ✅ Use meaningful operation summaries

### ❌ DON'T

- ❌ Leave controllers without @Tag
- ❌ Skip @Operation on public endpoints
- ❌ Miss error response documentation
- ❌ Use vague descriptions
- ❌ Leave required fields unmarked
- ❌ Use generic response descriptions

---

## Testing in Swagger UI

1. Open: `http://localhost:8080/swagger-ui.html`
2. Select service from dropdown
3. Expand endpoints
4. Click "Try it out"
5. Enter parameters
6. Click "Execute"
7. See response

---

## Example API Calls from Swagger UI

### User Service Example
```
GET /v1/banks/103/users/7025868222864118611

Response: 
{
  "userId": 7025868222864118611,
  "username": "DecUserOne",
  "email": "user@example.com",
  "fullName": "Dec User One",
  "bankId": 103,
  "status": "ACTIVE",
  "createdAt": "2025-12-06T10:30:00",
  "updatedAt": "2025-12-06T11:00:00"
}
```

### Auth Service Example
```
POST /auth/login
Request:
{
  "username": "DecUserOne",
  "password": "password123"
}

Response:
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "refreshToken123...",
  "expiresIn": 3600,
  "tokenType": "Bearer"
}
```

---

**Use these annotations to make your APIs fully documented in Swagger!** 📚

