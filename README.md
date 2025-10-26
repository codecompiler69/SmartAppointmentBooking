# Smart Appointment Booking System - Microservices

A comprehensive microservices-based appointment booking system for doctors, patients, and admins with JWT authentication, role-based access control, async notifications, and full API versioning.

## 🏗️ Architecture Overview

### Microservices

| Service | Port | Responsibility |
|---------|------|---|
| **Auth Service** | 8001 | JWT authentication, user registration, login, token refresh |
| **User Service** | 8002 | User profiles, doctor & patient management, search |
| **Appointment Service** | 8003 | Booking, confirmation, cancellation with optimistic locking |
| **Service Catalog Service** | 8004 | Medical services CRUD with pagination & filtering |
| **Notification Service** | 8005 | Async email/SMS notifications via RabbitMQ |

### Supporting Infrastructure

- **MySQL (3306)**: Shared database for all services
- **RabbitMQ (5672, 15672)**: Message broker for async communication
- **Swagger/OpenAPI (via each service)**: API documentation

## 📋 Prerequisites

- Java 21+ (required)
- Maven 3.8+
- MySQL 8.0+ (required)
- RabbitMQ 3.12+ (required)

## 🚀 Local Development Setup

### 1. Prerequisites Installation

#### Windows
```powershell
# Install MySQL 8.0+ (if not already installed)
# Download from: https://dev.mysql.com/downloads/mysql/

# Install RabbitMQ
# Download from: https://www.rabbitmq.com/download.html

# Verify installations
mysql --version
# Check RabbitMQ service is running
```

#### macOS/Linux
```bash
# Install MySQL
brew install mysql

# Install RabbitMQ
brew install rabbitmq

# Start services
brew services start mysql
brew services start rabbitmq
```

### 2. Database Setup

```bash
# Login to MySQL
mysql -u root -p

# Execute all schema files in order
source auth-service/database/schema.sql
source user-service/database/schema.sql
source appointment-service/database/schema.sql
source service-catalog-service/database/schema.sql
source notification-service/database/schema.sql
```

### 3. Start All Microservices Locally

Open separate terminal windows for each service:

```bash
# Terminal 1: Auth Service (Port 8001)
cd auth-service
mvn spring-boot:run

# Terminal 2: User Service (Port 8002)
cd user-service
mvn spring-boot:run

# Terminal 3: Appointment Service (Port 8003)
cd appointment-service
mvn spring-boot:run

# Terminal 4: Service Catalog Service (Port 8004)
cd service-catalog-service
mvn spring-boot:run

# Terminal 5: Notification Service (Port 8005)
cd notification-service
mvn spring-boot:run
```

### 4. Verify All Services are Running

```bash
# Check Auth Service
curl http://localhost:8001/swagger-ui.html

# Check User Service
curl http://localhost:8002/swagger-ui.html

# Check Appointment Service
curl http://localhost:8003/swagger-ui.html

# Check Service Catalog
curl http://localhost:8004/swagger-ui.html

# Check Notification Service
curl http://localhost:8005/swagger-ui.html
```

### 5. Access RabbitMQ Management Console

Open: http://localhost:15672
- Username: `guest`
- Password: `guest`

## 🔐 Security & JWT

### JWT Configuration

- **Secret Key**: Located in `auth-service/application.properties` → `jwt.secret`
- **Access Token Expiry**: 24 hours (86400000 ms)
- **Refresh Token Expiry**: 7 days (604800000 ms)

### Token Usage

```bash
# 1. Register a new user
curl -X POST http://localhost:8001/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "doctor@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "password": "SecurePass123",
    "phoneNumber": "+1234567890",
    "role": "ROLE_DOCTOR"
  }'

# Response includes access_token and refresh_token

# 2. Login
curl -X POST http://localhost:8001/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "doctor@example.com",
    "password": "SecurePass123"
  }'

# 3. Use token to access protected endpoints
curl -X GET http://localhost:8001/api/v1/auth/me \
  -H "Authorization: Bearer <your_access_token>"

# 4. Refresh token
curl -X POST http://localhost:8001/api/v1/auth/refresh \
  -H "Content-Type: application/json" \
  -d '?refreshToken=<your_refresh_token>'
```

## 📡 API Endpoints

### Auth Service `/api/v1/auth`

| Method | Endpoint | Description |
|--------|----------|---|
| POST | `/register` | Register new user |
| POST | `/login` | Authenticate user |
| GET | `/me` | Get current user (requires auth) |
| POST | `/refresh` | Refresh access token |
| POST | `/verify-email` | Verify email address |
| POST | `/forgot-password` | Initiate password reset |
| POST | `/reset-password` | Reset password |

### User Service `/api/v1/users`

| Method | Endpoint | Description |
|--------|----------|---|
| GET | `/` | List all users (paginated) |
| GET | `/{id}` | Get user by ID |
| PUT | `/{id}` | Update user profile |
| DELETE | `/{id}` | Soft delete user |
| GET | `/search` | Search users by role/specialization |

### Appointment Service `/api/v1/appointments`

| Method | Endpoint | Description |
|--------|----------|---|
| GET | `/` | List appointments (filtered, paginated) |
| POST | `/` | Create new appointment |
| GET | `/{id}` | Get appointment details |
| POST | `/{id}/confirm` | Confirm appointment |
| POST | `/{id}/cancel` | Cancel appointment |
| DELETE | `/{id}` | Delete appointment (Admin only) |

### Service Catalog `/api/v1/services`

| Method | Endpoint | Description |
|--------|----------|---|
| GET | `/` | List services (paginated, filtered) |
| POST | `/` | Create service |
| GET | `/{id}` | Get service details |
| PUT | `/{id}` | Update service |
| DELETE | `/{id}` | Delete service |

