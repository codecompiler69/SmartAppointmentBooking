package com.smartappointmentbooking.notification_service.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
    private Long id;
    private Long userId;
    private String recipientEmail;
    private String subject;
    private String message;
    private String notificationType;
    private String channel;
    private Boolean isRead;
    private String status;
    private LocalDateTime createdAt;
}
