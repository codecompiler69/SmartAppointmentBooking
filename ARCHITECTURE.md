# Smart Appointment Booking System - Architecture & Design Document

## Executive Summary

This is a production-grade microservices-based appointment booking system built with Spring Boot 3.4.5+, featuring:

- **Microservices Architecture**: 5 core services + 1 API Gateway with clear separation of concerns
- **API Gateway**: Centralized routing with Spring Cloud Gateway MVC (Port 8000)
- **JWT-based Security**: Role-based access control (Admin, Doctor, Patient)
- **API Versioning**: URI-based versioning for future compatibility
- **Data Consistency**: Transactional guarantees with optimized locking
- **Local Development**: Easy setup with MySQL databases
- **Complete Documentation**: Swagger/OpenAPI on every service + unified gateway documentation
- **Circuit Breaker**: Resilience4J for fault tolerance

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
│                     API GATEWAY :8000                           │
│    (Spring Cloud Gateway MVC - Routing, Security, Swagger)     │
│    Features: JWT Filter, Circuit Breaker, CORS, Logging        │
└────┬──────────┬──────────┬──────────┬──────────────────────────┘
     │          │          │          │          │
┌────▼──┐  ┌───▼──┐  ┌────▼──┐  ┌───▼───┐  ┌──▼──────┐
│ Auth  │  │User  │  │Appt   │  │Catalog│  │Notif.   │
│Svc    │  │Svc   │  │Svc    │  │Svc    │  │Svc      │
│:8001  │  │:8002 │  │:8003  │  │:8004  │  │:8005    │
└────┬──┘  └───┬──┘  └────┬──┘  └───┬───┘  └──┬──────┘
     │         │          │          │         │
     └────┬────┴────┬─────┴────┬─────┴─────────┘
          │         │          │
     ┌────▼─────────▼──────────▼────┐
     │      MySQL 8.0 Database      │
     │     (5 separate databases)   │
     │  auth_db, user_db, appt_db,  │
     │  catalog_db, notification_db │
     └──────────────────────────────┘
```

### Service Responsibilities

#### 0. **API Gateway** (Port 8000)
- **Technology**: Spring Cloud Gateway MVC 2025.0.0 (Servlet-based)
- **Purpose**: Single entry point for all microservices
- **Features**:
  - Centralized routing to all 5 microservices
  - JWT authentication filter for all requests
  - Circuit breaker with Resilience4J (50% failure threshold, 10s wait)
  - CORS configuration for cross-origin requests
  - Request/response logging
  - Unified Swagger documentation (aggregates all services)
  - Global exception handling
  - Fallback endpoints for circuit breaker
- **Key Components**:
  - `GatewayConfig.java` - Route definitions
  - `SecurityConfig.java` - JWT security configuration
  - `JwtAuthenticationFilter.java` - Token validation
  - `OpenApiConfig.java` - Swagger aggregation
  - 5 Documentation controllers (one per service)

#### 1. **Auth Service** (Port 8001)
- User registration & authentication
- JWT token generation & refresh
- Email verification
- Password reset flow
- Role assignment (ROLE_ADMIN, ROLE_DOCTOR, ROLE_PATIENT)
- **Database**: `auth_db`
  - Tables: `users`, `roles`, `user_roles` (join table), `refresh_tokens`
- **Key Features**:
  - BCrypt password hashing
  - Many-to-many user-role relationship
  - Refresh token rotation (7-day expiry)
  - Access token with 24-hour expiry

#### 2. **User Service** (Port 8002)
- User profile management (extended details)
- Doctor profile creation & updates
- Patient profile management
- Doctor search & filtering by specialization
- User soft-deletion
- **Database**: `user_db`
  - Tables: `users`, `doctor_profiles`
- **Key Features**:
  - One-to-one user-doctor relationship
  - 18 user profile fields (DOB, gender, address, emergency contacts, medical info)
  - 15 doctor profile fields (specialization, license, experience, fees, availability)

#### 3. **Appointment Service** (Port 8003)
- Appointment CRUD operations
- Booking with conflict detection
- Appointment confirmation & status management
- Cancellation management with reason tracking
- **Database**: `appointment_db`
  - Tables: `appointments`
- **Key Features**:
  - 6 appointment statuses (SCHEDULED, CONFIRMED, IN_PROGRESS, COMPLETED, CANCELLED, NO_SHOW)
  - Timestamp tracking (created, updated, cancelled)
  - Price calculation
  - Cancellation reason storage

#### 4. **Service Catalog Service** (Port 8004)
- Medical services management
- Service search & filtering by category
- Category-based organization
- **Database**: `service_catalog_db`
  - Tables: `services`
- **Key Features**:
  - 30+ pre-loaded medical services
  - 12+ service categories (Consultation, Diagnostics, Imaging, Vaccination, etc.)
  - Active/inactive toggle
  - Duration and pricing management

#### 5. **Notification Service** (Port 8005)
- Email notifications
- SMS notification support
- Multi-channel delivery (EMAIL, SMS)
- Notification tracking and status management
- **Database**: `notification_db`
  - Tables: `notifications`
- **Key Features**:
  - 8+ notification types (confirmations, reminders, cancellations, etc.)
  - Read/unread tracking
  - Status tracking (PENDING, SENT, FAILED, DELIVERED)
  - Error logging for failed notifications

---

## 🔐 Security Architecture

### JWT Token Flow

```
1. USER REGISTRATION/LOGIN
   ↓
