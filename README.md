# Smart Appointment Booking System - Microservices

A comprehensive microservices-based appointment booking system for doctors, patients, and admins with API Gateway, JWT authentication, role-based access control, and full Swagger documentation.

## 🏗️ Architecture Overview

### System Components

| Component | Port | Responsibility |
|---------|------|---|
| **API Gateway** | 8000 | Central entry point, JWT validation, routing, circuit breaker, unified Swagger |
| **Auth Service** | 8001 | JWT authentication, user registration, login, token refresh, role management |
| **User Service** | 8002 | User profiles, doctor & patient management, search by specialization |
| **Appointment Service** | 8003 | Booking, confirmation, cancellation, status management |
| **Service Catalog Service** | 8004 | Medical services CRUD (30+ services in 12+ categories) |
| **Notification Service** | 8005 | Email/SMS notifications with status tracking |

### Database Architecture

- **MySQL 8.0** - 5 separate databases (one per service)
  - `auth_db` - Users, roles, user_roles, refresh_tokens
  - `user_db` - User profiles, doctor profiles
  - `appointment_db` - Appointments
  - `service_catalog_db` - Medical services catalog
  - `notification_db` - Notification logs

### Key Features

✅ **API Gateway** - Spring Cloud Gateway MVC with circuit breaker  
✅ **JWT Security** - Access tokens (24h) + Refresh tokens (7 days)  
✅ **Role-Based Access** - 3 roles (ADMIN, DOCTOR, PATIENT)  
✅ **Unified Swagger** - All services documented at gateway level  
✅ **Dummy Data** - 20 users, 30+ services, 23 appointments pre-loaded  
✅ **Production Ready** - Exception handling, validation, soft deletes

## 📋 Prerequisites

- **Java 21+** (required)
- **Maven 3.8+** (required)
- **MySQL 8.0+** (required)

## 🚀 Quick Start Guide

### 1. Install MySQL 8.0+

#### Windows
```powershell
# Download from: https://dev.mysql.com/downloads/mysql/
# Install and start MySQL service
# Verify installation
mysql --version
```

#### macOS/Linux
```bash
# Install MySQL
brew install mysql

# Start MySQL service
brew services start mysql

# Verify installation
mysql --version
```

### 2. Create Databases and Load Dummy Data

```bash
# Login to MySQL
mysql -u root -p

# Execute the complete database setup script (creates all databases and loads dummy data)
source dummy-data.sql

# This script will:
# - Create 5 databases (auth_db, user_db, appointment_db, service_catalog_db, notification_db)
# - Create all required tables
# - Load 20 users, 30+ services, 23 appointments, 20+ notifications
```

**Dummy Data Included:**
- 20 users: 2 admins, 8 doctors (with profiles), 10 patients
- 3 roles: ROLE_ADMIN, ROLE_DOCTOR, ROLE_PATIENT
- 30+ medical services across 12+ categories
- 23 appointments with various statuses
- 20+ notifications (email and SMS)
- **All passwords:** `password123` (BCrypt hashed)

### 3. Start All Services

Open **6 separate terminal windows** (one for each service):

```bash
# Terminal 1: API Gateway (Port 8000) - START THIS FIRST for unified Swagger
cd api-gateway
mvn spring-boot:run

# Terminal 2: Auth Service (Port 8001)
cd auth-service
mvn spring-boot:run

# Terminal 3: User Service (Port 8002)
cd user-service
mvn spring-boot:run

# Terminal 4: Appointment Service (Port 8003)
cd appointment-service
mvn spring-boot:run

# Terminal 5: Service Catalog Service (Port 8004)
cd service-catalog-service
mvn spring-boot:run

# Terminal 6: Notification Service (Port 8005)
cd notification-service
mvn spring-boot:run
```

### 4. Verify All Services

**Health Checks:**
```bash
# API Gateway
curl http://localhost:8000/health

# Individual Services
curl http://localhost:8001/api/v1/auth/health  # Auth (via gateway: /auth/...)
curl http://localhost:8002/api/users/health    # User
curl http://localhost:8003/api/appointments/health  # Appointment
curl http://localhost:8004/api/services/health # Service Catalog
curl http://localhost:8005/api/notifications/health  # Notification
```

### 5. Access Swagger Documentation

**Option 1: Unified Documentation (Recommended)**
- **API Gateway Swagger:** http://localhost:8000/swagger-ui.html
- Shows all services in one place with organized tags

**Option 2: Individual Service Documentation**
- Auth Service: http://localhost:8001/swagger-ui.html
- User Service: http://localhost:8002/swagger-ui.html
- Appointment Service: http://localhost:8003/swagger-ui.html
- Service Catalog: http://localhost:8004/swagger-ui.html
- Notification Service: http://localhost:8005/swagger-ui.html

