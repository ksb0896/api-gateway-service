# ✅ Swagger Fix Checklist - Quick Guide

## The Issue
- ✅ Gateway Swagger page opens
- ❌ User Profile Service (8081) docs not loading
- ❌ Auth Service (8083) docs not loading

---

## Quick Fix (5 Steps)

### Step 1: Add Dependency to BOTH Backend Services

**User Profile Service (8081)** and **Auth Service (8083)** - Add to `pom.xml`:

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.5.0</version>
</dependency>
```

Then rebuild: `mvn clean package`

---

### Step 2: Add Swagger Config File to BOTH Services

**File**: `src/main/java/[package]/config/SwaggerConfig.java`

**For User Profile Service (8081)**:
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

**For Auth Service (8083)**:
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

### Step 3: Add Swagger Properties to BOTH Services

**User Profile Service (8081)** - `application.properties`:
```properties
springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.enabled=true
```

**Auth Service (8083)** - `application.properties`:
```properties
springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.enabled=true
```

---

### Step 4: Verify Gateway Configuration

**Gateway (8080)** - Check `application.properties` has these routes:

```properties
# Route 3: User Profile Service Docs
spring.cloud.gateway.routes[3].id=user-profile-docs
spring.cloud.gateway.routes[3].uri=http://localhost:8081
spring.cloud.gateway.routes[3].predicates[0]=Path=/v3/api-docs/user-profile/**
spring.cloud.gateway.routes[3].filters[0]=RewritePath=/v3/api-docs/user-profile(?<remaining>.*), /v3/api-docs

# Route 4: Auth Service Docs
spring.cloud.gateway.routes[4].id=auth-service-docs
spring.cloud.gateway.routes[4].uri=http://localhost:8083
spring.cloud.gateway.routes[4].predicates[0]=Path=/v3/api-docs/auth-service/**
spring.cloud.gateway.routes[4].filters[0]=RewritePath=/v3/api-docs/auth-service(?<remaining>.*), /v3/api-docs

# Swagger UI
springdoc.swagger-ui.urls[0].url=/v3/api-docs/user-profile
springdoc.swagger-ui.urls[0].name=User Profile Service

springdoc.swagger-ui.urls[1].url=/v3/api-docs/auth-service
springdoc.swagger-ui.urls[1].name=Auth Service
```

---

### Step 5: Restart All Services

```powershell
# Kill all Java processes
Stop-Process -Name java -Force -ErrorAction SilentlyContinue
Start-Sleep -Seconds 5

# Start each service in separate terminal

# Terminal 1: User Profile Service (8081)
java -jar user-profile-service.jar

# Terminal 2: Auth Service (8083)
java -jar auth-service.jar

# Terminal 3: API Gateway (8080)
java -jar api-gateway-service.jar
```

---

## Testing

### Test 1: User Profile Service Responding
```powershell
curl http://localhost:8081/v3/api-docs
```
✅ Should return JSON OpenAPI spec

### Test 2: Auth Service Responding
```powershell
curl http://localhost:8083/v3/api-docs
```
✅ Should return JSON OpenAPI spec

### Test 3: Gateway Swagger UI
```
http://localhost:8080/swagger-ui.html
```
✅ Should show main Swagger page
✅ Should have dropdown with "User Profile Service"
✅ Should have dropdown with "Auth Service"

### Test 4: Gateway Routes Working
```powershell
curl http://localhost:8080/v3/api-docs/user-profile
curl http://localhost:8080/v3/api-docs/auth-service
```
✅ Both should return JSON OpenAPI specs

---

## Common Issues & Fixes

| Issue | Fix |
|-------|-----|
| "Cannot read specification" | Add Swagger dependency to backend service |
| Service not responding | Verify service is running on correct port |
| Empty spec in Swagger | Add @Operation annotations to API methods |
| CORS error in console | Services need CORS config (see main guide) |
| Route not matching | Check predicates in gateway config |

---

## Files to Create/Modify

### User Profile Service (8081)

**File 1**: `pom.xml`
- Add springdoc-openapi dependency

**File 2**: `src/main/java/.../config/SwaggerConfig.java` (NEW)
- Create SwaggerConfig class

**File 3**: `src/main/resources/application.properties`
- Add Swagger properties

### Auth Service (8083)

**File 1**: `pom.xml`
- Add springdoc-openapi dependency

**File 2**: `src/main/java/.../config/SwaggerConfig.java` (NEW)
- Create SwaggerConfig class

**File 3**: `src/main/resources/application.properties`
- Add Swagger properties

### API Gateway (8080)

**File 1**: `src/main/resources/application.properties`
- Verify routes and Swagger UI config (already configured)

---

## Expected Result

```
Browser: http://localhost:8080/swagger-ui.html

✅ Main page loads
✅ Dropdown shows:
   - User Profile Service
   - Auth Service
✅ Click User Profile Service → See all /v1/users/** endpoints
✅ Click Auth Service → See all /auth/** endpoints
✅ Can test endpoints directly from Swagger UI
```

---

## Timeline

- **5 minutes**: Add dependencies and config
- **2 minutes**: Create SwaggerConfig files
- **3 minutes**: Restart services
- **2 minutes**: Test and verify

**Total: ~12 minutes** ⏱️

---

## Quick Reference Commands

```powershell
# View current Swagger endpoints
curl http://localhost:8081/swagger-ui.html
curl http://localhost:8083/swagger-ui.html
curl http://localhost:8080/swagger-ui.html

# View OpenAPI specs
curl http://localhost:8081/v3/api-docs
curl http://localhost:8083/v3/api-docs
curl http://localhost:8080/v3/api-docs

# Rebuild services
cd user-service-folder
mvn clean package

cd auth-service-folder
mvn clean package
```

---

**Follow this checklist to get Swagger working!** ✅

