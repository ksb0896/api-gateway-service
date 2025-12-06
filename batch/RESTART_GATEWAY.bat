@echo off
REM This script stops all Java processes and restarts the API Gateway

echo.
echo ========================================
echo API Gateway Restart Script
echo ========================================
echo.
echo Step 1: Killing all Java processes...
echo.

taskkill /F /IM java.exe 2>nul
if errorlevel 1 (
    echo No Java processes found (OK)
) else (
    echo Java processes terminated
)

echo.
echo Step 2: Waiting 3 seconds...
timeout /t 3 /nobreak

echo.
echo Step 3: Starting API Gateway...
echo.
echo JAR: target\api_gateway-0.0.1-SNAPSHOT.jar
echo Port: 8080
echo.
echo Logs will show below:
echo ============================================
echo.

cd /d "C:\ksb096-B\prjcts\sts\user-service\api-gateway-service"
java -jar target\api_gateway-0.0.1-SNAPSHOT.jar

pause

