package com.smartappointmentbooking.notification_service.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendSmsRequest {
    private Long userId;
    private String phoneNumber;
    private String message;
    private String notificationType; // APPOINTMENT_REMINDER, etc.
}
