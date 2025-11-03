package com.smartappointmentbooking.notification_service.service;

import com.smartappointmentbooking.notification_service.dto.SendEmailRequest;
import com.smartappointmentbooking.notification_service.dto.NotificationResponse;
import com.smartappointmentbooking.notification_service.entity.Notification;
import com.smartappointmentbooking.notification_service.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
        private final NotificationRepository notificationRepository;
        private final JavaMailSender javaMailSender;

        @Value("${spring.mail.username}")
        private String fromEmail;

        @Value("${app.name:Smart Appointment Booking}")
        private String appName;

        public NotificationResponse sendEmail(SendEmailRequest request) {
                try {
                        // Save notification to database with PENDING status
                        Notification notification = Notification.builder()
                                        .userId(request.getUserId() != null ? request.getUserId() : 0L)
                                        .recipientEmail(request.getRecipientEmail())
                                        .subject(request.getSubject())
                                        .message(request.getMessage())
                                        .notificationType(Notification.NotificationType.valueOf(
                                                        request.getNotificationType() != null
                                                                        ? request.getNotificationType()
                                                                        : "SYSTEM_ALERT"))
                                        .channel(Notification.NotificationChannel.EMAIL)
                                        .status("PENDING")
                                        .build();

                        // Create MIME message for HTML email
                        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
                        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

                        // Set email headers
                        helper.setTo(request.getRecipientEmail());
                        helper.setSubject(request.getSubject());
                        helper.setFrom(fromEmail, appName);

                        // Create HTML email body with professional formatting
                        String htmlContent = buildHtmlEmailBody(request.getSubject(), request.getMessage());
                        helper.setText(htmlContent, true);

                        // Send email
                        javaMailSender.send(mimeMessage);

                        // Update notification status to SENT
                        notification.setStatus("SENT");
                        notification.setSentAt(LocalDateTime.now());
                        notification = notificationRepository.save(notification);

                        log.info("Email sent successfully to: {} with subject: {}", request.getRecipientEmail(),
                                        request.getSubject());
                        return mapToResponse(notification);

                } catch (MessagingException e) {
                        log.error("Failed to send email to {}: {}", request.getRecipientEmail(), e.getMessage());
                        // Save failed notification record
                        Notification notification = Notification.builder()
                                        .userId(request.getUserId() != null ? request.getUserId() : 0L)
                                        .recipientEmail(request.getRecipientEmail())
                                        .subject(request.getSubject())
                                        .message(request.getMessage())
                                        .notificationType(Notification.NotificationType.valueOf(
                                                        request.getNotificationType() != null
                                                                        ? request.getNotificationType()
                                                                        : "SYSTEM_ALERT"))
                                        .channel(Notification.NotificationChannel.EMAIL)
                                        .status("FAILED")
                                        .errorMessage("MessagingException: " + e.getMessage())
                                        .build();
                        notification = notificationRepository.save(notification);
                        return mapToResponse(notification);
                } catch (Exception e) {
                        log.error("Unexpected error while sending email to {}: {}", request.getRecipientEmail(),
                                        e.getMessage());
                        Notification notification = Notification.builder()
                                        .userId(request.getUserId() != null ? request.getUserId() : 0L)
                                        .recipientEmail(request.getRecipientEmail())
                                        .subject(request.getSubject())
                                        .message(request.getMessage())
                                        .notificationType(Notification.NotificationType.valueOf(
                                                        request.getNotificationType() != null
                                                                        ? request.getNotificationType()
                                                                        : "SYSTEM_ALERT"))
                                        .channel(Notification.NotificationChannel.EMAIL)
                                        .status("FAILED")
                                        .errorMessage(e.getClass().getSimpleName() + ": " + e.getMessage())
                                        .build();
                        notification = notificationRepository.save(notification);
                        return mapToResponse(notification);
                }
        }

        private String buildHtmlEmailBody(String subject, String message) {
                return "<html><body style=\"font-family: Arial, sans-serif; line-height: 1.6; color: #333;\">" +
                                "<div style=\"max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 5px;\">"
                                +
                                "<div style=\"background-color: #f8f9fa; padding: 15px; border-radius: 5px; margin-bottom: 20px;\">"
                                +
                                "<h2 style=\"margin: 0; color: #0066cc;\">" + appName + "</h2>" +
                                "</div>" +
                                "<h3 style=\"color: #333; margin-top: 0;\">" + subject + "</h3>" +
                                "<p style=\"white-space: pre-wrap; word-wrap: break-word;\">" + escapeHtml(message)
                                + "</p>" +
                                "<hr style=\"border: none; border-top: 1px solid #ddd; margin: 20px 0;\">" +
                                "<p style=\"font-size: 12px; color: #999; text-align: center;\">" +
                                "This is an automated message from " + appName + ". Please do not reply to this email."
                                +
                                "</p>" +
                                "</div>" +
                                "</body></html>";
        }

        private String escapeHtml(String text) {
                if (text == null)
                        return "";
                return text.replace("&", "&amp;")
                                .replace("<", "&lt;")
                                .replace(">", "&gt;")
                                .replace("\"", "&quot;")
                                .replace("'", "&#39;");
        }

        public List<NotificationResponse> getNotificationsByUserId(Long userId) {
                return notificationRepository.findByUserId(userId).stream()
                                .map(this::mapToResponse)
                                .collect(Collectors.toList());
        }

        public NotificationResponse getNotificationById(Long id) {
                Notification notification = notificationRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Notification not found"));
                return mapToResponse(notification);
        }

        public NotificationResponse markAsRead(Long id) {
                Notification notification = notificationRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Notification not found"));
                notification.setIsRead(true);
                notification.setReadAt(LocalDateTime.now());
                notification = notificationRepository.save(notification);
                return mapToResponse(notification);
        }

        public void deleteNotification(Long id) {
                notificationRepository.deleteById(id);
        }

        public List<NotificationResponse> getAllNotifications() {
                return notificationRepository.findAll().stream()
                                .map(this::mapToResponse)
                                .collect(Collectors.toList());
        }

        private NotificationResponse mapToResponse(Notification notification) {
                return NotificationResponse.builder()
                                .id(notification.getId())
                                .userId(notification.getUserId())
                                .recipientEmail(notification.getRecipientEmail())
                                .subject(notification.getSubject())
                                .message(notification.getMessage())
                                .notificationType(notification.getNotificationType().toString())
                                .channel(notification.getChannel().toString())
                                .isRead(notification.getIsRead())
                                .status(notification.getStatus())
                                .createdAt(notification.getCreatedAt())
                                .build();
        }
}
