# Quick Start Guide - Smart Appointment Booking System

**Get the system running in 5 minutes!**

---

## ⚡ 5-Minute Quick Start

### Prerequisites Check
```bash
# Check Docker is installed
docker --version

# Check Docker Compose is installed
docker-compose --version
```

### Step 1: Clone/Navigate to Project
```bash
cd SmartAppointmentBookingSystem
```

### Step 2: Start Everything with One Command
```bash
# Linux/Mac
docker-compose up -d

# Windows PowerShell
docker-compose up -d
```

### Step 3: Wait for Services (30-60 seconds)
```bash
# Watch the logs
docker-compose logs -f

# Or check individual services
docker-compose ps
```

### Step 4: Access Services

| Service | URL |
|---------|-----|
| **Auth API** | http://localhost:8001/swagger-ui.html |
| **User API** | http://localhost:8002/swagger-ui.html |
| **Appointment API** | http://localhost:8003/swagger-ui.html |
| **Service Catalog API** | http://localhost:8004/swagger-ui.html |
| **Notification API** | http://localhost:8005/swagger-ui.html |
| **RabbitMQ Dashboard** | http://localhost:15672 (guest/guest) |

---

## 🧪 Test the System (5 Minutes)

### Step 1: Register as Doctor
```bash
curl -X POST http://localhost:8001/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "doctor@test.com",
    "firstName": "John",
    "lastName": "Smith",
    "password": "Doctor@123",
    "phoneNumber": "+1234567890",
    "role": "ROLE_DOCTOR"
  }'
```

**Response**: Save the `access_token` and `refresh_token`

### Step 2: Register as Patient
```bash
curl -X POST http://localhost:8001/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "patient@test.com",
    "firstName": "Jane",
    "lastName": "Doe",
    "password": "Patient@123",
    "phoneNumber": "+9876543210",
    "role": "ROLE_PATIENT"
  }'
```

**Response**: Save the `access_token`

### Step 3: Create a Medical Service
```bash
DOCTOR_TOKEN="<your_doctor_access_token>"

curl -X POST http://localhost:8004/api/v1/services \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $DOCTOR_TOKEN" \
  -d '{
    "name": "General Consultation",
    "description": "30-minute general doctor consultation",
    "category": "general",
    "durationMinutes": 30,
    "basePrice": 50.00,
    "isActive": true
  }'
```

