# Swagger Implementation - Visual Guide & Command Reference

## 🎯 Visual Overview

### System Architecture
```
┌──────────────────────────────────────────────────────────────────┐
│                         End User (Browser)                       │
│              http://localhost:8080/swagger-ui/                   │
└──────────────────────────┬───────────────────────────────────────┘
                           │
                    ┌──────▼──────────┐
                    │  Swagger UI     │
                    │  Main Interface │
                    └──────┬──────────┘
                           │
           ┌───────────────┴───────────────┐
           │                               │
      ┌────▼─────┐               ┌────────▼─────┐
      │ Dropdown  │               │ Try It Out   │
      │ Services  │               │ Test Buttons │
      └────┬─────┘               └──────────────┘
           │
    ┌──────▼────────────────────────┐
    │   Select Service:             │
    │   • User Profile Service      │
    │   • Profile Photo Service     │
    │   • Auth Service              │
    │   • API Gateway               │
    └──────┬────────────────────────┘
           │
    ┌──────▼──────────────────────┐
    │   API Gateway (8080)        │
    │   Route Processor           │
    └──────┬─────────┬───────┬────┘
           │         │       │
    ┌──────▼┐  ┌─────▼──┐  ┌─▼──────────┐
    │ 8081  │  │  8082  │  │    8083    │
    │ User  │  │ Photo  │  │    Auth    │
    │Profile│  │Service │  │  Service   │
    └───────┘  └────────┘  └────────────┘
```

### Data Flow Diagram
```
Step 1: User Opens Swagger
┌─────────────────────────────────────────┐
│ Browser                                 │
│ GET /swagger-ui/index.html              │
└──────────────────────┬──────────────────┘
                       │
                       ▼
        ┌──────────────────────────┐
        │ API Gateway (8080)       │
        │ Returns Swagger HTML     │
        └──────────────────────────┘

Step 2: Swagger UI Loads Service List
┌──────────────────────────────────┐
│ Browser                          │
│ GET /v3/api-docs                 │
│ (Get gateway's OpenAPI spec)     │
└────────────────┬─────────────────┘
                 │
                 ▼
    ┌────────────────────────────┐
    │ Gateway returns spec with  │
    │ service list               │
    │ • User Profile Service     │
    │ • Profile Photo Service    │
    │ • Auth Service             │
    │ • API Gateway              │
    └────────────────────────────┘

Step 3: User Selects Service
┌────────────────────────────────────┐
│ Browser                            │
│ GET /v3/api-docs/user-profile      │
│ (Request service documentation)    │
└──────────────────┬─────────────────┘
                   │
                   ▼
    ┌──────────────────────────────┐
    │ API Gateway Route 3           │
    │ Intercepts /v3/api-docs/...   │
    │ Rewrites to /v3/api-docs      │
    │ Proxies to 8081               │
    └────────────────┬──────────────┘
                     │
                     ▼
        ┌──────────────────────────┐
        │ User Profile Service     │
        │ Returns OpenAPI spec     │
        │ with all endpoints       │
        └──────────────────────────┘

Step 4: Swagger Displays Endpoints
┌──────────────────────────────┐
│ Swagger UI in Browser        │
│ Shows all endpoints:         │
│ • POST /v1/users             │
│ • GET /v1/users              │
│ • GET /v1/users/{id}         │
│ • PUT /v1/users/{id}         │
│ • DELETE /v1/users/{id}      │
│ (and more...)                │
└──────────────────────────────┘
```

### Gateway Route Processing
```
Incoming Request: /v3/api-docs/user-profile
                           │
                           ▼
         ┌──────────────────────────────────┐
         │ Gateway Route Matching           │
         │ Checks all routes (0-5)          │
         └──────────────────┬───────────────┘
                            │
         ┌──────────────────▼───────────────┐
         │ Route 3 Matches!                 │
         │ Path=/v3/api-docs/user-profile/**
         │ ID=user-profile-docs            │
         │ URI=http://localhost:8081       │
         └──────────────────┬───────────────┘
                            │
         ┌──────────────────▼───────────────┐
         │ Apply Filters                    │
         │ RewritePath /v3/api-docs/        │
         │ user-profile → /v3/api-docs     │
         └──────────────────┬───────────────┘
                            │
         ┌──────────────────▼───────────────┐
         │ Forward Request                  │
         │ To: http://localhost:8081       │
         │ Path: /v3/api-docs              │
         └──────────────────┬───────────────┘
                            │
         ┌──────────────────▼───────────────┐
         │ Receive Response                 │
         │ Send Back to Client             │
         └──────────────────────────────────┘
```

## 🔧 Command Reference

### Build and Start Services

