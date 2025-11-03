# 🚀 Quick Start Guide - API Gateway

## Prerequisites
- ✅ Java 21 installed
- ✅ Maven installed
- ✅ All backend services running (ports 8001-8005)

## Start in 3 Steps

### Step 1: Navigate to Gateway Directory
```powershell
cd "c:\Users\sarthakkhandekar\OneDrive - Nagarro\Desktop\SmartAppointmentBooking\api-gateway"
```

### Step 2: Build the Project
```powershell
.\mvnw clean install
```

### Step 3: Run the Gateway
```powershell
.\mvnw spring-boot:run
```

## Verify It's Working

### Check Health
```powershell
curl http://localhost:8000/api/gateway/health
```

Expected response:
```json
{
  "service": "api-gateway",
  "status": "UP",
  "timestamp": "2025-11-03T...",
  "message": "API Gateway is running successfully",
  "availableRoutes": {
    "auth": "/api/auth/**",
    "users": "/api/users/**",
    "appointments": "/api/appointments/**",
    "services": "/api/services/**",
    "notifications": "/api/notifications/**"
  }
}
```

## Access Swagger UI

Open in your browser:
```
http://localhost:8000/swagger-ui.html
```

## First API Test

### 1. Register a User
In Swagger UI:
- Expand **Authentication** → `POST /api/auth/register`
- Click **Try it out**
- Use this request:
```json
{
  "email": "test@example.com",
  "password": "Test123!",
  "firstName": "Test",
  "lastName": "User",
  "phone": "+1234567890",
  "role": "PATIENT"
}
```
- Click **Execute**
- Copy the `accessToken` from response

### 2. Authorize
- Click **Authorize** button (top right)
- Enter: `Bearer <your-access-token>`
- Click **Authorize**, then **Close**

### 3. Test Protected Endpoint
- Expand **Users** → `GET /api/users/profile`
- Click **Try it out**
- Click **Execute**
- You should see your profile!

## Common Commands

### Build without tests
```powershell
.\mvnw clean install -DskipTests
```

### Run with specific profile
```powershell
.\mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### View logs
```powershell
.\mvnw spring-boot:run
# Logs appear in console
```

## Troubleshooting

### Problem: Port 8000 already in use
**Solution**: 
```powershell
# Find process using port 8000
netstat -ano | findstr :8000

# Kill the process (replace PID)
taskkill /PID <PID> /F
```

### Problem: Cannot connect to backend services
**Solution**: Ensure all services are running:
- Auth Service: http://localhost:8001/actuator/health
- User Service: http://localhost:8002/actuator/health
- Appointment Service: http://localhost:8003/actuator/health
- Service Catalog: http://localhost:8004/actuator/health
- Notification Service: http://localhost:8005/actuator/health

### Problem: Build fails
**Solution**:
```powershell
# Clean and rebuild
.\mvnw clean
.\mvnw install -DskipTests
```

## Useful URLs

| Purpose | URL |
|---------|-----|
| Swagger UI | http://localhost:8000/swagger-ui.html |
| OpenAPI Spec | http://localhost:8000/v3/api-docs |
| Gateway Health | http://localhost:8000/api/gateway/health |
| Gateway Info | http://localhost:8000/api/gateway/info |
| Actuator | http://localhost:8000/actuator/health |
| Circuit Breakers | http://localhost:8000/actuator/circuitbreakers |

## Next Steps

1. ✅ **Explore Swagger UI** - Try all endpoints
2. ✅ **Read Documentation** - Check README.md and SWAGGER_GUIDE.md
3. ✅ **Test Authentication** - Register, login, authorize
4. ✅ **Create Test Data** - Add doctors, services, appointments
5. ✅ **Integrate Frontend** - Use the API from your frontend app

## Need Help?

- 📖 **Complete Guide**: [README.md](./README.md)
- 📚 **Swagger Guide**: [SWAGGER_GUIDE.md](./SWAGGER_GUIDE.md)
- 📋 **API Reference**: [API_REFERENCE.md](./API_REFERENCE.md)
- 📝 **Setup Summary**: [SETUP_SUMMARY.md](./SETUP_SUMMARY.md)

---

**Happy Coding! 🎉**
