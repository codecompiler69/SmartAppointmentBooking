package com.smartappointmentbooking.api_gateway.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

/**
 * This is a documentation-only controller.
 * Actual endpoints are proxied through the Gateway configuration.
 * These methods will never be called - they exist solely to generate Swagger
 * documentation.
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication and authorization endpoints (Proxied to Auth Service)")
public class AuthServiceDocsController {

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Register a new user account (Patient, Doctor, or Admin). Returns user details and JWT tokens.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
                      "refreshToken": "eyJhbGciOiJIUzUxMiJ9...",
                      "tokenType": "Bearer",
                      "expiresIn": 86400000,
                      "user": {
                        "id": 1,
                        "email": "patient@example.com",
                        "firstName": "John",
                        "lastName": "Doe",
                        "role": "PATIENT"
                      }
                    }
                    """))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "409", description = "User already exists")
    })
    public void register(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "User registration details", required = true, content = @Content(examples = @ExampleObject(value = """
                    {
                      "email": "patient@example.com",
                      "password": "Password123!",
                      "firstName": "John",
                      "lastName": "Doe",
                      "phone": "+1234567890",
                      "role": "PATIENT"
                    }
                    """))) Object request) {
        // Documentation only - actual implementation in Auth Service
    }

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user and receive JWT access and refresh tokens")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
                      "refreshToken": "eyJhbGciOiJIUzUxMiJ9...",
                      "tokenType": "Bearer",
                      "expiresIn": 86400000,
                      "user": {
                        "id": 1,
                        "email": "patient@example.com",
                        "firstName": "John",
                        "lastName": "Doe",
                        "role": "PATIENT"
                      }
                    }
                    """))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    public void login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Login credentials", required = true, content = @Content(examples = @ExampleObject(value = """
                    {
                      "email": "patient@example.com",
                      "password": "Password123!"
                    }
                    """))) Object request) {
        // Documentation only
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token", description = "Get a new access token using a valid refresh token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token refreshed successfully", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
                      "refreshToken": "eyJhbGciOiJIUzUxMiJ9...",
                      "tokenType": "Bearer",
                      "expiresIn": 86400000
                    }
                    """))),
            @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token")
    })
    public void refresh(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Refresh token", required = true, content = @Content(examples = @ExampleObject(value = """
                    {
                      "refreshToken": "eyJhbGciOiJIUzUxMiJ9..."
                    }
                    """))) Object request) {
        // Documentation only
    }

    @PostMapping("/logout")
    @Operation(summary = "User logout", description = "Invalidate the current refresh token", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logged out successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public void logout(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Refresh token to invalidate", required = true, content = @Content(examples = @ExampleObject(value = """
                    {
                      "refreshToken": "eyJhbGciOiJIUzUxMiJ9..."
                    }
                    """))) Object request) {
        // Documentation only
    }

    @GetMapping("/validate")
    @Operation(summary = "Validate JWT token", description = "Validate if the provided JWT token is valid and not expired", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token is valid", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "valid": true,
                      "email": "patient@example.com",
                      "role": "PATIENT"
                    }
                    """))),
            @ApiResponse(responseCode = "401", description = "Token is invalid or expired")
    })
    public void validate() {
        // Documentation only
    }
}
