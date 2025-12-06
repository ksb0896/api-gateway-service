# 📁 Batch Folder - Files Documentation

This folder contains automation scripts for managing the API Gateway application. These scripts simplify common operations like starting, stopping, and testing the gateway.

---

## 📋 Files Overview

### 1. **RESTART_GATEWAY.bat**

#### Purpose
Stops all running Java processes and restarts the API Gateway application cleanly.

#### When to Use
- When you need to restart the gateway with fresh state
- After making code changes and rebuilding the JAR
- To clear any stale connections or memory issues
- When the gateway is unresponsive

#### What It Does
```
Step 1: Kills all running Java processes using taskkill
Step 2: Waits 3 seconds for processes to fully terminate
Step 3: Displays status messages
Step 4: Prompts for further action
```

#### How to Use
```powershell
# Double-click the file in Windows Explorer
# OR run from PowerShell
.\batch\RESTART_GATEWAY.bat
```

#### Output
```
========================================
API Gateway Restart Script
========================================

Step 1: Killing all Java processes...
Java processes terminated

Step 2: Waiting 3 seconds...
```

---

### 2. **START_GATEWAY_DEBUG.cmd**

#### Purpose
Starts the API Gateway with full console output for debugging and monitoring.

#### When to Use
- When you want to see real-time application logs
- To debug issues with the gateway
- To monitor startup process
- To verify JWT token validation logs

#### What It Does
```
Step 1: Kills any existing Java processes on port 8080
Step 2: Waits 2 seconds
Step 3: Clears the console screen
Step 4: Displays gateway startup information
Step 5: Starts the JAR file with console attached
Step 6: Displays all logs in real-time
```

#### How to Use
```powershell
# Double-click the file in Windows Explorer
# OR run from Command Prompt
cd C:\ksb096-B\prjcts\sts\user-service\api-gateway-service\batch
START_GATEWAY_DEBUG.cmd
```

#### What You'll See in Console
```
===== STOPPING ANY EXISTING GATEWAY PROCESS =====

===== STARTING API GATEWAY WITH DEBUG LOGGING =====

JAR Location: C:\ksb096-B\prjcts\sts\user-service\api-gateway-service\target\api_gateway-0.0.1-SNAPSHOT.jar

[Then gateway logs will appear...]

>>> [GATEWAY FILTER] Processing request to: /v1/banks/100/users
>>> [JWT] ✓ Token validation SUCCESSFUL
>>> [GATEWAY FILTER] ✓ TOKEN VALID - Username: testuser
```

#### Key Features
- ✅ Automatic process cleanup
- ✅ Displays full path to JAR
- ✅ Shows all console output
- ✅ Easy monitoring of startup
- ✅ Pause at end for review

---

### 3. **test_gateway.ps1**

#### Purpose
PowerShell script that automates the complete process of starting the gateway and testing it with a sample API request.

#### When to Use
- To perform end-to-end testing of the gateway
- To verify gateway startup and routing
- To test JWT token validation
- For automated testing in CI/CD pipelines

#### What It Does
```
Step 1: Navigates to gateway directory
Step 2: Kills old Java processes
Step 3: Waits 3 seconds for cleanup
Step 4: Starts the gateway in background
Step 5: Waits 10 seconds for startup
Step 6: Sends test HTTP GET request with Bearer token
Step 7: Displays response with verbose output
Step 8: Shows gateway PID for reference
```

#### How to Use
```powershell
# Option 1: Run from PowerShell directly
cd C:\ksb096-B\prjcts\sts\user-service\api-gateway-service
.\batch\test_gateway.ps1

# Option 2: Run with execution policy bypass if needed
powershell -ExecutionPolicy Bypass -File .\batch\test_gateway.ps1
```

#### Test Details
- **URL**: `http://localhost:8080/v1/banks/100/users`
- **Method**: GET
- **Authentication**: Bearer token (JWT format)
- **Header**: Content-Type: application/json
- **Output**: Full response with HTTP headers and status code

#### Expected Output (Success)
```
Killing old Java processes...
Starting gateway...
Waiting for gateway to start...
Testing the endpoint...

< HTTP/1.1 200 OK
< Content-Type: application/json
< ...

{ "userId": 100, "userName": "user", ... }

Test completed. Gateway running with PID: 12345
```

#### Expected Output (Failure Cases)
```
# Connection refused
curl: (7) Failed to connect to localhost port 8080

# Invalid token
< HTTP/1.1 401 Unauthorized

# Route not matched
< HTTP/1.1 404 Not Found
```

---

## 🚀 Quick Start Guide

