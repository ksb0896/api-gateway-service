# Batch Scripts for API Gateway Swagger Implementation

## Overview

This folder contains helper scripts for managing and verifying the Swagger documentation implementation in the API Gateway.

## Available Scripts

### 1. `verify_swagger.ps1` (PowerShell - Recommended for Windows)
**Purpose**: Quickly verify all services are running and Swagger is working

**How to Run**:
```powershell
# Open PowerShell in this directory and run:
.\verify_swagger.ps1
```

**What It Does**:
- ✓ Checks if all 4 services are running
- ✓ Verifies Swagger UI is accessible
- ✓ Tests all documentation endpoints
- ✓ Provides summary report
- ✓ Suggests next steps

**Output Example**:
```
  ✓ API Gateway is running on port 8080
  ✓ User Profile Service is running on port 8081
  ✓ Profile Photo Service is running on port 8082
  ✓ Auth Service is running on port 8083

  ✓ Swagger UI is accessible
  ✓ Gateway OpenAPI Spec is accessible
  ✓ User Profile Service Docs (via Gateway) is accessible
  ✓ All systems operational!
```

### 2. `verify_swagger.bat` (Batch Script for Command Prompt)
**Purpose**: Verify services and Swagger (cmd.exe version)

**How to Run**:
```bash
# Open Command Prompt in this directory and run:
verify_swagger.bat
```

**Requirements**: `curl` must be available in PATH

## Running Services Before Verification

Before running verification scripts, ensure all 4 services are started:

### Quick Start Commands

```powershell
# Terminal 1: Auth Service (Port 8083)
cd path\to\auth-service
mvn spring-boot:run

# Terminal 2: User Profile Service (Port 8081)
cd path\to\user-profile-service
mvn spring-boot:run

# Terminal 3: Profile Photo Service (Port 8082)
cd path\to\profile-photo-service
mvn spring-boot:run

# Terminal 4: API Gateway (Port 8080)
cd C:\ksb096-B\prjcts\sts\user-service\api-gateway-service
mvn spring-boot:run
```

## Manual Verification Commands

If you prefer to test manually, use these commands:

### Check Gateway Health
```powershell
curl http://localhost:8080/actuator/health
```

### Check Swagger UI
```powershell
curl http://localhost:8080/swagger-ui/index.html
```

### Check Swagger Documentation
```powershell
# Gateway Docs
curl http://localhost:8080/v3/api-docs

# User Profile Docs
curl http://localhost:8080/v3/api-docs/user-profile

# Photo Service Docs
curl http://localhost:8080/v3/api-docs/profile-photo

# Auth Service Docs
curl http://localhost:8080/v3/api-docs/auth-service
```

## Service Ports

| Service | Port | Type |
|---------|------|------|
| API Gateway | 8080 | Entry Point |
| User Profile Service | 8081 | Backend |
| Profile Photo Service | 8082 | Backend |
| Auth Service | 8083 | Backend |

## Next Steps After Verification

1. ✓ Run verification script
2. ✓ Confirm all services are accessible
3. ✓ Open http://localhost:8080/swagger-ui/index.html
4. ✓ Select services from dropdown
5. ✓ View and test API endpoints

## Troubleshooting

### Script Won't Run (PowerShell)
```powershell
# Enable script execution
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
```

### Service Shows as Not Running
1. Check if service is actually running
2. Verify correct port in service configuration
3. Check firewall settings
4. Ensure no port conflicts

### Swagger Documentation Not Loading
1. Verify gateway is running
2. Check gateway logs for errors
3. Ensure all backend services are running
4. Try accessing gateway health first: http://localhost:8080/actuator/health

## Documentation

For complete documentation, see:
- `documents/SWAGGER_DOCUMENTATION_INDEX.md` - All documentation files
- `documents/SWAGGER_IMPLEMENTATION_CHECKLIST.md` - Setup guide
- `documents/SWAGGER_QUICK_REFERENCE.md` - Quick reference
- `documents/SWAGGER_VISUAL_GUIDE_AND_COMMANDS.md` - Commands and diagrams

## Support

If scripts don't work:
1. Check PowerShell execution policy
2. Verify curl is installed
3. Check services are running on correct ports
4. Review relevant documentation files

---

**Version**: 1.0
**Last Updated**: December 2025
**Status**: ✅ Ready to Use

