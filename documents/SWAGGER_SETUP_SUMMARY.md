# 🎯 Swagger Setup Summary - What You Need to Do

## The Problem
Your gateway Swagger page opens, but services on ports 8081 and 8083 aren't loading their documentation.

## The Solution - 3 Main Steps

---

## Step 1️⃣: Add Swagger Dependency to Backend Services

### User Profile Service (8081) - `pom.xml`
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.5.0</version>
</dependency>
```

### Auth Service (8083) - `pom.xml`
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.5.0</version>
</dependency>
```

**Then rebuild:**
```bash
mvn clean package
```

---

## Step 2️⃣: Create SwaggerConfig Class in Both Services

### User Profile Service (8081)
**File**: `src/main/java/com/ksb/micro/user/config/SwaggerConfig.java`

```java
package com.ksb.micro.user.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("User Profile Service API")
                        .description("User Profile Service")
                        .version("1.0.0"));
    }
}
```

### Auth Service (8083)
**File**: `src/main/java/com/ksb/micro/auth/config/SwaggerConfig.java`

```java
package com.ksb.micro.auth.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Auth Service API")
                        .description("Auth Service")
                        .version("1.0.0"));
    }
}
```

---

## Step 3️⃣: Add Swagger Properties to Both Services

### User Profile Service (8081) - `application.properties`
```properties
springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.enabled=true
```

### Auth Service (8083) - `application.properties`
```properties
springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.enabled=true
```

---

## Optional: Add API Documentation Annotations

To make your APIs appear properly in Swagger UI, add annotations to your controllers:

```java
@RestController
@RequestMapping("/v1/users")
@Tag(name = "Users", description = "User Management")
public class UserController {
    
    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    @ApiResponse(responseCode = "200", description = "User found")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        // Your code
    }
}
```

See: `SWAGGER_ANNOTATIONS_EXAMPLES.md` for full examples.

---

## Gateway Configuration (Already Done ✅)

The gateway configuration is already set up correctly:

```properties
# Routes to backend service docs
spring.cloud.gateway.routes[3].id=user-profile-docs
spring.cloud.gateway.routes[3].uri=http://localhost:8081
spring.cloud.gateway.routes[3].predicates[0]=Path=/v3/api-docs/user-profile/**
spring.cloud.gateway.routes[3].filters[0]=RewritePath=/v3/api-docs/user-profile(?<remaining>.*), /v3/api-docs

spring.cloud.gateway.routes[4].id=auth-service-docs
spring.cloud.gateway.routes[4].uri=http://localhost:8083
spring.cloud.gateway.routes[4].predicates[0]=Path=/v3/api-docs/auth-service/**
spring.cloud.gateway.routes[4].filters[0]=RewritePath=/v3/api-docs/auth-service(?<remaining>.*), /v3/api-docs

# Swagger UI configuration
springdoc.swagger-ui.urls[0].url=/v3/api-docs/user-profile
springdoc.swagger-ui.urls[0].name=User Profile Service

springdoc.swagger-ui.urls[1].url=/v3/api-docs/auth-service
springdoc.swagger-ui.urls[1].name=Auth Service
```

---

## Testing

### Test 1: Verify Services Have Docs
```bash
curl http://localhost:8081/v3/api-docs
curl http://localhost:8083/v3/api-docs
```
Both should return JSON OpenAPI specs.

### Test 2: Verify Gateway Routes
```bash
curl http://localhost:8080/v3/api-docs/user-profile
curl http://localhost:8080/v3/api-docs/auth-service
```
Both should return JSON specs.

### Test 3: Open Swagger UI
```
http://localhost:8080/swagger-ui.html
```
Should show:
- ✅ Main Swagger page loads
- ✅ Dropdown menu with services
- ✅ User Profile Service listed
- ✅ Auth Service listed
- ✅ Can click each service to see endpoints

---

## Timeline

| Task | Time |
|------|------|
| Add dependency to User Service | 2 min |
| Add dependency to Auth Service | 2 min |
| Create SwaggerConfig (User Service) | 2 min |
| Create SwaggerConfig (Auth Service) | 2 min |
| Add properties (User Service) | 1 min |
| Add properties (Auth Service) | 1 min |
| Rebuild all services | 5 min |
| Restart all services | 2 min |
| Test | 2 min |
| **TOTAL** | **~19 minutes** |

---

## Files You Need to Create/Modify

### User Profile Service (8081)
- ✏️ **Modify**: `pom.xml` - Add dependency
- ✏️ **Modify**: `application.properties` - Add Swagger config
- 🆕 **Create**: `SwaggerConfig.java` - New file

### Auth Service (8083)
- ✏️ **Modify**: `pom.xml` - Add dependency
- ✏️ **Modify**: `application.properties` - Add Swagger config
- 🆕 **Create**: `SwaggerConfig.java` - New file

### Gateway (8080)
- ✅ **Already Done**: No changes needed!

---

## Troubleshooting

### Issue: Still showing "Cannot read specification"
**Fix**: Ensure both services have restarted after adding dependency

### Issue: Dropdown shows service but no endpoints
**Fix**: Add @Operation and @Tag annotations to your controllers

### Issue: Still can't see services
**Fix**: 
1. Check `/v3/api-docs` endpoint directly: `curl http://localhost:8081/v3/api-docs`
2. If no response, service doesn't have Swagger dependency
3. If empty response, no controllers with @Tag annotation

### Issue: Gateway page shows error
**Fix**: Check gateway logs for route matching issues

---

## Documentation Files Created

For more detailed information, see:

1. **SWAGGER_QUICK_FIX.md** - Quick 5-step fix (START HERE)
2. **SWAGGER_CONFIGURATION_GUIDE.md** - Comprehensive guide
3. **SWAGGER_ANNOTATIONS_EXAMPLES.md** - Code examples for your APIs

---

## Command Reference

```bash
# Rebuild a service
cd service-folder
mvn clean package

# Start services in separate terminals
java -jar user-service.jar          # Port 8081
java -jar auth-service.jar          # Port 8083
java -jar api-gateway.jar           # Port 8080

# Test endpoints
curl http://localhost:8081/v3/api-docs
curl http://localhost:8083/v3/api-docs
curl http://localhost:8080/v3/api-docs

# Open Swagger UI
http://localhost:8080/swagger-ui.html
```

---

## Expected Result

After completing all steps:

```
✅ http://localhost:8080/swagger-ui.html opens
✅ Dropdown shows "User Profile Service"
✅ Dropdown shows "Auth Service"
✅ Clicking each service shows its APIs
✅ Can test endpoints directly from Swagger UI
✅ See request/response examples
```

---

## Next Steps

1. Add dependency to both services → Rebuild
2. Create SwaggerConfig.java in both services
3. Add Swagger properties to both services
4. Restart all services
5. Test in browser: http://localhost:8080/swagger-ui.html
6. (Optional) Add @Operation annotations to your APIs

---

**You're almost done! Follow the 3 steps above to get Swagger working!** 🎉

