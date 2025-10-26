#!/bin/bash

# Smart Appointment Booking System - Setup Script (Linux/Mac)
# This script initializes the local development environment

set -e

echo "=========================================="
echo "Smart Appointment Booking System Setup"
echo "=========================================="

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "❌ Java is not installed. Please install Java 21+ first."
    exit 1
fi

echo "✅ Java is installed"

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven is not installed. Please install Maven 3.8+ first."
    exit 1
fi

echo "✅ Maven is installed"

# Check if MySQL is installed
if ! command -v mysql &> /dev/null; then
    echo "❌ MySQL is not installed. Please install MySQL 8.0+ first."
    echo "macOS: brew install mysql"
    echo "Linux: sudo apt-get install mysql-server"
    exit 1
fi

echo "✅ MySQL is installed"

# Check if RabbitMQ is installed
if ! command -v rabbitmq-server &> /dev/null; then
    echo "⚠️  RabbitMQ not found. Please install RabbitMQ 3.12+."
    echo "macOS: brew install rabbitmq"
    echo "Linux: sudo apt-get install rabbitmq-server"
fi

# Test MySQL connection
echo ""
echo "📝 Testing MySQL connection..."
if mysql -u root -p -e "SELECT @@version;" > /dev/null 2>&1; then
    echo "✅ MySQL connection successful"
else
    echo "❌ Cannot connect to MySQL. Make sure MySQL service is running."
    echo "macOS: brew services start mysql"
    echo "Linux: sudo systemctl start mysql"
    exit 1
fi

# Start MySQL and RabbitMQ if needed
echo ""
echo "📝 Ensuring services are running..."

if command -v brew &> /dev/null; then
    # macOS
    brew services start mysql 2>/dev/null || true
    brew services start rabbitmq 2>/dev/null || true
    echo "✅ Services started (macOS)"
else
    # Linux
    echo "ℹ️  Make sure MySQL and RabbitMQ services are running:"
    echo "   sudo systemctl start mysql"
    echo "   sudo systemctl start rabbitmq-server"
fi

# Initialize databases
echo ""
echo "🗄️  Initializing databases..."

schemas=(
    "auth-service/database/schema.sql"
    "user-service/database/schema.sql"
    "appointment-service/database/schema.sql"
    "service-catalog-service/database/schema.sql"
    "notification-service/database/schema.sql"
)

for schema in "${schemas[@]}"; do
    if [ -f "$schema" ]; then
        echo "  📌 Loading $schema..."
        mysql -u root -p < "$schema"
        echo "  ✅ Loaded $schema"
    else
        echo "  ⚠️  Schema not found: $schema"
    fi
done

echo ""
echo "=========================================="
echo "✨ Setup Complete!"
echo "=========================================="
echo ""
echo "� Next Steps:"
echo ""
echo "1️⃣  Start Each Microservice in Separate Terminal Windows:"
echo ""
echo "   Terminal 1 - Auth Service (Port 8001):"
echo "   cd auth-service && mvn spring-boot:run"
echo ""
echo "   Terminal 2 - User Service (Port 8002):"
echo "   cd user-service && mvn spring-boot:run"
echo ""
echo "   Terminal 3 - Appointment Service (Port 8003):"
echo "   cd appointment-service && mvn spring-boot:run"
echo ""
echo "   Terminal 4 - Service Catalog Service (Port 8004):"
echo "   cd service-catalog-service && mvn spring-boot:run"
echo ""
echo "   Terminal 5 - Notification Service (Port 8005):"
echo "   cd notification-service && mvn spring-boot:run"
echo ""
echo "2️⃣  Access Services:"
echo "  - Auth Service: http://localhost:8001/swagger-ui.html"
echo "  - User Service: http://localhost:8002/swagger-ui.html"
echo "  - Appointment Service: http://localhost:8003/swagger-ui.html"
echo "  - Service Catalog: http://localhost:8004/swagger-ui.html"
echo "  - Notification Service: http://localhost:8005/swagger-ui.html"
echo ""
echo "3️⃣  Other Resources:"
echo "  - RabbitMQ Management: http://localhost:15672 (guest/guest)"
echo "  - MySQL: localhost:3306 (root/<your-password>)"
echo ""
echo "📖 Read QUICKSTART.md for testing examples"
echo ""
