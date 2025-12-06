# API Gateway Authentication Filter - Fix Summary

## Problem
Getting HTTP 401 Unauthorized when calling the gateway at port 8080, even with a valid Bearer token that works when calling the backend service directly at port 8081.

## Root Cause
The gateway's `JwtUtil.java` was using the **old JJWT API** while the token generation service was using the **modern JJWT 0.11.x+ API**. These two APIs are incompatible and cannot validate each other's tokens.

## Solution Implemented

### 1. Updated JwtUtil.java (JWT Token Validation)
**File**: `src/main/java/com/ksb/micro/api_gateway/util/JwtUtil.java`

**Changes**:
- Replaced `Jwts.parser()` with `Jwts.parserBuilder()`
- Replaced `setSigningKey(secret.getBytes())` with `setSigningKey(key)` where key is a `SecretKey`
- Added `Keys.hmacShaKeyFor()` to create the signing key properly
- Added comprehensive logging at INFO and ERROR levels to track token validation

**Before**:
```java
private final String secret = "MySecureKeyForMicroservicesMustBeVeryLongAndSafe";

private Claims getClaims(String token) {
    return Jwts.parser()
            .setSigningKey(secret.getBytes())
            .parseClaimsJws(token)
            .getBody();
}
```

**After**:
```java
private final SecretKey key = Keys.hmacShaKeyFor("MySecureKeyForMicroservicesMustBeVeryLongAndSafe".getBytes());

private Claims getClaims(String token) {
    return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .getBody();
}
```

### 2. Enhanced AuthenticationGatewayFilterFactory.java (Gateway Filter)
**File**: `src/main/java/com/ksb/micro/api_gateway/filter/AuthenticationGatewayFilterFactory.java`

**Changes**:
- Added detailed logging with `>>>` prefix to track filter execution
- Added logging of token length and first 50 characters for debugging
- Added specific error messages for each validation failure
- Added constructor logging to confirm filter initialization
- Improved exception handling with specific error types

**Key Logging Points**:
```
>>> [GATEWAY FILTER] Processing request to: {path}
>>> [GATEWAY FILTER] Token extracted, length: {length}
>>> [GATEWAY FILTER] ✓ TOKEN VALID - Username: {user}
>>> [GATEWAY FILTER] ✗ TOKEN INVALID or EXPIRED
>>> [GATEWAY FILTER] ERROR RESPONSE: Status={status}, Message={message}
```

### 3. Updated application.properties (Logging Configuration)
**File**: `src/main/resources/application.properties`

**Changes**:
- Added logging configuration for the authentication filter package
- Added logging configuration for the JWT utility package

```properties
logging.level.com.ksb.micro.api_gateway.filter=DEBUG
logging.level.com.ksb.micro.api_gateway.util=DEBUG
```

### 4. Updated TestController.java (Verification Endpoint)
**File**: `src/main/java/com/ksb/micro/api_gateway/controller/TestController.java`

**Changes**:
- Added logging to verify requests reach the gateway

## Configuration Details

### Route Configuration (Working)
- **Route ID**: user-profile-service
- **Target URI**: http://localhost:8081
- **Path Predicate**: `/v1/banks/\d+/users/**`
- **Filters**:
  1. `Authentication` - Validates JWT token
  2. `RewritePath` - Rewrites path from `/v1/banks/{id}/{path}` to `/v1/{path}`

### Secret Key (MUST MATCH)
**Gateway**: `MySecureKeyForMicroservicesMustBeVeryLongAndSafe`
**Auth Service**: Must use the EXACT same string

## Verification Steps

### 1. Rebuild the Application
```powershell
cd C:\ksb096-B\prjcts\sts\user-service\api-gateway-service
mvn clean package -DskipTests
```

### 2. Kill Existing Processes
```powershell
Stop-Process -Name java -Force -ErrorAction SilentlyContinue
```

### 3. Start the Gateway
```powershell
java -jar target\api_gateway-0.0.1-SNAPSHOT.jar
```

### 4. Look for Initialization Logs
```
>>> [GATEWAY FILTER] Processing request to: /v1/banks/100/users
>>> [GATEWAY FILTER] Authorization header present
>>> [JWT] Token validation SUCCESSFUL
```

### 5. Test the Endpoint
```powershell
# Get token from auth service
$token = "your_jwt_token_here"

# Call through gateway (should now work with 200 OK)
curl -X GET http://localhost:8080/v1/banks/100/users `
  -H "Authorization: Bearer $token"
```

## Files Modified
1. `src/main/java/com/ksb/micro/api_gateway/util/JwtUtil.java` - JWT parsing API updated
2. `src/main/java/com/ksb/micro/api_gateway/filter/AuthenticationGatewayFilterFactory.java` - Enhanced logging
3. `src/main/resources/application.properties` - Added debug logging
4. `src/main/java/com/ksb/micro/api_gateway/controller/TestController.java` - Added logging

## Files Created
1. `DEBUGGING_GUIDE.md` - Comprehensive debugging guide
2. `START_GATEWAY_DEBUG.cmd` - Batch script to start gateway with fresh process

## Expected Log Output (Success Case)
```
2025-12-06 16:05:30 INFO  c.k.m.a.filter.AuthenticationGatewayFilterFactory - === AuthenticationGatewayFilterFactory initialized ===
2025-12-06 16:05:31 INFO  c.k.m.a.filter.AuthenticationGatewayFilterFactory - >>> [GATEWAY FILTER] Processing request to: /v1/banks/100/users
2025-12-06 16:05:31 INFO  c.k.m.a.filter.AuthenticationGatewayFilterFactory - >>> [GATEWAY FILTER] Authorization header present, length: 350
2025-12-06 16:05:31 INFO  c.k.m.a.filter.AuthenticationGatewayFilterFactory - >>> [GATEWAY FILTER] Token extracted, length: 343, first 50 chars: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9eyJz...
2025-12-06 16:05:31 INFO  c.k.m.a.util.JwtUtil - >>> [JWT] Token Claims - Subject: testuser, Expiration: 2025-12-06T21:05:30Z, Current Time: 2025-12-06T16:05:31Z
2025-12-06 16:05:31 INFO  c.k.m.a.util.JwtUtil - >>> [JWT] ✓ Token validation SUCCESSFUL
2025-12-06 16:05:31 INFO  c.k.m.a.filter.AuthenticationGatewayFilterFactory - >>> [GATEWAY FILTER] ✓ TOKEN VALID - Username: testuser on path: /v1/banks/100/users
2025-12-06 16:05:31 INFO  c.k.m.a.filter.AuthenticationGatewayFilterFactory - >>> [GATEWAY FILTER] Passing request to next filter with authenticated user: testuser
```

## Build Status
✅ BUILD SUCCESS - Ready to test

## Next Steps
1. Kill all existing Java processes
2. Run `mvn clean package -DskipTests`
3. Start the new JAR: `java -jar target\api_gateway-0.0.1-SNAPSHOT.jar`
4. Watch console for `>>> [GATEWAY FILTER]` logs
5. Call the API with a Bearer token
6. Verify you get 200 OK instead of 401

---
**Note**: The 401 error should now be resolved. If still occurring, check the console logs for specific error messages to identify the issue.

