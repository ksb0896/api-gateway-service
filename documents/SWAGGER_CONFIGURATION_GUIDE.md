# 🔧 Swagger Documentation Configuration Guide

## Current Setup Analysis

### Problem
- ✅ Main Swagger page opens on gateway (8080)
- ❌ User Profile Service (8081) documentation not loading
- ❌ Auth Service (8083) documentation not loading

### Root Causes
1. Backend services might not have `springdoc-openapi` dependency
2. Backend services not exposing `/v3/api-docs` endpoint
3. Gateway routes not properly configured for Swagger endpoints
4. CORS issues between gateway and backend services
5. Backend services not configured for Swagger UI

---

## Solution: Configure Each Service for Swagger

### Step 1: Update Backend Service Dependencies

Add to **User Profile Service (8081)** `pom.xml`:
```xml
<!-- Swagger/OpenAPI Dependencies -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.5.0</version>
</dependency>
```

Add to **Auth Service (8083)** `pom.xml`:
```xml
<!-- Swagger/OpenAPI Dependencies -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.5.0</version>
</dependency>
```

---

### Step 2: Configure User Profile Service (8081)

**File**: `application.properties` (or `application.yml`)

```properties
# Server configuration
server.port=8081
spring.application.name=user-profile-service

# Swagger/OpenAPI Configuration
springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.url=http://localhost:8080/swagger-ui/index.html
springdoc.swagger-ui.urls[0].url=http://localhost:8081/v3/api-docs
springdoc.swagger-ui.urls[0].name=User Profile Service
springdoc.swagger-ui.enabled=true
springdoc.swagger-ui.use-root-path=true

# CORS Configuration (if needed)
spring.web.cors.allowed-origins=http://localhost:8080,http://localhost:3000
spring.web.cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS
spring.web.cors.allowed-headers=*
spring.web.cors.allow-credentials=true

# Actuator for health checks
management.endpoints.web.exposure.include=health,info
management.endpoint.health.show-details=when-authorized
```

---

### Step 3: Configure Auth Service (8083)

**File**: `application.properties` (or `application.yml`)

```properties
# Server configuration
server.port=8083
spring.application.name=auth-service

# Swagger/OpenAPI Configuration
springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.url=http://localhost:8080/swagger-ui/index.html
springdoc.swagger-ui.urls[0].url=http://localhost:8083/v3/api-docs
springdoc.swagger-ui.urls[0].name=Auth Service
springdoc.swagger-ui.enabled=true
springdoc.swagger-ui.use-root-path=true

# CORS Configuration (if needed)
spring.web.cors.allowed-origins=http://localhost:8080,http://localhost:3000
spring.web.cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS
spring.web.cors.allowed-headers=*
spring.web.cors.allow-credentials=true

# Actuator for health checks
management.endpoints.web.exposure.include=health,info
management.endpoint.health.show-details=when-authorized
```

---

### Step 4: Add OpenAPI Configuration Classes

**User Profile Service (8081)** - Create `SwaggerConfig.java`:

```java
package com.ksb.micro.user.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    
    @Bean
    public OpenAPI userProfileOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("User Profile Service API")
                        .description("User Profile Service - Manage user profiles and data")
                        .version("1.0.0"));
    }
}
```

**Auth Service (8083)** - Create `SwaggerConfig.java`:

```java
package com.ksb.micro.auth.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    
    @Bean
    public OpenAPI authServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Auth Service API")
                        .description("Authentication Service - Handle user authentication and JWT tokens")
                        .version("1.0.0"));
    }
}
```

---

## Step 5: Update Gateway Configuration

The gateway's `application.properties` is already mostly correct. Here's the optimal configuration:

```properties
# Swagger Routes on Gateway

# Route 3: User Profile Service Swagger Docs
spring.cloud.gateway.routes[3].id=user-profile-docs
spring.cloud.gateway.routes[3].uri=http://localhost:8081
spring.cloud.gateway.routes[3].predicates[0]=Path=/v3/api-docs/user-profile/**,/user-profile/v3/api-docs/**
spring.cloud.gateway.routes[3].filters[0]=RewritePath=/v3/api-docs/user-profile(?<remaining>.*), /v3/api-docs
spring.cloud.gateway.routes[3].order=1

# Route 4: Auth Service Swagger Docs
spring.cloud.gateway.routes[4].id=auth-service-docs
spring.cloud.gateway.routes[4].uri=http://localhost:8083
spring.cloud.gateway.routes[4].predicates[0]=Path=/v3/api-docs/auth-service/**,/auth-service/v3/api-docs/**
spring.cloud.gateway.routes[4].filters[0]=RewritePath=/v3/api-docs/auth-service(?<remaining>.*), /v3/api-docs
spring.cloud.gateway.routes[4].order=1

# Swagger UI Configuration
springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.urls[0].url=/v3/api-docs/user-profile
springdoc.swagger-ui.urls[0].name=User Profile Service

springdoc.swagger-ui.urls[1].url=/v3/api-docs/auth-service
springdoc.swagger-ui.urls[1].name=Auth Service

springdoc.swagger-ui.operations-sorter=method
springdoc.swagger-ui.tags-sorter=alpha
```

