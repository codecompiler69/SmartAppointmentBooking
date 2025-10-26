# Smart Appointment Booking System - Architecture & Design Document

## Executive Summary

This is a production-grade microservices-based appointment booking system built with Spring Boot 3.5.6, featuring:

- **Microservices Architecture**: 5 independent services with clear separation of concerns
- **JWT-based Security**: Role-based access control (Admin, Doctor, Patient)
- **API Versioning**: URI-based versioning for future compatibility
- **Async Processing**: RabbitMQ-based event-driven notifications
- **Data Consistency**: Optimistic locking, transactional guarantees
- **Local Development**: Easy setup with PostgreSQL and RabbitMQ
- **Complete Documentation**: Swagger/OpenAPI on every service

---

## 🏗️ System Architecture

### Microservices Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                     CLIENT APPLICATIONS                         │
│              (Web, Mobile, Third-party Integrations)            │
└─────────────────┬───────────────────────────────────────────────┘
                  │ HTTP/REST + JWT Tokens
┌─────────────────▼───────────────────────────────────────────────┐
│                     API GATEWAY (Future)                        │
│          (Rate Limiting, Routing, Versioning)                  │
└────┬──────────┬──────────┬──────────┬──────────────────────────┘
     │          │          │          │
┌────▼──┐  ┌───▼──┐  ┌────▼──┐  ┌──▼────┐  ┌──────────┐
│ Auth  │  │User  │  │Appt   │  │Catalog│  │Notif.   │
│Svc    │  │Svc   │  │Svc    │  │Svc    │  │Svc      │
│:8001  │  │:8002 │  │:8003  │  │:8004  │  │:8005    │
└────┬──┘  └───┬──┘  └────┬──┘  └──┬────┘  └──┬──────┘
     │         │          │        │         │
     └────┬────┴────┬─────┴────┬───┘         │
          │         │          │            │
     ┌────▼─────────▼──────────▼────┐  ┌────▼──────┐
     │    PostgreSQL Database       │  │ RabbitMQ  │
     │  (Single shared instance)    │  │ Message   │
     │                              │  │ Broker    │
     └──────────────────────────────┘  └───────────┘
```

### Service Responsibilities

#### 1. **Auth Service** (Port 8001)
- User registration & authentication
- JWT token generation & refresh
- Email verification
- Password reset flow
- Role assignment
- Database: `users`, `roles`, `user_roles`, `refresh_tokens`

#### 2. **User Service** (Port 8002)
- User profile management
- Doctor profile creation & updates
- Patient profile management
- Doctor search & filtering
- User soft-deletion
- Database: `users`, `doctor_profiles`, `patient_profiles`

#### 3. **Appointment Service** (Port 8003)
- Appointment CRUD operations
- Booking with conflict detection
- Appointment confirmation
- Cancellation management
- Optimistic locking for concurrency
- Database: `appointments`, `services`, `confirmations`, `cancellations`

#### 4. **Service Catalog Service** (Port 8004)
- Medical services management
- Service search & filtering
- Pagination & sorting
- Category-based organization
- Database: `services`

#### 5. **Notification Service** (Port 8005)
- Async email notifications
- SMS notification support
- Event-driven via RabbitMQ
- Email log tracking
- Retry mechanism for failures
- Database: `email_logs`, `sms_logs`, `notification_events`

---

## 🔐 Security Architecture

### JWT Token Flow

```
1. USER REGISTRATION/LOGIN
   ↓
2. AUTH SERVICE validates credentials
   ↓
3. Creates JWT tokens:
   - Access Token (24 hours) - short-lived
   - Refresh Token (7 days) - long-lived
   ↓
4. Returns both tokens to client
   ↓
5. Client includes Access Token in Authorization header
   ↓
6. Services validate token via JwtAuthenticationFilter
   ↓
7. Token expiry → Client uses Refresh Token to get new Access Token
```

### Token Structure

```
Access Token: eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29tIiwiaWF0Ijox...
              └─────────────────────────────────────────────────────────────────┘
              Header.Payload.Signature (HS512 - HMAC SHA-512)