2. AUTH SERVICE validates credentials
   ↓
3. Creates JWT tokens:
   - Access Token (24 hours)
   - Refresh Token (7 days)
   ↓
4. Stores refresh token in database
   ↓
5. Returns both tokens to client
   ↓
6. CLIENT stores tokens (localStorage/sessionStorage)
   ↓
7. SUBSEQUENT REQUESTS to any service via API Gateway:
   - Client sends: Authorization: Bearer {access_token}
   - Gateway validates JWT
   - Extracts user info (email, roles)
   - Forwards request to target service
   ↓
8. TOKEN REFRESH (when access token expires):
   - Client sends refresh token to /auth/refresh
   - Auth service validates refresh token
   - Issues new access token + new refresh token
   - Deletes old refresh token
   - Returns new tokens
```

### Role-Based Access Control

**Three Roles:**
1. **ROLE_ADMIN** - Full system access
2. **ROLE_DOCTOR** - Doctor-specific operations
3. **ROLE_PATIENT** - Patient-specific operations

**Implementation:**
- Many-to-many relationship (users ↔ roles via user_roles table)
- Roles stored as ENUM in database
- JWT contains role information
- Gateway validates role-based access
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

**Three Roles:**
- **ROLE_ADMIN** - Full system access, can delete appointments
- **ROLE_DOCTOR** - Doctor-specific operations like confirming appointments
- **ROLE_PATIENT** - Patient-specific operations

**Access Control:**
- Role-based authorization using Spring Security annotations
- Method-level security enforcement
- Multi-role access support for shared operations

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

**Solution**: Version-based optimistic locking using JPA @Version annotation

**Implementation Details:**
- Each appointment entity has a version field automatically managed by JPA
- Composite unique constraint on doctor_id, appointment_date, and appointment_time
- Database-level enforcement prevents duplicate bookings

**How it works**:

1. Transaction A reads Appointment (version=1)
2. Transaction B reads same Appointment (version=1)
3. Transaction A updates → version becomes 2, B's update fails
4. B retries, reads new data (version=2), updates succeed

### Transactional Guarantees

**Service-Level Transactions:**
- All service classes are marked as transactional by default
- Explicit transaction boundaries for critical operations
- Atomic operations ensure data consistency

**Appointment Booking Flow:**
1. Check doctor availability (with lock)
2. Validate service exists
3. Create appointment record
4. If any step fails, entire transaction rolls back
5. If all succeed, commits atomically

### Isolation Levels

**Default Configuration:**
- READ_COMMITTED isolation level (level 2)
- Configured via Hibernate properties

**Appointment Booking:**
- Uses REPEATABLE_READ or SERIALIZABLE to prevent phantom reads
- Ensures consistency during concurrent booking attempts

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
**Implementation:**
- Boolean flag (is_deleted) added to tables
- Partial unique index on email where is_deleted = false
- Allows email reuse after account deletion

**Benefits:**
- Preserves audit trail
- Allows data recovery
- No cascading deletes

#### 2. Composite Unique Constraints
**Appointments:**
- UNIQUE constraint on (doctor_id, appointment_date, appointment_time)
- Prevents double-booking at database level

**User Roles:**
- UNIQUE constraint on (user_id, role_id)
- Prevents duplicate role assignments

#### 3. Indexing Strategy
**Foreign Key Indexes:**
- Indexed on doctor_id in appointments table
- Improves join performance

**Search Query Indexes:**
- Category index on services table
- Enables fast category-based filtering

**Composite Indexes:**
- (doctor_id, appointment_date) for common appointment queries
- Optimizes date-range searches per doctor

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

**RabbitMQ Setup:**
- Host: localhost
- Port: 5672

**Exchanges:**
- appointment-events (topic exchange)

**Queues:**
- appointment.created.queue
- appointment.confirmed.queue
- appointment.cancelled.queue

**Binding Patterns:**
- appointment.created.# → appointment.created.queue
- appointment.confirmed.# → appointment.confirmed.queue
- appointment.cancelled.# → appointment.cancelled.queue

### Publisher Implementation

**AppointmentEventPublisher Service:**
- Uses RabbitTemplate for message publishing
- Creates event objects with event type and appointment data
- Publishes to appointment-events exchange
- Routing key includes doctor ID for targeted delivery

### Consumer Implementation

**AppointmentEventListener Service:**
- RabbitListener annotation for queue subscription
- Consumes messages from specific queues
- Triggers notification sending (email confirmations)
- Logs email delivery status

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

**Accept Header Format:**
- `Accept: application/vnd.appointmentbooking.v1+json`
- `Accept: application/vnd.appointmentbooking.v2+json`

**Implementation Approach:**
- Content negotiation using Accept headers
- Same endpoint path, different response versions
- Version-specific DTOs (AppointmentV1, AppointmentV2)
- Spring @RequestMapping with produces attribute

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

**Request Format:**
- Query parameters: page, size, sort
- Example: `GET /api/v1/appointments?page=0&size=20&sort=appointment_date,desc`

**Response Structure:**
- content: Array of results
- pageable: Page metadata (pageNumber, pageSize, offset)
- totalElements: Total count across all pages
- totalPages: Total number of pages
- last: Boolean indicating if this is the last page

### Filtering Pattern

**Query Parameter Approach:**
- Doctor filtering: `doctorId=5`
- Status filtering: `status=CONFIRMED`
- Date filtering: `date=2025-10-08`
- Combined: `GET /api/v1/appointments?doctorId=5&status=CONFIRMED&date=2025-10-08`

**Search Endpoints:**
- User search: `GET /api/v1/users/search?role=DOCTOR&specialization=cardiology&page=0&size=10`
- Multiple criteria supported with pagination

### Sorting Pattern

**Multiple Sort Parameters:**
- Primary sort: `sort=name,asc`
- Secondary sort: `sort=price,desc`
- Combined: `GET /api/v1/services?sort=name,asc&sort=price,desc`

**Sort Order:**
1. Primary sort applied first (name ascending)
2. Secondary sort for ties (price descending)

### Implementation Approach

**Technology Stack:**
- Spring Data JPA PageRequest for pagination
- Sort.by() for ordering
- JPA Specifications for dynamic filtering
- Predicate building for multiple filter criteria

**Flow:**
1. Parse query parameters (page, size, sort, filters)
2. Create PageRequest with sort configuration
3. Build dynamic Specification with predicates
4. Execute findAll with specification and pagination
5. Convert entities to DTOs
6. Return paginated response

---

## 🧪 Error Handling & Validation

### Global Exception Handler

**Implementation:**
- @ControllerAdvice annotation for centralized exception handling
- Handles specific exceptions (ResourceNotFoundException, MethodArgumentNotValidException)
- Returns consistent ErrorResponse objects

**ResourceNotFoundException Handling:**
- HTTP Status: 404 NOT_FOUND
- Includes error message and timestamp
- Returns structured error response

**Validation Exception Handling:**
- HTTP Status: 400 BAD_REQUEST
- Extracts field-level validation errors
- Maps field names to error messages
- Returns detailed validation error response

### Validation Approach

**Bean Validation Annotations:**
- @Email: Email format validation
- @NotBlank: Required field validation
- @Size: Length constraints (min/max characters)
- @Pattern: Regex-based validation (e.g., password complexity)

**Example Constraints:**
- Email: Must be valid email format
- First name: Required, 2-100 characters
- Password: Minimum 8 characters, must contain uppercase and numbers

### Error Response Format

**Standard Structure:**
- status: HTTP status code (e.g., 400)
- message: Human-readable error description
- validation_errors: Map of field names to error messages (for validation failures)
- timestamp: ISO 8601 timestamp

**Example Response:**
- Status: 400
- Message: "Validation failed"
- Validation errors for email and password fields
- Timestamp: ISO format

---

## 🔍 Monitoring & Observability

### Application Metrics

**Spring Boot Actuator Endpoints:**
- /actuator/metrics - General metrics
- /actuator/health - Health status

**Service-specific Metrics:**
- http.server.requests: API latency and request counts
- jpa.repositories.count: Database operation statistics
- rabbitmq.queue.size: Message queue depth monitoring

### Logging Strategy

**Configuration:**
- Root level: INFO
- Application packages: DEBUG level
- Console pattern includes timestamp, thread, log level, logger name, and message

**Benefits:**
- Detailed application-level logging for debugging
- Standard INFO level for third-party libraries
- Structured log format for parsing

### Distributed Tracing (Future)

**Planned Integration:**
- Spring Cloud Sleuth for trace IDs
- Zipkin for trace visualization
- Cross-service log correlation
- Request flow tracking through entire system

---

## 🎯 Performance Considerations

### Database Query Optimization

**N+1 Query Problem Prevention:**
- Avoid lazy loading in loops
- Use eager loading (FetchType.EAGER) when appropriate
- Implement JOIN FETCH queries for related entities
- Custom JPQL queries with LEFT JOIN FETCH

**Best Practices:**
- Fetch associations in single query when needed
- Use specific queries instead of default lazy loading
- Analyze query execution plans

### Caching Strategy

**Implementation:**
- Spring Cache abstraction
- Cache configuration at service level
- Cache names defined per service (e.g., "services")

**Caching Operations:**
- @Cacheable: Cache read operations (key-based)
- @CacheEvict: Clear cache on updates
- Cache key based on method parameters (e.g., entity ID)

**Benefits:**
- Reduced database load
- Faster response times for frequently accessed data
- Automatic cache invalidation on updates

### Connection Pooling

**HikariCP Configuration:**
- Maximum pool size: 20 connections
- Minimum idle connections: 5
- Connection timeout: 20 seconds

**Purpose:**
- Efficient database connection management
- Connection reuse across requests
- Prevents connection exhaustion
- Optimal performance under load

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
