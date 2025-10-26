# Quick Start Guide - Auth Service

## Prerequisites Check
- [ ] Java 21 installed (`java -version`)
- [ ] Maven installed (`mvn -version`)
- [ ] PostgreSQL installed and running
- [ ] Git installed (optional)

## Step-by-Step Setup

### 1. Database Setup (5 minutes)

#### Option A: Using PostgreSQL CLI
```bash
# Connect to PostgreSQL
psql -U postgres

# Create database
CREATE DATABASE auth_db;

# Exit
\q
```

#### Option B: Using Docker
```bash
docker run --name auth-postgres -e POSTGRES_DB=auth_db -e POSTGRES_PASSWORD=postgres -p 5432:5432 -d postgres:15-alpine
```

### 2. Configure Application (2 minutes)

Edit `src/main/resources/application.properties`:

**Required changes:**
```properties
# Update database credentials if needed
spring.datasource.username=your_username
spring.datasource.password=your_password

# Generate and set a secure JWT secret
jwt.secret=YOUR_SECURE_SECRET_KEY_HERE

# Configure email (for Gmail)
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
```

**To generate a secure JWT secret:**
```bash
# On Linux/Mac
openssl rand -base64 64

# On Windows PowerShell
[Convert]::ToBase64String((1..64 | ForEach-Object { Get-Random -Maximum 256 }))
```

### 3. Build and Run (2 minutes)

```bash
# Navigate to project directory
cd C:\SmartAppointmentBookingSystem\auth-service

# Build the project
mvnw clean install

# Run the application
mvnw spring-boot:run
```

### 4. Verify Installation (1 minute)

Open your browser and check:
- Health Check: http://localhost:8081/api/v1/health
- Swagger UI: http://localhost:8081/swagger-ui/index.html

You should see:
```json
{
  "status": "UP",
  "service": "auth-service",
  "version": "1.0.0"
}
```

## Test the API (5 minutes)

### Using Swagger UI (Recommended for beginners)
1. Open http://localhost:8081/swagger-ui/index.html
2. Expand "Authentication" section
3. Try "POST /api/v1/auth/register" endpoint
4. Click "Try it out"
5. Use sample data and click "Execute"

### Using cURL

**1. Register a user:**
```bash
curl -X POST http://localhost:8081/api/v1/auth/register ^
  -H "Content-Type: application/json" ^
  -d "{\"name\":\"Test User\",\"email\":\"test@example.com\",\"password\":\"Test@123\",\"role\":\"PATIENT\"}"
```

**2. Check email for verification token** (or check logs if email isn't configured)

**3. Verify email:**
```bash
curl -X POST http://localhost:8081/api/v1/auth/verify-email ^
  -H "Content-Type: application/json" ^
  -d "{\"token\":\"YOUR_TOKEN_HERE\"}"
```

**4. Login:**
```bash
curl -X POST http://localhost:8081/api/v1/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"email\":\"test@example.com\",\"password\":\"Test@123\"}"
```

**5. Copy the `accessToken` from response and test protected endpoint:**
```bash
curl -X GET http://localhost:8081/api/v1/auth/me ^
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

### Using Postman
1. Import collection from `postman/Auth-Service-API.postman_collection.json`
2. Set environment variable `base_url` to `http://localhost:8081`
3. Run requests in order

## Docker Deployment (Alternative)

If you prefer Docker:

```bash
# Start both database and application
docker-compose up -d

# Check logs
docker-compose logs -f auth-service

# Stop services
docker-compose down
```

## Troubleshooting

### Issue: "Port 8081 already in use"
**Solution:** Change port in `application.properties`:
```properties
server.port=8082
```

### Issue: "Cannot connect to database"
**Solution:** 
- Check PostgreSQL is running: `psql -U postgres -c "SELECT 1"`
- Verify credentials in `application.properties`
- Check database exists: `psql -U postgres -l`

### Issue: "Email not sending"
**Solution:** 
- Email errors won't prevent registration/login
- Check application logs for email errors
- For Gmail: Enable 2FA and generate App Password
- For testing: Check console logs for token values

### Issue: "Invalid JWT secret"
**Solution:** 
- Ensure JWT secret is at least 256 bits (32+ characters)
- Don't use spaces in the secret
- Use base64 encoded string

## Next Steps

1. **Configure Production Database**: Update connection string for production
2. **Set up Email Service**: Configure SMTP properly for email notifications
3. **Secure JWT Secret**: Use environment variables instead of properties file
4. **Enable HTTPS**: Configure SSL/TLS for production
5. **Add Monitoring**: Integrate with monitoring tools
6. **Set up CI/CD**: Automate deployment pipeline

## Useful Commands

```bash
# Clean build
mvnw clean

# Run tests
mvnw test

# Package without tests
mvnw package -DskipTests

# Run with specific profile
mvnw spring-boot:run -Dspring-boot.run.profiles=prod

# Check dependencies
mvnw dependency:tree

# Update dependencies
mvnw versions:display-dependency-updates
```

## Support

- **Documentation**: See `README.md` for detailed information
- **API Docs**: http://localhost:8081/swagger-ui/index.html
- **Issues**: Check logs in console or `logs/` directory

## Security Notes

⚠️ **Before Production:**
- [ ] Change default JWT secret
- [ ] Use environment variables for sensitive data
- [ ] Enable HTTPS/SSL
- [ ] Set up proper CORS configuration
- [ ] Configure rate limiting
- [ ] Enable security headers
- [ ] Set up proper logging and monitoring
- [ ] Review and update password policies
- [ ] Configure session timeout
- [ ] Enable audit logging

---

**Estimated Total Setup Time: 15 minutes**

Good luck! 🚀

