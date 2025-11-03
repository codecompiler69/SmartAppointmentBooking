# API Gateway - Quick Reference

## 🚀 Quick Start

### Access Points
- **Gateway**: http://localhost:8000
- **Swagger UI**: http://localhost:8000/swagger-ui.html
- **Health Check**: http://localhost:8000/api/gateway/health

### Get Started in 3 Steps
1. **Register**: `POST /api/auth/register`
2. **Login**: `POST /api/auth/login` → Get token
3. **Authorize**: Add `Authorization: Bearer <token>` header

---

## 📋 Service Ports

| Service | Port | Swagger |
|---------|------|---------|
| API Gateway | 8000 | http://localhost:8000/swagger-ui.html |
| Auth Service | 8001 | http://localhost:8001/swagger-ui.html |
| User Service | 8002 | http://localhost:8002/swagger-ui.html |
| Appointment Service | 8003 | http://localhost:8003/swagger-ui.html |
| Service Catalog | 8004 | http://localhost:8004/swagger-ui.html |
| Notification Service | 8005 | http://localhost:8005/swagger-ui.html |

---

## 🔐 Authentication Endpoints

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/api/auth/register` | Register new user | ❌ |
| POST | `/api/auth/login` | Login and get tokens | ❌ |
| POST | `/api/auth/refresh` | Refresh access token | ❌ |
| POST | `/api/auth/logout` | Logout user | ✅ |
| GET | `/api/auth/validate` | Validate token | ✅ |

---

## 👤 User Endpoints

| Method | Endpoint | Description | Role |
|--------|----------|-------------|------|
| GET | `/api/users/profile` | Get current user profile | ALL |
| PUT | `/api/users/profile` | Update user profile | ALL |
| GET | `/api/users/{id}` | Get user by ID | ALL |
| GET | `/api/users/doctors` | List all doctors | ALL |
| POST | `/api/users/doctors/profile` | Create doctor profile | DOCTOR |
| PUT | `/api/users/doctors/profile` | Update doctor profile | DOCTOR |
| GET | `/api/users/doctors/{id}` | Get doctor by ID | ALL |

---

## 📅 Appointment Endpoints

| Method | Endpoint | Description | Role |
|--------|----------|-------------|------|
| POST | `/api/appointments` | Create appointment | PATIENT |
| GET | `/api/appointments/{id}` | Get appointment by ID | ALL |
| GET | `/api/appointments/patient/{id}` | Get patient appointments | PATIENT |
| GET | `/api/appointments/doctor/{id}` | Get doctor appointments | DOCTOR |
| PUT | `/api/appointments/{id}` | Update appointment | ALL |
| DELETE | `/api/appointments/{id}` | Cancel appointment | ALL |
| GET | `/api/appointments` | Get all appointments | ADMIN |

---

## 🏥 Service Catalog Endpoints

| Method | Endpoint | Description | Role |
|--------|----------|-------------|------|
| GET | `/api/services` | Get all services | ALL |
| GET | `/api/services/public` | Get public services | NONE |
| GET | `/api/services/{id}` | Get service by ID | ALL |
| POST | `/api/services` | Create service | ADMIN |
| PUT | `/api/services/{id}` | Update service | ADMIN |
| DELETE | `/api/services/{id}` | Delete service | ADMIN |
| GET | `/api/services/category/{category}` | Get services by category | ALL |

---

## 📧 Notification Endpoints

| Method | Endpoint | Description | Role |
|--------|----------|-------------|------|
| POST | `/api/notifications/email` | Send email | ALL |
| POST | `/api/notifications/sms` | Send SMS | ALL |
| GET | `/api/notifications/user/{id}` | Get user notifications | ALL |
| GET | `/api/notifications/{id}` | Get notification by ID | ALL |
| GET | `/api/notifications` | Get all notifications | ADMIN |

---

## 🛡️ Roles & Permissions

### PATIENT
- ✅ Book appointments
- ✅ View own appointments
- ✅ Update own profile
- ✅ Browse services and doctors

### DOCTOR
- ✅ View assigned appointments
- ✅ Create/update doctor profile
- ✅ Update appointment status
- ✅ View patient information

### ADMIN
- ✅ Full access to all endpoints
- ✅ Manage services
- ✅ View all appointments
- ✅ Manage users

---

## 📝 Request Examples

### Register
```bash
POST /api/auth/register
{
  "email": "patient@example.com",
  "password": "Password123!",
  "firstName": "John",
  "lastName": "Doe",
  "phone": "+1234567890",
  "role": "PATIENT"
}
```

### Login
```bash
POST /api/auth/login
{
  "email": "patient@example.com",
  "password": "Password123!"
}
```

### Create Appointment
```bash
POST /api/appointments
Authorization: Bearer <token>
{
  "doctorId": 2,
  "patientId": 1,
  "serviceId": 1,
  "appointmentDate": "2025-11-05T10:00:00",
  "notes": "Regular checkup"
}
```

---

## 🔢 HTTP Status Codes

| Code | Meaning |
|------|---------|
| 200 | ✅ Success |
| 201 | ✅ Created |
| 204 | ✅ No Content (Deleted) |
| 400 | ❌ Bad Request |
| 401 | ❌ Unauthorized |
| 403 | ❌ Forbidden |
| 404 | ❌ Not Found |
| 409 | ❌ Conflict |
| 500 | ❌ Server Error |
| 503 | ❌ Service Unavailable |

---

## ⚡ Circuit Breaker

When a service is down, you'll receive a fallback response:

```json
{
  "message": "Service is temporarily unavailable. Please try again later.",
  "status": "SERVICE_UNAVAILABLE"
}
```

**Configuration:**
- Failure Rate Threshold: 50%
- Wait Duration: 10 seconds
- Timeout: 3 seconds

---

## 🔑 JWT Token

### Structure
```
Authorization: Bearer <access-token>
```

### Token Expiry
- **Access Token**: 24 hours
- **Refresh Token**: 7 days

### Refresh Token
```bash
POST /api/auth/refresh
{
  "refreshToken": "<your-refresh-token>"
}
```

---

## 🐛 Troubleshooting

| Issue | Solution |
|-------|----------|
| 401 Unauthorized | Login and get new token |
| 403 Forbidden | Check if you have correct role |
| 503 Service Unavailable | Check if backend service is running |
| Token expired | Use refresh token endpoint |
| CORS error | Verify allowed origins in config |

---

## 📚 Documentation Links

- **Swagger UI**: http://localhost:8000/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8000/v3/api-docs
- **Gateway Health**: http://localhost:8000/api/gateway/health
- **Gateway Info**: http://localhost:8000/api/gateway/info
- **Actuator**: http://localhost:8000/actuator/health

---

## 🎯 Common Workflows

### 1️⃣ New Patient Registration Flow
```
Register → Login → View Services → Find Doctor → Book Appointment
```

### 2️⃣ Doctor Setup Flow
```
Register (DOCTOR) → Login → Create Doctor Profile → View Appointments
```

### 3️⃣ Appointment Management Flow
```
Login → Create Appointment → View Appointments → Update Status
```

---

## 💡 Pro Tips

1. **Use Swagger UI** for interactive testing
2. **Copy cURL** commands from Swagger for scripts
3. **Download OpenAPI spec** for client generation
4. **Check circuit breaker** status at `/actuator/circuitbreakers`
5. **Monitor health** at `/actuator/health`

---

## 📞 Support

- **Email**: support@smartappointmentbooking.com
- **Logs**: Check service logs for errors
- **Health**: `GET /actuator/health`
