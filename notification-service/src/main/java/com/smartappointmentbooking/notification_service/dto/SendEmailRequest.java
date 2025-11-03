package com.smartappointmentbooking.notification_service.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendEmailRequest {
    private Long userId;
    private String recipientEmail;
    private String subject;
    private String message;
    private String notificationType; // APPOINTMENT_REMINDER, etc.
}
