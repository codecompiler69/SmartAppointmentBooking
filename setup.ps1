# Smart Appointment Booking System - Setup Script (Windows PowerShell)
# This script initializes the local development environment

Write-Host "=========================================="
Write-Host "Smart Appointment Booking System Setup" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan

# Check if Java is installed
try {
    java -version 2>&1 | Out-Null
    Write-Host "✅ Java is installed" -ForegroundColor Green
} catch {
    Write-Host "❌ Java is not installed. Please install Java 21+ first." -ForegroundColor Red
    exit 1
}

# Check if Maven is installed
try {
    mvn -version | Out-Null
    Write-Host "✅ Maven is installed" -ForegroundColor Green
} catch {
    Write-Host "❌ Maven is not installed. Please install Maven 3.8+ first." -ForegroundColor Red
    exit 1
}

# Check if MySQL is installed
try {
    mysql --version | Out-Null
    Write-Host "✅ MySQL is installed" -ForegroundColor Green
} catch {
    Write-Host "❌ MySQL is not installed. Please install MySQL 8.0+ first." -ForegroundColor Red
    Write-Host "Download from: https://dev.mysql.com/downloads/mysql/" -ForegroundColor Yellow
    exit 1
}

# Check if RabbitMQ is installed
$rabbitmqService = Get-Service RabbitMQ -ErrorAction SilentlyContinue
if ($null -ne $rabbitmqService) {
    Write-Host "✅ RabbitMQ is installed" -ForegroundColor Green
} else {
    Write-Host "⚠️  RabbitMQ not found as Windows Service" -ForegroundColor Yellow
    Write-Host "Download from: https://www.rabbitmq.com/download.html" -ForegroundColor Yellow
}

# Test MySQL connection
Write-Host "`n📝 Testing MySQL connection..." -ForegroundColor Yellow
try {
    mysql -u root -p -e "SELECT @@version;" > $null 2>&1
    Write-Host "✅ MySQL connection successful" -ForegroundColor Green
} catch {
    Write-Host "❌ Cannot connect to MySQL. Make sure MySQL service is running." -ForegroundColor Red
    Write-Host "  - Windows: Services → MySQL80" -ForegroundColor Yellow
    exit 1
}

# Initialize databases
Write-Host "`n🗄️  Initializing databases..." -ForegroundColor Yellow

$schemas = @(
    "auth-service/database/schema.sql",
    "user-service/database/schema.sql",
    "appointment-service/database/schema.sql",
    "service-catalog-service/database/schema.sql",
    "notification-service/database/schema.sql"
)

foreach ($schema in $schemas) {
    if (Test-Path $schema) {
        Write-Host "  📌 Loading $schema..." -ForegroundColor Cyan
        mysql -u root -p < $schema
        Write-Host "  ✅ Loaded $schema" -ForegroundColor Green
    } else {
        Write-Host "  ⚠️  Schema not found: $schema" -ForegroundColor Yellow
    }
}

Write-Host ""
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "✨ Setup Complete!" -ForegroundColor Green
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "� Next Steps:" -ForegroundColor Cyan
Write-Host ""
Write-Host "1️⃣  Start Each Microservice in Separate Terminal Windows:" -ForegroundColor Yellow
Write-Host ""
Write-Host "   Terminal 1 - Auth Service (Port 8001):"
Write-Host "   cd auth-service && mvn spring-boot:run"
Write-Host ""
Write-Host "   Terminal 2 - User Service (Port 8002):"
Write-Host "   cd user-service && mvn spring-boot:run"
Write-Host ""
Write-Host "   Terminal 3 - Appointment Service (Port 8003):"
Write-Host "   cd appointment-service && mvn spring-boot:run"
Write-Host ""
Write-Host "   Terminal 4 - Service Catalog Service (Port 8004):"
Write-Host "   cd service-catalog-service && mvn spring-boot:run"
Write-Host ""
Write-Host "   Terminal 5 - Notification Service (Port 8005):"
Write-Host "   cd notification-service && mvn spring-boot:run"
Write-Host ""
Write-Host "2️⃣  Access Services:" -ForegroundColor Yellow
Write-Host "  - Auth Service: http://localhost:8001/swagger-ui.html"
Write-Host "  - User Service: http://localhost:8002/swagger-ui.html"
Write-Host "  - Appointment Service: http://localhost:8003/swagger-ui.html"
Write-Host "  - Service Catalog: http://localhost:8004/swagger-ui.html"
Write-Host "  - Notification Service: http://localhost:8005/swagger-ui.html"
Write-Host ""
Write-Host "3️⃣  Other Resources:" -ForegroundColor Yellow
Write-Host "  - RabbitMQ Management: http://localhost:15672 (guest/guest)"
Write-Host "  - MySQL: localhost:3306 (root/<your-password>)"
Write-Host ""
Write-Host "📖 Read QUICKSTART.md for testing examples" -ForegroundColor Cyan
Write-Host ""