#### 1. Build API Gateway
```bash
cd C:\ksb096-B\prjcts\sts\user-service\api-gateway-service
mvn clean install
```

#### 2. Start Auth Service (Port 8083)
```bash
cd path/to/auth-service
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8083"
```

#### 3. Start User Profile Service (Port 8081)
```bash
cd path/to/user-profile-service
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"
```

#### 4. Start Profile Photo Service (Port 8082)
```bash
cd path/to/profile-photo-service
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8082"
```

#### 5. Start API Gateway (Port 8080)
```bash
cd C:\ksb096-B\prjcts\sts\user-service\api-gateway-service
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8080"
```

### Testing Commands (curl)

#### Check Gateway Health
```bash
curl -i http://localhost:8080/actuator/health
```

Expected Response:
```json
{
  "status": "UP"
}
```

#### Get Gateway OpenAPI Spec
```bash
curl -i http://localhost:8080/v3/api-docs | jq .
```

Expected: JSON with gateway information

#### Get User Profile Service Documentation (via Gateway)
```bash
curl -i http://localhost:8080/v3/api-docs/user-profile | jq .
```

Expected: User Profile Service's OpenAPI spec

#### Get Profile Photo Service Documentation (via Gateway)
```bash
curl -i http://localhost:8080/v3/api-docs/profile-photo | jq .
```

Expected: Profile Photo Service's OpenAPI spec

#### Get Auth Service Documentation (via Gateway)
```bash
curl -i http://localhost:8080/v3/api-docs/auth-service | jq .
```

Expected: Auth Service's OpenAPI spec

#### Check Swagger UI HTML
```bash
curl -i http://localhost:8080/swagger-ui/index.html | head -50
```

Expected: HTML containing Swagger UI code

#### Direct Service Access (Optional - no gateway)
```bash
# Direct to User Profile Service
curl -i http://localhost:8081/v3/api-docs | jq .

# Direct to Profile Photo Service
curl -i http://localhost:8082/v3/api-docs | jq .

# Direct to Auth Service
curl -i http://localhost:8083/v3/api-docs | jq .
```

### Troubleshooting Commands

#### Check if Service is Running
```bash
# Check gateway
curl http://localhost:8080/actuator/health

# Check user profile service
curl http://localhost:8081/actuator/health

# Check profile photo service
curl http://localhost:8082/actuator/health

# Check auth service
curl http://localhost:8083/actuator/health
```

#### Check Gateway Routes
```bash
curl -i http://localhost:8080/actuator/gateway/routes | jq .
```

#### Monitor Gateway Logs (Real-time)
```bash
# If using jar file
java -jar target/api_gateway-0.0.1-SNAPSHOT.jar --logging.level.org.springframework.cloud.gateway=DEBUG

# Or with Maven
mvn spring-boot:run -Dspring-boot.run.arguments="--logging.level.org.springframework.cloud.gateway=DEBUG"
```

#### Check Gateway Filters
```bash
curl -i http://localhost:8080/actuator/gateway/filters | jq .
```

#### Test API Endpoint (with JWT token)
```bash
# Replace YOUR_TOKEN with actual JWT token
curl -i -H "Authorization: Bearer YOUR_TOKEN" \
  http://localhost:8080/v1/banks/100/users

# Or using User Profile Service directly
curl -i -H "Authorization: Bearer YOUR_TOKEN" \
  http://localhost:8081/v1/banks/100/users
```

### Browser Access

#### Open Swagger UI
```
http://localhost:8080/swagger-ui/index.html
```

#### Test Individual Service (Optional)
```
# User Profile Service
http://localhost:8081/swagger-ui/index.html

# Profile Photo Service
http://localhost:8082/swagger-ui/index.html

# Auth Service
http://localhost:8083/swagger-ui/index.html
```

## 📊 Route Mapping Table

| Route ID | Port | Path | URI | Docs? | Auth? |
|----------|------|------|-----|-------|-------|
| profile_photo_route | 8080 | `/v1/banks/*/users/*/photo/**` | 8082 | ❌ | ✅ |
| user-profile-service | 8080 | `/v1/banks/*/users,/v1/banks/*/users/**` | 8081 | ❌ | ✅ |
| auth_route | 8080 | `/auth/**` | 8083 | ❌ | ❌ |
| user-profile-docs | 8080 | `/v3/api-docs/user-profile/**` | 8081 | ✅ | ❌ |
| profile-photo-docs | 8080 | `/v3/api-docs/profile-photo/**` | 8082 | ✅ | ❌ |
| auth-service-docs | 8080 | `/v3/api-docs/auth-service/**` | 8083 | ✅ | ❌ |

## 🔍 Swagger UI Dropdown Options

