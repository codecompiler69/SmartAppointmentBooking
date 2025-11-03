# API Gateway - Swagger Documentation Guide

## Overview

The API Gateway provides a unified Swagger/OpenAPI documentation interface that aggregates all microservices' API documentation in one place.

## Accessing Swagger UI

### Local Development
- **Swagger UI**: http://localhost:8000/swagger-ui.html
- **OpenAPI Specification (JSON)**: http://localhost:8000/v3/api-docs
- **OpenAPI Specification (YAML)**: http://localhost:8000/v3/api-docs.yaml

### Production
- **Swagger UI**: https://api.smartappointmentbooking.com/swagger-ui.html
- **OpenAPI Specification**: https://api.smartappointmentbooking.com/v3/api-docs

## Available API Groups

The Swagger UI organizes endpoints into the following categories:

### 1. Gateway
- Health check endpoints
- Gateway information
- Service status

### 2. Authentication (Auth Service - Port 8001)
- User registration
- User login
- Token refresh
- Logout
- Token validation

### 3. Users (User Service - Port 8002)
- User profile management
- Doctor profile management
- User lookup

### 4. Appointments (Appointment Service - Port 8003)
- Create appointments
- View appointments
- Update appointments
- Cancel appointments
- Filter by patient/doctor

### 5. Service Catalog (Service Catalog - Port 8004)
- Browse medical services
- Service details
- Manage services (Admin only)
- Filter by category

### 6. Notifications (Notification Service - Port 8005)
- Send email notifications
- Send SMS notifications
- View notification history

## How to Use Swagger UI

### Step 1: Access the Swagger UI
Navigate to http://localhost:8000/swagger-ui.html in your web browser.

### Step 2: Explore Available Endpoints
- Click on any tag (Gateway, Authentication, Users, etc.) to expand the endpoints
- Each endpoint shows:
  - HTTP method (GET, POST, PUT, DELETE)
  - Endpoint path
  - Description
  - Required parameters
  - Request body schema
  - Response examples

### Step 3: Authenticate (For Protected Endpoints)

Most endpoints require JWT authentication. Follow these steps:

1. **Get a Token**:
   - Expand the **Authentication** section
   - Click on `POST /api/auth/login`
   - Click the **Try it out** button
   - Enter login credentials:
     ```json
     {
       "email": "patient@example.com",
       "password": "Password123!"
     }
     ```
   - Click **Execute**
   - Copy the `accessToken` from the response

2. **Authorize Swagger**:
   - Click the **Authorize** button (🔓) at the top of the page
   - In the "Value" field, enter: `Bearer <your-access-token>`
     - Example: `Bearer eyJhbGciOiJIUzUxMiJ9...`
   - Click **Authorize**
   - Click **Close**
   - The lock icon (🔓) will change to (🔒) indicating you're authenticated

3. **Make Authenticated Requests**:
   - All subsequent API calls will automatically include your JWT token
   - You can now test protected endpoints

### Step 4: Test an Endpoint

1. Click on any endpoint to expand it
2. Click **Try it out**
3. Fill in required parameters or request body
4. Click **Execute**
5. View the response:
   - Response status code
   - Response body
   - Response headers
   - cURL command (can be copied for terminal use)

## Example Workflows

### Workflow 1: Register and Login

1. **Register a new patient**:
   - `POST /api/auth/register`
   - Request body:
     ```json
     {
       "email": "newpatient@example.com",
       "password": "SecurePass123!",
       "firstName": "Jane",
       "lastName": "Doe",
       "phone": "+1234567890",
       "role": "PATIENT"
     }
     ```
   - Execute and note the returned tokens

2. **Authorize with the token**:
   - Click **Authorize**
   - Enter: `Bearer <accessToken>`

3. **View your profile**:
   - `GET /api/users/profile`
   - Execute to see your user profile

### Workflow 2: Book an Appointment

1. **Login** (if not already authenticated)
2. **Browse available services**:
   - `GET /api/services/public`
   - Note the service ID you want

3. **Find a doctor**:
   - `GET /api/users/doctors`
   - Note the doctor ID

4. **Create an appointment**:
   - `POST /api/appointments`
   - Request body:
     ```json
     {
       "doctorId": 2,
       "patientId": 1,
       "serviceId": 1,
       "appointmentDate": "2025-11-05T10:00:00",
       "notes": "Regular checkup"
     }
     ```

5. **View your appointments**:
   - `GET /api/appointments/patient/{patientId}`
   - Replace `{patientId}` with your user ID

### Workflow 3: Doctor Creates Profile

1. **Register as a doctor**:
   - `POST /api/auth/register` with `"role": "DOCTOR"`