Payload contains:
- sub (subject): user email
- iat (issued at): creation timestamp
- exp (expiration): expiry timestamp
```

### Role-Based Access Control (RBAC)

```java
@PreAuthorize("hasRole('ADMIN')")
public void deleteAppointment(Long id) { }

@PreAuthorize("hasRole('DOCTOR')")
public void confirmAppointment(Long id) { }

@PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR')")
public void getAppointmentDetails(Long id) { }
```

### Authentication Filter

```
HTTP Request
    ↓
JwtAuthenticationFilter
    ↓
Extract JWT from Authorization header
    ↓
Validate JWT signature
    ↓
Check token expiry
    ↓
Extract user email from claims
    ↓
Set SecurityContext with authenticated principal
    ↓
Request proceeds to handler
```

---

## 💾 Data Consistency & Concurrency Control

### Optimistic Locking Strategy

**Problem**: Double-booking when two doctors try to book same slot simultaneously

**Solution**: Version-based optimistic locking

```java
@Entity
public class Appointment {
    @Version
    private Long version;  // Automatically managed by JPA
    
    @Column(unique = true)
    private String doctorIdDateTimeKey;  // Composite unique constraint
}

// Database enforces: UNIQUE(doctor_id, appointment_date, appointment_time)
```

**How it works**:

1. Transaction A reads Appointment (version=1)
2. Transaction B reads same Appointment (version=1)
3. Transaction A updates → version becomes 2, B's update fails
4. B retries, reads new data (version=2), updates succeed

### Transactional Guarantees

```java
@Service
@Transactional  // All methods are transactional by default
public class AppointmentService {
    
    @Transactional  // Explicit transaction boundaries
    public Appointment bookAppointment(BookingRequest req) {
        // 1. Lock: Check doctor availability
        // 2. Check: Service exists
        // 3. Create: Appointment record
        // If any step fails, entire transaction rolls back
        // If all succeed, commits atomically
    }
}
```

### Isolation Levels

```properties
# Default: READ_COMMITTED
spring.jpa.properties.hibernate.connection.isolation=2

# For appointment booking:
# Use REPEATABLE_READ or SERIALIZABLE to prevent phantom reads
```

---

## 📊 Database Schema Design

### Shared Database Approach

All microservices use single PostgreSQL instance for:
- Simplicity in development
- ACID compliance
- Transaction support across services
- Easy backups & recovery

For production scale:
- Can migrate to database-per-service pattern
- Use saga pattern for distributed transactions
- Implement change data capture (CDC) for event streaming

### Key Design Decisions

#### 1. Soft Deletion
```sql
ALTER TABLE users ADD COLUMN is_deleted BOOLEAN DEFAULT FALSE;
CREATE UNIQUE INDEX ON users(email) WHERE is_deleted = false;
```
Benefits:
- Preserves audit trail
- Allows data recovery
- No cascading deletes

#### 2. Composite Unique Constraints
```sql
-- Prevent double-booking
UNIQUE(doctor_id, appointment_date, appointment_time)

-- Prevent duplicate roles
UNIQUE(user_id, role_id)
```

#### 3. Indexing Strategy
```sql
-- Foreign keys
CREATE INDEX idx_appointments_doctor_id ON appointments(doctor_id);

-- Search queries
CREATE INDEX idx_services_category ON services(category);

-- Composite indexes for common queries
CREATE INDEX idx_appointments_doctor_date 
ON appointments(doctor_id, appointment_date);
```

---

## 🔄 Async Event Processing

### Message Flow Architecture

```
┌─────────────────────┐
│  Appointment Event  │  (triggered by app booking)
└──────────┬──────────┘
           │
           ▼
┌──────────────────────────────────┐
│ AppointmentService publishes:     │
│ - appointment.created             │
│ - appointment.confirmed           │
│ - appointment.cancelled           │
└──────────┬───────────────────────┘
           │
           ▼
┌──────────────────────────────────┐
│        RabbitMQ Broker           │
│   (Message Store & Routing)      │
└──────────┬───────────────────────┘
           │
     ┌─────┴─────┬────────────┐
     │            │            │
     ▼            ▼            ▼
