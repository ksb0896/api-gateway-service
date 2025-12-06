# COMPLETE TESTING CHECKLIST

## Phase 1: Preparation ✓
- [x] Identified root cause: JWT API incompatibility
- [x] Updated JwtUtil.java to modern API
- [x] Enhanced AuthenticationGatewayFilterFactory logging
- [x] Updated application.properties logging config
- [x] Built new JAR: `mvn clean package -DskipTests`
- [x] All files compiled successfully

## Phase 2: Pre-Deployment (DO THIS FIRST!)
- [ ] **BACKUP current state** (optional but recommended)
- [ ] Ensure all 3 services are configured to start:
  - [ ] Auth Service (port 8083) with secret: "MySecureKeyForMicroservicesMustBeVeryLongAndSafe"
  - [ ] User Service (port 8081) 
  - [ ] API Gateway (port 8080) with updated code
- [ ] Verify JAR file exists: `target/api_gateway-0.0.1-SNAPSHOT.jar`

## Phase 3: Stopping Old Instance (CRITICAL!)
- [ ] Open Task Manager or use PowerShell
- [ ] Kill ALL Java processes: `Stop-Process -Name java -Force`
- [ ] Wait 3 seconds
- [ ] Verify no Java processes running: `Get-Process java -ErrorAction SilentlyContinue`
  - Should return: (nothing/error is good)

## Phase 4: Starting New Instance
- [ ] Navigate to project: `cd C:\ksb096-B\prjcts\sts\user-service\api-gateway-service`
- [ ] Start gateway: `java -jar target\api_gateway-0.0.1-SNAPSHOT.jar`
- [ ] Wait 10 seconds for startup
- [ ] Look for this in console:
  ```
  === AuthenticationGatewayFilterFactory initialized ===
  ```
- [ ] Confirm log level messages appear:
  - Application started successfully
  - No FATAL errors

## Phase 5: Test Token Generation
- [ ] Open NEW terminal/PowerShell window
- [ ] Generate token from auth service:
  ```powershell
  $response = Invoke-RestMethod `
    -Uri "http://localhost:8083/auth/login" `
    -Method POST `
    -Body '{"username":"testuser","password":"password"}' `
    -ContentType "application/json"
  
  $token = $response.token
  echo $token
  ```
- [ ] Verify you got a token (long string starting with "eyJ")
- [ ] Copy token for next test

## Phase 6: Test Direct Backend Service (Sanity Check)
- [ ] Call User Service directly on 8081:
  ```powershell
  $response = Invoke-RestMethod `
    -Uri "http://localhost:8081/v1/users" `
    -Method GET `
    -Headers @{"Authorization"="Bearer $token"}
  
  $response.StatusCode  # Should be 200
  ```
- [ ] **Expected**: HTTP 200 OK with user data
- [ ] If not 200: Token generation might be failing

## Phase 7: Test Gateway (THE MAIN TEST!)
- [ ] Call API Gateway on 8080 (through filter):
  ```powershell
  $response = Invoke-RestMethod `
    -Uri "http://localhost:8080/v1/banks/100/users" `
    -Method GET `
    -Headers @{"Authorization"="Bearer $token"}
  
  $response.StatusCode  # Check response code
  $response            # Check content
  ```

- [ ] **EXPECTED SUCCESS RESPONSE**:
  - [ ] HTTP Status Code: **200** (NOT 401)
  - [ ] Contains user data
  - [ ] No error messages

- [ ] **CHECK GATEWAY CONSOLE FOR LOGS**:
  ```
  >>> [GATEWAY FILTER] Processing request to: /v1/banks/100/users
  >>> [GATEWAY FILTER] Authorization header present, length: [number]
  >>> [GATEWAY FILTER] Token extracted, length: [number]
  >>> [JWT] Token Claims - Subject: testuser, Expiration: [date]
  >>> [JWT] ✓ Token validation SUCCESSFUL
  >>> [GATEWAY FILTER] ✓ TOKEN VALID - Username: testuser
  >>> [GATEWAY FILTER] Passing request to next filter...
  ```

## Phase 8: Validation
### SUCCESS Indicators ✅
- [ ] Status code is **200** (not 401, 403, 500, etc)
- [ ] Response contains expected data
- [ ] Gateway console shows `>>> [GATEWAY FILTER]` messages
- [ ] Gateway console shows `✓ TOKEN VALID`
- [ ] No errors in gateway console

### FAILURE Indicators ❌
- [ ] Status code is **401**
- [ ] Status code is **500** with error
- [ ] No `>>> [GATEWAY FILTER]` logs
- [ ] Gateway console shows `❌` or error messages

## Phase 9: Troubleshooting (if still failing)

### Symptom: Still getting 401
- [ ] Check: Did you kill the old Java process?
  - `Stop-Process -Name java -Force` again
  - Wait 5 seconds
  - Start again
- [ ] Check: Did you rebuild the JAR?
  - `mvn clean package -DskipTests`
- [ ] Check: Is it using the new JAR?
  - File timestamp should be recent

### Symptom: No `>>> [GATEWAY FILTER]` logs
- [ ] The filter is not being invoked
- [ ] Check: Is the path matching?
  - You're calling: `/v1/banks/100/users`
  - Route pattern: `/v1/banks/\d+/users/**` ✓ matches
- [ ] Check: Is the request hitting the gateway?
  - Try: `curl http://localhost:8080/auth/test`
  - Should return: "Gateway OK"
  - If not: Gateway is not running

### Symptom: `Invalid JWT signature`
- [ ] Secret key mismatch
- [ ] Check: Auth service secret
- [ ] Compare with: Gateway secret in JwtUtil.java
- [ ] Both should be: `"MySecureKeyForMicroservicesMustBeVeryLongAndSafe"`
- [ ] Regenerate token if you changed the secret

### Symptom: `Token EXPIRED`
- [ ] Token creation time is too old
- [ ] Solution: Generate a fresh token

## Phase 10: Final Confirmation

After successful test:

- [ ] Gateway returns 200 OK (not 401)
- [ ] User data is returned correctly
- [ ] Console shows success logs
- [ ] No error messages
- [ ] Can repeat test multiple times

## Documentation to Reference

- [ ] QUICK_REFERENCE.txt - Quick commands
- [ ] DEBUGGING_GUIDE.md - Full debugging guide
- [ ] FIX_SUMMARY.md - What was changed
- [ ] VISUAL_EXPLANATION.md - How it works
- [ ] IMMEDIATE_ACTION_PLAN.md - Quick action plan

## FINAL STATUS

✅ Code: UPDATED and COMPILED
✅ JAR: BUILT and READY
✅ Logs: ENHANCED for debugging
✅ Documentation: COMPLETE

🚀 **READY FOR DEPLOYMENT**

---

## Success Criteria

| Test | Expected | Status |
|------|----------|--------|
| Call Gateway with valid token | 200 OK | [ ] Pass |
| Verify logs in console | Shows >>> logs | [ ] Pass |
| User data returned | JSON response | [ ] Pass |
| No 401 errors | Clean execution | [ ] Pass |

---

## Next Steps After Verification

1. ✅ Confirm all tests pass
2. ✅ Document any differences from expected
3. ✅ Share success/failure logs if needed
4. ✅ Consider production deployment

---

**Status**: 🟢 READY FOR TESTING
**Last Updated**: 2025-12-06

