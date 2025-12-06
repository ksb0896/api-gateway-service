# API Gateway 401 Error - Debugging Guide

## Steps to Fix the 401 Error:

### Step 1: KILL ALL EXISTING PROCESSES
**CRITICAL**: You MUST stop the old running application first!

```powershell
# In PowerShell, kill all Java processes
Stop-Process -Name java -Force -ErrorAction SilentlyContinue

# Or manually in Task Manager:
# - Find any "java.exe" processes
# - Right-click > End Task
```

### Step 2: REBUILD THE APPLICATION
```powershell
cd "C:\ksb096-B\prjcts\sts\user-service\api-gateway-service"
mvn clean package -DskipTests
```

### Step 3: START THE GATEWAY WITH THE NEW JAR
```powershell
java -jar target\api_gateway-0.0.1-SNAPSHOT.jar
```

### Step 4: VERIFY THE APPLICATION STARTED
Look for these messages in the console:
```
>>> [TEST ENDPOINT] indicates the TestController is loaded
>>> [GATEWAY FILTER] indicates the AuthenticationGatewayFilterFactory is loaded
>>> [JWT] indicates JwtUtil is operational
```

### Step 5: TEST THE ENDPOINT
In a NEW terminal/Postman:

```bash
# First, get a token from port 8081
curl -X POST http://localhost:8081/auth/login -H "Content-Type: application/json" -d '{"username":"test","password":"test"}'

# Response should be: {"token":"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."}

# Then call the gateway with the token
curl -X GET http://localhost:8080/v1/banks/100/users -H "Authorization: Bearer <YOUR_TOKEN_HERE>"
```

### Step 6: CHECK THE CONSOLE LOGS
You should see output like:
```
>>> [GATEWAY FILTER] Processing request to: /v1/banks/100/users
>>> [GATEWAY FILTER] Authorization header present, length: 350
>>> [GATEWAY FILTER] Token extracted, length: 343, first 50 chars: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9eyJz...
>>> [JWT] Starting token validation...
>>> [JWT] Token Claims - Subject: test, Expiration: 2025-12-06T11:00:00Z, Current Time: 2025-12-06T10:30:00Z
>>> [JWT] ✓ Token validation SUCCESSFUL
>>> [GATEWAY FILTER] ✓ TOKEN VALID - Username: test on path: /v1/banks/100/users
```

## If You Still See 401:

### Check 1: Is the filter being invoked?
- Look for `>>> [GATEWAY FILTER]` in logs
- If NOT present: The route predicate is not matching
- If PRESENT but INVALID: Token validation is failing

### Check 2: What's the exact error?
- `Missing Authorization header` - No Bearer token sent
- `Invalid Authorization scheme` - Token doesn't start with "Bearer "
- `✗ Invalid JWT signature` - Token generated with different secret key
- `✗ Token EXPIRED` - Token expiration time has passed

### Check 3: Verify the secret key matches
Gateway secret (line 16 in JwtUtil.java):
```
"MySecureKeyForMicroservicesMustBeVeryLongAndSafe"
```

Your auth service secret (should be IDENTICAL):
```
"MySecureKeyForMicroservicesMustBeVeryLongAndSafe"
```

### Check 4: Verify the route matches
For endpoint: `http://localhost:8080/v1/banks/100/users`
Route 1 predicate: `Path=/v1/banks/\\d+/users/**` ✓ MATCHES

## Common Issues:

1. **Application still running old code**
   - Solution: Kill Java process and restart

2. **Secret key mismatch**
   - Solution: Ensure both services use EXACT same secret

3. **Token expired**
   - Solution: Generate a fresh token

4. **Wrong port**
   - Gateway: 8080
   - User Service: 8081
   - Auth Service: 8083

## Manual Testing Steps:

```powershell
# 1. Start all three services
# Terminal 1: Auth Service (8083)
# Terminal 2: User Service (8081)  
# Terminal 3: API Gateway (8080)

# 2. Get a token
$response = Invoke-RestMethod -Uri "http://localhost:8083/auth/login" -Method POST -Body '{"username":"test","password":"test"}' -ContentType "application/json"
$token = $response.token

# 3. Test directly on user service (should work)
Invoke-RestMethod -Uri "http://localhost:8081/v1/users" -Method GET -Headers @{"Authorization"="Bearer $token"}

# 4. Test through gateway (should now work too)
Invoke-RestMethod -Uri "http://localhost:8080/v1/banks/100/users" -Method GET -Headers @{"Authorization"="Bearer $token"}
```

## Final Checklist:

- [ ] All Java processes killed
- [ ] Clean rebuild completed (mvn clean package)
- [ ] New JAR started
- [ ] Logs show `>>> [GATEWAY FILTER]` messages
- [ ] Token generated from auth service
- [ ] Bearer token passed in Authorization header
- [ ] Secret key matches in both services