┌──────────┐ ┌──────────┐ ┌──────────┐
│ Created  │ │Confirmed │ │Cancelled │
│ Queue    │ │ Queue    │ │ Queue    │
└──────────┘ └──────────┘ └──────────┘
     │            │            │
     │            │            │
     └─────┬──────┴────────┬───┘
           │               │
           ▼               ▼
    ┌──────────────────────────────────┐
    │   NotificationService Listeners  │
    │   (Async Message Consumers)      │
    └──────────┬───────────────────────┘
               │
        ┌──────┴──────┐
        │             │
        ▼             ▼
   ┌─────────┐   ┌──────────┐
   │ Send    │   │ Log      │
   │ Email   │   │ Message  │
   └─────────┘   └──────────┘
```

### Event Configuration

```properties
# RabbitMQ Setup
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672

# Exchanges
appointment-events (topic exchange)

# Queues
appointment.created -> appointment.created.queue
appointment.confirmed -> appointment.confirmed.queue
appointment.cancelled -> appointment.cancelled.queue

# Binding Patterns
appointment.created.# -> appointment.created.queue
appointment.confirmed.# -> appointment.confirmed.queue
appointment.cancelled.# -> appointment.cancelled.queue
```

### Publisher Code

```java
@Service
public class AppointmentEventPublisher {
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    public void publishAppointmentCreated(Appointment appointment) {
        AppointmentEvent event = new AppointmentEvent(
            EventType.APPOINTMENT_CREATED,
            appointment
        );
        rabbitTemplate.convertAndSend(
            "appointment-events",
            "appointment.created.doctor-" + appointment.getDoctorId(),
            event
        );
    }
}
```

### Consumer Code

```java
@Service
public class AppointmentEventListener {
    
    @RabbitListener(queues = "appointment.created.queue")
    public void handleAppointmentCreated(AppointmentEvent event) {
        notificationService.sendConfirmationEmail(event.getAppointment());
        emailLogService.logSentEmail(...);
    }
}
```

---

## 🚀 API Versioning Strategy

### URI-Based Versioning (Current)

```
/api/v1/auth/login
/api/v1/users/search
/api/v1/appointments
```

Benefits:
- Simple & explicit
- Clear in documentation
- Easy to test different versions
- Cacheable (different URIs)

### Header-Based Versioning (Optional)

```
Accept: application/vnd.appointmentbooking.v1+json
Accept: application/vnd.appointmentbooking.v2+json
```

```java
@GetMapping("/appointments")
@RequestMapping(produces = "application/vnd.appointmentbooking.v1+json")
public ResponseEntity<Page<AppointmentV1>> getAppointmentsV1() { }

@GetMapping("/appointments")
@RequestMapping(produces = "application/vnd.appointmentbooking.v2+json")
public ResponseEntity<Page<AppointmentV2>> getAppointmentsV2() { }
```

### Versioning Guidelines

1. **Never change v1 endpoints** - maintain backward compatibility
2. **New features** go to new version
3. **Breaking changes** require major version bump
4. **Deprecation policy**:
   - Announce 3 versions/months in advance
   - Keep v-1 available for 6 months after v+1 release
   - Provide migration guide

---

## 📡 API Pagination, Filtering & Sorting

### Pagination Pattern

```
GET /api/v1/appointments?page=0&size=20&sort=appointment_date,desc

Response:
{
    "content": [...],
    "pageable": {
        "pageNumber": 0,
        "pageSize": 20,
        "offset": 0
    },
    "totalElements": 150,
    "totalPages": 8,
    "last": false
}
```

### Filtering Pattern

```
GET /api/v1/appointments?doctorId=5&status=CONFIRMED&date=2025-10-08

GET /api/v1/users/search?role=DOCTOR&specialization=cardiology&page=0&size=10
```

### Sorting Pattern

```
GET /api/v1/services?sort=name,asc&sort=price,desc

