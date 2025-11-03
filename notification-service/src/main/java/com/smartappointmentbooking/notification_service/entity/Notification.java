package com.smartappointmentbooking.notification_service.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String recipientEmail;

    @Column(nullable = false)
    private String subject;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType notificationType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationChannel channel; // EMAIL, SMS

    @Builder.Default
    @Column(nullable = false)
    private Boolean isRead = false;

    @Column
    private LocalDateTime sentAt;

    @Column
    private LocalDateTime readAt;

    @Column
    private String status; // PENDING, SENT, FAILED

    @Column
    private String errorMessage;

    @Builder.Default
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    @Column
    private LocalDateTime updatedAt = LocalDateTime.now();

    public enum NotificationType {
        APPOINTMENT_REMINDER,
        APPOINTMENT_CONFIRMATION,
        APPOINTMENT_CANCELLED,
        APPOINTMENT_RESCHEDULED,
        DOCTOR_REVIEW,
        SYSTEM_ALERT
    }

    public enum NotificationChannel {
        EMAIL,
        SMS
    }
}
