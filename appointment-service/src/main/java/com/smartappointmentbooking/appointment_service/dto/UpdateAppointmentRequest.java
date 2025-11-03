package com.smartappointmentbooking.appointment_service.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAppointmentRequest {
    private LocalDateTime appointmentDate;
    private String reason;
    private String notes;
    private String status;
}