| Service | Port | Status | Default |
|---------|------|--------|---------|
| User Profile Service | 8081 | Enabled | ✅ Primary |
| Profile Photo Service | 8082 | Enabled | ❌ |
| Auth Service | 8083 | Enabled | ❌ |
| API Gateway | 8080 | Enabled | ❌ |

## ✅ Verification Matrix

```
Test Case                          Command                              Expected Result
────────────────────────────────────────────────────────────────────────────────────────
Gateway Health                     curl localhost:8080/actuator/health  {"status":"UP"}
Swagger UI Loads                   curl localhost:8080/swagger-ui/      HTTP 200 + HTML
Gateway OpenAPI Spec               curl localhost:8080/v3/api-docs      JSON with gateway info
User Profile Docs (via Gateway)    curl localhost:8080/v3/api-docs/...  JSON with service spec
Profile Photo Docs (via Gateway)   curl localhost:8080/v3/api-docs/...  JSON with service spec
Auth Service Docs (via Gateway)    curl localhost:8080/v3/api-docs/...  JSON with service spec
Direct Service Access              curl localhost:8081/v3/api-docs      JSON with service spec
Swagger UI Dropdown Shows 4 Items  Browser → Open Swagger UI            4 options visible
```

## 🎯 Expected Responses

### Gateway Health Check
```json
{
  "status": "UP"
}
```

### Gateway OpenAPI Spec (Excerpt)
```json
{
  "openapi": "3.0.1",
  "info": {
    "title": "API Gateway - Microservices Aggregator",
    "version": "1.0.0",
    "description": "Central entry point for all microservices..."
  },
  "servers": [
    {
      "url": "http://localhost:8080",
      "description": "API Gateway - Main Entry Point"
    }
  ]
}
```

### Service Documentation Response (Excerpt)
```json
{
  "openapi": "3.0.1",
  "info": {
    "title": "User Profile Service",
    "version": "1.0.0"
  },
  "paths": {
    "/v1/banks/{bankId}/users": {
      "post": {
        "tags": ["User Management"],
        "operationId": "createUser",
        "parameters": [...],
        "requestBody": {...},
        "responses": {...}
      }
    }
  }
}
```

## 🚨 Common Responses and Their Meanings

| Response Code | URL | Meaning |
|---|---|---|
| 200 OK | /v3/api-docs | Documentation is accessible |
| 200 OK | /swagger-ui/index.html | Swagger UI is accessible |
| 404 Not Found | /v3/api-docs | Service docs not available |
| 500 Internal Error | Any route | Service is down or misconfigured |
| 503 Service Unavailable | Any route | Downstream service is unavailable |
| 401 Unauthorized | /v1/banks/*/users | Missing or invalid JWT token |

## 💡 Pro Tips

### Tip 1: Pretty Print JSON Responses
```bash
curl http://localhost:8080/v3/api-docs | jq .
```

### Tip 2: Save Response to File
```bash
curl http://localhost:8080/v3/api-docs > gateway-spec.json
```

### Tip 3: Check Only Headers
```bash
curl -i http://localhost:8080/v3/api-docs
```

### Tip 4: Verbose Output for Debugging
```bash
curl -v http://localhost:8080/v3/api-docs
```

### Tip 5: Follow Redirects
```bash
curl -L http://localhost:8080/swagger-ui/
```

### Tip 6: Add Custom Headers
```bash
curl -H "Authorization: Bearer TOKEN" \
  http://localhost:8080/v1/banks/100/users
```

### Tip 7: Post Data to API
```bash
curl -X POST http://localhost:8080/v1/banks/100/users \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TOKEN" \
  -d '{"name":"John","email":"john@example.com"}'
```

## 🎓 Learning Path

1. **Understand Architecture**
   - Read SWAGGER_COMPLETE_SOLUTION.md
   - Review visual diagrams above

2. **Set Up Services**
   - Build project
   - Start all 4 services in order
   - Verify each service is accessible

3. **Access Swagger**
   - Open http://localhost:8080/swagger-ui/index.html
   - Try selecting different services

4. **Test APIs**
   - View endpoint documentation
   - Test API calls through Swagger UI
   - Observe request/response data

5. **Troubleshoot**
   - Check logs if issues arise
   - Use curl commands to debug
   - Refer to troubleshooting section

---

## 📞 Quick Links

- Main Swagger UI: http://localhost:8080/swagger-ui/index.html
- Gateway Health: http://localhost:8080/actuator/health
- Gateway Routes: http://localhost:8080/actuator/gateway/routes
- Documentation Files: See `/documents` folder
- Configuration: `application.properties` and `SwaggerConfig.java`