2. **Login and authorize**

3. **Create doctor profile**:
   - `POST /api/users/doctors/profile`
   - Request body:
     ```json
     {
       "specialization": "Cardiology",
       "licenseNumber": "DOC12345",
       "yearsOfExperience": 10,
       "consultationFee": 150.00,
       "bio": "Experienced cardiologist"
     }
     ```

4. **View your appointments**:
   - `GET /api/appointments/doctor/{doctorId}`

## Security

### Authentication Scheme
- **Type**: HTTP Bearer Token (JWT)
- **Scheme**: bearer
- **Bearer Format**: JWT
- **Header**: Authorization: Bearer <token>

### Token Expiration
- **Access Token**: 24 hours (86400000 ms)
- **Refresh Token**: 7 days (604800000 ms)

### Roles and Permissions
- **PATIENT**: Can book appointments, view own appointments, update profile
- **DOCTOR**: Can view assigned appointments, manage doctor profile, update appointment status
- **ADMIN**: Full access to all endpoints, can manage services and users

## Response Codes

- **200 OK**: Request successful
- **201 Created**: Resource created successfully
- **204 No Content**: Successful deletion
- **400 Bad Request**: Invalid input data
- **401 Unauthorized**: Authentication required or failed
- **403 Forbidden**: Insufficient permissions
- **404 Not Found**: Resource not found
- **409 Conflict**: Resource conflict (e.g., duplicate email)
- **500 Internal Server Error**: Server error
- **503 Service Unavailable**: Service temporarily down (circuit breaker open)

## Tips and Best Practices

### 1. Use Schemas
Swagger UI provides request/response schemas. Click on "Schema" tabs to see the data structure.

### 2. Example Values
Pre-filled example values are provided. Click "Try it out" to see them, then modify as needed.

### 3. Copy cURL Commands
After executing a request, scroll down to see the cURL command. Copy it for use in terminal or scripts.

### 4. Export OpenAPI Spec
Download the OpenAPI specification from http://localhost:8000/v3/api-docs for:
- Generating client SDKs (using tools like openapi-generator)
- Importing into Postman
- API documentation generation

### 5. Test Error Scenarios
- Try requests without authorization to see 401 responses
- Try accessing doctor endpoints as a patient to see 403 responses
- Try invalid data to see validation errors

### 6. Circuit Breaker Testing
If a backend service is down, the gateway will return a 503 Service Unavailable with a fallback response.

## Troubleshooting

### Issue: "Failed to fetch" error
- **Cause**: Backend service is not running
- **Solution**: Ensure all services (ports 8001-8005) are running

### Issue: 401 Unauthorized
- **Cause**: Missing or invalid token
- **Solution**: Login again and update authorization with new token

### Issue: Token expired
- **Cause**: Access token expired (after 24 hours)
- **Solution**: Use refresh token endpoint to get new access token

### Issue: 403 Forbidden
- **Cause**: Insufficient role permissions
- **Solution**: Ensure you're logged in with correct role (PATIENT, DOCTOR, or ADMIN)

### Issue: CORS errors in browser
- **Cause**: Request from unauthorized origin
- **Solution**: Add your frontend URL to CORS configuration in SecurityConfig.java

## Individual Service Documentation

For detailed service-specific documentation, visit each service's Swagger UI:

1. **Auth Service**: http://localhost:8001/swagger-ui.html
2. **User Service**: http://localhost:8002/swagger-ui.html
3. **Appointment Service**: http://localhost:8003/swagger-ui.html
4. **Service Catalog**: http://localhost:8004/swagger-ui.html
5. **Notification Service**: http://localhost:8005/swagger-ui.html

## Advanced Features

### Customizing Documentation
Edit `OpenApiConfig.java` to modify:
- API title and description
- Contact information
- Server URLs
- Security schemes

### Adding New Endpoints
1. Create or update a docs controller in `controller/docs/` package
2. Add Swagger annotations
3. Gateway will automatically include in documentation

### Disabling Swagger in Production
Add to `application.properties`:
```properties
springdoc.swagger-ui.enabled=false
springdoc.api-docs.enabled=false
```

## Additional Resources

- **OpenAPI Specification**: https://swagger.io/specification/
- **Springdoc OpenAPI**: https://springdoc.org/
- **JWT.io**: https://jwt.io/ (for decoding tokens)
- **Postman**: Import OpenAPI spec for API testing
- **Insomnia**: Alternative API testing tool

## Support

For issues or questions:
- Check service logs: `logs/api-gateway.log`
- Check individual service Swagger UIs
- Verify all services are running: `GET /actuator/health`
- Contact: support@smartappointmentbooking.com
