#!/bin/bash

# Smart Appointment Booking System - Setup Script
# This script initializes the development environment

set -e

echo "=========================================="
echo "Smart Appointment Booking System Setup"
echo "=========================================="

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    echo "❌ Docker is not installed. Please install Docker first."
    exit 1
fi

# Check if Docker Compose is installed
if ! command -v docker-compose &> /dev/null; then
    echo "❌ Docker Compose is not installed. Please install Docker Compose first."
    exit 1
fi

echo "✅ Docker and Docker Compose are installed"

# Create .env file if it doesn't exist
if [ ! -f ".env" ]; then
    echo "📝 Creating .env file from template..."
    cp .env.template .env
    echo "⚠️  Please edit .env file with your configuration"
fi

# Clean up old containers and volumes (optional)
read -p "Do you want to clean up existing containers and volumes? (y/n) " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    echo "🧹 Cleaning up..."
    docker-compose down -v
fi

# Build images
echo "🔨 Building Docker images..."
docker-compose build

# Start services
echo "🚀 Starting services..."
docker-compose up -d

# Wait for services to be ready
echo "⏳ Waiting for services to start..."
sleep 10

# Check service health
echo "🏥 Checking service health..."

services=(
    "auth-service:8001"
    "user-service:8002"
    "appointment-service:8003"
    "service-catalog-service:8004"
    "notification-service:8005"
)

for service in "${services[@]}"; do
    IFS=':' read -r name port <<< "$service"
    if curl -s http://localhost:$port/swagger-ui.html > /dev/null; then
        echo "✅ $name is running"
    else
        echo "⚠️  $name may not be ready yet"
    fi
done

echo ""
echo "=========================================="
echo "✨ Setup Complete!"
echo "=========================================="
echo ""
echo "📱 Services are running at:"
echo "  - Auth Service: http://localhost:8001/swagger-ui.html"
echo "  - User Service: http://localhost:8002/swagger-ui.html"
echo "  - Appointment Service: http://localhost:8003/swagger-ui.html"
echo "  - Service Catalog: http://localhost:8004/swagger-ui.html"
echo "  - Notification Service: http://localhost:8005/swagger-ui.html"
echo ""
echo "🐰 RabbitMQ Management: http://localhost:15672"
echo "  - Username: guest"
echo "  - Password: guest"
echo ""
echo "🗄️  PostgreSQL: localhost:5432"
echo "  - Username: postgres"
echo "  - Password: postgres"
echo "  - Database: appointment_db"
echo ""
echo "📚 Next steps:"
echo "  1. Review the README.md for API documentation"
echo "  2. Import Postman collections for testing"
echo "  3. Check logs: docker-compose logs -f"
echo ""