---

## 🔐 Testing with Swagger UI

### Step 1: Login and Get JWT Token

1. Go to **API Gateway Swagger**: http://localhost:8000/swagger-ui.html
2. Find **"Authentication"** section
3. Execute **POST /auth/login** with credentials:
   ```json
   {
     "email": "admin@hospital.com",
     "password": "password123"
   }
   ```
4. Copy the `accessToken` from the response

### Step 2: Authorize Swagger

1. Click **"Authorize"** button (lock icon) at the top
2. Enter: `Bearer {your_access_token}`
3. Click **"Authorize"** then **"Close"**

### Step 3: Test Protected Endpoints

Now you can test all endpoints that require authentication!

**Test Users (Password: `password123` for all):**
- **Admin:** `admin@hospital.com`
- **Doctors:** 
  - `dr.smith@hospital.com` (Cardiology)
  - `dr.johnson@hospital.com` (Pediatrics)
  - `dr.williams@hospital.com` (Dermatology)
- **Patients:** `patient1@email.com` through `patient10@email.com`

---

## � API Documentation

### Complete API Reference

For detailed API documentation, see:
- **[FEATURES.md](FEATURES.md)** - Complete feature list with all 48+ endpoints
- **[ARCHITECTURE.md](ARCHITECTURE.md)** - System architecture and design
- **[API_REFERENCE.md](api-gateway/API_REFERENCE.md)** - Quick API reference
- **[SWAGGER_GUIDE.md](api-gateway/SWAGGER_GUIDE.md)** - Swagger usage guide

### Quick API Overview

**Total Endpoints:** 48+

| Service | Endpoints | Key Operations |
|---------|-----------|----------------|
| Auth | 7 | Register, Login, Refresh Token, Get Current User |
| User | 13 | User CRUD, Doctor Profiles, Search by Specialization |
| Appointment | 10 | Create, Update, Cancel, Search by Doctor/Patient/Status |
| Service Catalog | 9 | Service CRUD, Filter by Category, Active Services |
| Notification | 8 | Send Email/SMS, Track Status, Mark as Read |
| Gateway | 1 | Health Check |

---

## 🗄️ Database Information

### Database Schemas

Each service has its own database:

```sql
-- Auth Service Database
auth_db
  ├── users (20 records)
  ├── roles (3 records: ADMIN, DOCTOR, PATIENT)
  ├── user_roles (20 mappings)
  └── refresh_tokens

-- User Service Database
user_db
  ├── users (18 profile records)
  └── doctor_profiles (8 records)

-- Appointment Service Database
appointment_db
  └── appointments (23 records)

-- Service Catalog Database
service_catalog_db
  └── services (30+ records)

-- Notification Service Database
notification_db
  └── notifications (20+ records)
```

### Sample Data

The `dummy-data.sql` script includes:
- ✅ 2 Admin users
- ✅ 8 Doctors with complete profiles (specializations, fees, availability)
- ✅ 10 Patients with medical information
- ✅ 30+ Medical services (Consultation, Diagnostics, Imaging, Vaccination, etc.)
- ✅ 23 Appointments (completed, confirmed, scheduled, cancelled)
- ✅ 20+ Notifications (email and SMS)

**All user passwords:** `password123` (BCrypt hashed)

---

## 🛠️ Development

### Project Structure

```
SmartAppointmentBooking/
├── api-gateway/              # API Gateway (Port 8000)
│   ├── src/main/java/
│   │   └── com/.../api_gateway/
│   │       ├── config/       # Gateway, Security, OpenAPI config
│   │       ├── controller/   # Fallback & Documentation controllers
│   │       ├── filter/       # JWT filter, Logging filter
│   │       └── util/         # JWT utilities
│   └── pom.xml
│
├── auth-service/             # Authentication (Port 8001)
│   ├── src/main/java/
│   │   └── com/.../auth_service/
│   │       ├── controller/   # Auth endpoints
│   │       ├── service/      # Business logic
│   │       ├── repository/   # Data access
│   │       ├── entity/       # User, Role, RefreshToken
│   │       ├── dto/          # Request/Response objects
│   │       └── security/     # JWT provider, filters
│   └── database/schema.sql
│
├── user-service/             # User Management (Port 8002)
│   ├── src/main/java/
│   │   └── com/.../user_service/
│   │       ├── controller/   # User & Doctor endpoints
│   │       ├── service/      # Business logic
│   │       ├── repository/   # Data access
│   │       ├── entity/       # User, DoctorProfile
│   │       └── dto/          # Request/Response objects
│   └── database/schema.sql
│
├── appointment-service/      # Appointments (Port 8003)
│   ├── src/main/java/
│   │   └── com/.../appointment_service/
│   │       ├── controller/   # Appointment endpoints
│   │       ├── service/      # Business logic
│   │       ├── repository/   # Data access
│   │       ├── entity/       # Appointment
│   │       └── dto/          # Request/Response objects
│   └── database/schema.sql
│
├── service-catalog-service/  # Medical Services (Port 8004)
│   ├── src/main/java/
│   │   └── com/.../service_catalog_service/
│   │       ├── controller/   # Service catalog endpoints
│   │       ├── service/      # Business logic
│   │       ├── repository/   # Data access
│   │       ├── entity/       # Service
│   │       └── dto/          # Request/Response objects
│   └── database/schema.sql
│
├── notification-service/     # Notifications (Port 8005)
│   ├── src/main/java/
│   │   └── com/.../notification_service/
│   │       ├── controller/   # Notification endpoints
│   │       ├── service/      # Business logic
│   │       ├── repository/   # Data access
│   │       ├── entity/       # Notification
│   │       └── dto/          # Request/Response objects
│   └── database/schema.sql
│
├── dummy-data.sql            # Complete database setup with test data
├── FEATURES.md               # Complete features list
├── ARCHITECTURE.md           # System architecture documentation
└── README.md                 # This file
```

