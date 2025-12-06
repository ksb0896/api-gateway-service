@echo off
REM Kill any existing process on port 8080
echo ===== STOPPING ANY EXISTING GATEWAY PROCESS =====
taskkill /F /FI "WINDOWTITLE eq *java*" 2>nul || echo No existing processes found
timeout /t 2 /nobreak

REM Clear the screen
cls

echo ===== STARTING API GATEWAY WITH DEBUG LOGGING =====
echo.
echo JAR Location: C:\ksb096-B\prjcts\sts\user-service\api-gateway-service\target\api_gateway-0.0.1-SNAPSHOT.jar
echo.

REM Start the application with debug logging
java -jar "C:\ksb096-B\prjcts\sts\user-service\api-gateway-service\target\api_gateway-0.0.1-SNAPSHOT.jar"

pause

