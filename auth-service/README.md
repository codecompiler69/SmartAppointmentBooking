# Auth Service - Smart Appointment Booking System

## Overview
The Auth Service is a microservice responsible for user authentication and authorization in the Smart Appointment Booking System. It provides JWT-based authentication, user registration with email verification, password reset functionality, and role-based access control.

## Features
- ✅ User Registration with Email Verification
- ✅ Secure Login with JWT Token Generation
- ✅ Refresh Token for Session Renewal
- ✅ Password Reset (Forgot/Reset Password Flow)
- ✅ Role-Based Access Control (ADMIN, DOCTOR, PATIENT)
- ✅ Get Current User Details
- ✅ Spring Security Integration
- ✅ API Documentation with Swagger

## Tech Stack
- **Language**: Java 21
- **Framework**: Spring Boot 3.5.6
- **Security**: Spring Security with JWT
- **Database**: PostgreSQL
- **ORM**: Spring Data JPA (Hibernate)
- **Validation**: Jakarta Bean Validation
- **Build Tool**: Maven
- **Email**: Spring Boot Mail
- **API Documentation**: SpringDoc OpenAPI (Swagger)

## Prerequisites
- Java 21 or higher
- Maven 3.6+
- PostgreSQL 12+
- SMTP Server (Gmail, SendGrid, etc.)

## Database Setup

### 1. Create PostgreSQL Database
```sql
CREATE DATABASE auth_db;
```

### 2. Database Configuration
Update `application.properties` with your database credentials:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/auth_db
spring.datasource.username=your_username
spring.datasource.password=your_password
```

## Email Configuration

### Using Gmail SMTP
1. Enable 2-Factor Authentication on your Gmail account
2. Generate an App Password: https://myaccount.google.com/apppasswords
3. Update `application.properties`:
```properties
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
app.mail.from=noreply@smartappointment.com
```

## JWT Configuration

### Generate a Secure Secret Key
Update the JWT secret in `application.properties`:
```properties
jwt.secret=your-very-long-secret-key-at-least-256-bits
```

To generate a secure key, you can use:
```bash
openssl rand -base64 64
```

## Installation & Running

### 1. Clone the Repository
```bash
cd C:\SmartAppointmentBookingSystem\auth-service
```

### 2. Build the Project
```bash
mvnw clean install
```

### 3. Run the Application
```bash
mvnw spring-boot:run
```

The service will start on **http://localhost:8081**

## API Documentation

### Swagger UI
Access the interactive API documentation at:
```
http://localhost:8081/swagger-ui/index.html
```

### OpenAPI JSON
```
http://localhost:8081/v3/api-docs
```

## API Endpoints

| Method | Endpoint                     | Description                | Auth Required     |
|--------|------------------------------|----------------------------|-------------------|
| POST   | `/api/v1/auth/register`      | Register new user          | ❌                 |
| POST   | `/api/v1/auth/verify-email`  | Verify email using token   | ❌                 |
| POST   | `/api/v1/auth/login`         | Login with email/password  | ❌                 |
| POST   | `/api/v1/auth/refresh`       | Get new access token       | ✅ (Refresh token) |
| POST   | `/api/v1/auth/forgot-password` | Send reset link to email | ❌                 |
| POST   | `/api/v1/auth/reset-password` | Reset password using token | ❌               |
| GET    | `/api/v1/auth/me`            | Get current user details   | ✅                 |

## Example API Usage

### 1. Register a New User
```bash
curl -X POST http://localhost:8081/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Dr. John Smith",
    "email": "john.smith@example.com",
    "password": "Password@123",
    "role": "DOCTOR"
  }'
```

### 2. Verify Email
```bash
curl -X POST http://localhost:8081/api/v1/auth/verify-email \
  -H "Content-Type: application/json" \
  -d '{
    "token": "verification-token-from-email"
  }'
```

### 3. Login
```bash
curl -X POST http://localhost:8081/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john.smith@example.com",
    "password": "Password@123"
  }'
```

**Response:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR...",
  "expiresIn": 3600
}
```

### 4. Get Current User (Protected Endpoint)
```bash
curl -X GET http://localhost:8081/api/v1/auth/me \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

### 5. Refresh Access Token
```bash
curl -X POST http://localhost:8081/api/v1/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "your-refresh-token"
  }'
```

### 6. Forgot Password
```bash
curl -X POST http://localhost:8081/api/v1/auth/forgot-password \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john.smith@example.com"
  }'
```

### 7. Reset Password
```bash
curl -X POST http://localhost:8081/api/v1/auth/reset-password \
  -H "Content-Type: application/json" \
  -d '{
    "token": "reset-token-from-email",
    "newPassword": "NewPassword@123"
  }'
```

## User Roles

The system supports three user roles:
- **ADMIN**: System administrators
- **DOCTOR**: Healthcare providers
- **PATIENT**: Service users

## Security Features

### Password Requirements
- Minimum 8 characters
- At least one uppercase letter
- At least one lowercase letter
- At least one digit
- At least one special character (@#$%^&+=)

### JWT Token Structure
- **Access Token**: Expires in 1 hour
- **Refresh Token**: Expires in 7 days

## Project Structure
```
auth-service/
└── src/main/java/com/smartappointmentbooking/auth_service/
    ├── config/
    │   ├── JwtAuthenticationFilter.java
    │   ├── SecurityConfig.java
    │   └── SwaggerConfig.java
    ├── controller/
    │   └── AuthController.java
    ├── dto/
    │   ├── ForgotPasswordRequest.java
    │   ├── JwtResponse.java
    │   ├── LoginRequest.java
    │   ├── MessageResponse.java
    │   ├── RefreshTokenRequest.java
    │   ├── RegisterRequest.java
    │   ├── ResetPasswordRequest.java
    │   ├── UserResponse.java
    │   └── VerifyEmailRequest.java
    ├── entity/
    │   ├── Role.java
    │   └── User.java
    ├── exception/
    │   ├── AccountNotEnabledException.java
    │   ├── ApiError.java
    │   ├── GlobalExceptionHandler.java
    │   ├── InvalidTokenException.java
    │   ├── UserAlreadyExistsException.java
    │   └── UserNotFoundException.java
    ├── repository/
    │   └── UserRepository.java
    ├── service/
    │   ├── AuthService.java
    │   └── CustomUserDetailsService.java
    ├── util/
    │   ├── EmailUtil.java
    │   └── JwtUtil.java
    └── AuthServiceApplication.java
```

## Testing

### Run Tests
```bash
mvnw test
```

## Troubleshooting

### Database Connection Issues
- Ensure PostgreSQL is running
- Verify database credentials in `application.properties`
- Check if the database `auth_db` exists

### Email Not Sending
- Verify SMTP credentials
- Check if 2FA is enabled and App Password is generated (for Gmail)
- Review application logs for email-related errors

### JWT Token Issues
- Ensure JWT secret is at least 256 bits
- Check token expiration settings
- Verify Authorization header format: `Bearer <token>`

## Environment Variables (Production)

For production deployment, use environment variables instead of hardcoding in `application.properties`:

```bash
export DB_URL=jdbc:postgresql://localhost:5432/auth_db
export DB_USERNAME=your_username
export DB_PASSWORD=your_password
export JWT_SECRET=your-secure-secret-key
export MAIL_USERNAME=your-email@gmail.com
export MAIL_PASSWORD=your-app-password
```

## License
Apache 2.0

## Support
For issues and questions, contact: support@smartappointment.com

