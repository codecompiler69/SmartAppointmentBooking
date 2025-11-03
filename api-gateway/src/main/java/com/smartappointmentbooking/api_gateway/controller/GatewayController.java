package com.smartappointmentbooking.api_gateway.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/gateway")
@Tag(name = "Gateway", description = "API Gateway health, status and information endpoints")
public class GatewayController {

    @Value("${spring.application.name}")
    private String applicationName;

    @GetMapping("/health")
    @Operation(summary = "Check gateway health status", description = "Returns the health status of the API Gateway and lists all available routes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Gateway is healthy and operational", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class), examples = @ExampleObject(value = """
                    {
                      "service": "api-gateway",
                      "status": "UP",
                      "timestamp": "2025-11-03T10:30:00",
                      "message": "API Gateway is running successfully",
                      "availableRoutes": {
                        "auth": "/api/auth/**",
                        "users": "/api/users/**",
                        "appointments": "/api/appointments/**",
                        "services": "/api/services/**",
                        "notifications": "/api/notifications/**"
                      }
                    }
                    """)))
    })
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("service", applicationName);
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now());
        health.put("message", "API Gateway is running successfully");

        Map<String, String> routes = new HashMap<>();
        routes.put("auth", "/api/auth/**");
        routes.put("users", "/api/users/**");
        routes.put("appointments", "/api/appointments/**");
        routes.put("services", "/api/services/**");
        routes.put("notifications", "/api/notifications/**");

        health.put("availableRoutes", routes);

        return ResponseEntity.ok(health);
    }

    @GetMapping("/info")
    @Operation(summary = "Get gateway information", description = "Returns detailed information about the API Gateway including version, connected services, and their URLs")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Gateway information retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class), examples = @ExampleObject(value = """
                    {
                      "name": "Smart Appointment Booking API Gateway",
                      "version": "1.0.0",
                      "description": "API Gateway for Smart Appointment Booking System",
                      "timestamp": "2025-11-03T10:30:00",
                      "connectedServices": {
                        "auth-service": "http://localhost:8001",
                        "user-service": "http://localhost:8002",
                        "appointment-service": "http://localhost:8003",
                        "service-catalog-service": "http://localhost:8004",
                        "notification-service": "http://localhost:8005"
                      }
                    }
                    """)))
    })
    public ResponseEntity<Map<String, Object>> info() {
        Map<String, Object> info = new HashMap<>();
        info.put("name", "Smart Appointment Booking API Gateway");
        info.put("version", "1.0.0");
        info.put("description", "API Gateway for Smart Appointment Booking System");
        info.put("timestamp", LocalDateTime.now());

        Map<String, String> services = new HashMap<>();
        services.put("auth-service", "http://localhost:8001");
        services.put("user-service", "http://localhost:8002");
        services.put("appointment-service", "http://localhost:8003");
        services.put("service-catalog-service", "http://localhost:8004");
        services.put("notification-service", "http://localhost:8005");

        info.put("connectedServices", services);

        return ResponseEntity.ok(info);
    }
}
