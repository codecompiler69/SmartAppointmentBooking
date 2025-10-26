-- Appointment Service Database Schema

CREATE TABLE IF NOT EXISTS services (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL,
    description TEXT,
    category VARCHAR(100),
    duration_minutes INTEGER,
    base_price DECIMAL(10, 2),
    is_active BOOLEAN DEFAULT TRUE,
    is_deleted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS appointments (
    id BIGSERIAL PRIMARY KEY,
    doctor_id BIGINT NOT NULL,
    patient_id BIGINT NOT NULL,
    service_id BIGINT NOT NULL,
    appointment_date TIMESTAMP NOT NULL,
    appointment_time TIME,
    duration_minutes INTEGER,
    status VARCHAR(50) DEFAULT 'SCHEDULED',
    notes TEXT,
    cancelled_reason VARCHAR(255),
    cancelled_by VARCHAR(50),
    cancelled_at TIMESTAMP,
    version BIGINT DEFAULT 0,
    is_deleted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (service_id) REFERENCES services(id),
    UNIQUE(doctor_id, appointment_date, appointment_time)
);

CREATE TABLE IF NOT EXISTS appointment_cancellations (
    id BIGSERIAL PRIMARY KEY,
    appointment_id BIGINT NOT NULL,
    cancelled_by_user_id BIGINT NOT NULL,
    cancellation_reason VARCHAR(255),
    cancelled_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (appointment_id) REFERENCES appointments(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS appointment_confirmations (
    id BIGSERIAL PRIMARY KEY,
    appointment_id BIGINT NOT NULL,
    confirmed_by_user_id BIGINT NOT NULL,
    confirmation_notes TEXT,
    confirmed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (appointment_id) REFERENCES appointments(id) ON DELETE CASCADE
);

-- Create indexes for performance
CREATE INDEX IF NOT EXISTS idx_services_category ON services(category);
CREATE INDEX IF NOT EXISTS idx_services_is_active ON services(is_active);
CREATE INDEX IF NOT EXISTS idx_services_is_deleted ON services(is_deleted);

CREATE INDEX IF NOT EXISTS idx_appointments_doctor_id ON appointments(doctor_id);
CREATE INDEX IF NOT EXISTS idx_appointments_patient_id ON appointments(patient_id);
CREATE INDEX IF NOT EXISTS idx_appointments_service_id ON appointments(service_id);
CREATE INDEX IF NOT EXISTS idx_appointments_appointment_date ON appointments(appointment_date);
CREATE INDEX IF NOT EXISTS idx_appointments_status ON appointments(status);
CREATE INDEX IF NOT EXISTS idx_appointments_is_deleted ON appointments(is_deleted);
CREATE INDEX IF NOT EXISTS idx_appointments_doctor_date ON appointments(doctor_id, appointment_date);

CREATE INDEX IF NOT EXISTS idx_cancellations_appointment_id ON appointment_cancellations(appointment_id);
CREATE INDEX IF NOT EXISTS idx_confirmations_appointment_id ON appointment_confirmations(appointment_id);
