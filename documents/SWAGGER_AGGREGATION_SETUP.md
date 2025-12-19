# Swagger/OpenAPI Documentation Setup for API Gateway

## Overview
This API Gateway now aggregates Swagger documentation from all microservices into a single, unified entry point. You can access the complete API documentation for all services from one location.

## Access Points

### Main Entry Point
```
http://localhost:8080/swagger-ui/index.html
```

### Individual Service Documentation (via Gateway)
- **User Profile Service**: http://localhost:8080/v3/api-docs/user-profile
- **Profile Photo Service**: http://localhost:8080/v3/api-docs/profile-photo
- **Auth Service**: http://localhost:8080/v3/api-docs/auth-service
- **API Gateway**: http://localhost:8080/v3/api-docs

## Features

✅ **Unified Documentation**: Access all microservice APIs from a single Swagger UI interface
✅ **Service Dropdown**: Select different services from the Swagger UI dropdown menu
✅ **Gateway Authentication**: All requests pass through the Authentication filter for JWT validation
✅ **Real-time Proxying**: Documentation is proxied through the gateway in real-time
✅ **Organized APIs**: Each service is clearly labeled with its port number

## How It Works

### Gateway Routes for Documentation
The gateway has configured routes specifically for Swagger documentation:

1. **User Profile Service Docs** (Route 3)
   - Path: `/v3/api-docs/user-profile/**`
   - Proxy to: `http://localhost:8081`
   - Rewrite: `/v3/api-docs/user-profile` → `/v3/api-docs`

2. **Profile Photo Service Docs** (Route 4)
   - Path: `/v3/api-docs/profile-photo/**`
   - Proxy to: `http://localhost:8082`
   - Rewrite: `/v3/api-docs/profile-photo` → `/v3/api-docs`

3. **Auth Service Docs** (Route 5)
   - Path: `/v3/api-docs/auth-service/**`
   - Proxy to: `http://localhost:8083`
   - Rewrite: `/v3/api-docs/auth-service` → `/v3/api-docs`

### Swagger UI Configuration
The Swagger UI is configured to display all available services:
- Dropdown menu with all microservices
- Each service is clickable and shows its OpenAPI specification
- User Profile Service is set as the primary/default service

## Configuration Details

### Dependencies
```xml
<!-- WebFlux UI for reactive Spring Cloud Gateway -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webflux-ui</artifactId>
    <version>2.5.0</version>
</dependency>

<!-- Alternative WebMVC UI (for regular Spring Web) -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.5.0</version>
</dependency>
```

### Application Properties
Located in: `src/main/resources/application.properties`

Key properties:
- `springdoc.api-docs.path=/v3/api-docs` - OpenAPI JSON endpoint
- `springdoc.swagger-ui.path=/swagger-ui/index.html` - Swagger UI path
- `springdoc.swagger-ui.urls[n].url` - List of service documentation URLs
- `springdoc.swagger-ui.urls[n].name` - Display name for each service

### SwaggerConfig Class
Located in: `src/main/java/com/ksb/micro/api_gateway/config/SwaggerConfig.java`

This configuration class:
- Defines the gateway's OpenAPI information
- Sets up the gateway server information
- Describes all connected microservices

## Usage Guide

### Step 1: Start All Services
Ensure all microservices are running:
- API Gateway: Port 8080
- User Profile Service: Port 8081
- Profile Photo Service: Port 8082
- Auth Service: Port 8083

### Step 2: Access Swagger UI
Navigate to: `http://localhost:8080/swagger-ui/index.html`

### Step 3: Select a Service
Use the dropdown menu to switch between services:
```
Dropdown shows:
- User Profile Service (8081) [Default]
- Profile Photo Service (8082)
- Auth Service (8083)
- API Gateway (8080)
```

### Step 4: Explore APIs
- View all available endpoints for the selected service
- See request/response schemas
- Try out API calls directly from Swagger UI

## Important Notes

### JWT Authentication
- Swagger documentation routes **DO NOT** require authentication
- They are accessed via `/v3/api-docs/**` paths without the Authentication filter
- The actual API calls through the gateway **WILL** require a valid JWT token

### Service-to-Gateway Communication
```
Swagger UI Request Flow:
┌─────────────────┐
│  Swagger UI     │
│  (Port 8080)    │
└────────┬────────┘
         │
         ▼
┌─────────────────────────────────┐
│  API Gateway (8080)             │
│  - Routes requests              │
│  - Proxies /v3/api-docs/**      │
└────────┬────────────────────────┘
         │
         ├─────────────────────────────────────────┐
         │                                         │
         ▼                                         ▼
    ┌─────────────┐                         ┌──────────────┐
    │ User Prof   │                         │ Auth Service │
    │ Service 8081│                         │ Service 8083 │
    └─────────────┘                         └──────────────┘
```

## Troubleshooting

### Issue: Swagger UI shows 404 for service docs
**Solution**: 
1. Verify all services are running on their respective ports
2. Check that service has `/v3/api-docs` endpoint accessible
3. Verify gateway routes are configured correctly
4. Check logs: `logging.level.org.springframework.cloud.gateway=DEBUG`

### Issue: Service documentation not updating
**Solution**:
1. Restart the gateway service
2. Clear browser cache
3. Verify the service's Swagger configuration

### Issue: Can't execute requests from Swagger UI
**Solution**:
1. Ensure JWT token is valid when testing real API endpoints (not docs)
2. Some endpoints may require authentication - see Authentication Filter logs
3. Check CORS settings on downstream services

## Related Configuration Files

1. **application.properties** - Main gateway configuration with routes and Swagger settings
2. **SwaggerConfig.java** - Spring bean configuration for OpenAPI
3. **GatewaySecurityConfig.java** - Security configuration (excludes /v3/api-docs from auth)
4. **AuthenticationGatewayFilterFactory.java** - JWT token validation filter

## Next Steps

- Add more services to the gateway and their documentation routes
- Customize the Swagger UI theme and appearance
- Add API versioning if needed
- Document business logic and request/response examples in service controllers

## References

- [SpringDoc OpenAPI Documentation](https://springdoc.org/)
- [Spring Cloud Gateway Documentation](https://spring.io/projects/spring-cloud-gateway)
- [OpenAPI 3.0 Specification](https://spec.openapis.org/oas/v3.0.3)