---

## Testing the Configuration

### Test 1: Check Gateway Swagger UI
```
http://localhost:8080/swagger-ui.html
```
Expected: Main Swagger page loads with 2 service tabs

### Test 2: Test User Profile Service Docs
```
curl http://localhost:8080/v3/api-docs/user-profile
```
Expected: JSON response with User Profile Service OpenAPI spec

### Test 3: Test Auth Service Docs
```
curl http://localhost:8080/v3/api-docs/auth-service
```
Expected: JSON response with Auth Service OpenAPI spec

### Test 4: Direct Service Access (for debugging)
```
# User Profile Service
curl http://localhost:8081/v3/api-docs

# Auth Service
curl http://localhost:8083/v3/api-docs
```
Expected: Both return OpenAPI specs

---

## Troubleshooting

### Issue 1: "Cannot read specification" Error
**Cause**: Backend service doesn't have Swagger dependency or not responding
**Solution**: 
1. Add `springdoc-openapi-starter-webmvc-ui` dependency
2. Restart the service
3. Verify it's responding to `http://localhost:PORT/v3/api-docs`

### Issue 2: Gateway Route Not Matching
**Solution**: Ensure routes have correct predicates and order
```properties
spring.cloud.gateway.routes[3].order=1  # Lower order = higher priority
```

### Issue 3: CORS Errors in Browser Console
**Solution**: Add CORS configuration to backend services
```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:8080", "http://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowCredentials(true);
    }
}
```

### Issue 4: Swagger UI Shows Empty Specification
**Solution**: Ensure backend service has API endpoints documented
```java
@RestController
@RequestMapping("/v1/users")
@Tag(name = "Users", description = "User Management APIs")
public class UserController {
    
    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        // implementation
    }
}
```

---

## Full Working Example

### For User Profile Service (8081)

**pom.xml** - Add dependency:
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.5.0</version>
</dependency>
```

**application.properties**:
```properties
server.port=8081
spring.application.name=user-profile-service

# Swagger
springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.enabled=true
```

**SwaggerConfig.java**:
```java
@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("User Profile Service")
                .version("1.0.0")
                .description("User Profile API"));
    }
}
```

**UserController.java**:
```java
@RestController
@RequestMapping("/v1/users")
@Tag(name = "Users")
public class UserController {
    
    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieve a user by their ID")
    @ApiResponse(responseCode = "200", description = "User found")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        // Your implementation
        return ResponseEntity.ok(new User());
    }
}
```

---

## Summary of Changes Required

| Service | Changes | Priority |
|---------|---------|----------|
| **Gateway (8080)** | Verify routes are correct | ✅ Already done |
| **User Service (8081)** | Add Swagger dependency, config, SwaggerConfig class | 🔴 REQUIRED |
| **Auth Service (8083)** | Add Swagger dependency, config, SwaggerConfig class | 🔴 REQUIRED |

---

## Verification Checklist

- [ ] User Profile Service has `springdoc-openapi` dependency
- [ ] Auth Service has `springdoc-openapi` dependency
- [ ] Both services have `SwaggerConfig.java` class
- [ ] Both services have Swagger configuration in `application.properties`
- [ ] Gateway routes are configured for docs endpoints
- [ ] Can access http://localhost:8080/swagger-ui.html
- [ ] Can see User Profile Service in dropdown
- [ ] Can see Auth Service in dropdown
- [ ] Both services' APIs are visible in Swagger UI
- [ ] API endpoints are documented with @Operation annotations

---

## Commands to Test

```bash
# Test gateway swagger page
curl -I http://localhost:8080/swagger-ui.html

# Test user service docs endpoint
curl http://localhost:8080/v3/api-docs/user-profile

# Test auth service docs endpoint
curl http://localhost:8080/v3/api-docs/auth-service

# Test direct access (for debugging)
curl http://localhost:8081/v3/api-docs
curl http://localhost:8083/v3/api-docs
```

---

**Follow these steps to get Swagger working for all services!** 🎉

