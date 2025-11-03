package com.smartappointmentbooking.appointment_service.service;

import com.smartappointmentbooking.appointment_service.dto.CreateAppointmentRequest;
import com.smartappointmentbooking.appointment_service.dto.UpdateAppointmentRequest;
import com.smartappointmentbooking.appointment_service.dto.AppointmentResponse;
import com.smartappointmentbooking.appointment_service.entity.Appointment;
import com.smartappointmentbooking.appointment_service.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;

    public AppointmentResponse createAppointment(CreateAppointmentRequest request) {
        Appointment appointment = Appointment.builder()
                .patientId(request.getPatientId())
                .doctorId(request.getDoctorId())
                .serviceId(request.getServiceId())
                .appointmentDate(request.getAppointmentDate())
                .reason(request.getReason())
                .notes(request.getNotes())
                .totalPrice(request.getTotalPrice())
                .status(Appointment.AppointmentStatus.SCHEDULED)
                .build();

        appointment = appointmentRepository.save(appointment);
        log.info("Appointment created: {}", appointment.getId());
        return mapToResponse(appointment);
    }

    public List<AppointmentResponse> getAllAppointments() {
        return appointmentRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public AppointmentResponse getAppointmentById(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        return mapToResponse(appointment);
    }

    public AppointmentResponse updateAppointment(Long id, UpdateAppointmentRequest request) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (request.getAppointmentDate() != null) {
            appointment.setAppointmentDate(request.getAppointmentDate());
        }
        if (request.getReason() != null) {
            appointment.setReason(request.getReason());
        }
        if (request.getNotes() != null) {
            appointment.setNotes(request.getNotes());
        }
        if (request.getStatus() != null) {
            appointment.setStatus(Appointment.AppointmentStatus.valueOf(request.getStatus()));
        }

        appointment.setUpdatedAt(LocalDateTime.now());
        appointment = appointmentRepository.save(appointment);
        log.info("Appointment updated: {}", id);
        return mapToResponse(appointment);
    }

    public void cancelAppointment(Long id, String cancellationReason) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        appointment.setStatus(Appointment.AppointmentStatus.CANCELLED);
        appointment.setCancelledAt(LocalDateTime.now());
        appointment.setCancellationReason(cancellationReason);
        appointmentRepository.save(appointment);
        log.info("Appointment cancelled: {}", id);
    }

    public List<AppointmentResponse> getAppointmentsByPatient(Long patientId) {
        return appointmentRepository.findByPatientId(patientId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<AppointmentResponse> getAppointmentsByDoctor(Long doctorId) {
        return appointmentRepository.findByDoctorId(doctorId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private AppointmentResponse mapToResponse(Appointment appointment) {
        return AppointmentResponse.builder()
                .id(appointment.getId())
                .patientId(appointment.getPatientId())
                .doctorId(appointment.getDoctorId())
                .serviceId(appointment.getServiceId())
                .appointmentDate(appointment.getAppointmentDate())
                .status(appointment.getStatus().toString())
                .reason(appointment.getReason())
                .notes(appointment.getNotes())
                .totalPrice(appointment.getTotalPrice())
                .createdAt(appointment.getCreatedAt())
                .updatedAt(appointment.getUpdatedAt())
                .build();
    }
}
