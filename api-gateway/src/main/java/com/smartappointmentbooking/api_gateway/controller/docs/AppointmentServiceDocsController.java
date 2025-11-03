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
@RequestMapping("/api/appointments")
@Tag(name = "Appointments", description = "Appointment booking and management endpoints (Proxied to Appointment Service)")
public class AppointmentServiceDocsController {

    @PostMapping
    @Operation(summary = "Create new appointment", description = "Book a new appointment with a doctor", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Appointment created successfully", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "id": 1,
                      "doctorId": 2,
                      "patientId": 1,
                      "serviceId": 1,
                      "appointmentDate": "2025-11-05T10:00:00",
                      "status": "SCHEDULED",
                      "notes": "Regular checkup",
                      "createdAt": "2025-11-03T10:30:00"
                    }
                    """))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "409", description = "Time slot not available")
    })
    public void createAppointment(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Appointment details", content = @Content(examples = @ExampleObject(value = """
                    {
                      "doctorId": 2,
                      "patientId": 1,
                      "serviceId": 1,
                      "appointmentDate": "2025-11-05T10:00:00",
                      "notes": "Regular checkup"
                    }
                    """))) Object request) {
        // Documentation only
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get appointment by ID", description = "Retrieve appointment details by appointment ID", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Appointment found", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "id": 1,
                      "doctorId": 2,
                      "patientId": 1,
                      "serviceId": 1,
                      "appointmentDate": "2025-11-05T10:00:00",
                      "status": "SCHEDULED",
                      "notes": "Regular checkup",
                      "createdAt": "2025-11-03T10:30:00",
                      "updatedAt": "2025-11-03T10:30:00"
                    }
                    """))),
            @ApiResponse(responseCode = "404", description = "Appointment not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public void getAppointmentById(
            @Parameter(description = "Appointment ID") @PathVariable Long id) {
        // Documentation only
    }

    @GetMapping("/patient/{patientId}")
    @Operation(summary = "Get patient appointments", description = "Retrieve all appointments for a specific patient (PATIENT role required)", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Appointments retrieved successfully", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    [
                      {
                        "id": 1,
                        "doctorId": 2,
                        "patientId": 1,
                        "serviceId": 1,
                        "appointmentDate": "2025-11-05T10:00:00",
                        "status": "SCHEDULED",
                        "notes": "Regular checkup"
                      }
                    ]
                    """))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - PATIENT role required")
    })
    public void getPatientAppointments(
            @Parameter(description = "Patient ID") @PathVariable Long patientId) {
        // Documentation only
    }

    @GetMapping("/doctor/{doctorId}")
    @Operation(summary = "Get doctor appointments", description = "Retrieve all appointments for a specific doctor (DOCTOR role required)", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Appointments retrieved successfully", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    [
                      {
                        "id": 1,
                        "doctorId": 2,
                        "patientId": 1,
                        "serviceId": 1,
                        "appointmentDate": "2025-11-05T10:00:00",
                        "status": "SCHEDULED",
                        "notes": "Regular checkup"
                      }
                    ]
                    """))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - DOCTOR role required")
    })
    public void getDoctorAppointments(
            @Parameter(description = "Doctor ID") @PathVariable Long doctorId) {
        // Documentation only
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update appointment", description = "Update appointment details or status", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Appointment updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Appointment not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public void updateAppointment(
            @Parameter(description = "Appointment ID") @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Updated appointment details", content = @Content(examples = @ExampleObject(value = """
                    {
                      "appointmentDate": "2025-11-05T14:00:00",
                      "status": "CONFIRMED",
                      "notes": "Updated notes"
                    }
                    """))) Object request) {
        // Documentation only
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancel appointment", description = "Cancel an existing appointment", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Appointment cancelled successfully"),
            @ApiResponse(responseCode = "404", description = "Appointment not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public void cancelAppointment(
            @Parameter(description = "Appointment ID") @PathVariable Long id) {
        // Documentation only
    }

    @GetMapping
    @Operation(summary = "Get all appointments", description = "Retrieve all appointments (ADMIN role required)", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Appointments retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - ADMIN role required")
    })
    public void getAllAppointments() {
        // Documentation only
    }
}