## 🗄️ Database Schema

### Key Tables

#### users (Auth Service)
```sql
- id (PK)
- email (UNIQUE)
- password
- firstName, lastName
- emailVerified, emailVerifiedAt
- roles (M2M with roles table)
- createdAt, updatedAt
```

#### doctor_profiles (User Service)
```sql
- id (PK)
- user_id (FK, UNIQUE)
- specialization
- medicalLicenseNumber
- consultationFee
- verified
```

#### appointments (Appointment Service)
```sql
- id (PK)
- doctor_id, patient_id (FK)
- service_id (FK)
- appointment_date, appointment_time
- status (SCHEDULED, CONFIRMED, CANCELLED)
- version (for optimistic locking)
- UNIQUE constraint: (doctor_id, appointment_date, appointment_time)
```

## 🔄 Concurrency & Transactions

### Optimistic Locking

Appointments use `@Version` field to prevent double-booking:

```java
@Entity
public class Appointment {
    @Version
    private Long version;
    // ...
}
```

### Transactional Guarantees

```java
@Service
@Transactional
public class AppointmentService {
    @Transactional
    public Appointment bookAppointment(...) {
        // Atomically check availability and book
    }
}
```

## 📧 Async Notifications

### Message Flow

```
User books appointment
    ↓
AppointmentService publishes event to RabbitMQ
    ↓
NotificationService listens on queue
    ↓
Sends email/SMS notification
```

### RabbitMQ Exchanges & Queues

- **Exchange**: `appointment-events`
- **Queues**:
  - `appointment.created` → sends confirmation email
  - `appointment.confirmed` → sends confirmation to doctor
  - `appointment.cancelled` → sends cancellation notice

## 🧪 Testing APIs

### Using Postman

Import the included Postman collections:
- `auth-service/postman/Auth-Service-API.postman_collection.json`

### Manual Testing

```bash
# 1. Register as Doctor
POST http://localhost:8001/api/v1/auth/register

# 2. Register as Patient
POST http://localhost:8001/api/v1/auth/register

# 3. Create Service
POST http://localhost:8004/api/v1/services

# 4. Book Appointment
POST http://localhost:8003/api/v1/appointments

# 5. Confirm Appointment
POST http://localhost:8003/api/v1/appointments/{id}/confirm

# 6. View Notifications
GET http://localhost:8005/api/v1/notifications
```

## 🛠️ Local Development Setup

### Without Docker

#### 1. Start MySQL

```bash
# Mac with Homebrew
brew services start mysql

# Linux
sudo systemctl start mysql

# Windows
# Services → MySQL80 → Start
```

#### 2. Verify MySQL Connection

```bash
mysql -u root -p
```

#### 3. Import Schemas

```bash
mysql -u root -p < auth-service/database/schema.sql
mysql -u root -p < user-service/database/schema.sql
mysql -u root -p < appointment-service/database/schema.sql
mysql -u root -p < service-catalog-service/database/schema.sql
mysql -u root -p < notification-service/database/schema.sql
```

#### 4. Start RabbitMQ

```bash
# Mac
brew services start rabbitmq

# Docker
docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3.12-management-alpine
```

#### 5. Build & Run Services

```bash
# In each service directory
./mvnw spring-boot:run

# Or build first
./mvnw clean package

# Then run
java -jar target/service-name-0.0.1-SNAPSHOT.jar
```

## 📚 API Versioning

All endpoints use URI versioning:

```
/api/v1/auth/login       # Current version
/api/v2/auth/login       # Future version (when needed)
```

## 🔍 Monitoring & Logs

### View Service Logs

```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f auth-service
```

### Health Endpoints

```bash
curl http://localhost:8001/actuator/health
curl http://localhost:8002/actuator/health
curl http://localhost:8003/actuator/health
curl http://localhost:8004/actuator/health
curl http://localhost:8005/actuator/health
```

## 🐛 Troubleshooting

### Database Connection Issues

```bash
# Check PostgreSQL is running
docker ps | grep postgres

# Connect to database
docker exec -it appointment_db psql -U postgres -d appointment_db
```

### RabbitMQ Connection Issues

```bash
# Check RabbitMQ is running
docker ps | grep rabbitmq

# Access management console
http://localhost:15672
```

### Port Conflicts

If ports are already in use:

```yaml
# docker-compose.yml
services:
  postgres:
    ports:
      - "5433:5432"  # Change to 5433
```

## 📝 Configuration Files

### Key Properties

Each service has `application.properties`:

```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/appointment_db
spring.datasource.username=postgres
spring.datasource.password=postgres

# RabbitMQ
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672

# Mail (Notification Service)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
```

## 🚢 Production Deployment

### Environment Variables

```bash
# .env file
SPRING_DATASOURCE_URL=jdbc:postgresql://prod-db:5432/appointment_db
SPRING_DATASOURCE_USERNAME=prod_user
SPRING_DATASOURCE_PASSWORD=${DB_PASSWORD}
JWT_SECRET=${JWT_SECRET_KEY}
SPRING_RABBITMQ_HOST=prod-rabbitmq
SPRING_MAIL_PASSWORD=${MAIL_PASSWORD}
```

### Docker Compose Production

```bash
docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d
```

## 📜 License

MIT License - see LICENSE file for details

## 👥 Support

For issues and questions:
- Check the API documentation at `http://localhost:<port>/swagger-ui.html`
- Review service logs: `docker-compose logs`
- Inspect RabbitMQ: `http://localhost:15672`

---

**Last Updated**: October 26, 2025
**Version**: 1.0.0
