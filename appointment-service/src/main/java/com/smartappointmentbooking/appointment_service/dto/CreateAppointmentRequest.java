package com.smartappointmentbooking.appointment_service.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAppointmentRequest {
    private Long patientId;
    private Long doctorId;
    private Long serviceId;
    private LocalDateTime appointmentDate;
    private String reason;
    private String notes;
    private Double totalPrice;
}
