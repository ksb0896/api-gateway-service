#!/bin/bash
cd "C:\ksb096-B\prjcts\sts\user-service\api-gateway-service"

echo "Killing old Java processes..."
Stop-Process -Name java -Force -ErrorAction SilentlyContinue
sleep 3

echo "Starting gateway..."
java -jar target\api_gateway-0.0.1-SNAPSHOT.jar &
GATEWAY_PID=$!

echo "Waiting for gateway to start..."
sleep 10

echo "Testing the endpoint..."
curl -X GET "http://localhost:8080/v1/banks/100/users" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9" \
  -H "Content-Type: application/json" \
  -v

echo ""
echo "Test completed. Gateway running with PID: $GATEWAY_PID"

