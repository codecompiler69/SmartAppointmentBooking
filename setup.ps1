# Smart Appointment Booking System - Setup Script (Windows PowerShell)
# This script initializes the development environment

Write-Host "=========================================="
Write-Host "Smart Appointment Booking System Setup" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan

# Check if Docker is installed
try {
    docker --version | Out-Null
    Write-Host "✅ Docker is installed" -ForegroundColor Green
} catch {
    Write-Host "❌ Docker is not installed. Please install Docker Desktop first." -ForegroundColor Red
    exit 1
}

# Check if Docker Compose is installed
try {
    docker-compose --version | Out-Null
    Write-Host "✅ Docker Compose is installed" -ForegroundColor Green
} catch {
    Write-Host "❌ Docker Compose is not installed." -ForegroundColor Red
    exit 1
}

# Create .env file if it doesn't exist
if (-Not (Test-Path ".env")) {
    Write-Host "📝 Creating .env file from template..." -ForegroundColor Yellow
    Copy-Item ".env.template" ".env"
    Write-Host "⚠️  Please edit .env file with your configuration" -ForegroundColor Yellow
}

# Ask if user wants to clean up
$cleanup = Read-Host "Do you want to clean up existing containers and volumes? (y/n)"
if ($cleanup -eq "y" -or $cleanup -eq "Y") {
    Write-Host "🧹 Cleaning up..." -ForegroundColor Yellow
    docker-compose down -v
}

# Build images
Write-Host "🔨 Building Docker images..." -ForegroundColor Yellow
docker-compose build

# Start services
Write-Host "🚀 Starting services..." -ForegroundColor Yellow
docker-compose up -d

# Wait for services to be ready
Write-Host "⏳ Waiting for services to start..." -ForegroundColor Yellow
Start-Sleep -Seconds 10

# Check service health
Write-Host "🏥 Checking service health..." -ForegroundColor Yellow

$services = @(
    @{Name="auth-service"; Port=8001},
    @{Name="user-service"; Port=8002},
    @{Name="appointment-service"; Port=8003},
    @{Name="service-catalog-service"; Port=8004},
    @{Name="notification-service"; Port=8005}
)

foreach ($service in $services) {
    try {
        Invoke-WebRequest -Uri "http://localhost:$($service.Port)/swagger-ui.html" -UseBasicParsing -TimeoutSec 2 | Out-Null
        Write-Host "✅ $($service.Name) is running" -ForegroundColor Green
    } catch {
        Write-Host "⚠️  $($service.Name) may not be ready yet" -ForegroundColor Yellow
    }
}

Write-Host ""
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "✨ Setup Complete!" -ForegroundColor Green
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "📱 Services are running at:" -ForegroundColor Cyan
Write-Host "  - Auth Service: http://localhost:8001/swagger-ui.html"
Write-Host "  - User Service: http://localhost:8002/swagger-ui.html"
Write-Host "  - Appointment Service: http://localhost:8003/swagger-ui.html"
Write-Host "  - Service Catalog: http://localhost:8004/swagger-ui.html"
Write-Host "  - Notification Service: http://localhost:8005/swagger-ui.html"
Write-Host ""
Write-Host "🐰 RabbitMQ Management: http://localhost:15672" -ForegroundColor Cyan
Write-Host "  - Username: guest"
Write-Host "  - Password: guest"
Write-Host ""
Write-Host "🗄️  PostgreSQL: localhost:5432" -ForegroundColor Cyan
Write-Host "  - Username: postgres"
Write-Host "  - Password: postgres"
Write-Host "  - Database: appointment_db"
Write-Host ""
Write-Host "📚 Next steps:" -ForegroundColor Cyan
Write-Host "  1. Review the README.md for API documentation"
Write-Host "  2. Import Postman collections for testing"
Write-Host "  3. Check logs: docker-compose logs -f"
Write-Host ""
