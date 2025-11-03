# API Gateway - Smart Appointment Booking System

## Overview
The API Gateway serves as the single entry point for all client requests in the Smart Appointment Booking System. It routes requests to the appropriate microservices, handles authentication, and provides cross-cutting concerns like logging, security, and circuit breaking.

## Port Configuration
- **API Gateway**: `http://localhost:8000`

## Features

### 1. **Routing**
Routes requests to backend services:
- `/api/auth/**` → Auth Service (Port 8001)
- `/api/users/**` → User Service (Port 8002)
- `/api/appointments/**` → Appointment Service (Port 8003)
- `/api/services/**` → Service Catalog Service (Port 8004)
- `/api/notifications/**` → Notification Service (Port 8005)

### 2. **Security**
- JWT-based authentication
- Role-based access control (ADMIN, DOCTOR, PATIENT)
- CORS configuration for frontend integration
- Stateless session management

### 3. **Resilience**
- Circuit Breaker pattern using Resilience4J
- Fallback responses when services are unavailable
- Timeout management (3 seconds default)

### 4. **Monitoring**
- Request/Response logging
- Actuator endpoints for health checks
- Circuit breaker metrics

## Running the Gateway

### Prerequisites
1. Java 21
2. Maven
3. All backend services running (ports 8001-8005)

### Steps to Run

1. **Navigate to the gateway directory:**
   ```bash
   cd api-gateway
   ```

2. **Clean and build:**
   ```bash
   ./mvnw clean install
   ```

3. **Run the application:**
   ```bash
   ./mvnw spring-boot:run
   ```

4. **Verify it's running:**
   ```bash
   curl http://localhost:8000/api/gateway/health
   ```

5. **Access Swagger UI:**
   ```
   http://localhost:8000/swagger-ui.html
   ```

## API Documentation

The API Gateway provides comprehensive Swagger/OpenAPI documentation for all microservices.

### Accessing Swagger UI
- **URL**: http://localhost:8000/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8000/v3/api-docs

### Features
- Interactive API testing
- Complete endpoint documentation
- Request/Response examples
- Authentication testing with JWT tokens
- Links to individual service Swagger UIs

### Individual Service Swagger URLs
- **Auth Service**: http://localhost:8001/swagger-ui.html
- **User Service**: http://localhost:8002/swagger-ui.html
- **Appointment Service**: http://localhost:8003/swagger-ui.html
- **Service Catalog**: http://localhost:8004/swagger-ui.html
- **Notification Service**: http://localhost:8005/swagger-ui.html

## API Endpoints

### Gateway Endpoints
- `GET /api/gateway/health` - Check gateway health
- `GET /api/gateway/info` - Get gateway information
- `GET /actuator/health` - Actuator health endpoint
- `GET /actuator/circuitbreakers` - Circuit breaker status

### Public Endpoints (No Authentication Required)
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login
- `POST /api/auth/refresh` - Refresh access token
- `GET /api/services/public/**` - Public service catalog

### Protected Endpoints (Authentication Required)

#### User Service
- `GET /api/users/profile` - Get user profile
- `PUT /api/users/profile` - Update user profile
- `GET /api/users/doctors` - List all doctors (DOCTOR role)
- `POST /api/users/doctors/profile` - Create doctor profile (DOCTOR role)

#### Appointment Service
- `POST /api/appointments` - Create appointment
- `GET /api/appointments/patient/{patientId}` - Get patient appointments (PATIENT role)
- `GET /api/appointments/doctor/{doctorId}` - Get doctor appointments (DOCTOR role)
- `PUT /api/appointments/{id}` - Update appointment
- `DELETE /api/appointments/{id}` - Cancel appointment

#### Service Catalog
- `GET /api/services` - List all services
- `GET /api/services/{id}` - Get service details
- `POST /api/services` - Create service (ADMIN role)
- `PUT /api/services/{id}` - Update service (ADMIN role)
- `DELETE /api/services/{id}` - Delete service (ADMIN role)

#### Notification Service
- `POST /api/notifications/email` - Send email notification
- `POST /api/notifications/sms` - Send SMS notification
- `GET /api/notifications/user/{userId}` - Get user notifications