### Scenario 1: Start Gateway with Full Debug Output
```powershell
.\batch\START_GATEWAY_DEBUG.cmd
```

### Scenario 2: Restart Gateway (Kill and Restart)
```powershell
.\batch\RESTART_GATEWAY.bat
```

### Scenario 3: Test Gateway Completely
```powershell
.\batch\test_gateway.ps1
```

### Scenario 4: Manual Start (No Script)
```powershell
Stop-Process -Name java -Force -ErrorAction SilentlyContinue
Start-Sleep -Seconds 3
java -jar target\api_gateway-0.0.1-SNAPSHOT.jar
```

---

## 📊 File Comparison

| Feature | RESTART_GATEWAY.bat | START_GATEWAY_DEBUG.cmd | test_gateway.ps1 |
|---------|-------------------|------------------------|------------------|
| **Type** | Batch (.bat) | Command (.cmd) | PowerShell (.ps1) |
| **Kills Java** | ✅ Yes | ✅ Yes | ✅ Yes |
| **Starts Gateway** | ❌ No | ✅ Yes | ✅ Yes |
| **Shows Logs** | ❌ Limited | ✅ Full | ✅ Full |
| **Tests API** | ❌ No | ❌ No | ✅ Yes |
| **Background Process** | N/A | ❌ Foreground | ✅ Background |
| **Best Use Case** | Cleanup | Development | Testing |

---

## 🔧 System Requirements

### For All Scripts
- Windows OS (10 or later)
- Java 17+ installed
- JAR file built: `target/api_gateway-0.0.1-SNAPSHOT.jar`
- Port 8080 available (or modify scripts)

### For PowerShell Script (test_gateway.ps1)
- PowerShell 5.0+
- curl command available (usually included)
- Network access to localhost:8080

### For Batch Scripts
- Command Prompt or PowerShell
- taskkill utility (built-in)

---

## 🐛 Troubleshooting

### Issue: "Port 8080 already in use"
**Solution**: Run RESTART_GATEWAY.bat first to kill old processes

### Issue: Script won't execute (PowerShell)
**Solution**: 
```powershell
# Set execution policy
Set-ExecutionPolicy -ExecutionPolicy Bypass -Scope Process
.\batch\test_gateway.ps1
```

### Issue: "File not found" error
**Solution**: Make sure you're in the correct directory
```powershell
cd C:\ksb096-B\prjcts\sts\user-service\api-gateway-service
```

### Issue: Gateway doesn't start
**Solution**: 
1. Check if JAR file exists: `dir target\api_gateway-0.0.1-SNAPSHOT.jar`
2. Verify Java is installed: `java -version`
3. Check port 8080: `netstat -ano | findstr :8080`

---

## 📝 Notes

### Modifying Scripts

**To change port number:**
Edit the scripts and replace `8080` with your desired port:
```powershell
# Example for port 9090
curl -X GET "http://localhost:9090/v1/banks/100/users" \
```

**To change test endpoint:**
Edit test_gateway.ps1 and modify the URL:
```powershell
curl -X GET "http://localhost:8080/YOUR_PATH_HERE" \
```

**To change JAR file path:**
Update the hardcoded path in START_GATEWAY_DEBUG.cmd:
```batch
java -jar "C:\path\to\your\jar.jar"
```

---

## 🎯 Best Practices

1. **Always use RESTART_GATEWAY.bat before testing** - Ensures clean state
2. **Use START_GATEWAY_DEBUG.cmd for development** - See all logs in real-time
3. **Use test_gateway.ps1 for validation** - Confirms end-to-end functionality
4. **Keep scripts in batch folder** - Organized and easy to find
5. **Don't modify paths in scripts** - Keep them relative to project structure

---

## 📞 Common Commands Reference

### Check if Java is running
```powershell
Get-Process java
```

### Kill all Java processes
```powershell
Stop-Process -Name java -Force
```

### Check if port 8080 is in use
```powershell
netstat -ano | findstr :8080
```

### View gateway logs in real-time
```powershell
Get-Content -Path "logs\gateway.log" -Wait
```

### Test gateway endpoint
```powershell
curl -X GET http://localhost:8080/v1/banks/100/users \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

## ✅ Summary

| Script | Purpose | When to Use |
|--------|---------|------------|
| **RESTART_GATEWAY.bat** | Clean restart | After code changes, before testing |
| **START_GATEWAY_DEBUG.cmd** | Development/debugging | Active development, troubleshooting |
| **test_gateway.ps1** | Automated testing | Validation, CI/CD, quick verification |

All scripts are designed to make gateway management simple and efficient! 🚀

