# Swagger Implementation - Step by Step Setup

## ✅ What Was Completed

### Phase 1: Code Implementation ✓
- [x] Created `SwaggerConfig.java` - OpenAPI bean configuration
- [x] Updated `application.properties` - Added Swagger routes and configuration
- [x] Verified `GatewaySecurityConfig.java` - Security allows Swagger paths
- [x] Verified `pom.xml` - Has all necessary Swagger dependencies

### Phase 2: Configuration ✓
- [x] Added Route 3 - User Profile Service Documentation Proxy
- [x] Added Route 4 - Profile Photo Service Documentation Proxy
- [x] Added Route 5 - Auth Service Documentation Proxy
- [x] Configured Swagger UI URLs dropdown
- [x] Set User Profile Service as primary/default service
- [x] Added group configuration for all services

### Phase 3: Documentation ✓
- [x] Created `SWAGGER_AGGREGATION_SETUP.md` - Detailed setup documentation
- [x] Created `SWAGGER_QUICK_REFERENCE.md` - Quick access guide
- [x] Created `SWAGGER_IMPLEMENTATION_GUIDE.md` - Implementation guide

---

## 🚀 How to Run

### Prerequisites
- Java 17+ installed
- Maven 3.8+ installed
- All three microservices built and ready
- Ports 8080-8083 are available

### Step 1: Build the API Gateway
```bash
cd C:\ksb096-B\prjcts\sts\user-service\api-gateway-service
mvn clean install
```

### Step 2: Start Services (Open 4 Terminal Windows)

**Terminal 1 - Auth Service (Port 8083)**
```bash
cd path/to/auth-service
mvn spring-boot:run
# Wait for: "Started AuthServiceApplication"
```

**Terminal 2 - User Profile Service (Port 8081)**
```bash
cd path/to/user-profile-service
mvn spring-boot:run
# Wait for: "Started UserProfileServiceApplication"
```

**Terminal 3 - Profile Photo Service (Port 8082)**
```bash
cd path/to/profile-photo-service
mvn spring-boot:run
# Wait for: "Started ProfilePhotoServiceApplication"
```

**Terminal 4 - API Gateway (Port 8080)**
```bash
cd C:\ksb096-B\prjcts\sts\user-service\api-gateway-service
mvn spring-boot:run
# Wait for: "Started ApiGatewayApplication"
```

Expected log in Terminal 4:
```
o.s.c.g.r.RouteDefinitionRouteLocator     : RouteDefinition matched: 5 routes
com.ksb.micro.api_gateway.config.SwaggerConfig : Configuring OpenAPI for API Gateway
```

### Step 3: Access Swagger UI
Open your browser and navigate to:
```
http://localhost:8080/swagger-ui/index.html
```

### Step 4: Select a Service
1. Look for the dropdown at the top left that says "Select a definition"
2. You should see 4 options:
   - **User Profile Service (8081)** ← Default
   - **Profile Photo Service (8082)**
   - **Auth Service (8083)**
   - **API Gateway (8080)**
3. Click on each to see the service's API documentation

---

## 📊 Expected Result

### Swagger UI Layout
```
┌─────────────────────────────────────────────────────────┐
│  Swagger UI                                      🔍 ⚙️ │
├─────────────────────────────────────────────────────────┤
│  [Dropdown: User Profile Service (8081) ▼]             │
│                                                          │
│  API Gateway - Microservices Aggregator v1.0.0         │
│                                                          │
│  BASE URL: http://localhost:8080                       │
│                                                          │
│  Available Endpoints:                                   │
│  ├── POST /v1/banks/*/users                            │
│  ├── GET /v1/banks/*/users                             │
│  ├── GET /v1/banks/*/users/{id}                        │
│  └── ... (more endpoints)                              │
└─────────────────────────────────────────────────────────┘
```

---

## ✓ Verification Checklist

### Gateway is Running
- [ ] Gateway is running on port 8080
- [ ] Check: http://localhost:8080/actuator/health

### All Services Are Running
- [ ] Auth Service is running on port 8083
- [ ] User Profile Service is running on port 8081
- [ ] Profile Photo Service is running on port 8082

### Swagger UI Works
- [ ] Can access http://localhost:8080/swagger-ui/index.html
- [ ] Page loads without 404 errors
- [ ] Can see the Swagger UI interface

### Dropdown Shows All Services
- [ ] Dropdown is visible (top left)
- [ ] Shows "User Profile Service (8081)" - Default
- [ ] Shows "Profile Photo Service (8082)"
- [ ] Shows "Auth Service (8083)"
- [ ] Shows "API Gateway (8080)"

### Can Switch Services
- [ ] Click on User Profile Service - loads successfully
- [ ] Click on Profile Photo Service - loads successfully
- [ ] Click on Auth Service - loads successfully
- [ ] Click on API Gateway - loads successfully

### API Documentation is Visible
- [ ] Each service shows its endpoints
- [ ] Can see request/response schemas
- [ ] Can see parameter descriptions
- [ ] Can see status codes

---

## 🔍 Testing Individual Components

### Test 1: Gateway OpenAPI Spec
```bash
# Should return the gateway's OpenAPI specification
curl http://localhost:8080/v3/api-docs | jq .
```

Expected output includes:
```json
{
  "openapi": "3.0.1",
  "info": {
    "title": "API Gateway - Microservices Aggregator",
    "version": "1.0.0"
  }
}
```