## Authentication Flow

1. **Login:** Client sends credentials to `/api/auth/login`
2. **Receive Token:** Gateway forwards to Auth Service, returns JWT
3. **Subsequent Requests:** Client includes token in `Authorization: Bearer <token>` header
4. **Gateway Validation:** Gateway validates JWT and extracts user info
5. **Forward Request:** Gateway forwards to appropriate service with user context

## Example Requests

### Using Swagger UI for Testing

1. **Open Swagger UI**: Navigate to http://localhost:8000/swagger-ui.html
2. **Login to get token**:
   - Expand the `Authentication` section
   - Click on `POST /api/auth/login`
   - Click "Try it out"
   - Enter credentials in the request body
   - Click "Execute"
   - Copy the `accessToken` from the response

3. **Authorize subsequent requests**:
   - Click the "Authorize" button at the top of Swagger UI
   - Enter: `Bearer <your-access-token>`
   - Click "Authorize"
   - Now all authenticated endpoints will include your token

### Using cURL

### 1. Login
```bash
curl -X POST http://localhost:8000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "patient@example.com",
    "password": "password123"
  }'
```

### 2. Get User Profile (With Token)
```bash
curl -X GET http://localhost:8000/api/users/profile \
  -H "Authorization: Bearer <your-jwt-token>"
```

### 3. Create Appointment
```bash
curl -X POST http://localhost:8000/api/appointments \
  -H "Authorization: Bearer <your-jwt-token>" \
  -H "Content-Type: application/json" \
  -d '{
    "doctorId": 1,
    "patientId": 2,
    "serviceId": 1,
    "appointmentDate": "2025-11-05T10:00:00",
    "notes": "Regular checkup"
  }'
```

## Circuit Breaker Configuration

The gateway includes circuit breakers for all services:

- **Sliding Window Size**: 10 calls
- **Minimum Calls**: 5 calls before evaluation
- **Failure Rate Threshold**: 50%
- **Wait Duration in Open State**: 10 seconds
- **Timeout**: 3 seconds

When a circuit is open, the gateway returns a fallback response.

## Security Configuration

### JWT Secret
Make sure the `jwt.secret` in `application.properties` matches the one in the Auth Service.

### Role-Based Access
- **ADMIN**: Full access to all endpoints
- **DOCTOR**: Access to doctor-specific endpoints
- **PATIENT**: Access to patient-specific endpoints

### CORS
Configured to allow requests from:
- `http://localhost:3000` (React)
- `http://localhost:4200` (Angular)

Modify in `SecurityConfig.java` if needed.

## Troubleshooting

### Gateway can't connect to services
- Ensure all services are running on their respective ports
- Check service URLs in `application.properties`

### JWT validation fails
- Verify `jwt.secret` matches across Auth Service and Gateway
- Check token expiration time

### Circuit breaker is open
- Check if the downstream service is running
- Review logs for service health issues
- Wait for the configured wait duration (10s) before retry

## Logging

Gateway logs include:
- Incoming requests (method, URI, IP)
- Request duration and status
- JWT validation results
- Circuit breaker state changes

Logs are output to console with timestamp format: `yyyy-MM-dd HH:mm:ss`

## Development Tips

1. **Add New Route**: Update `GatewayConfig.java` with new route bean
2. **Modify Security**: Update `SecurityConfig.java` for new endpoints
3. **Custom Filters**: Extend `OncePerRequestFilter` for custom filtering
4. **Update Circuit Breaker**: Modify `application.properties` for resilience settings

## Production Considerations

1. **Environment Variables**: Use environment variables for sensitive data
2. **HTTPS**: Enable SSL/TLS for production
3. **Rate Limiting**: Consider adding rate limiting for API endpoints
4. **Monitoring**: Integrate with monitoring tools (Prometheus, Grafana)
5. **Service Discovery**: Consider Eureka for dynamic service discovery
6. **API Documentation**: Add Swagger/OpenAPI aggregation

## License
Part of Smart Appointment Booking System
