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
@RequestMapping("/api/users")
@Tag(name = "Users", description = "User and doctor profile management endpoints (Proxied to User Service)")
public class UserServiceDocsController {

    @GetMapping("/profile")
    @Operation(summary = "Get current user profile", description = "Retrieve the profile of the currently authenticated user", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile retrieved successfully", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "id": 1,
                      "email": "patient@example.com",
                      "firstName": "John",
                      "lastName": "Doe",
                      "phone": "+1234567890",
                      "role": "PATIENT",
                      "createdAt": "2025-11-01T10:00:00"
                    }
                    """))),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public void getCurrentUserProfile() {
        // Documentation only
    }

    @PutMapping("/profile")
    @Operation(summary = "Update user profile", description = "Update the profile information of the currently authenticated user", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public void updateUserProfile(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Updated profile information", content = @Content(examples = @ExampleObject(value = """
                    {
                      "firstName": "John",
                      "lastName": "Doe",
                      "phone": "+1234567890"
                    }
                    """))) Object request) {
        // Documentation only
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieve user profile by user ID", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public void getUserById(
            @Parameter(description = "User ID") @PathVariable Long id) {
        // Documentation only
    }

    @GetMapping("/doctors")
    @Operation(summary = "Get all doctors", description = "Retrieve a list of all registered doctors with their profiles", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Doctors retrieved successfully", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    [
                      {
                        "id": 2,
                        "userId": 2,
                        "specialization": "Cardiology",
                        "licenseNumber": "DOC12345",
                        "yearsOfExperience": 10,
                        "consultationFee": 150.00,
                        "bio": "Experienced cardiologist",
                        "user": {
                          "id": 2,
                          "firstName": "Jane",
                          "lastName": "Smith",
                          "email": "doctor@example.com"
                        }
                      }
                    ]
                    """))),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public void getAllDoctors() {
        // Documentation only
    }

    @PostMapping("/doctors/profile")
    @Operation(summary = "Create doctor profile", description = "Create a professional profile for a doctor (DOCTOR role required)", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Doctor profile created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - DOCTOR role required")
    })
    public void createDoctorProfile(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Doctor profile information", content = @Content(examples = @ExampleObject(value = """
                    {
                      "specialization": "Cardiology",
                      "licenseNumber": "DOC12345",
                      "yearsOfExperience": 10,
                      "consultationFee": 150.00,
                      "bio": "Experienced cardiologist specializing in heart diseases"
                    }
                    """))) Object request) {
        // Documentation only
    }

    @GetMapping("/doctors/{id}")
    @Operation(summary = "Get doctor profile by ID", description = "Retrieve detailed doctor profile information", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Doctor profile found"),
            @ApiResponse(responseCode = "404", description = "Doctor not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public void getDoctorById(
            @Parameter(description = "Doctor ID") @PathVariable Long id) {
        // Documentation only
    }

    @PutMapping("/doctors/profile")
    @Operation(summary = "Update doctor profile", description = "Update the professional profile of the currently authenticated doctor", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Doctor profile updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - DOCTOR role required")
    })
    public void updateDoctorProfile(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Updated doctor profile information", content = @Content(examples = @ExampleObject(value = """
                    {
                      "specialization": "Cardiology",
                      "yearsOfExperience": 11,
                      "consultationFee": 175.00,
                      "bio": "Updated bio"
                    }
                    """))) Object request) {
        // Documentation only
    }
}