### Test 2: User Profile Service Docs (via Gateway)
```bash
# Should proxy to and return User Profile Service docs
curl http://localhost:8080/v3/api-docs/user-profile | jq .
```

Expected output includes endpoints from User Profile Service

### Test 3: Direct Service Access (Optional)
```bash
# Direct access to User Profile Service (bypass gateway)
curl http://localhost:8081/v3/api-docs | jq .
```

### Test 4: Swagger UI HTML
```bash
# Should return the Swagger UI HTML page
curl http://localhost:8080/swagger-ui/index.html | head -20
```

---

## 🐛 Troubleshooting

### Issue: Swagger UI shows 404
**Symptoms**: Page says "Not Found"
**Solution**:
1. Verify gateway is running: `curl http://localhost:8080/actuator/health`
2. Check Maven built successfully: `mvn clean install`
3. Check log contains: "Started ApiGatewayApplication"

### Issue: Dropdown is empty or missing
**Symptoms**: No services in dropdown
**Solution**:
1. Verify all microservices are running (8081, 8082, 8083)
2. Check each service has `/v3/api-docs` endpoint:
   ```bash
   curl http://localhost:8081/v3/api-docs
   curl http://localhost:8082/v3/api-docs
   curl http://localhost:8083/v3/api-docs
   ```
3. Clear browser cache and reload page
4. Restart gateway service

### Issue: Selected service shows 404
**Symptoms**: Can select service but says "Not Found"
**Solution**:
1. Verify the service is actually running
2. Check the gateway route is configured correctly:
   ```bash
   curl -v http://localhost:8080/v3/api-docs/user-profile
   ```
3. Check RewritePath filter is working:
   - Look for "RewritePath" in gateway logs
4. Restart the gateway

### Issue: Getting CORS errors in browser console
**Symptoms**: Console shows CORS related errors
**Solution**:
1. This is typically OK for Swagger UI
2. Swagger UI handles CORS automatically
3. If persists, check microservice CORS configuration
4. Ensure microservice allows requests from localhost:8080

### Issue: Services don't show correct ports
**Symptoms**: Docs show wrong port numbers in dropdowns
**Solution**:
1. Edit `application.properties`
2. Update the `springdoc.swagger-ui.urls[n].name` properties
3. Restart gateway

---

## 📝 Configuration Files

### Key File: application.properties
Located: `src/main/resources/application.properties`

Important sections:
```properties
# Routes 3-5: Swagger documentation routes (no auth required)
spring.cloud.gateway.routes[3].id=user-profile-docs
spring.cloud.gateway.routes[3].uri=http://localhost:8081
spring.cloud.gateway.routes[3].predicates[0]=Path=/v3/api-docs/user-profile/**

# Swagger UI configuration
springdoc.swagger-ui.path=/swagger-ui/index.html
springdoc.swagger-ui.urls[0].url=/v3/api-docs/user-profile
springdoc.swagger-ui.urls[0].name=User Profile Service (8081)
```

### Key File: SwaggerConfig.java
Located: `src/main/java/com/ksb/micro/api_gateway/config/SwaggerConfig.java`

Defines the OpenAPI bean for the gateway.

---

## 🎯 Success Indicators

You'll know everything is working when:

1. ✅ Browser shows Swagger UI page without errors
2. ✅ Dropdown menu shows all 4 services
3. ✅ Can click each service and see its endpoints
4. ✅ Gateway logs show routes are loading correctly
5. ✅ No 404 or 500 errors when switching services
6. ✅ All services' endpoints are visible and properly documented

---

## 📚 Documentation Files

All documentation is in the `documents/` folder:

| File | Purpose |
|------|---------|
| `SWAGGER_AGGREGATION_SETUP.md` | Detailed technical setup and architecture |
| `SWAGGER_QUICK_REFERENCE.md` | Quick access URLs and basic info |
| `SWAGGER_IMPLEMENTATION_GUIDE.md` | Complete implementation with code examples |
| `SWAGGER_IMPLEMENTATION_CHECKLIST.md` | This file - step-by-step setup |

---

## 🔧 Next Steps

1. **Build and Deploy**
   - Build the gateway: `mvn clean install`
   - Deploy to your environment

2. **Verify All Services**
   - Ensure each microservice has proper OpenAPI annotations
   - Test each service's documentation independently

3. **Customize Documentation**
   - Add @OpenAPI annotations to controllers
   - Add request/response examples
   - Document error codes and edge cases

4. **Add More Services (Optional)**
   - Follow the same pattern for new services
   - Add new routes and dropdown entries
   - Update documentation

5. **Monitor and Maintain**
   - Check logs regularly
   - Keep Swagger UI updated
   - Document any changes to the gateway configuration

---

## 💡 Pro Tips

1. **Clear Browser Cache**: If changes don't appear, press `Ctrl+Shift+Delete` and clear cache
2. **Use Debug Logging**: Add this to application.properties:
   ```properties
   logging.level.org.springframework.cloud.gateway=DEBUG
   ```
3. **Test with curl**: Before using UI, test with curl to debug issues
4. **Keep Logs Open**: Monitor gateway logs while testing
5. **Document APIs**: Add descriptions to your API methods for better documentation

---

## ✨ Summary

You now have:
- 🎯 **Centralized API Documentation**: All services in one place
- 📚 **Easy Service Selection**: Dropdown to switch between services
- 🔐 **Secure**: Docs are public, but APIs require JWT tokens
- 📖 **Professional**: Properly configured for production use
- 🚀 **Scalable**: Easy to add more services

**Enjoy your unified API documentation!** 🎉


