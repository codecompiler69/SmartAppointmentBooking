# Quick Start Guide - Smart Appointment Booking System

**Get the system running in 10 minutes!**

---

## ⚡ 10-Minute Quick Start (Local Development)

### Prerequisites Check
```powershell
# Check Java is installed
java -version

# Check Maven is installed
mvn -version

# Verify MySQL is installed
mysql --version

# Verify RabbitMQ is installed (and running)
# Windows: Check Services for RabbitMQ
# macOS/Linux: brew services list
```

### Step 1: Verify Database & RabbitMQ Services

#### Windows
```powershell
# Make sure MySQL is running
# Check: Services → MySQL80
# Or run mysql to verify
mysql -u root -p -e "SELECT @@version;"

# Make sure RabbitMQ is running
# Check: Services → RabbitMQ
```

#### macOS/Linux
```bash
# Start MySQL if not running
brew services start mysql

# Start RabbitMQ if not running
brew services start rabbitmq
```

### Step 2: Set Up Databases

Create and initialize the databases for each service:

```powershell
# Windows PowerShell
mysql -u root -p < auth-service/database/schema.sql
mysql -u root -p < user-service/database/schema.sql
mysql -u root -p < appointment-service/database/schema.sql
mysql -u root -p < service-catalog-service/database/schema.sql
mysql -u root -p < notification-service/database/schema.sql
```

```bash
# macOS/Linux
mysql -u root -p < auth-service/database/schema.sql
mysql -u root -p < user-service/database/schema.sql
mysql -u root -p < appointment-service/database/schema.sql
mysql -u root -p < service-catalog-service/database/schema.sql
mysql -u root -p < notification-service/database/schema.sql
```

### Step 3: Start All Microservices

Open **5 separate terminal/PowerShell windows** and run each command:

**Terminal 1 - Auth Service (Port 8001)**
```bash
cd auth-service
mvn spring-boot:run
```

**Terminal 2 - User Service (Port 8002)**
```bash
cd user-service
mvn spring-boot:run
```

**Terminal 3 - Appointment Service (Port 8003)**
```bash
cd appointment-service
mvn spring-boot:run
```

**Terminal 4 - Service Catalog Service (Port 8004)**
```bash
cd service-catalog-service
mvn spring-boot:run
```

**Terminal 5 - Notification Service (Port 8005)**
```bash
cd notification-service
mvn spring-boot:run
```

### Step 4: Access Services

Once all services are running, access them at:

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

### Check All Services Are Running
```powershell
# Windows: Check each port is responding
Invoke-WebRequest -Uri "http://localhost:8001/swagger-ui.html"
Invoke-WebRequest -Uri "http://localhost:8002/swagger-ui.html"
Invoke-WebRequest -Uri "http://localhost:8003/swagger-ui.html"
Invoke-WebRequest -Uri "http://localhost:8004/swagger-ui.html"
Invoke-WebRequest -Uri "http://localhost:8005/swagger-ui.html"

# Or use curl
curl http://localhost:8001/actuator/health
curl http://localhost:8002/actuator/health
curl http://localhost:8003/actuator/health
curl http://localhost:8004/actuator/health
curl http://localhost:8005/actuator/health
```

### Check Service Logs
Look at the terminal window where you started each service. You should see:
```
Started [ServiceName] in X.XXX seconds
```

### Access Databases
```powershell
# Connect to MySQL using mysql
mysql -u root -p

# In mysql shell, check tables
SHOW DATABASES;  # List databases
USE auth_db;  # Connect to auth_db
SHOW TABLES;  # List tables
SELECT COUNT(*) FROM users;  # Check user count
EXIT;  # Exit
```

### Access RabbitMQ Management
1. Open: http://localhost:15672
2. Login: `guest` / `guest`
3. Check Queues tab for `appointment.created.queue`, etc.

---

## 🛑 Stopping & Cleanup

### Stop Individual Services
Simply close the terminal windows or press `Ctrl+C` in each terminal

### Stop MySQL & RabbitMQ
```powershell
# Windows
# Services → Stop MySQL80 and RabbitMQ

# Or using command line:
net stop MySQL80
net stop RabbitMQ
```

```bash
# macOS/Linux
brew services stop mysql
brew services stop rabbitmq
```

---

## 🆘 Troubleshooting

### Services not starting?

**Check if Java is running properly:**
```powershell
# Windows PowerShell
Get-Process java

# Or check specific port
netstat -ano | findstr :8001
```

```bash
# macOS/Linux
ps aux | grep java
lsof -i :8001
```

### Database connection issues?

```bash
# Test MySQL connection
mysql -u root -p -e "SELECT @@version;"

# Check if MySQL service is running
mysqladmin -u root -p status
```

### RabbitMQ issues?

```powershell
# Windows: Check if RabbitMQ service is running
Get-Service RabbitMQ

# Or access the dashboard
# http://localhost:15672 (guest/guest)
```

### Port already in use?

```powershell
# Windows: Find what's using the port
netstat -ano | findstr :8001

# Kill the process (replace PID)
taskkill /PID <PID> /F
```

```bash
# macOS/Linux
lsof -i :8001
kill -9 <PID>
```

### Service compilation errors?

```bash
# Clean and rebuild a service
cd auth-service
mvn clean compile

# Run with debug output
mvn -X spring-boot:run
```

---

## 📚 Next Steps

### 1. **Explore API Documentation**
Visit each service's Swagger UI:
- Auth: http://localhost:8001/swagger-ui.html
- User: http://localhost:8002/swagger-ui.html
- Appointment: http://localhost:8003/swagger-ui.html
- Catalog: http://localhost:8004/swagger-ui.html
- Notification: http://localhost:8005/swagger-ui.html

### 2. **Read Architecture Documentation**
Open: `ARCHITECTURE.md`

### 3. **Review Implementation Details**
Open: `IMPLEMENTATION_SUMMARY.md`

### 4. **Try Postman**
Import: `auth-service/postman/Auth-Service-API.postman_collection.json`

### 5. **Review Source Code**
- Auth Service logic: `auth-service/src/main/java/.../`
- Database schemas: `*/database/schema.sql`

---

## 🚀 Common Operations

### Fresh Start
```bash
# 1. Stop all services (close all terminal windows)
# 2. Stop MySQL and RabbitMQ
# 3. Delete databases
mysql -u root -p -e "DROP DATABASE IF EXISTS auth_db;"
mysql -u root -p -e "DROP DATABASE IF EXISTS user_db;"
# ... repeat for all databases

# 4. Recreate databases by running schemas
mysql -u root -p < auth-service/database/schema.sql
# ... repeat for all services

# 5. Start all services again
```

### View Logs in Real-time

In each terminal where a service is running:
```bash
# Logs are printed to console automatically
# Ctrl+C to stop the service
```

### Connect to MySQL Directly
```bash
# Connect
mysql -u root -p

# List databases
SHOW DATABASES;

# Connect to a database
USE auth_db;

# List tables
SHOW TABLES;

# Query data
SELECT email, first_name FROM users;
SELECT COUNT(*) FROM appointments;

# Exit
EXIT;
```

### Check Environment & Configuration
Each service reads configuration from:
- `src/main/resources/application.properties`
- `src/main/resources/application-dev.properties` (if present)

---

## 💡 Pro Tips

### Tip 1: Use Swagger UI for Testing
- More intuitive than curl
- Auto-complete parameters
- Shows request/response models
- No need to copy tokens manually

### Tip 2: Use MySQL CLI for Queries
```bash
# Quick query without entering interactive mode
mysql -u root -p -e "SELECT * FROM auth_db.users;"
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
