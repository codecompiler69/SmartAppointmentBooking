package com.smartappointmentbooking.appointment_service.controller;

import com.smartappointmentbooking.appointment_service.dto.CreateAppointmentRequest;
import com.smartappointmentbooking.appointment_service.dto.UpdateAppointmentRequest;
import com.smartappointmentbooking.appointment_service.dto.AppointmentResponse;
import com.smartappointmentbooking.appointment_service.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
@Tag(name = "Appointments", description = "Appointment management endpoints")
public class AppointmentController {
    private final AppointmentService appointmentService;

    @PostMapping
    @Operation(summary = "Create new appointment")
    public ResponseEntity<AppointmentResponse> createAppointment(@RequestBody CreateAppointmentRequest request) {
        AppointmentResponse response = appointmentService.createAppointment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Get all appointments")
    public ResponseEntity<List<AppointmentResponse>> getAllAppointments() {
        List<AppointmentResponse> appointments = appointmentService.getAllAppointments();
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get appointment by ID")
    public ResponseEntity<AppointmentResponse> getAppointmentById(@PathVariable Long id) {
        AppointmentResponse appointment = appointmentService.getAppointmentById(id);
        return ResponseEntity.ok(appointment);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update appointment")
    public ResponseEntity<AppointmentResponse> updateAppointment(
            @PathVariable Long id,
            @RequestBody UpdateAppointmentRequest request) {
        AppointmentResponse appointment = appointmentService.updateAppointment(id, request);
        return ResponseEntity.ok(appointment);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancel appointment")
    public ResponseEntity<Void> cancelAppointment(
            @PathVariable Long id,
            @RequestParam(required = false) String reason) {
        appointmentService.cancelAppointment(id, reason != null ? reason : "No reason provided");
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/doctor/{doctorId}")
    @Operation(summary = "Get appointments by doctor ID")
    public ResponseEntity<List<AppointmentResponse>> getAppointmentsByDoctor(@PathVariable Long doctorId) {
        List<AppointmentResponse> appointments = appointmentService.getAppointmentsByDoctor(doctorId);
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/patient/{patientId}")
    @Operation(summary = "Get appointments by patient ID")
    public ResponseEntity<List<AppointmentResponse>> getAppointmentsByPatient(@PathVariable Long patientId) {
        List<AppointmentResponse> appointments = appointmentService.getAppointmentsByPatient(patientId);
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/health")
    @Operation(summary = "Health check")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Appointment service is running");
    }
}