**Response**: Save the `id` (let's call it SERVICE_ID)

### Step 4: Book an Appointment
```bash
PATIENT_TOKEN="<your_patient_access_token>"
DOCTOR_ID=1
SERVICE_ID=1

curl -X POST http://localhost:8003/api/v1/appointments \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $PATIENT_TOKEN" \
  -d '{
    "doctorId": '$DOCTOR_ID',
    "patientId": 2,
    "serviceId": '$SERVICE_ID',
    "appointmentDate": "2025-11-15T10:00:00",
    "notes": "Initial consultation"
  }'
```

**Response**: You should get appointment details with status "SCHEDULED"

### Step 5: Confirm the Appointment
```bash
APPOINTMENT_ID=1

curl -X POST http://localhost:8003/api/v1/appointments/$APPOINTMENT_ID/confirm \
  -H "Authorization: Bearer $DOCTOR_TOKEN"
```

**Result**: Status changes from "SCHEDULED" to "CONFIRMED"

### Step 6: Check Notifications
```bash
curl -X GET http://localhost:8005/api/v1/email-logs \
  -H "Authorization: Bearer $PATIENT_TOKEN"
```

**Result**: See notification logs for email sent

---

## 🔍 Verify Everything Works

### Check All Containers Are Running
```bash
docker-compose ps

# Should show:
# - postgres (healthy)
# - rabbitmq (healthy)
# - auth-service (Up)
# - user-service (Up)
# - appointment-service (Up)
# - service-catalog-service (Up)
# - notification-service (Up)
```

### Check Service Logs
```bash
# All services
docker-compose logs

# Specific service
docker-compose logs auth-service
docker-compose logs appointment-service
docker-compose logs notification-service
```

### Access Databases
```bash
# Connect to PostgreSQL
docker exec -it appointment_db psql -U postgres -d appointment_db

# In psql shell:
\dt  # List tables
SELECT COUNT(*) FROM users;  # Check user count
SELECT * FROM refresh_tokens;  # Check tokens
```

### Access RabbitMQ Management
1. Open: http://localhost:15672
2. Login: `guest` / `guest`
3. Check Queues tab for `appointment.created.queue`, etc.

---

## 🛑 Stopping & Cleanup

### Stop All Services (keep data)
```bash
docker-compose stop
```

### Stop & Remove Containers (keep data)
```bash
docker-compose down
```

### Full Cleanup (remove everything)
```bash
docker-compose down -v  # -v removes volumes (data!)
```

### Restart Services
```bash
docker-compose restart
```

---

## 🆘 Troubleshooting

### Services not starting?
```bash
# Check logs
docker-compose logs

# Rebuild containers
docker-compose build --no-cache

# Start fresh
docker-compose up --force-recreate
```

### Database connection issues?
```bash
# Check PostgreSQL is running
docker-compose logs postgres

# Wait for PostgreSQL to be healthy
docker-compose ps postgres

# Test connection
docker exec -it appointment_db psql -U postgres -c "SELECT 1"
```

### RabbitMQ issues?
```bash
# Check RabbitMQ logs
docker-compose logs rabbitmq

# Restart RabbitMQ
docker-compose restart rabbitmq

# Access dashboard
# http://localhost:15672 (guest/guest)
```

### Port already in use?
```bash
# Find what's using the port (Mac/Linux)
lsof -i :8001

# Kill the process (replace 12345 with PID)
kill -9 12345

# Or change the port in docker-compose.yml
```

---

## 📚 Next Steps

### 1. **Explore API Documentation**
Visit: http://localhost:8001/swagger-ui.html

### 2. **Read Architecture Documentation**
Open: `ARCHITECTURE.md`

### 3. **Review Implementation Details**
Open: `IMPLEMENTATION_SUMMARY.md`

### 4. **Try Postman**
Import: `auth-service/postman/Auth-Service-API.postman_collection.json`

### 5. **Review Source Code**
- Auth Service logic: `auth-service/src/main/java/.../auth_service/`
- Database schemas: `*/database/schema.sql`

---

## 🚀 Common Operations

### Reset Everything to Fresh Start
```bash
docker-compose down -v  # Remove all data
docker-compose build --no-cache  # Rebuild
docker-compose up -d  # Start fresh
```

### View Real-time Logs
```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f auth-service

# Last 50 lines
docker-compose logs --tail=50
```

### Execute Commands in Container
```bash
# Connect to PostgreSQL
docker-compose exec postgres psql -U postgres -d appointment_db

# List all users
# In psql: SELECT email, first_name FROM users;

# Check appointment count
# In psql: SELECT COUNT(*) FROM appointments;
```

### View Environment Variables
```bash
docker-compose config | grep -E "environment|ports|SPRING_"
```

---

## 💡 Pro Tips

### Tip 1: Use Swagger UI for Testing
- More intuitive than curl
- Auto-complete parameters
- Shows request/response models
- No need to copy tokens manually

### Tip 2: Keep a Terminal Open for Logs
```bash
# In one terminal, watch logs while testing
docker-compose logs -f
```

### Tip 3: Save Tokens to Shell Variables
```bash
# After registration, extract token
TOKEN=$(echo $RESPONSE | jq -r '.access_token')
echo $TOKEN  # Use in subsequent requests
```

### Tip 4: Use Postman for Complex Workflows
- Save authentication flows as collections
- Automate multi-step tests
- Share with team members

### Tip 5: Check RabbitMQ for Message Flow
- Visit http://localhost:15672
- Go to Queues tab
- See real-time message counts

---

## 🎯 Success Checklist

- [ ] All containers running (`docker-compose ps`)
- [ ] Can access Auth API (http://localhost:8001/swagger-ui.html)
- [ ] Registered a doctor user successfully
- [ ] Registered a patient user successfully
- [ ] Created a medical service
- [ ] Booked an appointment
- [ ] Confirmed the appointment
- [ ] Saw email notification logged
- [ ] Checked RabbitMQ dashboard
- [ ] Read README.md & ARCHITECTURE.md

✅ **If all above are checked: Your system is fully functional!**

---

## 📞 Need Help?

1. **Check Logs**: `docker-compose logs -f`
2. **Read README**: `README.md`
3. **Read Architecture**: `ARCHITECTURE.md`
4. **API Docs**: http://localhost:8001/swagger-ui.html
5. **RabbitMQ Dashboard**: http://localhost:15672

---

**Congratulations! Your Smart Appointment Booking System is running! 🎉**

Start with Step 1 in the "5-Minute Quick Start" section above.