### Technology Stack

**Backend Framework:**
- Spring Boot 3.4.5+
- Spring Data JPA
- Spring Security
- Spring Cloud Gateway MVC 2025.0.0

**Security:**
- JWT (jjwt 0.12.3)
- BCrypt password hashing
- Role-based access control

**Database:**
- MySQL 8.0+
- HikariCP connection pooling

**Documentation:**
- Springdoc OpenAPI 2.7.0
- Swagger UI

**Resilience:**
- Resilience4J Circuit Breaker

**Build Tool:**
- Maven 3.8+

---

## 🎯 Common Use Cases

### 1. Patient Books an Appointment

```bash
# 1. Login as patient
POST http://localhost:8000/auth/login
{
  "email": "patient1@email.com",
  "password": "password123"
}

# 2. Browse available services
GET http://localhost:8000/services/active

# 3. Find doctors by specialization
GET http://localhost:8000/users/doctors/specialization/Cardiology

# 4. Create appointment
POST http://localhost:8000/appointments
Authorization: Bearer {token}
{
  "patientId": 11,
  "doctorId": 3,
  "serviceId": 1,
  "appointmentDate": "2024-12-01T10:00:00",
  "reason": "Chest pain checkup"
}
```

### 2. Doctor Views Their Appointments

```bash
# 1. Login as doctor
POST http://localhost:8000/auth/login
{
  "email": "dr.smith@hospital.com",
  "password": "password123"
}

# 2. Get all appointments for doctor
GET http://localhost:8000/appointments/doctor/3
Authorization: Bearer {token}

# 3. Get only confirmed appointments
GET http://localhost:8000/appointments/doctor/3/status/CONFIRMED
Authorization: Bearer {token}
```

### 3. Admin Manages Services

```bash
# 1. Login as admin
POST http://localhost:8000/auth/login
{
  "email": "admin@hospital.com",
  "password": "password123"
}

# 2. Create new service
POST http://localhost:8000/services
Authorization: Bearer {token}
{
  "name": "Full Body Checkup",
  "description": "Comprehensive health screening",
  "category": "Preventive",
  "basePrice": 500.00,
  "durationMinutes": 120
}

# 3. Get all services by category
GET http://localhost:8000/services/category/Preventive
Authorization: Bearer {token}
```

---

## 🐛 Troubleshooting

### Common Issues

**1. Service won't start - Port already in use**
```bash
# Windows
netstat -ano | findstr :8000
taskkill /PID <PID> /F

# macOS/Linux
lsof -ti:8000 | xargs kill -9
```

**2. Database connection failed**
```bash
# Check MySQL is running
# Windows: Services → MySQL
# macOS/Linux: brew services list

# Verify database exists
mysql -u root -p
SHOW DATABASES;
```

**3. JWT token expired**
- Access tokens expire after 24 hours
- Use refresh token endpoint to get new access token
- Refresh tokens expire after 7 days - user must re-login

**4. Cannot access Swagger UI**
- Ensure service is fully started (check console logs)
- Try: http://localhost:8000/swagger-ui/index.html (with /index.html)
- Clear browser cache

---

## 📝 License

This project is licensed under the MIT License.

---

## 👥 Contributors

Developed as a comprehensive microservices learning project.

---

## 📞 Support

For questions or issues:
1. Check **[FEATURES.md](FEATURES.md)** for complete feature documentation
2. Check **[ARCHITECTURE.md](ARCHITECTURE.md)** for architecture details
3. Review Swagger documentation at http://localhost:8000/swagger-ui.html
4. Check service logs for error messages

---

**Happy Coding! 🚀**
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
