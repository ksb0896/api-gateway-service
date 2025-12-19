# ============================================================================
# Swagger Implementation - Quick Start & Verification Script (PowerShell)
# Run this script to verify the Swagger implementation
# Usage: .\verify_swagger.ps1
# ============================================================================

Write-Host ""
Write-Host "============================================================================" -ForegroundColor Cyan
Write-Host "  API Gateway - Swagger Documentation Aggregation" -ForegroundColor Cyan
Write-Host "  Quick Start & Verification Script" -ForegroundColor Cyan
Write-Host "============================================================================" -ForegroundColor Cyan
Write-Host ""

# Function to test service health
function Test-ServiceHealth {
    param(
        [string]$ServiceName,
        [string]$Port,
        [string]$HealthEndpoint
    )

    $url = "http://localhost:$Port$HealthEndpoint"
    try {
        $response = Invoke-WebRequest -Uri $url -Method Get -ErrorAction SilentlyContinue
        if ($response.StatusCode -eq 200) {
            Write-Host "  ✓ $ServiceName is running on port $Port" -ForegroundColor Green
            return $true
        }
    }
    catch {
        Write-Host "  ✗ $ServiceName is NOT running on port $Port" -ForegroundColor Red
        return $false
    }
}

# Check if all services are running
Write-Host "Checking service status..." -ForegroundColor Yellow
Write-Host ""

$results = @()
$results += Test-ServiceHealth "API Gateway" "8080" "/actuator/health"
$results += Test-ServiceHealth "User Profile Service" "8081" "/actuator/health"
$results += Test-ServiceHealth "Profile Photo Service" "8082" "/actuator/health"
$results += Test-ServiceHealth "Auth Service" "8083" "/actuator/health"

Write-Host ""
Write-Host "============================================================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Testing Swagger Documentation..." -ForegroundColor Yellow
Write-Host ""

# Function to test Swagger endpoints
function Test-SwaggerEndpoint {
    param(
        [string]$EndpointName,
        [string]$Url
    )

    try {
        $response = Invoke-WebRequest -Uri $url -Method Get -ErrorAction SilentlyContinue
        if ($response.StatusCode -eq 200) {
            Write-Host "  ✓ $EndpointName is accessible" -ForegroundColor Green
            return $true
        }
    }
    catch {
        Write-Host "  ✗ $EndpointName is NOT accessible (Status: $($_.Exception.Response.StatusCode))" -ForegroundColor Red
        return $false
    }
}

$swaggerResults = @()
$swaggerResults += Test-SwaggerEndpoint "Swagger UI" "http://localhost:8080/swagger-ui/index.html"
$swaggerResults += Test-SwaggerEndpoint "Gateway OpenAPI Spec" "http://localhost:8080/v3/api-docs"
$swaggerResults += Test-SwaggerEndpoint "User Profile Service Docs (via Gateway)" "http://localhost:8080/v3/api-docs/user-profile"
$swaggerResults += Test-SwaggerEndpoint "Profile Photo Service Docs (via Gateway)" "http://localhost:8080/v3/api-docs/profile-photo"
$swaggerResults += Test-SwaggerEndpoint "Auth Service Docs (via Gateway)" "http://localhost:8080/v3/api-docs/auth-service"

Write-Host ""
Write-Host "============================================================================" -ForegroundColor Cyan
Write-Host ""

# Summary
$servicesRunning = ($results | Where-Object { $_ -eq $true }).Count
$swaggerWorking = ($swaggerResults | Where-Object { $_ -eq $true }).Count

Write-Host "SUMMARY" -ForegroundColor Cyan
Write-Host "========" -ForegroundColor Cyan
Write-Host "Services Running: $servicesRunning / 4" -ForegroundColor $(if ($servicesRunning -eq 4) { "Green" } else { "Yellow" })
Write-Host "Swagger Endpoints: $swaggerWorking / 5" -ForegroundColor $(if ($swaggerWorking -eq 5) { "Green" } else { "Yellow" })
Write-Host ""

if ($servicesRunning -eq 4 -and $swaggerWorking -eq 5) {
    Write-Host "✓ All systems operational!" -ForegroundColor Green
    Write-Host ""
    Write-Host "Next Steps:" -ForegroundColor Cyan
    Write-Host "  1. Open: http://localhost:8080/swagger-ui/index.html" -ForegroundColor White
    Write-Host "  2. Check dropdown for all 4 services" -ForegroundColor White
    Write-Host "  3. Select each service to view documentation" -ForegroundColor White
    Write-Host "  4. See SWAGGER_DOCUMENTATION_INDEX.md for detailed guides" -ForegroundColor White
}
else {
    Write-Host "⚠ Some services are not running or not accessible" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Troubleshooting:" -ForegroundColor Cyan
    Write-Host "  1. Ensure all 4 services are started" -ForegroundColor White
    Write-Host "  2. Check port numbers: 8080, 8081, 8082, 8083" -ForegroundColor White
    Write-Host "  3. Check firewall settings" -ForegroundColor White
    Write-Host "  4. Run gateway in debug mode for more info" -ForegroundColor White
}

Write-Host ""
Write-Host "============================================================================" -ForegroundColor Cyan
Write-Host ""

