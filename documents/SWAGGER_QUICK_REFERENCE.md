# Quick Reference: Swagger Documentation Access

## 🎯 Quick Access URLs

| Service | URL | Port |
|---------|-----|------|
| **Swagger UI (All Services)** | http://localhost:8080/swagger-ui/index.html | 8080 |
| User Profile Service Docs | http://localhost:8080/v3/api-docs/user-profile | 8080 → 8081 |
| Profile Photo Service Docs | http://localhost:8080/v3/api-docs/profile-photo | 8080 → 8082 |
| Auth Service Docs | http://localhost:8080/v3/api-docs/auth-service | 8080 → 8083 |
| Gateway Docs | http://localhost:8080/v3/api-docs | 8080 |

## 📋 Service Mapping

```
Gateway (8080)
├── User Profile Service (8081)
│   └── Routes: /v1/banks/*/users, /v1/banks/*/users/**
│   └── Docs: /v3/api-docs/user-profile
│
├── Profile Photo Service (8082)
│   └── Routes: /v1/banks/*/users/*/photo/**
│   └── Docs: /v3/api-docs/profile-photo
│
└── Auth Service (8083)
    └── Routes: /auth/**
    └── Docs: /v3/api-docs/auth-service
```

## 🚀 How to Use

### 1. Open Swagger UI
```
Navigate to: http://localhost:8080/swagger-ui/index.html
```

### 2. Select Service from Dropdown
The dropdown will show:
- ✅ User Profile Service (8081) - Default
- ✅ Profile Photo Service (8082)
- ✅ Auth Service (8083)
- ✅ API Gateway (8080)

### 3. View & Test APIs
- Browse all endpoints
- View schemas and parameters
- Execute test requests

## 🔐 Authentication Note

- ✅ Swagger docs are **public** (no auth required to view docs)
- ✅ Docs accessed via `/v3/api-docs/**` (documentation routes)
- ⚠️ Actual API calls through `/v1/**` paths require JWT token

## ⚙️ Configuration Summary

### Routes Configuration (application.properties)
```properties
# Routes 0-2: API Routes
Route[0]: Profile Photo Service (8082)
Route[1]: User Profile Service (8081)  
Route[2]: Auth Service (8083)

# Routes 3-5: Documentation Routes
Route[3]: User Profile Docs → /v3/api-docs/user-profile
Route[4]: Profile Photo Docs → /v3/api-docs/profile-photo
Route[5]: Auth Service Docs → /v3/api-docs/auth-service
```

### Swagger UI Configuration
```properties
springdoc.swagger-ui.urls[0] = User Profile Service (8081)
springdoc.swagger-ui.urls[1] = Profile Photo Service (8082)
springdoc.swagger-ui.urls[2] = Auth Service (8083)
springdoc.swagger-ui.urls[3] = API Gateway (8080)
```

## ✅ Verification Checklist

- [ ] All 3 microservices are running (8081, 8082, 8083)
- [ ] API Gateway is running (8080)
- [ ] No port conflicts
- [ ] Gateway logs show DEBUG level enabled
- [ ] Accessing http://localhost:8080/swagger-ui/index.html works
- [ ] Dropdown shows all 4 services
- [ ] Can switch between services
- [ ] Each service shows its endpoints

## 🔍 Debugging

### Check Gateway Logs
```bash
# Enable DEBUG logging for gateway
logging.level.org.springframework.cloud.gateway=DEBUG
```

### Test Documentation Route
```bash
# Test if docs are accessible
curl http://localhost:8080/v3/api-docs/user-profile
curl http://localhost:8080/v3/api-docs/profile-photo
curl http://localhost:8080/v3/api-docs/auth-service
```

### Verify Service Health
```bash
# Check if each service is running
curl http://localhost:8081/actuator/health
curl http://localhost:8082/actuator/health
curl http://localhost:8083/actuator/health
```

## 📚 Files Modified/Created

1. ✅ `SwaggerConfig.java` - Created (OpenAPI bean configuration)
2. ✅ `application.properties` - Updated (Swagger routes and properties)
3. ✅ `SWAGGER_AGGREGATION_SETUP.md` - Created (Detailed documentation)
4. ✅ `SWAGGER_QUICK_REFERENCE.md` - This file

## 🎓 Next Steps

1. **Start all services** in this order:
   - Auth Service (8083)
   - User Profile Service (8081)
   - Profile Photo Service (8082)
   - API Gateway (8080)

2. **Access Swagger**: http://localhost:8080/swagger-ui/index.html

3. **Test APIs**: Select a service and try endpoint calls

4. **Document APIs**: Each service should have proper @OpenAPI annotations

## 📞 Support

For issues:
- Check DEBUG logs
- Verify all services running
- Ensure correct port mappings
- See SWAGGER_AGGREGATION_SETUP.md for detailed troubleshooting

