package com.smartappointmentbooking.api_gateway.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notifications", description = "Email and SMS notification endpoints (Proxied to Notification Service)")
public class NotificationServiceDocsController {

    @PostMapping("/email")
    @Operation(summary = "Send email notification", description = "Send an email notification to a user", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email sent successfully", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "id": 1,
                      "userId": 1,
                      "type": "EMAIL",
                      "recipient": "patient@example.com",
                      "subject": "Appointment Confirmation",
                      "content": "Your appointment has been confirmed",
                      "status": "SENT",
                      "sentAt": "2025-11-03T10:30:00"
                    }
                    """))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Email sending failed")
    })
    public void sendEmail(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Email notification details", content = @Content(examples = @ExampleObject(value = """
                    {
                      "userId": 1,
                      "recipient": "patient@example.com",
                      "subject": "Appointment Confirmation",
                      "content": "Your appointment has been confirmed for November 5th at 10:00 AM"
                    }
                    """))) Object request) {
        // Documentation only
    }

    @PostMapping("/sms")
    @Operation(summary = "Send SMS notification", description = "Send an SMS notification to a user's phone number", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SMS sent successfully", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "id": 2,
                      "userId": 1,
                      "type": "SMS",
                      "recipient": "+1234567890",
                      "content": "Appointment reminder: Tomorrow at 10:00 AM",
                      "status": "SENT",
                      "sentAt": "2025-11-03T10:30:00"
                    }
                    """))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "SMS sending failed")
    })
    public void sendSms(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "SMS notification details", content = @Content(examples = @ExampleObject(value = """
                    {
                      "userId": 1,
                      "phoneNumber": "+1234567890",
                      "message": "Appointment reminder: Tomorrow at 10:00 AM"
                    }
                    """))) Object request) {
        // Documentation only
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user notifications", description = "Retrieve all notifications for a specific user", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notifications retrieved successfully", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    [
                      {
                        "id": 1,
                        "userId": 1,
                        "type": "EMAIL",
                        "recipient": "patient@example.com",
                        "subject": "Appointment Confirmation",
                        "content": "Your appointment has been confirmed",
                        "status": "SENT",
                        "sentAt": "2025-11-03T10:30:00"
                      },
                      {
                        "id": 2,
                        "userId": 1,
                        "type": "SMS",
                        "recipient": "+1234567890",
                        "content": "Appointment reminder",
                        "status": "SENT",
                        "sentAt": "2025-11-03T11:00:00"
                      }
                    ]
                    """))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public void getUserNotifications(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        // Documentation only
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get notification by ID", description = "Retrieve a specific notification by its ID", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notification found", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "id": 1,
                      "userId": 1,
                      "type": "EMAIL",
                      "recipient": "patient@example.com",
                      "subject": "Appointment Confirmation",
                      "content": "Your appointment has been confirmed",
                      "status": "SENT",
                      "sentAt": "2025-11-03T10:30:00",
                      "createdAt": "2025-11-03T10:30:00"
                    }
                    """))),
            @ApiResponse(responseCode = "404", description = "Notification not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public void getNotificationById(
            @Parameter(description = "Notification ID") @PathVariable Long id) {
        // Documentation only
    }

    @GetMapping
    @Operation(summary = "Get all notifications (ADMIN only)", description = "Retrieve all notifications in the system (requires ADMIN role)", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notifications retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - ADMIN role required")
    })
    public void getAllNotifications() {
        // Documentation only
    }
}
