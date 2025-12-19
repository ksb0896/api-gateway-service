@echo off
REM ============================================================================
REM Swagger Implementation - Quick Start Script for Windows
REM This script helps verify the Swagger implementation
REM ============================================================================

setlocal enabledelayedexpansion

echo.
echo ============================================================================
echo   API Gateway - Swagger Documentation Aggregation
echo   Quick Start & Verification Script
echo ============================================================================
echo.

REM Check if all services are running
echo Checking service status...
echo.

echo [1/4] Checking API Gateway (8080)...
for /f "tokens=*" %%i in ('curl -s -o /dev/null -w "%%{http_code}" http://localhost:8080/actuator/health 2^>nul') do (
    if "%%i"=="200" (
        echo   ✓ API Gateway is running on port 8080
    ) else (
        echo   ✗ API Gateway is NOT running on port 8080
    )
)

echo.
echo [2/4] Checking User Profile Service (8081)...
for /f "tokens=*" %%i in ('curl -s -o /dev/null -w "%%{http_code}" http://localhost:8081/actuator/health 2^>nul') do (
    if "%%i"=="200" (
        echo   ✓ User Profile Service is running on port 8081
    ) else (
        echo   ✗ User Profile Service is NOT running on port 8081
    )
)

echo.
echo [3/4] Checking Profile Photo Service (8082)...
for /f "tokens=*" %%i in ('curl -s -o /dev/null -w "%%{http_code}" http://localhost:8082/actuator/health 2^>nul') do (
    if "%%i"=="200" (
        echo   ✓ Profile Photo Service is running on port 8082
    ) else (
        echo   ✗ Profile Photo Service is NOT running on port 8082
    )
)

echo.
echo [4/4] Checking Auth Service (8083)...
for /f "tokens=*" %%i in ('curl -s -o /dev/null -w "%%{http_code}" http://localhost:8083/actuator/health 2^>nul') do (
    if "%%i"=="200" (
        echo   ✓ Auth Service is running on port 8083
    ) else (
        echo   ✗ Auth Service is NOT running on port 8083
    )
)

echo.
echo ============================================================================
echo.
echo Testing Swagger Documentation...
echo.

echo [1/4] Testing Gateway Swagger UI...
for /f "tokens=*" %%i in ('curl -s -o /dev/null -w "%%{http_code}" http://localhost:8080/swagger-ui/index.html 2^>nul') do (
    if "%%i"=="200" (
        echo   ✓ Swagger UI is accessible
    ) else (
        echo   ✗ Swagger UI is NOT accessible (Status: %%i)
    )
)

echo.
echo [2/4] Testing Gateway OpenAPI Spec...
for /f "tokens=*" %%i in ('curl -s -o /dev/null -w "%%{http_code}" http://localhost:8080/v3/api-docs 2^>nul') do (
    if "%%i"=="200" (
        echo   ✓ Gateway OpenAPI spec is accessible
    ) else (
        echo   ✗ Gateway OpenAPI spec is NOT accessible (Status: %%i)
    )
)

echo.
echo [3/4] Testing User Profile Service Docs (via Gateway)...
for /f "tokens=*" %%i in ('curl -s -o /dev/null -w "%%{http_code}" http://localhost:8080/v3/api-docs/user-profile 2^>nul') do (
    if "%%i"=="200" (
        echo   ✓ User Profile Service docs are accessible
    ) else (
        echo   ✗ User Profile Service docs are NOT accessible (Status: %%i)
    )
)

echo.
echo [4/4] Testing Auth Service Docs (via Gateway)...
for /f "tokens=*" %%i in ('curl -s -o /dev/null -w "%%{http_code}" http://localhost:8080/v3/api-docs/auth-service 2^>nul') do (
    if "%%i"=="200" (
        echo   ✓ Auth Service docs are accessible
    ) else (
        echo   ✗ Auth Service docs are NOT accessible (Status: %%i)
    )
)

echo.
echo ============================================================================
echo.
echo ✓ Verification Complete!
echo.
echo Next Steps:
echo   1. Open: http://localhost:8080/swagger-ui/index.html
echo   2. Check dropdown for all 4 services
echo   3. Select each service to view documentation
echo   4. See SWAGGER_DOCUMENTATION_INDEX.md for detailed guides
echo.
echo ============================================================================
echo.

pause