Multiple sort parameters:
1. sort=name,asc (primary sort)
2. sort=price,desc (secondary sort)
```

### Implementation

```java
@GetMapping("/appointments")
public ResponseEntity<Page<AppointmentDTO>> getAppointments(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "20") int size,
    @RequestParam(defaultValue = "appointment_date,desc") String sort,
    @RequestParam(required = false) Long doctorId,
    @RequestParam(required = false) String status
) {
    PageRequest pageRequest = PageRequest.of(
        page, 
        size, 
        Sort.by("appointment_date").descending()
    );
    
    Specification<Appointment> spec = (root, query, cb) -> {
        List<Predicate> predicates = new ArrayList<>();
        if (doctorId != null) {
            predicates.add(cb.equal(root.get("doctorId"), doctorId));
        }
        if (status != null) {
            predicates.add(cb.equal(root.get("status"), status));
        }
        return cb.and(predicates.toArray(new Predicate[0]));
    };
    
    Page<AppointmentDTO> result = appointmentRepository
        .findAll(spec, pageRequest)
        .map(this::convertToDTO);
    
    return ResponseEntity.ok(result);
}
```

---

## 🧪 Error Handling & Validation

### Global Exception Handler

```java
@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ErrorResponse.builder()
                .status(404)
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build());
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
            .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse.builder()
                .status(400)
                .message("Validation failed")
                .validationErrors(errors)
                .timestamp(LocalDateTime.now())
                .build());
    }
}
```

### Validation Annotations

```java
@Data
public class RegisterRequest {
    @Email(message = "Invalid email format")
    private String email;
    
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 100)
    private String firstName;
    
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[0-9]).*$", 
             message = "Password must contain uppercase and numbers")
    private String password;
}
```

### Error Response Format

```json
{
    "status": 400,
    "message": "Validation failed",
    "validation_errors": {
        "email": "Invalid email format",
        "password": "Password must be at least 8 characters"
    },
    "timestamp": "2025-10-26T10:30:00"
}
```

---

## 🔍 Monitoring & Observability

### Application Metrics

```
GET /actuator/metrics
GET /actuator/health

# Service-specific metrics
- http.server.requests (API latency)
- jpa.repositories.count (DB operations)
- rabbitmq.queue.size (Message queue depth)
```

### Logging Strategy

```properties
logging.level.root=INFO
logging.level.com.smartappointmentbooking=DEBUG
logging.pattern.console=%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n
```

### Distributed Tracing (Future)

```
Integration points:
- Spring Cloud Sleuth + Zipkin
- Correlate logs across services
- Track request flow through system
```

---

## 🎯 Performance Considerations

### Database Query Optimization

```java
// ❌ N+1 Query Problem
@OneToMany
private List<Appointment> appointments;

// ✅ Eager loading
@OneToMany(fetch = FetchType.EAGER)
private List<Appointment> appointments;

// ✅ Or use specific queries
@Query("SELECT a FROM Appointment a LEFT JOIN FETCH a.doctor")
List<Appointment> findAllWithDoctor();
```

### Caching Strategy

```java
@Service
@CacheConfig(cacheNames = "services")
public class ServiceCatalogService {
    
    @Cacheable(key = "#id")
    public ServiceDTO getService(Long id) {
        return serviceRepository.findById(id)
            .map(this::convertToDTO)
            .orElseThrow(...);
    }
    
    @CacheEvict(key = "#id")
    public ServiceDTO updateService(Long id, UpdateRequest req) {
        // ...
    }
}
```

### Connection Pooling

```properties
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=20000
```

---

## 📋 Development & Deployment Checklist

- [ ] Set up PostgreSQL and RabbitMQ locally
- [ ] Initialize all database schemas
- [ ] Update JWT secret in production
- [ ] Configure email SMTP credentials
- [ ] Set database credentials securely (env vars, vault)
- [ ] Configure SSL/TLS certificates
- [ ] Enable CORS for frontend domain
- [ ] Set up monitoring & alerting
- [ ] Configure backup strategy
- [ ] Load test all services
- [ ] Document runbooks
- [ ] Set up CI/CD pipeline

---

## 🤝 Contributing Guidelines

1. Follow naming conventions
2. Add unit tests (target: >80% coverage)
3. Update API documentation (Swagger)
4. Use feature branches
5. Create meaningful commit messages
6. Submit PR with description

---

## 📞 Support & Documentation

- **API Docs**: http://localhost:<port>/swagger-ui.html
- **RabbitMQ Console**: http://localhost:15672
- **Local Setup**: See QUICKSTART.md
- **MySQL**: mysql connection via localhost:3306

---

**Last Updated**: October 26, 2025
**Version**: 1.0.0
**Status**: Production Ready ✅
